package com.timepieces.automation.utils;

import static org.monte.media.FormatKeys.EncodingKey;
import static org.monte.media.FormatKeys.FrameRateKey;
import static org.monte.media.FormatKeys.KeyFrameIntervalKey;
import static org.monte.media.FormatKeys.MIME_AVI;
import static org.monte.media.FormatKeys.MediaTypeKey;
import static org.monte.media.FormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.CompressorNameKey;
import static org.monte.media.VideoFormatKeys.DepthKey;
import static org.monte.media.VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE;
import static org.monte.media.VideoFormatKeys.KeyFrameIntervalKey;
import static org.monte.media.VideoFormatKeys.QualityKey;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;
import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.Registry;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

/**
 * ThreadLocal-safe wrapper around Monte Screen Recorder for capturing
 * per-scenario videos.
 *
 * <p>
 * Recorder operates on the full desktop, so concurrent recordings on the same
 * machine all capture the same pixels — useful only for headed, ideally serial,
 * executions. Headless JVMs short-circuit safely with no recording produced.
 *
 * <p>
 * Monte writes a TechSmith-codec AVI, which browsers cannot play inline. On
 * extraction the AVI is transcoded to H.264 MP4 via ffmpeg so the Allure report
 * embeds a playable HTML5 video instead of a download-only link; if ffmpeg is
 * absent the raw AVI is attached as a fallback.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of ScreenRecorderUtil"
* @since 2026-07-03
 */

@Log4j2
public final class ScreenRecorderUtil {

    private static final ThreadLocal<NamedScreenRecorder> TL_RECORDER = new ThreadLocal<>();
    private static final String OUTPUT_DIR = "target/screen-recordings";

    private ScreenRecorderUtil() {
    }

    /**
     * Decides whether a recording would actually show the test, and says so out loud when
     * it would not.
     *
     * <h2>Why this guard exists — the thing to understand about this recorder</h2>
     * Monte records the <b>physical desktop</b>, not the browser:
     *
     * <pre>
     * Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
     * Rectangle captureArea = new Rectangle(0, 0, screen.width, screen.height);
     * </pre>
     *
     * A <b>headless</b> browser never paints to that desktop. So "record video" and "run
     * headless" are not two settings that combine — turning both on gives you a video of
     * your wallpaper while the test runs invisibly. On a 476-test suite that is hundreds of
     * large, identical, useless files, and it is not obvious from the filenames that they
     * are junk. Failing loudly here is much kinder than shipping that.
     *
     * <h2>Why {@code GraphicsEnvironment.isHeadless()} alone was not enough</h2>
     * That flag describes the <b>JVM</b>, not the browser. On Windows or macOS with a
     * desktop session it is {@code false} even when Chrome is running
     * {@code --headless=new} — so the old check passed and recording proceeded anyway,
     * capturing an empty screen. The browser's own headless flag has to be consulted too.
     *
     * <h2>Where video DOES work</h2>
     * <ul>
     *   <li><b>Headed locally</b> — {@code -Dheadless=false}. Best for debugging one test.</li>
     *   <li><b>Docker</b> — the image ships <b>Xvfb</b>, a virtual display. Chrome runs
     *       headed <i>inside</i> it, paints to that virtual desktop, and Monte records it
     *       properly. This is the supported way to get video for a whole run.</li>
     * </ul>
     * Screenshots are unaffected either way: Selenium's {@code TakesScreenshot} asks the
     * <i>browser</i> for its pixels, so failure screenshots keep working in headless.
     *
     * @param fileName the recording that was requested, for the log message
     * @return true only if a recording would genuinely capture the browser
     */
    private static boolean canRecordMeaningfully(String fileName) {
        if (GraphicsEnvironment.isHeadless()) {
            log.warn("Headless JVM (no display available); skipping screen recording for [{}].", fileName);
            return false;
        }

        // The browser's own headless setting — the case the old guard missed entirely.
        if (Boolean.parseBoolean(ConfigReader.get("headless"))) {
            log.warn("Screen recording SKIPPED for [{}]: the browser is running headless, and this "
                    + "recorder captures the DESKTOP, not the browser — it would record an empty "
                    + "screen. Re-run with -Dheadless=false for video locally, or use Docker, whose "
                    + "Xvfb virtual display lets the browser paint somewhere Monte can see it. "
                    + "Failure SCREENSHOTS are unaffected and still captured.", fileName);
            return false;
        }
        return true;
    }

    /**
     * Begins recording the primary display into an AVI keyed by {@code fileName}.
     *
     * <p>Silently does nothing when recording could not produce anything useful — see
     * {@link #canRecordMeaningfully()}, which is the important method to read here.
     */
    public static void start(String fileName) {
        if (!canRecordMeaningfully(fileName)) {
            return;
        }
        try {
            File outputDir = new File(OUTPUT_DIR);
            if (!outputDir.exists() && !outputDir.mkdirs()) {
                log.error("Unable to create video output directory: {}", outputDir.getAbsolutePath());
                return;
            }

            GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration();

            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle captureArea = new Rectangle(0, 0, screen.width, screen.height);

            NamedScreenRecorder recorder = new NamedScreenRecorder(
                    gc,
                    captureArea,
                    new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                    new Format(
                            MediaTypeKey, MediaType.VIDEO,
                            EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            DepthKey, 24,
                            FrameRateKey, Rational.valueOf(15),
                            QualityKey, 1.0f,
                            KeyFrameIntervalKey, 15 * 60),
                    new Format(
                            MediaTypeKey, MediaType.VIDEO,
                            EncodingKey, "black",
                            FrameRateKey, Rational.valueOf(30)),
                    null,
                    outputDir,
                    fileName);

            recorder.start();
            TL_RECORDER.set(recorder);
            log.info("Started screen recording: {}", fileName);
        } catch (HeadlessException | IOException | AWTException e) {
            log.error("Failed to start screen recording for [{}]: {}", fileName, e.getMessage(), e);
        }
    }

    /**
     * Immutable holder for an extracted recording: the raw bytes plus the media
     * type / extension so the caller can attach it to Allure with the correct
     * MIME type (MP4 renders an inline HTML5 player; AVI is only a download link).
     */
    public record Recording(byte[] bytes, String mediaType, String fileExtension) {
    }

    /**
     * Stops the active recording, transcodes the raw AVI to a browser-playable
     * MP4 (H.264 / yuv420p / +faststart) via ffmpeg so Allure can play it inline,
     * and returns the MP4 bytes. If ffmpeg is unavailable or the transcode fails,
     * the original AVI bytes are returned instead so a recording is never lost.
     * On-disk files are deleted after read. Returns {@code null} if no recording
     * was active or capture failed.
     */
    public static Recording stopAndExtract() {
        NamedScreenRecorder recorder = TL_RECORDER.get();
        if (recorder == null) {
            return null;
        }
        File avi = null;
        File mp4 = null;
        try {
            recorder.stop();
            List<File> files = recorder.getCreatedMovieFiles();
            if (files.isEmpty()) {
                return null;
            }
            avi = files.get(files.size() - 1);

            mp4 = transcodeToMp4(avi);
            if (mp4 != null && mp4.exists() && mp4.length() > 0) {
                byte[] bytes = Files.readAllBytes(mp4.toPath());
                log.info("Transcoded recording to MP4 for inline Allure playback ({} bytes).", bytes.length);
                return new Recording(bytes, "video/mp4", "mp4");
            }

            // Fallback: attach the raw AVI so the evidence survives even without ffmpeg.
            log.warn("MP4 transcode unavailable/failed; attaching the original AVI "
                    + "(viewers may need to download it to play).");
            byte[] bytes = Files.readAllBytes(avi.toPath());
            return new Recording(bytes, "video/avi", "avi");
        } catch (IOException e) {
            log.error("Failed to stop / extract screen recording: {}", e.getMessage(), e);
            return null;
        } finally {
            deleteQuietly(avi);
            deleteQuietly(mp4);
            TL_RECORDER.remove();
        }
    }

    /**
     * Transcodes {@code avi} to a sibling {@code .mp4} using ffmpeg. Uses libx264
     * with the {@code yuv420p} pixel format and {@code +faststart} so the result
     * plays inline in browsers/Allure; even dimensions are forced because H.264
     * requires them. Returns the MP4 file on success, or {@code null} if ffmpeg is
     * missing, times out, or exits non-zero.
     */
    private static File transcodeToMp4(File avi) {
        String ffmpeg = resolveFfmpegBinary();
        String baseName = avi.getName().replaceFirst("\\.[^.]+$", "");
        File mp4 = new File(avi.getParentFile(), baseName + ".mp4");

        ProcessBuilder pb = new ProcessBuilder(
                ffmpeg,
                "-y",
                "-i", avi.getAbsolutePath(),
                "-vf", "scale=trunc(iw/2)*2:trunc(ih/2)*2",
                "-c:v", "libx264",
                "-preset", "veryfast",
                "-crf", "23",
                "-pix_fmt", "yuv420p",
                "-movflags", "+faststart",
                mp4.getAbsolutePath());
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();
            drainStream(process);
            boolean finished = process.waitFor(120, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                log.error("ffmpeg transcode timed out after 120s for [{}].", avi.getName());
                return null;
            }
            if (process.exitValue() != 0) {
                log.error("ffmpeg exited with code {} while transcoding [{}].", process.exitValue(), avi.getName());
                return null;
            }
            return mp4;
        } catch (IOException e) {
            // Most commonly: ffmpeg not found on PATH / at the configured path.
            log.warn("Could not launch ffmpeg [{}] to transcode the recording: {}. "
                    + "Set 'ffmpeg.path' (or the FFMPEG_PATH env var) to enable inline MP4 playback.",
                    ffmpeg, e.getMessage());
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while waiting for ffmpeg transcode of [{}].", avi.getName());
            return null;
        }
    }

    /**
     * Resolves the ffmpeg executable: {@code ffmpeg.path} config key first, then
     * the {@code FFMPEG_PATH} environment variable, defaulting to {@code ffmpeg}
     * on the PATH.
     */
    private static String resolveFfmpegBinary() {
        String configured = ConfigReader.get("ffmpeg.path");
        if (configured != null && !configured.isBlank()) {
            return configured;
        }
        String envPath = System.getenv("FFMPEG_PATH");
        if (envPath != null && !envPath.isBlank()) {
            return envPath;
        }
        return "ffmpeg";
    }

    /**
     * Consumes the merged stdout/stderr so the ffmpeg process never blocks on a
     * full pipe buffer during a long transcode.
     */
    private static void drainStream(Process process) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            while (reader.readLine() != null) {
                // Discard ffmpeg progress chatter; failures surface via the exit code.
            }
        } catch (IOException e) {
            log.debug("Non-fatal error draining ffmpeg output: {}", e.getMessage());
        }
    }

    private static void deleteQuietly(File file) {
        if (file == null) {
            return;
        }
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            log.warn("Could not delete temporary recording file [{}]: {}", file.getAbsolutePath(), e.getMessage());
        }
    }

    /**
     * Stops the active recording and deletes the file without reading it.
     */
    public static void stopAndDiscard() {
        NamedScreenRecorder recorder = TL_RECORDER.get();
        if (recorder == null) {
            return;
        }
        try {
            recorder.stop();
            for (File f : recorder.getCreatedMovieFiles()) {
                Files.deleteIfExists(f.toPath());
            }
        } catch (IOException e) {
            log.error("Failed to discard screen recording: {}", e.getMessage(), e);
        } finally {
            TL_RECORDER.remove();
        }
    }

    /**
     * Subclass override forces deterministic filenames instead of Monte's
     * timestamp default.
     */
    private static final class NamedScreenRecorder extends ScreenRecorder {

        private final String fileName;

        NamedScreenRecorder(
                GraphicsConfiguration cfg,
                Rectangle captureArea,
                Format fileFormat,
                Format screenFormat,
                Format mouseFormat,
                Format audioFormat,
                File outputDir,
                String fileName)
                throws IOException, AWTException {
            super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, outputDir);
            this.fileName = fileName;
        }

        @Override
        protected File createMovieFile(Format fileFormat) throws IOException {
            if (!movieFolder.exists() && !movieFolder.mkdirs()) {
                throw new IOException("Could not create movie folder: " + movieFolder.getAbsolutePath());
            }
            String extension = Registry.getInstance().getExtension(fileFormat);
            return new File(movieFolder, fileName + "." + extension);
        }
    }
}

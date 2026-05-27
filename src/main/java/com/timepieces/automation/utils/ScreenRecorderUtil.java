package com.timepieces.automation.utils;

import static org.monte.media.FormatKeys.EncodingKey;
import static org.monte.media.FormatKeys.FrameRateKey;
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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.Registry;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

/**
 * ThreadLocal-safe wrapper around Monte Screen Recorder for capturing per-scenario videos.
 *
 * <p>Recorder operates on the full desktop, so concurrent recordings on the same machine all
 * capture the same pixels — useful only for headed, ideally serial, executions. Headless JVMs
 * short-circuit safely with no recording produced.
 */
@Log4j2
public final class ScreenRecorderUtil {

  private static final ThreadLocal<NamedScreenRecorder> TL_RECORDER = new ThreadLocal<>();
  private static final String OUTPUT_DIR = "target/screen-recordings";

  private ScreenRecorderUtil() {}

  /** Begins recording the primary display into an AVI keyed by {@code fileName}. */
  public static void start(String fileName) {
    if (GraphicsEnvironment.isHeadless()) {
      log.warn("Headless JVM detected; skipping screen recording for [{}].", fileName);
      return;
    }
    try {
      File outputDir = new File(OUTPUT_DIR);
      if (!outputDir.exists() && !outputDir.mkdirs()) {
        log.error("Unable to create video output directory: {}", outputDir.getAbsolutePath());
        return;
      }

      GraphicsConfiguration gc =
          GraphicsEnvironment.getLocalGraphicsEnvironment()
              .getDefaultScreenDevice()
              .getDefaultConfiguration();

      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
      Rectangle captureArea = new Rectangle(0, 0, screen.width, screen.height);

      NamedScreenRecorder recorder =
          new NamedScreenRecorder(
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
   * Stops the active recording and returns the AVI bytes. The on-disk file is deleted after read.
   * Returns {@code null} if no recording was active or capture failed.
   */
  public static byte[] stopAndExtract() {
    NamedScreenRecorder recorder = TL_RECORDER.get();
    if (recorder == null) {
      return null;
    }
    try {
      recorder.stop();
      List<File> files = recorder.getCreatedMovieFiles();
      if (files.isEmpty()) {
        return null;
      }
      File video = files.get(files.size() - 1);
      byte[] bytes = Files.readAllBytes(video.toPath());
      Files.deleteIfExists(video.toPath());
      return bytes;
    } catch (IOException e) {
      log.error("Failed to stop / extract screen recording: {}", e.getMessage(), e);
      return null;
    } finally {
      TL_RECORDER.remove();
    }
  }

  /** Stops the active recording and deletes the file without reading it. */
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

  /** Subclass override forces deterministic filenames instead of Monte's timestamp default. */
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

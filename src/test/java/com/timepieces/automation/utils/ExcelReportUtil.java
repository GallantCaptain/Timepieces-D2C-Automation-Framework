package com.timepieces.automation.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import io.qameta.allure.Allure;
import lombok.extern.log4j.Log4j2;

/**
 * Writes an {@code .xlsx} workbook from the search-test result rows and attaches
 * it to the Allure report, so an Excel report downloads alongside the existing
 * markdown one. Used by {@code SearchApiTest} and {@code SearchApiExploratoryTest}.
 *
 * <p>
 * The workbook has two sheets: <b>Summary</b> (headline metrics + a per-category
 * breakdown) and <b>Results</b> (every unique keyword and the exact API response).
 * Result rows are read from the {@code Map} keys the tests populate:
 * {@code group} (category), {@code keyword}, {@code returned}, {@code error}.
 * There is deliberately no pass/fail column — search relevance is subjective and
 * tuned by the developers.
 *
 * @version 1.0
 * @author Avinash Rath
 * @since 2026-07-13
 */
@Log4j2
public final class ExcelReportUtil {

    private static final String XLSX_MIME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    /** Excel's hard per-cell limit is 32,767 chars; stay safely under it. */
    private static final int MAX_CELL_CHARS = 32000;

    private ExcelReportUtil() {
    }

    /**
     * Builds the workbook, writes it to {@code filePath}, and attaches it to Allure.
     * Never throws — failures are logged so report generation cannot break a run.
     *
     * @param filePath        where to write the .xlsx (e.g. {@code target/x.xlsx})
     * @param attachmentName  the Allure attachment label
     * @param groupLabel      header for the grouping column ("Group" or "Category")
     * @param summary         ordered headline metrics (metric → value)
     * @param breakdownHeader column headers for the per-group breakdown, or null
     * @param breakdownRows   per-group breakdown rows, or null
     * @param results         the executed result rows (shared Map shape)
     */
    public static void writeAndAttach(String filePath, String attachmentName, String groupLabel,
            LinkedHashMap<String, String> summary, String[] breakdownHeader, List<Object[]> breakdownRows,
            List<Map<String, Object>> results) {
        try (Workbook workbook = new XSSFWorkbook()) {
            CellStyle header = headerStyle(workbook);
            buildSummarySheet(workbook, header, summary, breakdownHeader, breakdownRows);
            buildResultsSheet(workbook, "Results", header, groupLabel, results);

            File out = new File(filePath);
            if (out.getParentFile() != null) {
                out.getParentFile().mkdirs();
            }
            try (FileOutputStream fos = new FileOutputStream(out)) {
                workbook.write(fos);
            }
            log.info("Excel report written to: {}", out.getAbsolutePath());

            try (InputStream in = new FileInputStream(out)) {
                Allure.addAttachment(attachmentName, XLSX_MIME, in, ".xlsx");
                log.info("Attached Excel report '{}' to Allure.", attachmentName);
            }
        } catch (Exception e) {
            log.error("Failed to generate/attach Excel report to {}: {}", filePath, e.getMessage(), e);
        }
    }

    private static CellStyle headerStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private static void buildSummarySheet(Workbook workbook, CellStyle header,
            LinkedHashMap<String, String> summary, String[] breakdownHeader, List<Object[]> breakdownRows) {
        Sheet sheet = workbook.createSheet("Summary");
        int r = 0;

        Row titleRow = sheet.createRow(r++);
        cell(titleRow, 0, "Metric", header);
        cell(titleRow, 1, "Value", header);
        if (summary != null) {
            for (Map.Entry<String, String> e : summary.entrySet()) {
                Row row = sheet.createRow(r++);
                cell(row, 0, e.getKey(), null);
                cell(row, 1, e.getValue(), null);
            }
        }

        if (breakdownHeader != null && breakdownRows != null && !breakdownRows.isEmpty()) {
            r++; // blank spacer row
            Row bh = sheet.createRow(r++);
            for (int c = 0; c < breakdownHeader.length; c++) {
                cell(bh, c, breakdownHeader[c], header);
            }
            for (Object[] br : breakdownRows) {
                Row row = sheet.createRow(r++);
                for (int c = 0; c < br.length; c++) {
                    cell(row, c, br[c] == null ? "" : br[c].toString(), null);
                }
            }
        }

        sheet.setColumnWidth(0, 40 * 256);
        sheet.setColumnWidth(1, 22 * 256);
    }

    private static void buildResultsSheet(Workbook workbook, String sheetName, CellStyle header,
            String groupLabel, List<Map<String, Object>> results) {
        Sheet sheet = workbook.createSheet(sheetName);
        String[] cols = { groupLabel, "Keyword", "Actual Results Returned", "Error" };
        Row headerRow = sheet.createRow(0);
        for (int c = 0; c < cols.length; c++) {
            cell(headerRow, c, cols[c], header);
        }

        int r = 1;
        if (results != null) {
            for (Map<String, Object> res : results) {
                Row row = sheet.createRow(r++);
                cell(row, 0, str(res.get("group")), null);
                cell(row, 1, str(res.get("keyword")), null);
                cell(row, 2, str(res.get("returned")), null);
                cell(row, 3, str(res.get("error")), null);
            }
        }

        int[] widthChars = { 22, 30, 100, 40 };
        for (int c = 0; c < widthChars.length; c++) {
            sheet.setColumnWidth(c, widthChars[c] * 256);
        }
        sheet.createFreezePane(0, 1);
    }

    private static void cell(Row row, int col, String value, CellStyle style) {
        Cell c = row.createCell(col);
        if (value != null && value.length() > MAX_CELL_CHARS) {
            value = value.substring(0, MAX_CELL_CHARS) + "…(truncated)";
        }
        c.setCellValue(value == null ? "" : value);
        if (style != null) {
            c.setCellStyle(style);
        }
    }

    private static String str(Object value) {
        return value == null ? "" : value.toString();
    }
}

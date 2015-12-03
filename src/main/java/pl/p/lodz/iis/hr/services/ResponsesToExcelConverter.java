package pl.p.lodz.iis.hr.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.exceptions.ErrorPageException;
import pl.p.lodz.iis.hr.models.courses.Commission;
import pl.p.lodz.iis.hr.models.courses.Review;
import pl.p.lodz.iis.hr.models.forms.Input;
import pl.p.lodz.iis.hr.models.forms.InputScale;
import pl.p.lodz.iis.hr.models.forms.Question;
import pl.p.lodz.iis.hr.models.response.Answer;
import pl.p.lodz.iis.hr.models.response.Response;
import pl.p.lodz.iis.hr.utils.DTFormatter;
import pl.p.lodz.iis.hr.utils.ProxyUtils;
import pl.p.lodz.iis.hr.utils.SafeFilenameUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

@Service
public class ResponsesToExcelConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponsesToExcelConverter.class);

    private final LocaleService localeService;

    @Autowired
    public ResponsesToExcelConverter(LocaleService localeService) {
        this.localeService = localeService;
    }

    public byte[] convert(Review review) throws ErrorPageException {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFCreationHelper creationHelper = wb.getCreationHelper();

            Sheet sheet = wb.createSheet(
                    SafeFilenameUtils.toFilenameSafeString(
                            String.format("%s_%s",
                                    review.getName(),
                                    SafeFilenameUtils.getCurrentTimestamp()))
            );

            sheet.setDefaultColumnWidth(20);

            int rowCnt = 0;

            XSSFCellStyle headCellStyle = wb.createCellStyle();
            headCellStyle.setWrapText(true);
            headCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // --------------------------------------------------------

            appendInfoRow(sheet, rowCnt, "m.commission.response.xls.info.gen", DTFormatter.format(LocalDateTime.now()));
            rowCnt += 2;

            appendInfoRow(sheet, rowCnt, "m.commission.response.xls.info.name", review.getName());
            rowCnt++;

            appendInfoRow(sheet, rowCnt, "m.commission.response.xls.info.created", DTFormatter.format(review.getCreated()));
            rowCnt++;

            appendInfoRow(sheet, rowCnt, "m.commission.response.xls.info.closed", DTFormatter.format(review.getClosed()));
            rowCnt++;

            appendInfoRow(sheet, rowCnt, "m.commission.response.xls.info.course", review.getCourse().getName());
            rowCnt++;

            appendInfoRow(sheet, rowCnt, "m.commission.response.xls.info.form", review.getForm().getName());
            rowCnt++;

            appendInfoRow(sheet, rowCnt, "m.commission.response.xls.info.repo", review.getRepository());
            rowCnt++;

            appendInfoRow(sheet, rowCnt, "m.commission.response.xls.info.rsp", Long.toString(review.getCommPerPeer()));
            rowCnt += 2;


            // --------------------------------------------------------

            Row headRow = sheet.createRow(rowCnt);
            headRow.setHeightInPoints(60.0f);
            int headRowCellCnt = 0;

            appendHeadCell(headRow, headRowCellCnt, headCellStyle, "m.commission.response.xls.head.timestamp");
            headRowCellCnt++;

            appendHeadCell(headRow, headRowCellCnt, headCellStyle, "m.commission.response.xls.head.assessor");
            headRowCellCnt++;

            appendHeadCell(headRow, headRowCellCnt, headCellStyle, "m.commission.response.xls.head.assessed");
            headRowCellCnt++;

            appendHeadCell(headRow, headRowCellCnt, headCellStyle, "m.commission.response.xls.head.ass.gh.url");
            headRowCellCnt++;

            appendHeadCell(headRow, headRowCellCnt, headCellStyle, "m.commission.response.xls.head.status");
            headRowCellCnt++;

            // --------------------------------------------------------

            for (Question question : review.getForm().getQuestions()) {
                appendQuestionToHead(headRow, headRowCellCnt, headCellStyle, question);
                headRowCellCnt++;
            }
            rowCnt++;

            // --------------------------------------------------------

            for (Commission commission : review.getCommissions()) {
                appendCommissionRow(sheet, creationHelper, rowCnt, commission);
                rowCnt++;
            }


            sheet.setAutoFilter(new CellRangeAddress(10, rowCnt, 1, 4));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);

            return baos.toByteArray();

        } catch (Exception e) {
            LOGGER.warn("ResponsesToExcelConverter.convert exception on review {}", review, e);
            throw (ErrorPageException)
                    new ErrorPageException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).initCause(e);
        }
    }


    private void appendInfoRow(Sheet sheet, int rowCnt, String keyCode, String value) {
        int cellCnt = 0;
        Row infoRow = sheet.createRow(rowCnt);

        Cell infoCellGen = infoRow.createCell(cellCnt);
        infoCellGen.setCellValue(localeService.get(keyCode));
        cellCnt++;

        Cell infoCellGen2 = infoRow.createCell(cellCnt);
        infoCellGen2.setCellValue(value);
    }

    private void appendHeadCell(Row headRow, int cellCnt, CellStyle headCellStyle, String keyCode) {
        Cell headCellTimestamp = headRow.createCell(cellCnt);
        headCellTimestamp.setCellValue(localeService.get(keyCode));
        headCellTimestamp.setCellStyle(headCellStyle);
    }

    private void appendQuestionToHead(Row headRow, int cellCnt, CellStyle headCellStyle, Question question) {
        StringBuilder headQI = new StringBuilder(100);

        for (Input input : question.getInputs()) {
            headQI.setLength(0);
            headQI.append(String.format("%s %s", input.getQuestion().getQuestionText(), input.getLabel()));

            if (ProxyUtils.isInstanceOf(input, InputScale.class)) {
                InputScale input1 = (InputScale) input;
                headQI.append(String.format(" (%d - %d)", input1.getFromS(), input1.getToS()));
            }

            Cell cellQI = headRow.createCell(cellCnt);
            cellQI.setCellValue(headQI.toString());
            cellQI.setCellStyle(headCellStyle);
        }
    }

    private void appendCommissionRow(Sheet sheet, CreationHelper creationHelper, int rowCnt, Commission commission) {
        int cellCnt = 0;

        Response response = commission.getResponse();
        Row responseRow = sheet.createRow(rowCnt);

        if (response != null) {
            Cell createdCell = responseRow.createCell(cellCnt);
            createdCell.setCellValue(
                    DTFormatter.format(response.getCreated())
            );
        }
        cellCnt++;

        if (commission.getAssessor() != null) {
            Cell assessorCell = responseRow.createCell(cellCnt);
            assessorCell.setCellValue(commission.getAssessor().getName());
        }
        cellCnt++;

        Cell assessedCell = responseRow.createCell(cellCnt);
        assessedCell.setCellValue(commission.getAssessed().getName());
        cellCnt++;

        if (commission.getAssessedGhUrl() != null) {
            Hyperlink assGhUrl = creationHelper.createHyperlink(Hyperlink.LINK_URL);
            assGhUrl.setAddress(commission.getAssessedGhUrl());

            Cell assGhUrlCell = responseRow.createCell(cellCnt);
            assGhUrlCell.setCellValue(commission.getAssessedGhUrl());
            assGhUrlCell.setHyperlink(assGhUrl);
        }
        cellCnt++;

        Cell statusCell = responseRow.createCell(cellCnt);
        statusCell.setCellValue(
                localeService.get(commission.getStatus().getLocaleCode())
                        .toUpperCase(localeService.getLocale())
        );
        cellCnt++;

        if (response != null) {
            for (Answer answer : response.getAnswers()) {
                Cell answerCell = responseRow.createCell(cellCnt);

                if (ProxyUtils.isInstanceOf(answer.getInput(), InputScale.class)) {
                    answerCell.setCellValue((double) answer.getAnswerAsNumber());
                } else {
                    answerCell.setCellValue(answer.getAnswer());
                }

                cellCnt++;
            }
        }
    }
}

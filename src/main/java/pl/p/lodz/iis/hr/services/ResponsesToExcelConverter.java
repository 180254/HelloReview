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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Date;

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
            int cellCnt = 0;

            XSSFCellStyle headCellStyle = wb.createCellStyle();
            headCellStyle.setWrapText(true);
            headCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // --------------------------------------------------------

            Row infoRowGen = sheet.createRow(rowCnt);
            Cell infoCellGen = infoRowGen.createCell(cellCnt);
            infoCellGen.setCellValue(localeService.get("m.commission.response.xls.info.gen"));
            cellCnt++;
            Cell infoCellGen2 = infoRowGen.createCell(cellCnt);
            infoCellGen2.setCellValue(DTFormatter.format(LocalDateTime.now()));
            rowCnt += 2;

            cellCnt = 0;
            Row infoRowName = sheet.createRow(rowCnt);
            Cell infoCellName = infoRowName.createCell(cellCnt);
            infoCellName.setCellValue(localeService.get("m.commission.response.xls.info.name"));
            cellCnt++;
            Cell infoCellName2 = infoRowName.createCell(cellCnt);
            infoCellName2.setCellValue(review.getName());
            rowCnt++;

            cellCnt = 0;
            Row infoRowCrated = sheet.createRow(rowCnt);
            Cell infoCellCreated = infoRowCrated.createCell(cellCnt);
            infoCellCreated.setCellValue(localeService.get("m.commission.response.xls.info.created"));
            cellCnt++;
            Cell infoCellCreated2 = infoRowCrated.createCell(cellCnt);
            infoCellCreated2.setCellValue(DTFormatter.format(review.getCreated()));
            rowCnt++;

            cellCnt = 0;
            Row infoRowClosed = sheet.createRow(rowCnt);
            Cell infoCellClosed = infoRowClosed.createCell(cellCnt);
            infoCellClosed.setCellValue(localeService.get("m.commission.response.xls.info.closed"));
            cellCnt++;
            if (review.getClosed() != null) {
                Cell infoCellClosed2 = infoRowClosed.createCell(cellCnt);
                infoCellClosed2.setCellValue(DTFormatter.format(review.getClosed()));
            }
            rowCnt++;

            cellCnt = 0;
            Row infoRowCourse = sheet.createRow(rowCnt);
            Cell infoCellCourse = infoRowCourse.createCell(cellCnt);
            infoCellCourse.setCellValue(localeService.get("m.commission.response.xls.info.course"));
            cellCnt++;
            Cell infoCellCourse2 = infoRowCourse.createCell(cellCnt);
            infoCellCourse2.setCellValue(review.getCourse().getName());
            rowCnt++;

            cellCnt = 0;
            Row infoRowTeam = sheet.createRow(rowCnt);
            Cell infoCellTeam = infoRowTeam.createCell(cellCnt);
            infoCellTeam.setCellValue(localeService.get("m.commission.response.xls.info.form"));
            cellCnt++;
            Cell infoCellTeam2 = infoRowTeam.createCell(cellCnt);
            infoCellTeam2.setCellValue(review.getForm().getName());
            rowCnt++;

            cellCnt = 0;
            Row infoRowRepo = sheet.createRow(rowCnt);
            Cell infoCellRepo = infoRowRepo.createCell(cellCnt);
            infoCellRepo.setCellValue(localeService.get("m.commission.response.xls.info.repo"));
            cellCnt++;
            Cell infoCellRepo2 = infoRowRepo.createCell(cellCnt);
            infoCellRepo2.setCellValue(review.getRepository());
            rowCnt++;

            cellCnt = 0;
            Row infoRowCommPerPeer = sheet.createRow(rowCnt);
            Cell infoCellCommPerPeer = infoRowCommPerPeer.createCell(cellCnt);
            infoCellCommPerPeer.setCellValue(localeService.get("m.commission.response.xls.info.rsp"));
            cellCnt++;
            Cell infoCellCommPerPeer2 = infoRowCommPerPeer.createCell(cellCnt);
            infoCellCommPerPeer2.setCellValue(Long.toString(review.getCommPerPeer()));
            rowCnt += 2;

            // --------------------------------------------------------

            cellCnt = 0;
            Row headRow = sheet.createRow(rowCnt);
            headRow.setHeightInPoints(60.0f);

            Cell headCellTimestamp = headRow.createCell(cellCnt);
            headCellTimestamp.setCellValue(localeService.get("m.commission.response.xls.head.timestamp"));
            headCellTimestamp.setCellStyle(headCellStyle);
            cellCnt++;

            Cell headCellAssessor = headRow.createCell(cellCnt);
            headCellAssessor.setCellValue(localeService.get("m.commission.response.xls.head.assessor"));
            headCellAssessor.setCellStyle(headCellStyle);
            cellCnt++;

            Cell headCellAssessed = headRow.createCell(cellCnt);
            headCellAssessed.setCellValue(localeService.get("m.commission.response.xls.head.assessed"));
            headCellAssessed.setCellStyle(headCellStyle);
            cellCnt++;

            Cell headCellAssGhUrl = headRow.createCell(cellCnt);
            headCellAssGhUrl.setCellValue(localeService.get("m.commission.response.xls.head.ass.gh.url"));
            headCellAssGhUrl.setCellStyle(headCellStyle);
            cellCnt++;

            Cell headCellStatus = headRow.createCell(cellCnt);
            headCellStatus.setCellValue(localeService.get("m.commission.response.xls.head.status"));
            headCellStatus.setCellStyle(headCellStyle);
            cellCnt++;

            // --------------------------------------------------------

            for (Question question : review.getForm().getQuestions()) {
                for (Input input : question.getInputs()) {
                    String headQI = String.format("%s %s", input.getQuestion().getQuestionText(), input.getLabel());

                    if (ProxyUtils.isInstanceOf(input, InputScale.class)) {
                        InputScale input1 = (InputScale) input;
                        headQI += String.format(" (%d - %d)", input1.getFromS(), input1.getToS());
                    }

                    Cell cellQI = headRow.createCell(cellCnt);
                    cellQI.setCellValue(headQI);
                    cellQI.setCellStyle(headCellStyle);
                    cellCnt++;
                }
            }

            for (Commission commission : review.getCommissions()) {
                rowCnt++;
                cellCnt = 0;

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

    public Date toDate(ChronoLocalDateTime<LocalDate> localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


}

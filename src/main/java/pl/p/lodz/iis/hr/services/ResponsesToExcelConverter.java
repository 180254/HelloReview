package pl.p.lodz.iis.hr.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
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

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class ResponsesToExcelConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponsesToExcelConverter.class);

    private final ProxyService proxyService;
    private final LocaleService localeService;

    @Autowired
    public ResponsesToExcelConverter(ProxyService proxyService,
                                     LocaleService localeService) {
        this.proxyService = proxyService;
        this.localeService = localeService;
    }

    public byte[] convert(Review review) throws ErrorPageException {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            Sheet sheet = wb.createSheet(
                    String.format("%s_%s",
                            review.getName(),
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm")))
            );
            sheet.setDefaultColumnWidth(20);

            int rowCnt = 0;
            int cellCnt = 0;

            XSSFCellStyle headCellStyle = wb.createCellStyle();
            headCellStyle.setWrapText(true);
            headCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

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

            Cell headCellStatus = headRow.createCell(cellCnt);
            headCellStatus.setCellValue(localeService.get("m.commission.response.xls.head.status"));
            headCellStatus.setCellStyle(headCellStyle);
            cellCnt++;

            for (Question question : review.getForm().getQuestions()) {
                for (Input input : question.getInputs()) {
                    String headQI = String.format("%s %s", input.getQuestion().getQuestionText(), input.getLabel());

                    if (proxyService.isInstanceOf(input, InputScale.class)) {
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
                            response.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
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

                Cell statusCell = responseRow.createCell(cellCnt);
                statusCell.setCellValue(localeService.get(commission.getStatus().getLocaleCode()).toUpperCase());
                cellCnt++;

                if (response != null) {
                    for (Answer answer : response.getAnswers()) {
                        Cell answerCell = responseRow.createCell(cellCnt);

                        if (proxyService.isInstanceOf(answer.getInput(), InputScale.class)) {
                            answerCell.setCellValue((double) answer.getAnswerAsNumber());
                        } else {
                            answerCell.setCellValue(answer.getAnswer());
                        }

                        cellCnt++;
                    }
                }
            }


            sheet.setAutoFilter(new CellRangeAddress(0, rowCnt, 1, 3));

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

package com.leave.service;

import com.leave.entity.LeaveApplication;
import com.leave.repository.LeaveApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final LeaveApplicationRepository leaveRepository;

    public void exportLeaveRecords(HttpServletResponse response) throws IOException {
        List<LeaveApplication> applications = leaveRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("请假记录");

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        String[] headers = {"请假单号", "申请人", "学号/工号", "班级", "请假类型", "开始时间", "结束时间", "天数", "事由", "状态", "申请时间"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 5000);
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        int rowNum = 1;
        for (LeaveApplication app : applications) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(app.getLeaveNo());
            row.createCell(1).setCellValue(app.getApplicant().getRealName());
            row.createCell(2).setCellValue(app.getApplicant().getUsername());
            row.createCell(3).setCellValue(app.getApplicant().getClassInfo() != null ? app.getApplicant().getClassInfo().getClassName() : "");
            row.createCell(4).setCellValue(app.getLeaveType().getTypeName());
            row.createCell(5).setCellValue(app.getStartTime().format(fmt));
            row.createCell(6).setCellValue(app.getEndTime().format(fmt));
            row.createCell(7).setCellValue(app.getDuration().doubleValue());
            row.createCell(8).setCellValue(app.getReason());
            row.createCell(9).setCellValue(getStatusText(app.getStatus()));
            row.createCell(10).setCellValue(app.getCreatedAt().format(fmt));
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("请假记录.xlsx", StandardCharsets.UTF_8.name()));
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "待审批";
            case 1: return "审批中";
            case 2: return "已通过";
            case 3: return "已驳回";
            case 4: return "已撤销";
            case 5: return "已销假";
            default: return "未知";
        }
    }
}

package com.leave.controller;

import com.leave.common.Result;
import com.leave.dto.StatisticsDTO;
import com.leave.service.ExportService;
import com.leave.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final ExportService exportService;

    @GetMapping("/overview")
    @PreAuthorize("hasAnyRole('COUNSELOR', 'DEPT_ADMIN', 'ADMIN')")
    public Result<StatisticsDTO> getOverview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return Result.success(statisticsService.getOverviewStatistics(start, end));
    }

    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('COUNSELOR', 'DEPT_ADMIN', 'ADMIN')")
    public void exportRecords(HttpServletResponse response) throws IOException {
        exportService.exportLeaveRecords(response);
    }
}

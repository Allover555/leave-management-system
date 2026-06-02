package com.leave.service;

import com.leave.dto.StatisticsDTO;
import com.leave.repository.LeaveApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final LeaveApplicationRepository leaveRepository;

    public StatisticsDTO getOverviewStatistics(LocalDateTime start, LocalDateTime end) {
        if (start == null) start = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        if (end == null) end = LocalDateTime.now();

        long total = leaveRepository.count();
        List<Object[]> typeStats = leaveRepository.countByTypeAndDateRange(start, end);
        List<Object[]> classStats = leaveRepository.countByClassAndDateRange(start, end);

        List<Map<String, Object>> typeDistribution = typeStats.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", row[0]);
            map.put("value", row[1]);
            return map;
        }).collect(Collectors.toList());

        List<Map<String, Object>> classDistribution = classStats.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", row[0]);
            map.put("value", row[1]);
            return map;
        }).collect(Collectors.toList());

        List<Map<String, Object>> monthlyTrend = new ArrayList<>();
        YearMonth startMonth = YearMonth.from(start);
        YearMonth endMonth = YearMonth.from(end);
        YearMonth current = startMonth;
        while (!current.isAfter(endMonth)) {
            LocalDateTime monthStart = current.atDay(1).atStartOfDay();
            LocalDateTime monthEnd = current.atEndOfMonth().atTime(23, 59, 59);
            List<Object[]> monthTypeStats = leaveRepository.countByTypeAndDateRange(monthStart, monthEnd);
            long monthTotal = monthTypeStats.stream().mapToLong(r -> (Long) r[1]).sum();

            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", current.toString());
            monthData.put("count", monthTotal);
            monthlyTrend.add(monthData);
            current = current.plusMonths(1);
        }

        return StatisticsDTO.builder()
                .totalApplications(total)
                .typeDistribution(typeDistribution)
                .classDistribution(classDistribution)
                .monthlyTrend(monthlyTrend)
                .build();
    }
}

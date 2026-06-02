package com.leave.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO {

    private Long totalApplications;
    private Long pendingApplications;
    private Long approvedApplications;
    private Long rejectedApplications;
    private Double totalDuration;
    private List<Map<String, Object>> typeDistribution;
    private List<Map<String, Object>> classDistribution;
    private List<Map<String, Object>> monthlyTrend;
}

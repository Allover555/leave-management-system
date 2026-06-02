package com.leave.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class LeaveApplicationDTO {

    private Long id;
    private String leaveNo;
    private Long applicantId;
    private String applicantName;
    private String applicantUsername;
    private String className;
    private String departmentName;
    private Long leaveTypeId;
    private String leaveTypeName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal duration;
    private String reason;
    private Integer status;
    private String statusText;
    private String currentNodeName;
    private LocalDateTime cancelTime;
    private String cancelReason;
    private List<AttachmentDTO> attachments;
    private List<ApprovalRecordDTO> approvalRecords;
    private LocalDateTime createdAt;
}

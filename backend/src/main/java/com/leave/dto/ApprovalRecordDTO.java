package com.leave.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApprovalRecordDTO {

    private Long id;
    private String nodeName;
    private Long approverId;
    private String approverName;
    private Integer action;
    private String actionText;
    private String comment;
    private LocalDateTime approvedAt;
}

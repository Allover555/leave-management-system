package com.leave.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "leave_application")
public class LeaveApplication extends BaseEntity {

    @Column(name = "leave_no", nullable = false, unique = true)
    private String leaveNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private SysUser applicant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal duration;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Integer status = 0;

    @Column(name = "current_node_id")
    private Long currentNodeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flow_id")
    private ApprovalFlowConfig flow;

    @Column(name = "cancel_time")
    private LocalDateTime cancelTime;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @OneToMany(mappedBy = "leaveApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LeaveAttachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "leaveApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("approvedAt ASC")
    private List<ApprovalRecord> approvalRecords = new ArrayList<>();
}

package com.leave.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "approval_record")
public class ApprovalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_id", nullable = false)
    private LeaveApplication leaveApplication;

    @Column(name = "node_id")
    private Long nodeId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approver_id", nullable = false)
    private SysUser approver;

    @Column(nullable = false)
    private Integer action;

    private String comment;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @PrePersist
    protected void onCreate() {
        approvedAt = LocalDateTime.now();
    }
}

package com.leave.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "leave_cancellation")
public class LeaveCancellation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_id", nullable = false)
    private LeaveApplication leaveApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private SysUser applicant;

    @Column(name = "actual_return_time", nullable = false)
    private LocalDateTime actualReturnTime;

    @Column(name = "is_overdue", columnDefinition = "TINYINT DEFAULT 0")
    private Integer isOverdue = 0;

    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Integer status = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private SysUser reviewer;

    @Column(name = "review_comment")
    private String reviewComment;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

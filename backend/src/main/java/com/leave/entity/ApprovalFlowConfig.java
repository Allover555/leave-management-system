package com.leave.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "approval_flow_config")
public class ApprovalFlowConfig extends BaseEntity {

    @Column(name = "flow_name", nullable = false)
    private String flowName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id")
    private LeaveType leaveType;

    @Column(name = "min_days")
    private Integer minDays = 0;

    @Column(name = "max_days")
    private Integer maxDays = 9999;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;

    @OneToMany(mappedBy = "flow", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("nodeOrder ASC")
    private List<ApprovalFlowNode> nodes = new ArrayList<>();
}

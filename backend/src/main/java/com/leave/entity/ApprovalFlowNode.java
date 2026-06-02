package com.leave.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "approval_flow_node")
public class ApprovalFlowNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flow_id", nullable = false)
    private ApprovalFlowConfig flow;

    @Column(name = "node_order", nullable = false)
    private Integer nodeOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approver_role_id", nullable = false)
    private SysRole approverRole;

    @Column(name = "node_name", nullable = false)
    private String nodeName;
}

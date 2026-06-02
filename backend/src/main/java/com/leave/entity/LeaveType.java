package com.leave.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "leave_type")
public class LeaveType extends BaseEntity {

    @Column(name = "type_name", nullable = false)
    private String typeName;

    @Column(name = "type_code", nullable = false, unique = true)
    private String typeCode;

    @Column(name = "max_days")
    private Integer maxDays = 0;

    @Column(name = "need_proof", columnDefinition = "TINYINT DEFAULT 0")
    private Integer needProof = 0;

    private String description;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;
}

package com.leave.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "department")
public class Department extends BaseEntity {

    @Column(name = "dept_name", nullable = false)
    private String deptName;

    @Column(name = "dept_code", nullable = false, unique = true)
    private String deptCode;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;
}

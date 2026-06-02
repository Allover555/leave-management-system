package com.leave.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "class_info")
public class ClassInfo extends BaseEntity {

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(name = "class_code", nullable = false, unique = true)
    private String classCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    private String grade;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;
}

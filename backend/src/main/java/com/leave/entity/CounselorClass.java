package com.leave.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "counselor_class")
public class CounselorClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counselor_id", nullable = false)
    private SysUser counselor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassInfo classInfo;
}

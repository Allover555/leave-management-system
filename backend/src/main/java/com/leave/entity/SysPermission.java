package com.leave.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sys_permission")
public class SysPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "perm_name", nullable = false)
    private String permName;

    @Column(name = "perm_code", nullable = false, unique = true)
    private String permCode;

    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

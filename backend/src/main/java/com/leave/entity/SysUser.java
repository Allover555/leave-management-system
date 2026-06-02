package com.leave.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_user")
public class SysUser extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "real_name", nullable = false)
    private String realName;

    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Integer gender = 0;

    private String phone;

    private String email;

    private String avatar;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private SysRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private ClassInfo classInfo;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;
}

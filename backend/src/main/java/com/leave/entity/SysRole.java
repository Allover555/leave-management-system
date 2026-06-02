package com.leave.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_role")
public class SysRole extends BaseEntity {

    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;

    @Column(name = "role_code", nullable = false, unique = true)
    private String roleCode;

    private String description;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "sys_role_permission",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<SysPermission> permissions = new HashSet<>();
}

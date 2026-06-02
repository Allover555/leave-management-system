package com.leave.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {

    private Long id;
    private String username;
    private String realName;
    private Integer gender;
    private String phone;
    private String email;
    private String avatar;
    private String roleCode;
    private String roleName;
    private Long departmentId;
    private String departmentName;
    private Long classId;
    private String className;
    private Integer status;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
}

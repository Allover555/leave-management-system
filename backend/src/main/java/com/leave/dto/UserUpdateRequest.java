package com.leave.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {

    private String realName;
    private Integer gender;
    private String phone;
    private String email;
    private Long departmentId;
    private Long classId;
    private Integer status;
    private Long roleId;
}

package com.leave.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RegisterRequest {

    @NotBlank(message = "用户名(学号/工号)不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "姓名不能为空")
    private String realName;

    @NotNull(message = "角色不能为空")
    private Long roleId;

    private Integer gender;
    private String phone;
    private String email;
    private Long departmentId;
    private Long classId;
}

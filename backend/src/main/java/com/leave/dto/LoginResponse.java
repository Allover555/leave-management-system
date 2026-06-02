package com.leave.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String token;
    private Long userId;
    private String username;
    private String realName;
    private String avatar;
    private String roleCode;
    private String roleName;
}

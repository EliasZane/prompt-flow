package com.promptflow.user.model.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    private String phone;
    private String code;
    private String loginType; // PASSWORD, SMS
}

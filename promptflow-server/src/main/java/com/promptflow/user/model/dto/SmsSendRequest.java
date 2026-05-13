package com.promptflow.user.model.dto;

import lombok.Data;

@Data
public class SmsSendRequest {
    private String phone;
    private String scene; // REGISTER, LOGIN, RESET_PWD
    private String username; // 仅注册场景需要校验
    private String ip; // 请求来源IP
}

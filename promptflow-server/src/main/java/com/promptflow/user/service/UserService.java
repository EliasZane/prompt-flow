package com.promptflow.user.service;

import com.promptflow.user.model.dto.LoginRequest;
import com.promptflow.user.model.dto.RegisterRequest;
import com.promptflow.user.model.dto.SmsSendRequest;
import com.promptflow.user.model.entity.UserEntity;

public interface UserService {
    UserEntity register(RegisterRequest request);
    UserEntity login(LoginRequest request);
    void sendSmsCode(SmsSendRequest request);
    void resetPassword(String phone, String code, String newPassword);
    UserEntity findByUsername(String username);
    UserEntity findByPhone(String phone);
}

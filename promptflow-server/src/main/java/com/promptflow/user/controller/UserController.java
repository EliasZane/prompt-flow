package com.promptflow.user.controller;

import com.promptflow.common.result.Result;
import com.promptflow.common.utils.JwtUtils;
import com.promptflow.user.model.dto.LoginRequest;
import com.promptflow.user.model.dto.RegisterRequest;
import com.promptflow.user.model.dto.SmsSendRequest;
import com.promptflow.user.model.entity.UserEntity;
import com.promptflow.user.model.vo.UserResponse;
import com.promptflow.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/me")
    public Result<UserResponse> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByUsername(username);
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return Result.success(response);
    }

    @PostMapping("/register")
    public Result<UserResponse> register(@RequestBody RegisterRequest request) {
        UserEntity user = userService.register(request);
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        response.setToken(jwtUtils.generateToken(user.getUsername()));
        return Result.success(response);
    }

    @PostMapping("/login")
    public Result<UserResponse> login(@RequestBody LoginRequest request) {
        UserEntity user = userService.login(request);
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        response.setToken(jwtUtils.generateToken(user.getUsername()));
        return Result.success(response);
    }

    @PostMapping("/sms/send")
    public Result<Void> sendSms(@RequestBody SmsSendRequest request, HttpServletRequest servletRequest) {
        // 获取客户端IP
        String ip = servletRequest.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = servletRequest.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = servletRequest.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = servletRequest.getRemoteAddr();
        }
        request.setIp(ip);

        userService.sendSmsCode(request);
        return Result.success(null);
    }

    @PostMapping("/password/reset")
    public Result<Void> resetPassword(@RequestParam String phone, @RequestParam String code, @RequestParam String newPassword) {
        userService.resetPassword(phone, code, newPassword);
        return Result.success(null);
    }
}

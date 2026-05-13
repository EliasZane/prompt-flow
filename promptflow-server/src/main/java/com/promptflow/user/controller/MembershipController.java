package com.promptflow.user.controller;

import com.promptflow.common.result.Result;
import com.promptflow.user.model.entity.RechargePackageEntity;
import com.promptflow.user.model.entity.RechargeRequestEntity;
import com.promptflow.user.model.entity.UsageRecordEntity;
import com.promptflow.user.model.entity.UserEntity;
import com.promptflow.user.service.MembershipService;
import com.promptflow.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/membership")
public class MembershipController {

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private UserService userService;

    @GetMapping("/packages")
    public Result<List<RechargePackageEntity>> getPackages() {
        return Result.success(membershipService.getActivePackages());
    }

    @PostMapping("/recharge")
    public Result<String> createRecharge(@RequestBody Map<String, Object> params) {
        Long packageId = Long.valueOf(params.get("packageId").toString());
        String payChannel = params.get("payChannel").toString();
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByUsername(username);
        
        String payUrl = membershipService.createRechargeOrder(user.getId(), packageId, payChannel);
        return Result.success(payUrl);
    }

    @PostMapping("/pay/notify/alipay")
    public String alipayNotify(@RequestParam Map<String, String> params) {
        return membershipService.processPayNotify(params);
    }

    @GetMapping("/recharge/history")
    public Result<List<RechargeRequestEntity>> getRechargeHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByUsername(username);
        return Result.success(membershipService.getUserRechargeRequests(user.getId()));
    }

    @GetMapping("/usage/history")
    public Result<List<UsageRecordEntity>> getUsageHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByUsername(username);
        return Result.success(membershipService.getUserUsageRecords(user.getId()));
    }

    @GetMapping("/audit/list")
    public Result<List<RechargeRequestEntity>> getAuditList() {
        // 简单鉴权：仅 admin 用户可以访问
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!"admin".equals(username)) {
            throw new RuntimeException("权限不足");
        }
        return Result.success(membershipService.getAllRechargeRequests());
    }
}

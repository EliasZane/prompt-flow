package com.promptflow.user.service;

import com.promptflow.user.model.entity.RechargePackageEntity;
import com.promptflow.user.model.entity.RechargeRequestEntity;
import com.promptflow.user.model.entity.UsageRecordEntity;

import java.util.List;
import java.util.Map;

public interface MembershipService {
    /**
     * 检查并扣减生成次数
     */
    void deductQuota(Long userId, Long runId);

    /**
     * 创建充值订单并获取支付链接
     */
    String createRechargeOrder(Long userId, Long packageId, String payChannel);

    /**
     * 处理支付回调
     */
    String processPayNotify(Map<String, String> params);

    /**
     * 获取所有启用的套餐
     */
    List<RechargePackageEntity> getActivePackages();

    /**
     * 获取用户的充值记录
     */
    List<RechargeRequestEntity> getUserRechargeRequests(Long userId);

    /**
     * 获取用户的消耗记录
     */
    List<UsageRecordEntity> getUserUsageRecords(Long userId);

    // 管理员接口
    List<RechargeRequestEntity> getAllRechargeRequests();
}

package com.promptflow.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.promptflow.common.exception.BusinessException;
import com.promptflow.user.mapper.RechargePackageMapper;
import com.promptflow.user.mapper.RechargeRequestMapper;
import com.promptflow.user.mapper.UsageRecordMapper;
import com.promptflow.user.mapper.UserMapper;
import com.promptflow.user.model.entity.RechargePackageEntity;
import com.promptflow.user.model.entity.RechargeRequestEntity;
import com.promptflow.user.model.entity.UsageRecordEntity;
import com.promptflow.user.model.entity.UserEntity;
import com.promptflow.user.service.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.internal.util.AlipaySignature;
import com.promptflow.user.mapper.PaymentTransactionMapper;
import com.promptflow.user.model.entity.PaymentTransactionEntity;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class MembershipServiceImpl implements MembershipService {

    @Value("${pay.alipay.server-url}")
    private String alipayServerUrl;

    @Value("${pay.alipay.app-id}")
    private String alipayAppId;

    @Value("${pay.alipay.private-key}")
    private String alipayPrivateKey;

    @Value("${pay.alipay.alipay-public-key}")
    private String alipayPublicKey;

    @Value("${pay.alipay.notify-url}")
    private String alipayNotifyUrl;

    @Value("${pay.alipay.return-url}")
    private String alipayReturnUrl;

    @Value("${pay.alipay.sign-type}")
    private String alipaySignType;

    @Value("${pay.alipay.charset}")
    private String alipayCharset;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RechargePackageMapper packageMapper;

    @Autowired
    private RechargeRequestMapper rechargeRequestMapper;

    @Autowired
    private PaymentTransactionMapper paymentTransactionMapper;

    @Autowired
    private UsageRecordMapper usageRecordMapper;

    @Override
    @Transactional
    public void deductQuota(Long userId, Long runId) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getRemainingCount() <= 0) {
            throw new BusinessException("生成次数已用完，请充值");
        }

        // 扣减次数
        user.setRemainingCount(user.getRemainingCount() - 1);
        user.setTotalUsedCount(user.getTotalUsedCount() + 1);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        // 记录消耗
        UsageRecordEntity record = new UsageRecordEntity();
        record.setUserId(userId);
        record.setRunId(runId);
        record.setChangeCount(-1);
        record.setBalanceAfter(user.getRemainingCount());
        record.setChangeType("GENERATE");
        record.setRemark("AI生成消耗");
        record.setCreatedAt(LocalDateTime.now());
        usageRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public String createRechargeOrder(Long userId, Long packageId, String payChannel) {
        RechargePackageEntity pkg = packageMapper.selectById(packageId);
        if (pkg == null || pkg.getStatus() == 0) {
            throw new RuntimeException("充值套餐不存在或已禁用");
        }

        // 1. 生成业务订单号
        String orderNo = "PF" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) 
                + (100 + new Random().nextInt(900));

        // 2. 创建业务订单记录
        RechargeRequestEntity order = new RechargeRequestEntity();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setPackageId(packageId);
        order.setAmount(pkg.getPrice());
        order.setGenerateCount(pkg.getGenerateCount());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        rechargeRequestMapper.insert(order);

        // 3. 生成支付流水号 (out_trade_no)
        String outTradeNo = orderNo + "T" + (10 + new Random().nextInt(90));

        // 4. 创建支付交易记录
        PaymentTransactionEntity transaction = new PaymentTransactionEntity();
        transaction.setOrderNo(orderNo);
        transaction.setOutTradeNo(outTradeNo);
        transaction.setPayChannel(payChannel);
        transaction.setPayScene("PC_WEB");
        transaction.setPayAmount(pkg.getPrice());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        paymentTransactionMapper.insert(transaction);

        // 5. 调用支付宝电脑网站支付
        if ("ALIPAY".equals(payChannel)) {
            try {
                AlipayClient alipayClient = new DefaultAlipayClient(alipayServerUrl, alipayAppId, alipayPrivateKey, "json", alipayCharset, alipayPublicKey, alipaySignType);
                AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
                request.setNotifyUrl(alipayNotifyUrl);
                request.setReturnUrl(alipayReturnUrl);

                Map<String, Object> bizContent = new HashMap<>();
                bizContent.put("out_trade_no", outTradeNo);
                bizContent.put("total_amount", pkg.getPrice().toString());
                bizContent.put("subject", pkg.getPackageName());
                bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
                
                String jsonBizContent = com.promptflow.common.utils.JsonUtils.toJson(bizContent);
                log.info("Alipay request bizContent: {}", jsonBizContent);
                request.setBizContent(jsonBizContent);
                
                AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
                if (response.isSuccess()) {
                    String form = response.getBody();
                    log.debug("Alipay generated form: {}", form);
                    return form;
                } else {
                    log.error("Alipay order failed. Msg: {}, ErrorResponse: {}", response.getMsg(), response.getBody());
                    throw new RuntimeException("支付宝下单失败: " + response.getMsg());
                }
            } catch (Exception e) {
                throw new RuntimeException("调用支付宝异常", e);
            }
        }

        throw new RuntimeException("暂不支持的支付方式: " + payChannel);
    }

    @Override
    @Transactional
    public String processPayNotify(Map<String, String> params) {
        try {
            // 1. 验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayPublicKey, alipayCharset, alipaySignType);
            if (!signVerified) {
                return "fail";
            }

            // 2. 获取订单信息
            String outTradeNo = params.get("out_trade_no");
            String tradeNo = params.get("trade_no");
            String tradeStatus = params.get("trade_status");
            String totalAmount = params.get("total_amount");

            // 3. 查询支付交易记录
            PaymentTransactionEntity transaction = paymentTransactionMapper.selectOne(new LambdaQueryWrapper<PaymentTransactionEntity>()
                    .eq(PaymentTransactionEntity::getOutTradeNo, outTradeNo));
            if (transaction == null) {
                return "fail";
            }

            // 4. 幂等检查
            if (transaction.getPaidAt() != null) {
                return "success";
            }

            // 5. 验证金额
            if (new BigDecimal(totalAmount).compareTo(transaction.getPayAmount()) != 0) {
                return "fail";
            }

            // 6. 处理支付结果
            transaction.setNotifyData(params.toString());
            transaction.setNotifyTime(LocalDateTime.now());
            transaction.setNotifyCount(transaction.getNotifyCount() + 1);
            transaction.setThirdTradeNo(tradeNo);
            transaction.setTradeStatus(tradeStatus);

            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                transaction.setPaidAt(LocalDateTime.now());
                paymentTransactionMapper.updateById(transaction);

                // 7. 更新业务订单状态
                RechargeRequestEntity order = rechargeRequestMapper.selectOne(new LambdaQueryWrapper<RechargeRequestEntity>()
                        .eq(RechargeRequestEntity::getOrderNo, transaction.getOrderNo()));
                if (order != null && "PENDING".equals(order.getStatus())) {
                    order.setStatus("SUCCESS");
                    order.setPaidAt(LocalDateTime.now());
                    order.setUpdatedAt(LocalDateTime.now());
                    rechargeRequestMapper.updateById(order);

                    // 8. 发放次数
                    UserEntity user = userMapper.selectById(order.getUserId());
                    user.setRemainingCount(user.getRemainingCount() + order.getGenerateCount());
                    user.setUpdatedAt(LocalDateTime.now());
                    userMapper.updateById(user);

                    // 9. 记录流水
                    UsageRecordEntity record = new UsageRecordEntity();
                    record.setOrderNo(order.getOrderNo());
                    record.setUserId(user.getId());
                    record.setChangeCount(order.getGenerateCount());
                    record.setBalanceAfter(user.getRemainingCount());
                    record.setChangeType("RECHARGE");
                    record.setRemark("支付宝充值: " + order.getGenerateCount() + "次");
                    record.setCreatedAt(LocalDateTime.now());
                    usageRecordMapper.insert(record);
                }
            } else {
                paymentTransactionMapper.updateById(transaction);
            }

            return "success";
        } catch (Exception e) {
            return "fail";
        }
    }

    @Override
    public List<RechargePackageEntity> getActivePackages() {
        return packageMapper.selectList(new LambdaQueryWrapper<RechargePackageEntity>()
                .eq(RechargePackageEntity::getStatus, 1)
                .orderByAsc(RechargePackageEntity::getPrice));
    }

    @Override
    public List<RechargeRequestEntity> getUserRechargeRequests(Long userId) {
        return rechargeRequestMapper.selectList(new LambdaQueryWrapper<RechargeRequestEntity>()
                .eq(RechargeRequestEntity::getUserId, userId)
                .orderByDesc(RechargeRequestEntity::getCreatedAt));
    }

    @Override
    public List<UsageRecordEntity> getUserUsageRecords(Long userId) {
        return usageRecordMapper.selectList(new LambdaQueryWrapper<UsageRecordEntity>()
                .eq(UsageRecordEntity::getUserId, userId)
                .orderByDesc(UsageRecordEntity::getCreatedAt));
    }

    @Override
    public List<RechargeRequestEntity> getAllRechargeRequests() {
        return rechargeRequestMapper.selectList(new LambdaQueryWrapper<RechargeRequestEntity>()
                .orderByDesc(RechargeRequestEntity::getCreatedAt));
    }
}

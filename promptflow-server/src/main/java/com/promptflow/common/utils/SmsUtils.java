package com.promptflow.common.utils;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dypnsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.google.gson.Gson;
import com.promptflow.common.exception.BusinessException;
import darabonba.core.client.ClientOverrideConfiguration;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class SmsUtils {

    @Value("${aliyun.sms.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.sms.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.sms.sign-name}")
    private String signName;

    @Value("${aliyun.sms.template-code}")
    private String templateCode;

    private AsyncClient client;

    @PostConstruct
    public void init() {
        if (accessKeyId == null || accessKeyId.isEmpty()) {
            log.warn("Aliyun SMS access-key-id is not configured, SMS feature will not work.");
            return;
        }

        // Configure Credentials
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(accessKeyId)
                .accessKeySecret(accessKeySecret)
                .build());

        // Configure the Client
        this.client = AsyncClient.builder()
                .region("ap-southeast-1") // 尝试使用官方示例中的 region
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dypnsapi.aliyuncs.com") // 强制指向国内端点，如果 region 是国际但端点是国内
                )
                .build();
    }

    @PreDestroy
    public void close() {
        if (client != null) {
            client.close();
        }
    }

    public void sendSms(String phone, String code) {
        if (client == null) {
            log.error("Aliyun SMS client is not initialized.");
            throw new BusinessException("短信服务未配置");
        }

        // 参数设置
        SendSmsVerifyCodeRequest request = SendSmsVerifyCodeRequest.builder()
                .signName(signName)
                .templateCode(templateCode)
                .phoneNumber(phone)
                .templateParam("{\"code\":\"" + code + "\",\"min\":\"5\"}")
                .build();

        try {
            // 异步调用
            CompletableFuture<SendSmsVerifyCodeResponse> responseFuture = client.sendSmsVerifyCode(request);
            SendSmsVerifyCodeResponse response = responseFuture.get();
            
            if (response.getBody() == null || !"OK".equals(response.getBody().getCode())) {
                String codeStr = response.getBody() != null ? response.getBody().getCode() : "Unknown";
                String errorMsg = response.getBody() != null ? response.getBody().getMessage() : "Unknown Error";
                
                log.error("Failed to send SMS Verify Code: {}, code: {}", errorMsg, codeStr);
                
                // 针对阿里云常见的业务错误进行转换，提供更友好的提示
                if ("isv.BUSINESS_LIMIT_CONTROL".equals(codeStr)) {
                    throw new BusinessException("验证码发送过于频繁，请稍后再试");
                } else if ("isv.MOBILE_NUMBER_ILLEGAL".equals(codeStr)) {
                    throw new BusinessException("手机号码格式错误");
                }
                
                throw new BusinessException("发送验证码失败: " + errorMsg);
            }
            
            log.info("SMS Send Success for phone: {}", phone);
        } catch (Exception e) {
            log.error("Error sending SMS Verify Code", e);
            // 如果捕获到 404，打印更多细节
            throw new BusinessException("短信服务异常，请检查配置或联系管理员");
        }
    }
}

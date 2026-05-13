package com.promptflow.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

public class PayUtils {

    /**
     * 生成易支付签名
     */
    public static String generateEpaySign(Map<String, String> params, String key) {
        // 1. 剔除 sign 和 sign_type 字段，并按参数名升序排序
        Map<String, String> sortedParams = new TreeMap<>(params);
        sortedParams.remove("sign");
        sortedParams.remove("sign_type");

        // 2. 拼接待签名字符串
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        // 去掉最后一个 &
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        // 3. 拼接商户密钥并进行 MD5 加密
        return md5(sb.toString() + key);
    }

    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 encryption failed", e);
        }
    }
}

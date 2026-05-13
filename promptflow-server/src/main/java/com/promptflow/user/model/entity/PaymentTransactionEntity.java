package com.promptflow.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment_transaction")
public class PaymentTransactionEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private String payChannel; // ALIPAY, WECHAT, EPAY
    private String payScene; // PC_WEB, FACE_TO_FACE, H5, JSAPI
    private String outTradeNo;
    private String thirdTradeNo;
    private String tradeStatus;
    private BigDecimal payAmount;
    private String notifyData;
    private Integer notifyCount;
    private LocalDateTime notifyTime;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

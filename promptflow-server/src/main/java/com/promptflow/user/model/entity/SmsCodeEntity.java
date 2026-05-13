package com.promptflow.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sms_code")
public class SmsCodeEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String phone;
    private String code;
    private String scene;
    private Integer used;
    private LocalDateTime expireTime;
    private LocalDateTime createdAt;
    private String ip;
}

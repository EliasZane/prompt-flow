package com.promptflow.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("usage_record")
public class UsageRecordEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private Long runId;
    private Integer changeCount;
    private Integer balanceAfter;
    private String changeType; // GENERATE, RECHARGE, ADMIN
    private String remark;
    private LocalDateTime createdAt;
}

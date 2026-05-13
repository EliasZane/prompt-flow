package com.promptflow.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("recharge_package")
public class RechargePackageEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String packageName;
    private BigDecimal price;
    private Integer generateCount;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

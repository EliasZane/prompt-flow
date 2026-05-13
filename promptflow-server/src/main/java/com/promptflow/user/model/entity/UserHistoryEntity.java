package com.promptflow.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_history")
public class UserHistoryEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long templateId;
    private String templateCode;
    private String tag;
    private String inputData; // JSON String
    private String outputData; // Generated content
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

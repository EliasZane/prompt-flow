package com.promptflow.workflow.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@TableName(value = "workflow_run", autoResultMap = true)
public class WorkflowRunEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long templateId;

    private String templateCode;

    private String runStatus; // PENDING/RUNNING/SUCCESS/FAILED

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> inputData; // JSON Object

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object resultData; // JSON String or Object

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> extraData; // JSON Object

    private String errorMessage;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

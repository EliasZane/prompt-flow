package com.promptflow.template.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.promptflow.template.model.config.FormFieldConfig;
import com.promptflow.template.model.config.PromptConfig;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "template", autoResultMap = true)
public class TemplateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String templateCode;

    private String templateName;

    private String templateType;

    private String description;

    private String sceneDescription;

    private String outputDescription;

    private String icon;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<FormFieldConfig> formSchema;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private PromptConfig promptConfig;

    private Integer sortNum;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

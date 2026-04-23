package com.promptflow.template.model.vo;

import com.promptflow.template.model.config.FormFieldConfig;
import com.promptflow.template.model.config.PromptConfig;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 模板详情VO
 */
@Data
public class TemplateDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String templateCode;
    private String templateName;
    private String templateType;
    private String description;
    private String sceneDescription;
    private String outputDescription;
    private String icon;
    private List<FormFieldConfig> formSchema;
    private PromptConfig promptConfig;
    private Integer sortNum;
}

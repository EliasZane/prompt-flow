package com.promptflow.template.model.config;

import lombok.Data;
import java.util.List;

/**
 * 表单字段配置项
 */
@Data
public class FormFieldConfig {
    private String fieldKey;
    private String label;
    private String componentType;
    private Boolean required;
    private String placeholder;
    private String defaultValue;
    private List<OptionItem> options;
}

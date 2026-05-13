package com.promptflow.template.model.config;

import lombok.Data;
import java.util.List;
import java.util.Map;

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
    private Object defaultValue;
    private List<OptionItem> options;
    private Boolean multiple;
    private Integer maxSelect;
    private Integer min;
    private Integer max;
    private Integer step;
    private String unit;
    private List<Map<String, Object>> marks;
}

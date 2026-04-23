package com.promptflow.template.model.config;

import lombok.Data;

/**
 * Prompt 配置项
 */
@Data
public class PromptConfig {
    private String promptFile;
    private Integer maxLength;
    private Boolean enableStyleAnalysis;
}

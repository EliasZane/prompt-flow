package com.promptflow.workflow.utils;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PromptBuilder {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)\\}");

    /**
     * 将输入数据中的字段替换到Prompt模板中
     * @param template Prompt模板，例如 "Hello, {name}!"
     * @param inputData 输入数据，例如 {"name": "World"}
     * @return 替换后的Prompt
     */
    public String buildPrompt(String template, Map<String, Object> inputData) {
        if (template == null || inputData == null || inputData.isEmpty()) {
            return template;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String fieldKey = matcher.group(1);
            Object value = inputData.get(fieldKey);
            if (value != null) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(value.toString()));
            } else {
                // 如果输入数据中没有对应的字段，则保留原样
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}

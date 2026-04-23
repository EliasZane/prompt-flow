package com.promptflow.workflow.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PromptTemplateLoader {

    private final Map<String, String> templateCache = new ConcurrentHashMap<>();

    /**
     * 从resources/prompts目录下加载Prompt模板文件
     * @param promptFile 模板文件名，例如 "ai_cover_prompt.txt"
     * @return 模板内容
     * @throws IOException 如果文件不存在或读取失败
     */
    public String loadPromptTemplate(String promptFile) throws IOException {
        return templateCache.computeIfAbsent(promptFile, file -> {
            try {
                // 如果传入的路径已经包含了 prompts/ 前缀，则不再重复拼接
                String path = file.startsWith("prompts/") ? file : "prompts/" + file;
                ClassPathResource resource = new ClassPathResource(path);
                return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load prompt template: " + file, e);
            }
        });
    }
}

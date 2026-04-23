package com.promptflow.workflow.ai;

import com.promptflow.template.model.config.PromptConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiClientService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ai.deepseek.api-key}")
    private String apiKey;

    @Value("${ai.deepseek.base-url}")
    private String baseUrl;

    @Value("${ai.deepseek.model}")
    private String model;

    /**
     * 调用 DeepSeek API 生成内容
     * @param prompt 组装好的Prompt
     * @param promptConfig 模板配置
     * @return AI生成结果
     */
    public String generateContent(String prompt, PromptConfig promptConfig) {
        String url = baseUrl + "/chat/completions";

        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 构建消息体
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant.");
        messages.add(systemMessage);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        // 构建请求参数
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", messages);
        requestBody.put("stream", false);

        // 如果 promptConfig 中有相关参数也可以在此注入，例如 maxLength
        // if (promptConfig.getMaxLength() != null) {
        //     requestBody.put("max_tokens", promptConfig.getMaxLength());
        // }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map> choices = (List<Map>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map message = (Map) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
            throw new RuntimeException("DeepSeek API call failed with status: " + response.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException("Error calling DeepSeek API: " + e.getMessage(), e);
        }
    }

    /**
     * 解析 AI 返回的额外数据
     * @param aiResult AI 原始返回结果（此处已简化为内容字符串，若需更详细数据需调整 generateContent 返回值）
     * @return 额外数据
     */
    public Map<String, Object> parseExtraData(String aiResult) {
        Map<String, Object> extra = new HashMap<>();
        // 此处简单记录模型信息，真实场景中建议在 generateContent 中返回完整 response 对象以获取 usage 信息
        extra.put("model", model);
        extra.put("source", "DeepSeek-Chat");
        return extra;
    }
}

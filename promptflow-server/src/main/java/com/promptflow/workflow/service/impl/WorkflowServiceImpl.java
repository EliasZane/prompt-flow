package com.promptflow.workflow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.promptflow.common.result.Result;
import com.promptflow.template.model.config.PromptConfig;
import com.promptflow.template.model.entity.TemplateEntity;
import com.promptflow.template.service.TemplateService;
import com.promptflow.workflow.ai.AiClientService;
import com.promptflow.workflow.mapper.WorkflowRunMapper;
import com.promptflow.workflow.model.dto.WorkflowRunDTO;
import com.promptflow.workflow.model.entity.WorkflowRunEntity;
import com.promptflow.workflow.model.vo.WorkflowResultVO;
import com.promptflow.workflow.model.vo.WorkflowRunVO;
import com.promptflow.workflow.service.WorkflowService;
import com.promptflow.workflow.utils.PromptBuilder;
import com.promptflow.workflow.utils.PromptTemplateLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class WorkflowServiceImpl extends ServiceImpl<WorkflowRunMapper, WorkflowRunEntity> implements WorkflowService {

    private static final Random RANDOM = new Random();

    private static final Map<String, String> STYLE_TAG_MAP = Map.of(
            "R&B", "#R&B翻唱",
            "Pop", "#流行翻唱",
            "Soul", "#Soul音乐",
            "Acoustic", "#原声翻唱",
            "Rock", "#摇滚翻唱"
    );

    private static final Map<String, String> MOOD_TAG_MAP = Map.of(
            "sad", "#伤感音乐",
            "melancholic", "#伤感音乐",
            "introspective", "#深夜听歌",
            "romantic", "#情歌",
            "happy", "#轻松音乐"
    );

    private static final Map<String, String> MOOD_WORD_MAP = Map.of(
            "sad", "扎心",
            "melancholic", "伤感",
            "introspective", "安静",
            "romantic", "温柔"
    );

    private static final List<String> COVER_TEMPLATES = Arrays.asList(
            "{songName}｜{styleTag}",
            "{songName}｜{moodTag}",
            "如果《{songName}》变成{moodTag}",
            "{songName}这样唱，太{moodWord}了",
            "一首《{songName}》，唱出{moodWord}"
    );

    @Autowired
    private TemplateService templateService;
    @Autowired
    private PromptTemplateLoader promptTemplateLoader;
    @Autowired
    private PromptBuilder promptBuilder;
    @Autowired
    private AiClientService aiClientService;

    @Override
    @Transactional
    public WorkflowRunVO runWorkflow(WorkflowRunDTO workflowRunDTO) {
        WorkflowRunVO workflowRunVO = new WorkflowRunVO();
        WorkflowRunEntity workflowRunEntity = new WorkflowRunEntity();
        workflowRunEntity.setRunStatus("RUNNING");
        workflowRunEntity.setCreatedAt(LocalDateTime.now());
        workflowRunEntity.setUpdatedAt(LocalDateTime.now());

        try {
            // 1. 根据 templateCode 查询 template 表
            TemplateEntity template = templateService.getTemplateByCode(workflowRunDTO.getTemplateCode());
            if (template == null) {
                throw new RuntimeException("Template not found or disabled: " + workflowRunDTO.getTemplateCode());
            }
            workflowRunEntity.setTemplateId(template.getId());
            workflowRunEntity.setTemplateCode(template.getTemplateCode());
            workflowRunEntity.setInputData(workflowRunDTO.getInputData());
            save(workflowRunEntity);

            // 2. 获取 Prompt 配置
            PromptConfig promptConfig = template.getPromptConfig();
            if (promptConfig == null || promptConfig.getPromptFile() == null) {
                throw new RuntimeException("Prompt config or file not specified in template.");
            }
            String promptFile = promptConfig.getPromptFile();

            // 3. 加载对应 prompt txt 文件
            String promptTemplate = promptTemplateLoader.loadPromptTemplate(promptFile);

            // 4. 将 inputData 中的字段替换到 prompt 模板中
            String finalPrompt = promptBuilder.buildPrompt(promptTemplate, workflowRunDTO.getInputData());

            // 5. 调用 AiClientService 获取结果
            String aiResult = aiClientService.generateContent(finalPrompt, promptConfig);

            // 6. 解析AI返回的内容为 Key-Value 形式
            Map<String, String> parsedResultMap = parseAiResult(aiResult);

            // 7. 封装为结构化对象
            WorkflowResultVO structuredResult = wrapResult(parsedResultMap, workflowRunDTO.getInputData());

            // 8. 解析AI返回的额外数据
            Map<String, Object> extraData = aiClientService.parseExtraData(aiResult);

            // 9. 保存 workflow_run 记录
            workflowRunEntity.setRunStatus("SUCCESS");
            workflowRunEntity.setResultData(structuredResult);
            workflowRunEntity.setExtraData(extraData);
            workflowRunEntity.setUpdatedAt(LocalDateTime.now());
            updateById(workflowRunEntity);

            // 10. 返回 runId、runStatus、result、extra
            workflowRunVO.setRunId(workflowRunEntity.getId());
            workflowRunVO.setTemplateCode(workflowRunEntity.getTemplateCode());
            workflowRunVO.setRunStatus("SUCCESS");
            workflowRunVO.setResult(structuredResult);
            workflowRunVO.setExtra(extraData);
            workflowRunVO.setCreatedAt(workflowRunEntity.getCreatedAt());

        } catch (Exception e) {
            // 记录错误信息
            workflowRunEntity.setRunStatus("FAILED");
            workflowRunEntity.setErrorMessage(e.getMessage());
            workflowRunEntity.setUpdatedAt(LocalDateTime.now());
            updateById(workflowRunEntity);

            workflowRunVO.setRunId(workflowRunEntity.getId());
            workflowRunVO.setRunStatus("FAILED");
            workflowRunVO.setResult(null);
            workflowRunVO.setExtra(new HashMap<>());
            throw new RuntimeException("Workflow execution failed: " + e.getMessage(), e);
        }
        return workflowRunVO;
    }

    @Override
    public WorkflowRunVO getRunDetail(Long runId) {
        WorkflowRunEntity entity = getById(runId);
        if (entity == null) {
            return null;
        }
        
        WorkflowRunVO vo = new WorkflowRunVO();
        vo.setRunId(entity.getId());
        vo.setTemplateCode(entity.getTemplateCode());
        vo.setRunStatus(entity.getRunStatus());
        
        // 处理结果数据转换
        if (entity.getResultData() != null) {
            if (entity.getResultData() instanceof WorkflowResultVO) {
                vo.setResult((WorkflowResultVO) entity.getResultData());
            } else if (entity.getResultData() instanceof Map) {
                // 如果是从数据库读取的 Map，尝试转换为 VO
                vo.setResult(com.promptflow.common.utils.JsonUtils.convert(entity.getResultData(), WorkflowResultVO.class));
            }
        }
        
        vo.setExtra(entity.getExtraData());
        vo.setCreatedAt(entity.getCreatedAt());
        
        return vo;
    }

    /**
     * 将解析后的 Map 封装为结构化的 VO 对象
     */
    private WorkflowResultVO wrapResult(Map<String, String> parsedResult, Map<String, Object> inputData) {
        WorkflowResultVO vo = new WorkflowResultVO();

        // 1. 提取 AI 生成的字段 (作为参考或直接使用)
        String aiTitle = findAndRemove(parsedResult, "title", "标题", "推荐视频标题");
        String aiCoverText = findAndRemove(parsedResult, "coverText", "封面文案", "封面");
        String aiSuggestion = findAndRemove(parsedResult, "suggestion", "建议", "使用建议");
        String aiTagsStr = findAndRemove(parsedResult, "tags", "标签", "热门标签");

        // 2. 从 inputData 提取关键变量用于规则生成
        String songName = getInputValue(inputData, "songName", "song", "name", "歌曲名");
        String mood = getInputValue(inputData, "mood", "emotion", "情绪");
        String style = getInputValue(inputData, "style", "genre", "曲风");
        Integer tempo = getTempoValue(inputData);

        // 3. 应用标签 (tags) 生成规则
        vo.setTags(generateTags(aiTagsStr, songName, style, mood, tempo));

        // 4. 应用封面文案 (coverText) 规则
        vo.setCoverText(aiCoverText != null ? aiCoverText : generateCoverText(songName, style, mood));

        // 5. 应用使用建议 (suggestion) 规则
        vo.setSuggestion(aiSuggestion != null ? aiSuggestion : generateSuggestion(style, mood, tempo));

        // 6. 设置标题
        vo.setTitle(aiTitle != null ? aiTitle : (songName != null ? "把这首歌改成" + style + "翻唱，氛围感直接拉满" : "AI 音乐翻唱方案"));

        // 7. 剩余的所有字段放入 prompt 对象中
        vo.setPrompt(new HashMap<>(parsedResult));

        return vo;
    }

    private String getInputValue(Map<String, Object> inputData, String... keys) {
        for (String key : keys) {
            for (Map.Entry<String, Object> entry : inputData.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(key) && entry.getValue() != null) {
                    return String.valueOf(entry.getValue());
                }
            }
        }
        return null;
    }

    private Integer getTempoValue(Map<String, Object> inputData) {
        String val = getInputValue(inputData, "tempo", "bpm");
        if (val == null) return null;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private List<String> generateTags(String aiTagsStr, String songName, String style, String mood, Integer tempo) {
        Set<String> tagSet = new LinkedHashSet<>();
        // 固定标签
        tagSet.add("#AI翻唱");
        tagSet.add("#AI音乐");

        // 歌曲名标签
        if (songName != null && !songName.isEmpty()) {
            tagSet.add("#" + songName);
        }

        // 风格标签映射
        if (style != null) {
            for (Map.Entry<String, String> entry : STYLE_TAG_MAP.entrySet()) {
                if (style.toLowerCase().contains(entry.getKey().toLowerCase())) {
                    tagSet.add(entry.getValue());
                    break;
                }
            }
        }

        // 情绪标签映射
        if (mood != null) {
            for (Map.Entry<String, String> entry : MOOD_TAG_MAP.entrySet()) {
                if (mood.toLowerCase().contains(entry.getKey().toLowerCase())) {
                    tagSet.add(entry.getValue());
                    break;
                }
            }
        }

        // 节奏标签
        if (tempo != null) {
            if (tempo <= 80) tagSet.add("#慢节奏");
            else if (tempo >= 100) tagSet.add("#节奏感强");
        }

        // 如果 AI 返回了标签，也加进去
        if (aiTagsStr != null) {
            String[] aiTags = aiTagsStr.split("[,，\\s#]+");
            for (String tag : aiTags) {
                if (!tag.trim().isEmpty()) {
                    tagSet.add("#" + tag.trim());
                }
            }
        }

        List<String> result = new ArrayList<>(tagSet);
        // 最多保留 6 个
        return result.size() > 6 ? result.subList(0, 6) : result;
    }

    private String generateCoverText(String songName, String style, String mood) {
        if (songName == null) return "AI 翻唱｜氛围感直接拉满";

        String template = COVER_TEMPLATES.get(RANDOM.nextInt(COVER_TEMPLATES.size()));

        String styleTag = "翻唱";
        if (style != null) {
            for (Map.Entry<String, String> entry : STYLE_TAG_MAP.entrySet()) {
                if (style.toLowerCase().contains(entry.getKey().toLowerCase())) {
                    styleTag = entry.getValue().replace("#", "");
                    break;
                }
            }
        }

        String moodTag = "翻唱版";
        String moodWord = "动听";
        if (mood != null) {
            for (Map.Entry<String, String> entry : MOOD_TAG_MAP.entrySet()) {
                if (mood.toLowerCase().contains(entry.getKey().toLowerCase())) {
                    moodTag = entry.getValue().replace("#", "");
                    break;
                }
            }
            for (Map.Entry<String, String> entry : MOOD_WORD_MAP.entrySet()) {
                if (mood.toLowerCase().contains(entry.getKey().toLowerCase())) {
                    moodWord = entry.getValue();
                    break;
                }
            }
        }

        return template.replace("{songName}", songName)
                .replace("{styleTag}", styleTag)
                .replace("{moodTag}", moodTag)
                .replace("{moodWord}", moodWord);
    }

    private String generateSuggestion(String style, String mood, Integer tempo) {
        StringBuilder suggestion = new StringBuilder();

        // 情绪 -> 画面
        if (mood != null) {
            String lowerMood = mood.toLowerCase();
            if (lowerMood.contains("sad") || lowerMood.contains("introspective") || lowerMood.contains("melancholic")) {
                suggestion.append("建议搭配夜景、街景、路灯画面");
            } else if (lowerMood.contains("romantic")) {
                suggestion.append("建议使用情侣、回忆类画面");
            } else {
                suggestion.append("建议搭配氛围感十足的视频画面");
            }
        } else {
            suggestion.append("建议搭配氛围感十足的视频画面");
        }

        // 节奏 -> 剪辑
        if (tempo != null) {
            suggestion.append("，");
            if (tempo <= 80) {
                suggestion.append("剪辑节奏放慢，突出情绪");
            } else if (tempo >= 100) {
                suggestion.append("可增加节奏卡点剪辑");
            } else {
                suggestion.append("剪辑节奏适中即可");
            }
        }

        // 风格 -> 场景
        if (style != null) {
            suggestion.append("，");
            String lowerStyle = style.toLowerCase();
            if (lowerStyle.contains("r&b")) {
                suggestion.append("适合都市夜景、车窗、街头氛围");
            } else if (lowerStyle.contains("acoustic")) {
                suggestion.append("适合室内、安静环境、纯色背景");
            } else {
                suggestion.append("适合多种生活化场景");
            }
        }

        suggestion.append("。");
        return suggestion.toString();
    }

    private String findAndRemove(Map<String, String> map, String... keys) {
        for (String key : keys) {
            for (Map.Entry<String, String> entry : new HashMap<>(map).entrySet()) {
                if (entry.getKey().equalsIgnoreCase(key)) {
                    map.remove(entry.getKey());
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 将 AI 生成的文本解析为 Key-Value 结构
     * 规则：以 "Header:" 开头的行作为 Key，其后的内容作为 Value
     */
    private Map<String, String> parseAiResult(String aiResult) {
        Map<String, String> result = new HashMap<>();
        if (aiResult == null || aiResult.isEmpty()) {
            return result;
        }

        // 使用正则表达式匹配 "Header:" 结构，支持多种换行符
        // 匹配逻辑：找到所有以 "文字:" 开头的片段
        String[] lines = aiResult.split("\n");
        String currentKey = null;
        StringBuilder currentContent = new StringBuilder();

        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) {
                if (currentContent.length() > 0) {
                    currentContent.append("\n");
                }
                continue;
            }

            // 判断是否是新的 Key (例如 "Style:" 或 "Original Style Analysis:")
            // 简单判断：包含冒号且冒号前字符不多（通常是标题）
            if (trimmedLine.contains(":") && trimmedLine.indexOf(":") < 40 && !trimmedLine.startsWith("http")) {
                // 如果之前有 Key，先保存
                if (currentKey != null) {
                    result.put(currentKey, currentContent.toString().trim());
                }
                
                int colonIndex = trimmedLine.indexOf(":");
                currentKey = trimmedLine.substring(0, colonIndex).trim();
                currentContent = new StringBuilder(trimmedLine.substring(colonIndex + 1).trim());
            } else {
                if (currentKey != null) {
                    if (currentContent.length() > 0) {
                        currentContent.append("\n");
                    }
                    currentContent.append(trimmedLine);
                }
            }
        }

        // 保存最后一个 Key
        if (currentKey != null) {
            result.put(currentKey, currentContent.toString().trim());
        }

        // 如果没有成功解析出任何 Key-Value，则返回原始文本
        if (result.isEmpty()) {
            result.put("content", aiResult);
        }

        return result;
    }
}

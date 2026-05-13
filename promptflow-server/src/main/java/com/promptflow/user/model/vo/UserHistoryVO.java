package com.promptflow.user.model.vo;

import com.promptflow.workflow.model.vo.WorkflowResultVO;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class UserHistoryVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long templateId;
    private String templateCode;
    private String tag;
    private Map<String, Object> inputData;
    private WorkflowResultVO outputData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

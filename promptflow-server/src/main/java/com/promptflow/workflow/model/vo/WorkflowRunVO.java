package com.promptflow.workflow.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class WorkflowRunVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long runId;
    private String templateCode;
    private String runStatus;
    private WorkflowResultVO result; // 最终结果
    private Map<String, Object> extra; // 额外信息
    private LocalDateTime createdAt;
}

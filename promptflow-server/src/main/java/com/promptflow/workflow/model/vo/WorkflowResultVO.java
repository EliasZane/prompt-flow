package com.promptflow.workflow.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class WorkflowResultVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String coverText;
    private List<String> tags;
    private String suggestion;
    private Map<String, String> prompt;
}

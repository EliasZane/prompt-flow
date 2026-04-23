package com.promptflow.template.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TemplateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String templateCode;
    private String templateName;
    private String templateType;
    private String description;
    private String sceneDescription;
    private String outputDescription;
    private String icon;
    private Integer status;
    private Integer sortNum;
}

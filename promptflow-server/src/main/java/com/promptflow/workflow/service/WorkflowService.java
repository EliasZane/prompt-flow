package com.promptflow.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.promptflow.workflow.model.dto.WorkflowRunDTO;
import com.promptflow.workflow.model.entity.WorkflowRunEntity;
import com.promptflow.workflow.model.vo.WorkflowRunVO;

public interface WorkflowService extends IService<WorkflowRunEntity> {

    /**
     * 执行工作流
     * @param workflowRunDTO 工作流运行请求DTO
     * @return 工作流运行结果VO
     */
    WorkflowRunVO runWorkflow(WorkflowRunDTO workflowRunDTO);

    /**
     * 根据运行ID获取运行记录详情
     * @param runId 运行ID
     * @return 运行记录VO
     */
    WorkflowRunVO getRunDetail(Long runId);
}

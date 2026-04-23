package com.promptflow.workflow.controller;

import com.promptflow.common.result.Result;
import com.promptflow.workflow.model.dto.WorkflowRunDTO;
import com.promptflow.workflow.model.vo.WorkflowRunVO;
import com.promptflow.workflow.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    /**
     * 执行工作流
     * POST /api/workflows/run
     * @param workflowRunDTO 工作流运行请求DTO
     * @return 工作流运行结果VO
     */
    @PostMapping("/run")
    public Result<WorkflowRunVO> runWorkflow(@RequestBody WorkflowRunDTO workflowRunDTO) {
        try {
            WorkflowRunVO result = workflowService.runWorkflow(workflowRunDTO);
            return Result.success(result);
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 获取运行记录详情
     * GET /api/workflows/run/{runId}
     * @param runId 运行ID
     * @return 运行记录VO
     */
    @GetMapping("/run/{runId}")
    public Result<WorkflowRunVO> getRunDetail(@PathVariable Long runId) {
        WorkflowRunVO result = workflowService.getRunDetail(runId);
        if (result == null) {
            return Result.failed("运行记录不存在");
        }
        return Result.success(result);
    }
}

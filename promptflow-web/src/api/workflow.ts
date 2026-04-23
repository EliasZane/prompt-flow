import request from '../utils/request'
import type { WorkflowResult } from '../types/workflow'

/**
 * 执行工作流
 * @param data { templateCode, inputData }
 */
export const runWorkflow = (data: { templateCode: string; inputData: Record<string, any> }): Promise<WorkflowResult> => {
  return request.post('/workflows/run', data)
}

/**
 * 获取工作流运行记录详情
 * @param runId 运行 ID
 */
export const getWorkflowRunDetail = (runId: string | number): Promise<WorkflowResult> => {
  return request.get(`/workflows/run/${runId}`)
}

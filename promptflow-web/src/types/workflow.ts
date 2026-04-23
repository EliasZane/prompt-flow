export type TaskStatus = 'PENDING' | 'RUNNING' | 'SUCCESS' | 'FAILED'

export interface WorkflowStep {
  id: string
  stepName: string
  status: TaskStatus
  summary?: string
  expandContent?: string
}

export interface WorkflowResult {
  runId: number
  templateId: number
  templateCode: string
  templateName?: string // 详情中可能需要额外关联查询
  runStatus: TaskStatus
  createdAt: string
  inputData: Record<string, any>
  result: Record<string, string> | string // 结构化的 KV 或原始文本
  extra: Record<string, any>
  steps?: WorkflowStep[] // V1 暂时由前端根据状态模拟或后端后续扩展
}
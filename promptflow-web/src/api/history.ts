import request from '../utils/request'
import type { WorkflowResult } from '../types/workflow'

export interface HistoryItem {
  id: number
  templateCode: string
  templateName: string
  inputData: string
  resultData: string
  status: number
  createdAt: string
}

export function getHistoryList() {
  return request<HistoryItem[]>({
    url: '/user/history',
    method: 'get'
  })
}

export function getHistoryDetail(runId: string | number) {
  return request<WorkflowResult>({
    url: `/workflows/run/${runId}`,
    method: 'get'
  })
}

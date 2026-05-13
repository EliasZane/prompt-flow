import request from '../utils/request'

export interface RechargePackage {
  id: number
  packageName: string
  price: number
  generateCount: number
  status: number
}

export interface RechargeRequest {
  id?: number
  orderNo?: string
  packageId: number
  payChannel: string
  payProvider?: string
  payUrl?: string
  payScreenshot?: string
  remark?: string
  status?: string
  amount?: number
  generateCount?: number
  createdAt?: string
}

export interface UsageRecord {
  id: number
  orderNo?: string
  changeCount: number
  balanceAfter: number
  changeType: string
  remark: string
  createdAt: string
}

export function getPackages() {
  return request<RechargePackage[]>({
    url: '/membership/packages',
    method: 'get'
  })
}

export function createRecharge(data: { packageId: number, payChannel: string }) {
  return request<string>({
    url: '/membership/recharge',
    method: 'post',
    data
  })
}

export function getRechargeHistory() {
  return request<RechargeRequest[]>({
    url: '/membership/recharge/history',
    method: 'get'
  })
}

export function getUsageHistory() {
  return request<UsageRecord[]>({
    url: '/membership/usage/history',
    method: 'get'
  })
}

export function getAllRechargeRequests() {
  return request<RechargeRequest[]>({
    url: '/membership/audit/list',
    method: 'get'
  })
}

export function auditRecharge(requestId: number, status: string, remark?: string) {
  return request<void>({
    url: '/membership/audit',
    method: 'post',
    data: { requestId, status, remark }
  })
}

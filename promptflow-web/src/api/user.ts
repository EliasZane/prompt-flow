import request from '../utils/request'

export interface UserInfo {
  id: number
  username: string
  email: string
  remainingCount: number
  totalUsedCount: number
  createdAt: string
  updatedAt: string
}

export interface LoginResponse {
  token: string
  username: string
  remainingCount: number
  totalUsedCount: number
}

export function login(data: any) {
  return request<LoginResponse>({
    url: '/user/login',
    method: 'post',
    data
  })
}

export function register(data: any) {
  return request<LoginResponse>({
    url: '/user/register',
    method: 'post',
    data
  })
}

export function getUserInfo() {
  return request<UserInfo>({
    url: '/user/me',
    method: 'get'
  })
}

export function sendSmsCode(data: { phone: string, scene: 'REGISTER' | 'LOGIN' | 'RESET_PWD', username?: string }) {
  return request<void>({
    url: '/user/sms/send',
    method: 'post',
    data
  })
}

export function resetPassword(data: any) {
  return request<void>({
    url: '/user/password/reset',
    method: 'post',
    params: data
  })
}

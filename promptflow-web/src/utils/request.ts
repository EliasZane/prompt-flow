import axios, { type AxiosRequestConfig } from 'axios'
import userStore from '../stores/userStore' // 引入 userStore

const instance = axios.create({
  baseURL: import.meta.env.VITE_BASE_URL,
  timeout: 60000
})

// 请求拦截器
instance.interceptors.request.use(
  config => {
    const token = userStore.state.token // 从 userStore 获取 token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('Request Error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
instance.interceptors.response.use(
  (response) => {
    const res = response.data
    // 统一处理返回结构 { code, message, data }
    if (res.code !== 200) {
      const msg = res.message || res.msg || 'Error'
      console.error(msg)
      return Promise.reject(new Error(msg))
    }
    return res.data
  },
  (error) => {
    console.error('API Error:', error)
    let message = error.message
    if (error.response && error.response.data) {
      // 优先使用后端返回的 message 或 msg
      message = error.response.data.message || error.response.data.msg || message
      
      const { status } = error.response
      if (status === 401 || status === 403) {
        userStore.logout() // 清除登录状态
        // 只有当不是登录请求本身失败时（比如Token过期），才跳转并提示
        if (!error.config?.url?.includes('/user/login')) {
          window.dispatchEvent(new CustomEvent('notification', { 
            detail: { message: message, type: 'error' } 
          }))
          window.dispatchEvent(new CustomEvent('navigate', { detail: { path: '/login' } }))
        }
      }
    }
    // 创建一个包含后端消息的新错误对象并返回
    const enhancedError = new Error(message)
    return Promise.reject(enhancedError)
  }
)

interface Request {
  <T = any>(config: AxiosRequestConfig): Promise<T>
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T>
  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T>
  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T>
  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T>
}

const request: Request = <T = any>(config: AxiosRequestConfig): Promise<T> => {
  return instance.request(config)
}

request.get = <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> => {
  return instance.get(url, config)
}

request.post = <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> => {
  return instance.post(url, data, config)
}

request.put = <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> => {
  return instance.put(url, data, config)
}

request.delete = <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> => {
  return instance.delete(url, config)
}

export default request

/**
 * HTTP 请求封装
 * 基于 axios 的统一请求处理
 */

import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

// 响应数据接口
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp?: number
  traceId?: string
}

// 分页数据接口
export interface PageData<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 分页参数
export interface PageParams {
  current?: number
  size?: number
  [key: string]: any
}

// 创建 axios 实例
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 添加 token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // 添加请求时间戳（防止缓存）
    if (config.method === 'get') {
      config.params = {
        ...config.params,
        _t: Date.now()
      }
    }

    // 添加 traceId（用于链路追踪）
    const traceId = `trace_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
    config.headers['X-Trace-Id'] = traceId

    return config
  },
  (error: AxiosError) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const res = response.data

    // 业务错误处理
    if (res.code !== 200 && res.code !== 0) {
      // 401: 未授权
      if (res.code === 401) {
        ElMessageBox.confirm('登录已过期，请重新登录', '提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          window.location.href = '/login'
        })
        return Promise.reject(new Error(res.message || '未授权'))
      }

      // 403: 无权限
      if (res.code === 403) {
        ElMessage.error('无权限访问')
        return Promise.reject(new Error(res.message || '无权限'))
      }

      // 404: 资源不存在
      if (res.code === 404) {
        ElMessage.error('资源不存在')
        return Promise.reject(new Error(res.message || '资源不存在'))
      }

      // 500: 服务器错误
      if (res.code >= 500) {
        ElMessage.error(res.message || '服务器错误')
        return Promise.reject(new Error(res.message || '服务器错误'))
      }

      // 其他业务错误
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }

    return res
  },
  (error: AxiosError) => {
    console.error('HTTP 错误:', error)

    // 网络错误
    if (error.message === 'Network Error') {
      ElMessage.error('网络连接失败，请检查网络')
      return Promise.reject(new Error('网络连接失败'))
    }

    // 超时错误
    if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请重试')
      return Promise.reject(new Error('请求超时'))
    }

    // 其他错误
    const message = error.response?.data || error.message || '请求失败'
    ElMessage.error(typeof message === 'string' ? message : '请求失败')
    return Promise.reject(error)
  }
)

/**
 * GET 请求
 */
export function get<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return service.get(url, { params, ...config })
}

/**
 * POST 请求
 */
export function post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return service.post(url, data, config)
}

/**
 * PUT 请求
 */
export function put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return service.put(url, data, config)
}

/**
 * DELETE 请求
 */
export function del<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return service.delete(url, { params, ...config })
}

/**
 * 下载文件
 */
export function download(url: string, params?: any, filename?: string): Promise<void> {
  return service.get(url, {
    params,
    responseType: 'blob'
  }).then((res) => {
    const blob = new Blob([res.data])
    const link = document.createElement('a')
    link.href = window.URL.createObjectURL(blob)
    link.download = filename || 'download'
    link.click()
    window.URL.revokeObjectURL(link.href)
  })
}

/**
 * 上传文件
 */
export function upload<T = any>(url: string, file: File, data?: any): Promise<ApiResponse<T>> {
  const formData = new FormData()
  formData.append('file', file)
  if (data) {
    Object.keys(data).forEach(key => {
      formData.append(key, data[key])
    })
  }

  return service.post(url, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export default service

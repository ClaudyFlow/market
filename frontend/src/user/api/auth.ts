import request from './request'
import type { UserInfo } from '../types'

interface RegisterData {
  name: string
  password: string
  email?: string
  confirmPassword?: string
}

interface LoginData {
  name: string
  password: string
}

interface ApiResponse<T> {
  data: T
  message?: string
  code?: number
}

// 用户注册
export function register(data: RegisterData) {
  return request<ApiResponse<{ token: string; user: UserInfo }>>({
    url: '/auth/register',
    method: 'post',
    data
  })
}

// 用户登录
export function login(data: LoginData) {
  return request<ApiResponse<{ token: string; user: UserInfo }>>({
    url: '/auth/login',
    method: 'post',
    data
  })
}

// 用户登出
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

// 获取当前用户信息
export function getCurrentUser() {
  return request<ApiResponse<UserInfo>>({
    url: '/auth/me',
    method: 'get'
  })
}

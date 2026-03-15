import request from './request'
import type { UserInfo, VipInfo, UserPoints } from '../types'

interface ApiResponse<T> {
  data: T
  message?: string
  code?: number
}

// 获取用户信息
export function getUserInfo() {
  return request<ApiResponse<UserInfo>>({
    url: '/user/info',
    method: 'get'
  })
}

// 获取 VIP 信息
export function getVipInfo() {
  return request<ApiResponse<VipInfo>>({
    url: '/user/vip',
    method: 'get'
  })
}

// 获取用户积分
export function getUserPoints() {
  return request<ApiResponse<UserPoints>>({
    url: '/user/points',
    method: 'get'
  })
}

// 签到
export function checkIn() {
  return request<ApiResponse<{ points: number }>>({
    url: '/user/checkin',
    method: 'post'
  })
}

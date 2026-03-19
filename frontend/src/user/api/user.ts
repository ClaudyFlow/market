import request from './request'
import type { UserInfo, VipInfo, UserCredit } from '../types'

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
export function getUserCredit() {
  return request<ApiResponse<UserCredit>>({
    url: '/user/credit',
    method: 'get'
  })
}

// 签到
export function checkIn() {
  return request<ApiResponse<{ credit: number }>>({
    url: '/user/checkin',
    method: 'post'
  })
}

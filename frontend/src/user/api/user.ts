/**
 * 用户相关 API
 */

import { get, post, put, del } from './request'
import type { User, UserProfile, UserSettings } from '@user/types/user'
import type { PageData, PageParams } from './request'

const BASE_URL = '/user'

/**
 * 获取当前用户信息
 */
export function getCurrentUser(): Promise<User> {
  return get(`${BASE_URL}/info`)
}

/**
 * 获取用户详情
 */
export function getUserDetail(userId: number | string): Promise<UserProfile> {
  return get(`${BASE_URL}/${userId}`)
}

/**
 * 更新用户信息
 */
export function updateUserInfo(data: Partial<User>): Promise<User> {
  return put(`${BASE_URL}/info`, data)
}

/**
 * 更新头像
 */
export function updateAvatar(file: File): Promise<{ avatar: string }> {
  const formData = new FormData()
  formData.append('file', file)
  return post(`${BASE_URL}/avatar`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/**
 * 修改密码
 */
export function changePassword(oldPassword: string, newPassword: string): Promise<void> {
  return post(`${BASE_URL}/password`, { oldPassword, newPassword })
}

/**
 * 绑定手机
 */
export function bindPhone(phone: string, code: string): Promise<void> {
  return post(`${BASE_URL}/bind-phone`, { phone, code })
}

/**
 * 绑定邮箱
 */
export function bindEmail(email: string, code: string): Promise<void> {
  return post(`${BASE_URL}/bind-email`, { email, code })
}

/**
 * 获取用户设置
 */
export function getSettings(): Promise<UserSettings> {
  return get(`${BASE_URL}/settings`)
}

/**
 * 更新用户设置
 */
export function updateSettings(settings: Partial<UserSettings>): Promise<UserSettings> {
  return put(`${BASE_URL}/settings`, settings)
}

/**
 * 获取用户地址列表
 */
export function getAddressList(): Promise<{ id: number; name: string; phone: string; province: string; city: string; district: string; detail: string; isDefault: boolean }[]> {
  return get(`${BASE_URL}/addresses`)
}

/**
 * 添加地址
 */
export function addAddress(data: { name: string; phone: string; province: string; city: string; district: string; detail: string; isDefault?: boolean }): Promise<void> {
  return post(`${BASE_URL}/address`, data)
}

/**
 * 更新地址
 */
export function updateAddress(id: number, data: Partial<{ name: string; phone: string; province: string; city: string; district: string; detail: string; isDefault: boolean }>): Promise<void> {
  return put(`${BASE_URL}/address/${id}`, data)
}

/**
 * 删除地址
 */
export function deleteAddress(id: number): Promise<void> {
  return del(`${BASE_URL}/address/${id}`)
}

/**
 * 设置默认地址
 */
export function setDefaultAddress(id: number): Promise<void> {
  return put(`${BASE_URL}/address/${id}/default`)
}

/**
 * 获取用户收藏
 */
export function getFavorites(params?: PageParams): Promise<PageData<{ id: number; type: string; itemId: number; name: string; image: string; price: number; createTime: string }>> {
  return get(`${BASE_URL}/favorites`, params)
}

/**
 * 获取用户浏览历史
 */
export function getBrowseHistory(params?: PageParams): Promise<PageData<{ id: number; productId: number; name: string; image: string; price: number; browseTime: string }>> {
  return get(`${BASE_URL}/history`, params)
}

/**
 * 清除浏览历史
 */
export function clearBrowseHistory(): Promise<void> {
  return del(`${BASE_URL}/history/clear`)
}

/**
 * 获取用户积分
 */
export function getPoints(): Promise<{ points: number; totalPoints: number; usedPoints: number }> {
  return get(`${BASE_URL}/points`)
}

/**
 * 获取用户等级
 */
export function getLevel(): Promise<{ level: number; name: string; experience: number; nextLevel: number; progress: number }> {
  return get(`${BASE_URL}/level`)
}

/**
 * 用户签到
 */
export function dailySignin(): Promise<{ points: number; consecutiveDays: number }> {
  return post(`${BASE_URL}/signin`)
}

/**
 * 获取签到状态
 */
export function getSigninStatus(): Promise<{ signed: boolean; consecutiveDays: number }> {
  return get(`${BASE_URL}/signin`)
}

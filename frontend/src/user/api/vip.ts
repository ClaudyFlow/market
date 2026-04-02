/**
 * VIP 相关 API
 */

import { get, post } from './request'

const BASE_URL = '/vip'

/**
 * 获取 VIP 信息
 */
export function getVipInfo(): Promise<{
  level: number
  name: string
  experience: number
  nextLevelExperience: number
  progress: number
  benefits: VipBenefit[]
  expireTime: string
  isVip: boolean
}> {
  return get(`${BASE_URL}/info`)
}

/**
 * 获取 VIP 等级列表
 */
export function getVipLevels(): Promise<{
  level: number
  name: string
  icon: string
  experience: number
  benefits: string[]
}[]> {
  return get(`${BASE_URL}/levels`)
}

/**
 * 获取 VIP 特权
 */
export function getVipBenefits(): Promise<VipBenefit[]> {
  return get(`${BASE_URL}/benefits`)
}

/**
 * 购买 VIP
 */
export function purchaseVip(months: number, autoRenew?: boolean): Promise<{
  orderId: string
  amount: number
  originalPrice: number
  discountPrice: number
}> {
  return post(`${BASE_URL}/purchase`, { months, autoRenew })
}

/**
 * 续费 VIP
 */
export function renewVip(months?: number): Promise<{
  orderId: string
  amount: number
}> {
  return post(`${BASE_URL}/renew`, { months })
}

/**
 * VIP 体验卡
 */
export function useTrialCard(): Promise<{
  expireTime: string
  days: number
}> {
  return post(`${BASE_URL}/trial`)
}

/**
 * 获取 VIP 任务
 */
export function getVipTasks(): Promise<{
  id: number
  name: string
  description: string
  rewardExperience: number
  status: 'pending' | 'completed' | 'claimed'
  progress: number
  target: number
}[]> {
  return get(`${BASE_URL}/tasks`)
}

/**
 * 领取 VIP 任务奖励
 */
export function claimVipTask(taskId: number): Promise<void> {
  return post(`${BASE_URL}/task/${taskId}/claim`)
}

/**
 * 获取 VIP 专属优惠券
 */
export function getVipCoupons(): Promise<{
  id: number
  name: string
  amount: number
  condition: number
  validDays: number
}[]> {
  return get(`${BASE_URL}/coupons`)
}

export interface VipBenefit {
  id: number
  name: string
  description: string
  icon: string
  enabled: boolean
}

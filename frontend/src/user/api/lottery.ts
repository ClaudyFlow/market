/**
 * 抽奖相关 API
 */

import { get, post } from './request'

const BASE_URL = '/lottery'

/**
 * 获取抽奖活动信息
 */
export function getLotteryActivity(): Promise<{
  id: number
  name: string
  description: string
  startTime: string
  endTime: string
  totalChances: number
  usedChances: number
  prizes: LotteryPrize[]
}> {
  return get(`${BASE_URL}/activity`)
}

/**
 * 获取奖品列表
 */
export function getPrizes(): Promise<LotteryPrize[]> {
  return get(`${BASE_URL}/prizes`)
}

/**
 * 抽奖
 */
export function draw(): Promise<{
  prizeId: number
  prizeName: string
  prizeType: string
  prizeValue: number
  message: string
}> {
  return post(`${BASE_URL}/draw`)
}

/**
 * 获取我的奖品
 */
export function getMyPrizes(params?: { status?: 'pending' | 'used' | 'expired' }): Promise<{
  id: number
  prizeName: string
  prizeType: string
  prizeValue: number
  winTime: string
  status: string
  expireTime: string
}[]> {
  return get(`${BASE_URL}/my-prizes`, params)
}

/**
 * 领取奖品
 */
export function claimPrize(prizeId: number, addressId?: number): Promise<void> {
  return post(`${BASE_URL}/prize/${prizeId}/claim`, { addressId })
}

/**
 * 获取中奖记录
 */
export function getWinRecords(limit?: number): Promise<{
  userName: string
  prizeName: string
  winTime: string
}[]> {
  return get(`${BASE_URL}/records`, { limit })
}

export interface LotteryPrize {
  id: number
  name: string
  type: 'coupon' | 'points' | 'physical' | 'virtual'
  value: number
  image: string
  probability: number
  stock: number
  remainingStock: number
  description: string
}

import request from '@user/api/request'

export interface VipLevel {
  level: number
  name: string
  icon?: string
  growthValueRequired: number
  discountRate: number
  dailyCredit: number
  monthlyCredit: number
  freeShippingCount: number
  refundPriority: boolean
  exclusiveService: boolean
  description?: string
  backgroundColor?: string
  textColor?: string
}

export interface VipGift {
  id: number
  name: string
  type: string // DAILY, MONTHLY, BEGINNER, LEVEL_UP
  vipLevelRequired: number
  creditReward: number
  claimType: string // DAILY, MONTHLY, ONCE
  claimIntervalHours: number
  description?: string
  image?: string
  status?: string
  canClaim?: boolean
  lastClaimed?: string
  nextAvailable?: string
  remainingSeconds?: number
  remainingDays?: number
  claimed?: boolean
}

export interface VipInfo {
  currentLevel: VipLevel
  growthValue: number
  progressPercent: number
  nextLevel?: VipLevel
  remainingGrowth: number
}

export interface RechargeOrder {
  orderNo: string
  amount: number
  growthValue: number
  status: string
  paymentMethod?: string
  paidAt?: string
  createdAt: string
}

interface ApiResponse<T> {
  code: number
  data: T
  message?: string
}

// ==================== VIP 等级 ====================

/**
 * 获取 VIP 等级列表
 */
export function getVipLevels(): Promise<ApiResponse<VipLevel[]>> {
  return request.get('/vip/levels')
}

/**
 * 获取我的 VIP 信息
 */
export function getMyVipInfo(): Promise<ApiResponse<VipInfo>> {
  return request.get('/vip/my')
}

/**
 * 获取 VIP 权益详情
 */
export function getPrivileges(): Promise<ApiResponse<{ currentLevel: VipLevel; benefits: any }>> {
  return request.get('/vip/privileges')
}

// ==================== VIP 礼包 ====================

/**
 * 获取每日礼包列表
 */
export function getDailyGifts(): Promise<ApiResponse<VipGift[]>> {
  return request.get('/vip/gifts/daily')
}

/**
 * 获取每月礼包列表
 */
export function getMonthlyGifts(): Promise<ApiResponse<VipGift[]>> {
  return request.get('/vip/gifts/monthly')
}

/**
 * 获取所有礼包
 */
export function getAllGifts(): Promise<ApiResponse<VipGift[]>> {
  return request.get('/vip/gifts')
}

/**
 * 领取礼包
 */
export function claimGift(giftId: number): Promise<ApiResponse<any>> {
  return request.post(`/vip/gifts/${giftId}/claim`)
}

/**
 * 获取礼包领取记录
 */
export function getGiftRecords(type?: string): Promise<ApiResponse<any[]>> {
  return request.get('/vip/gifts/records', { params: { type } })
}

// ==================== VIP 充值 ====================

/**
 * 创建充值订单
 */
export function createRechargeOrder(amount: number): Promise<ApiResponse<RechargeOrder>> {
  return request.post('/vip/recharge', null, { params: { amount } })
}

/**
 * 支付充值订单
 */
export function payRechargeOrder(orderNo: string, paymentMethod: string): Promise<ApiResponse<RechargeOrder>> {
  return request.post(`/vip/recharge/${orderNo}/pay`, null, { params: { paymentMethod } })
}

/**
 * 获取充值记录
 */
export function getRechargeRecords(): Promise<ApiResponse<RechargeOrder[]>> {
  return request.get('/vip/recharge/records')
}

/**
 * 获取充值统计
 */
export function getRechargeStats(): Promise<ApiResponse<{ totalAmount: number; totalGrowth: number }>> {
  return request.get('/vip/recharge/stats')
}

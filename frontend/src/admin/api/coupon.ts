import request from '@admin/api/request'

export interface Coupon {
  id: number
  name: string
  type: string // PERCENT-折扣券，FIXED-满减券
  discountValue: number
  minPurchase?: number
  maxDiscount?: number
  validFrom?: string
  validTo?: string
  totalCount: number
  usedCount: number
  remainCount: number
  status: string // ACTIVE, INACTIVE, EXPIRED, USED_UP
  description?: string
  scope: string // ALL, CATEGORY, PRODUCT
  categoryIds?: string
  productIds?: string
  createdAt?: string
  updatedAt?: string
  merchantId?: number
  merchantName?: string
}

export interface CouponStats {
  total: number
  active: number
  expired: number
  usedUp: number
  totalDiscount: number
}

interface CouponListParams {
  page?: number
  size?: number
  status?: string
  sortBy?: string
  merchantId?: number
}

interface CouponListResponse {
  list: Coupon[]
  total: number
  page: number
  size: number
}

// 创建平台优惠券
export function createPlatformCoupon(data: Coupon): Promise<Coupon> {
  return request.post('/coupon', data)
}

// 更新平台优惠券
export function updatePlatformCoupon(id: number, data: Partial<Coupon>): Promise<Coupon> {
  return request.put(`/coupon/${id}`, data)
}

// 删除平台优惠券
export function deletePlatformCoupon(id: number): Promise<void> {
  return request.delete(`/coupon/${id}`)
}

// 获取平台优惠券列表
export function getPlatformCouponList(params?: CouponListParams): Promise<CouponListResponse> {
  return request.get('/coupon/list', { params })
}

// 获取平台优惠券详情
export function getPlatformCouponDetail(id: number): Promise<Coupon> {
  return request.get(`/coupon/${id}`)
}

// 获取平台优惠券统计
export function getPlatformCouponStats(): Promise<CouponStats> {
  return request.get('/coupon/stats')
}

// 获取所有商家优惠券列表（平台管理）
export function getMerchantCouponList(params?: CouponListParams): Promise<CouponListResponse> {
  return request.get('/coupon/merchant/list', { params })
}

// 优惠券排行统计
export function getCouponRank(type?: string, limit?: number): Promise<{ id: number; name: string; usedCount: number; totalCount: number; redemptionRate: number }[]> {
  return request.get('/coupon/rank', { params: { type, limit } })
}

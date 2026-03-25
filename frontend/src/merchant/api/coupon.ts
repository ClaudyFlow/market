import request from '@merchant/api/request'

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
  inactive: number
  expired: number
  usedUp: number
  totalDiscount: number
  redemptionRate: string
}

interface ApiResponse<T> {
  code: number
  data: T
  message?: string
}

interface CouponListParams {
  page?: number
  size?: number
  status?: string
  sortBy?: string
}

interface CouponListResponse {
  list: Coupon[]
  total: number
  page: number
  size: number
}

// 创建优惠券
export function createCoupon(data: Coupon): Promise<ApiResponse<Coupon>> {
  return request.post('/coupon', data)
}

// 更新优惠券
export function updateCoupon(id: number, data: Partial<Coupon>): Promise<ApiResponse<Coupon>> {
  return request.put(`/coupon/${id}`, data)
}

// 删除优惠券
export function deleteCoupon(id: number): Promise<ApiResponse<void>> {
  return request.delete(`/coupon/${id}`)
}

// 获取优惠券列表
export function getCouponList(params?: CouponListParams): Promise<ApiResponse<CouponListResponse>> {
  return request.get('/coupon/list', { params })
}

// 获取优惠券详情
export function getCouponDetail(id: number): Promise<ApiResponse<Coupon>> {
  return request.get(`/coupon/${id}`)
}

// 获取优惠券统计
export function getCouponStats(): Promise<ApiResponse<CouponStats>> {
  return request.get('/coupon/stats')
}

// 获取即将过期的优惠券
export function getExpiringCoupons(days?: number): Promise<ApiResponse<Coupon[]>> {
  return request.get('/coupon/expiring', { params: { days } })
}

// 上下架优惠券
export function toggleCouponStatus(id: number, status: string): Promise<ApiResponse<Coupon>> {
  return request.put(`/coupon/${id}/status`, null, { params: { status } })
}

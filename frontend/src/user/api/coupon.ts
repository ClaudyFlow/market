import request from '@user/api/request'

export interface Coupon {
  id: number
  userCouponId?: number
  couponId: number
  name: string
  type: string // PERCENT-折扣券，FIXED-满减券
  discountValue: number
  minPurchase?: number
  maxDiscount?: number
  validFrom?: string
  validTo?: string
  status: string // UNUSED, USED, EXPIRED
  description?: string
  scope?: string
  label?: string
  obtainedAt?: string
  usedAt?: string
  merchantId?: number
  merchantName?: string
}

export interface CouponStats {
  total: number
  unused: number
  used: number
  expired: number
  savedAmount: number
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
}

interface CouponListResponse {
  list: Coupon[]
  total: number
  page: number
  size: number
}

// 领取优惠券
export function takeCoupon(id: number): Promise<ApiResponse<Coupon>> {
  return request.post(`/coupon/take/${id}`)
}

// 获取我的优惠券列表
export function getMyCoupons(params?: CouponListParams): Promise<ApiResponse<CouponListResponse>> {
  return request.get('/coupon/list', { params })
}

// 获取可用优惠券（下单时选择）
export function getAvailableCoupons(merchantId?: number, orderAmount?: number): Promise<ApiResponse<Coupon[]>> {
  return request.get('/coupon/available', { 
    params: { merchantId, orderAmount } 
  })
}

// 获取订单可用优惠券（智能推荐）
export function getOrderAvailableCoupons(
  merchantId: number,
  orderAmount: number,
  productIds?: number[],
  categories?: string[]
): Promise<ApiResponse<Coupon[]>> {
  return request.get('/coupon/order/available', {
    params: { merchantId, orderAmount, productIds, categories }
  })
}

// 获取最优优惠券
export function getBestCoupon(
  merchantId: number,
  orderAmount: number,
  productIds?: number[],
  categories?: string[]
): Promise<ApiResponse<Coupon>> {
  return request.get('/coupon/best', {
    params: { merchantId, orderAmount, productIds, categories }
  })
}

// 使用优惠券
export function useCoupon(id: number): Promise<ApiResponse<void>> {
  return request.post(`/coupon/${id}/use`)
}

// 退还优惠券（订单取消时）
export function returnCoupon(id: number): Promise<ApiResponse<void>> {
  return request.post(`/coupon/${id}/return`)
}

// 获取优惠券统计
export function getCouponStats(): Promise<ApiResponse<CouponStats>> {
  return request.get('/coupon/stats')
}

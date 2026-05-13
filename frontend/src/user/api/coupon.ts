/**
 * 优惠券相关 API
 */

import { get, post, del } from './request'
import type { Coupon, CouponTemplate } from '@user/types/coupon'
import type { PageData, PageParams } from './request'

const BASE_URL = '/coupon'

/**
 * 获取优惠券列表（用户已领取的）
 */
export function getCouponList(params?: PageParams): Promise<PageData<Coupon>> {
  return get(BASE_URL, params)
}

/**
 * 获取优惠券模板列表（可领取的）
 */
export function getCouponTemplates(params?: PageParams): Promise<PageData<CouponTemplate>> {
  return get(`${BASE_URL}/templates`, params)
}

/**
 * 获取优惠券详情
 */
export function getCouponDetail(couponId: number | string): Promise<Coupon> {
  return get(`${BASE_URL}/${couponId}`)
}

/**
 * 获取优惠券模板详情
 */
export function getCouponTemplateDetail(templateId: number | string): Promise<CouponTemplate> {
  return get(`${BASE_URL}/templates/${templateId}`)
}

/**
 * 领取优惠券
 */
export function receiveCoupon(templateId: number | string): Promise<Coupon> {
  return post(`${BASE_URL}/receive`, { templateId })
}

/**
 * 批量领取优惠券
 */
export function batchReceiveCoupon(templateIds: number[]): Promise<Coupon[]> {
  return post(`${BASE_URL}/batch-receive`, { templateIds })
}

/**
 * 使用优惠券
 */
export function useCoupon(couponId: number | string, orderId: number | string): Promise<void> {
  return post(`${BASE_URL}/${couponId}/use`, { orderId })
}

/**
 * 退还优惠券
 */
export function returnCoupon(couponId: number | string): Promise<void> {
  return post(`${BASE_URL}/${couponId}/return`)
}

/**
 * 删除优惠券
 */
export function deleteCoupon(couponId: number | string): Promise<void> {
  return del(`${BASE_URL}/${couponId}`)
}

/**
 * 检查优惠券是否可用
 */
export function checkCouponAvailable(couponId: number | string, amount?: number): Promise<{ available: boolean; reason?: string }> {
  return get(`${BASE_URL}/${couponId}/check`, { amount })
}

/**
 * 获取可用优惠券列表
 */
export function getAvailableCoupons(amount?: number, shopId?: number): Promise<Coupon[]> {
  return get(`${BASE_URL}/available`, { amount, shopId })
}

/**
 * 获取即将过期优惠券
 */
export function getExpiringCoupons(days?: number): Promise<Coupon[]> {
  return get(`${BASE_URL}/expiring`, { days })
}

/**
 * 店铺优惠券列表
 */
export function getShopCoupons(shopId: number | string): Promise<CouponTemplate[]> {
  return get(`/shop/${shopId}/coupons`)
}

/**
 * 商品优惠券列表
 */
export function getProductCoupons(productId: number | string): Promise<CouponTemplate[]> {
  return get(`/product/${productId}/coupons`)
}

/**
 * 获取我的优惠券（已领取的）
 */
export function getMyCoupons(params?: PageParams): Promise<PageData<Coupon>> {
  return get(BASE_URL, params)
}

/**
 * 领取优惠券（简化版）
 */
export function takeCoupon(templateId: number | string): Promise<void> {
  return post(`${BASE_URL}/receive`, { templateId })
}

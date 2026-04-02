/**
 * 店铺相关 API
 */

import { get, post, put, del } from './request'
import type { Shop, ShopDetail, ShopStats, ShopProduct, ShopCoupon } from '@user/types/shop'
import type { PageData, PageParams } from './request'

const BASE_URL = '/shop'

/**
 * 获取店铺列表
 */
export function getShopList(params?: PageParams): Promise<PageData<Shop>> {
  return get(BASE_URL, params)
}

/**
 * 获取店铺详情
 */
export function getShopDetail(shopId: number | string): Promise<ShopDetail> {
  return get(`${BASE_URL}/${shopId}`)
}

/**
 * 获取店铺统计信息
 */
export function getShopStats(shopId: number | string): Promise<ShopStats> {
  return get(`${BASE_URL}/${shopId}/stats`)
}

/**
 * 获取店铺商品列表
 */
export function getShopProducts(shopId: number | string, params?: PageParams): Promise<PageData<ShopProduct>> {
  return get(`${BASE_URL}/${shopId}/products`, params)
}

/**
 * 获取店铺优惠券
 */
export function getShopCoupons(shopId: number | string): Promise<ShopCoupon[]> {
  return get(`${BASE_URL}/${shopId}/coupons`)
}

/**
 * 领取店铺优惠券
 */
export function receiveCoupon(couponId: number | string): Promise<void> {
  return post(`${BASE_URL}/coupons/${couponId}/receive`)
}

/**
 * 关注店铺
 */
export function followShop(shopId: number | string): Promise<void> {
  return post(`${BASE_URL}/${shopId}/follow`)
}

/**
 * 取消关注店铺
 */
export function unfollowShop(shopId: number | string): Promise<void> {
  return del(`${BASE_URL}/${shopId}/follow`)
}

/**
 * 检查是否已关注
 */
export function checkFollowing(shopId: number | string): Promise<{ following: boolean }> {
  return get(`${BASE_URL}/${shopId}/following`)
}

/**
 * 获取我的关注店铺列表
 */
export function getFollowedShops(params?: PageParams): Promise<PageData<Shop>> {
  return get(`${BASE_URL}/followed`, params)
}

/**
 * 搜索店铺
 */
export function searchShops(keyword: string, params?: PageParams): Promise<PageData<Shop>> {
  return get(`${BASE_URL}/search`, { keyword, ...params })
}

/**
 * 联系商家客服
 */
export function contactMerchant(shopId: number | string): Promise<{ chatId: string }> {
  return post(`${BASE_URL}/${shopId}/contact`)
}

/**
 * 分享店铺
 */
export function shareShop(shopId: number | string): Promise<{ shareUrl: string; shareCode: string }> {
  return post(`${BASE_URL}/${shopId}/share`)
}

/**
 * 举报店铺
 */
export function reportShop(shopId: number | string, reason: string, description?: string): Promise<void> {
  return post(`${BASE_URL}/${shopId}/report`, { reason, description })
}

/**
 * 获取店铺公告
 */
export function getShopAnnouncement(shopId: number | string): Promise<{ content: string; updateTime: string }> {
  return get(`${BASE_URL}/${shopId}/announcement`)
}

/**
 * 获取店铺分类列表
 */
export function getShopCategories(shopId: number | string): Promise<{ id: number; name: string; count: number }[]> {
  return get(`${BASE_URL}/${shopId}/categories`)
}

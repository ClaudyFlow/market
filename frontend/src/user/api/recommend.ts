/**
 * 推荐系统 API
 */

import { get } from './request'
import type { Product } from '@user/types/product'

const BASE_URL = '/recommend'

/**
 * 获取推荐商品（猜你喜欢）
 */
export function getRecommendProducts(limit?: number): Promise<Product[]> {
  return get(`${BASE_URL}/products`, { limit })
}

/**
 * 获取热门商品
 */
export function getHotProducts(limit?: number): Promise<Product[]> {
  return get(`${BASE_URL}/hot`, { limit })
}

/**
 * 获取看了又看
 */
export function getViewedAlsoViewed(productId: number, limit?: number): Promise<Product[]> {
  return get(`${BASE_URL}/viewed-also-viewed`, { productId, limit })
}

/**
 * 获取买了又买
 */
export function getBoughtAlsoBought(productId: number, limit?: number): Promise<Product[]> {
  return get(`${BASE_URL}/bought-also-bought`, { productId, limit })
}

/**
 * 获取店铺推荐商品
 */
export function getShopRecommend(merchantId: number, limit?: number): Promise<Product[]> {
  return get(`${BASE_URL}/shop`, { merchantId, limit })
}
/**
 * 首页相关 API
 */

import { get } from './request'
import type { Product, Shop } from '@user/types'

const BASE_URL = '/home'

/**
 * 获取首页轮播图
 */
export function getBanners(position?: 'home' | 'product' | 'shop'): Promise<{ id: number; image: string; title: string; link: string; position: string }[]> {
  return get(`${BASE_URL}/banners`, { position })
}

/**
 * 获取首页分类数据
 */
export function getCategories(): Promise<{ id: number; name: string; icon: string; subCategories?: { id: number; name: string }[] }[]> {
  return get(`${BASE_URL}/categories`)
}

/**
 * 获取首页商品（按楼层）
 */
export function getFloorProducts(floorId?: number): Promise<{
  id: number
  name: string
  products: Product[]
}[]> {
  return get(`${BASE_URL}/floor-products`, { floorId })
}

/**
 * 获取首页店铺推荐
 */
export function getRecommendedShops(limit?: number): Promise<Shop[]> {
  return get(`${BASE_URL}/recommended-shops`, { limit })
}

/**
 * 获取首页活动信息
 */
export function getActivities(): Promise<{
  id: number
  title: string
  subtitle: string
  image: string
  startTime: string
  endTime: string
  link: string
}[]> {
  return get(`${BASE_URL}/activities`)
}

/**
 * 获取秒杀活动
 */
export function getFlashSales(): Promise<{
  id: number
  startTime: string
  endTime: string
  status: 'upcoming' | 'ongoing' | 'ended'
  products: {
    id: number
    name: string
    image: string
    flashPrice: number
    originalPrice: number
    sales: number
    stock: number
    progress: number
  }[]
}[]> {
  return get(`${BASE_URL}/flash-sales`)
}

/**
 * 获取品牌专区
 */
export function getBrandZones(): Promise<{
  id: number
  name: string
  logo: string
  description: string
  productCount: number
}[]> {
  return get(`${BASE_URL}/brands`)
}

/**
 * 获取新人专享区
 */
export function getNewUserZone(): Promise<{
  coupons: { id: number; amount: number; condition: number }[]
  products: Product[]
}> {
  return get(`${BASE_URL}/new-user`)
}

/**
 * 获取猜你喜欢
 */
export function getRecommendForYou(params?: { page?: number; size?: number }): Promise<{
  products: Product[]
  hasMore: boolean
}> {
  return get(`${BASE_URL}/recommend`, params)
}

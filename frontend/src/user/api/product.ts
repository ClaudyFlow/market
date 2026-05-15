/**
 * 商品相关 API
 */

import { get, post, put, del } from './request'
import type { Product, ProductDetail, ProductSku, ProductImage, ProductComment } from '@user/types/product'
import type { PageData, PageParams } from './request'

const BASE_URL = '/product'

/**
 * 获取商品列表
 */
export function getProductList(params?: PageParams): Promise<PageData<Product>> {
  return get(BASE_URL, params)
}

/**
 * 获取商品详情
 */
export function getProductDetail(productId: number | string): Promise<ProductDetail> {
  return get(`${BASE_URL}/${productId}`)
}

/**
 * 获取商品 SKU 信息
 */
export function getProductSkus(productId: number | string): Promise<ProductSku[]> {
  return get(`${BASE_URL}/${productId}/skus`)
}

/**
 * 获取商品图片
 */
export function getProductImages(productId: number | string): Promise<ProductImage[]> {
  return get(`${BASE_URL}/${productId}/images`)
}

/**
 * 获取商品评价
 */
export function getProductComments(productId: number | string, params?: PageParams): Promise<PageData<ProductComment>> {
  return get(`${BASE_URL}/${productId}/comments`, params)
}

/**
 * 搜索商品
 */
export function searchProducts(keyword: string, params?: PageParams): Promise<PageData<Product>> {
  return get(`${BASE_URL}/search`, { keyword, ...params })
}

/**
 * 获取推荐商品
 */
export function getRecommendedProducts(limit?: number): Promise<Product[]> {
  return get(`${BASE_URL}/recommended`, { limit })
}

/**
 * 获取热销商品
 */
export function getHotProducts(limit?: number): Promise<Product[]> {
  return get(`${BASE_URL}/hot`, { limit })
}

/**
 * 获取新品
 */
export function getNewProducts(limit?: number): Promise<Product[]> {
  return get(`${BASE_URL}/new`, { limit })
}

/**
 * 获取促销商品
 */
export function getSaleProducts(limit?: number): Promise<Product[]> {
  return get(`${BASE_URL}/sale`, { limit })
}

/**
 * 获取商品库存
 */
export function getProductStock(productId: number | string, skuId?: number | string): Promise<{ stock: number }> {
  return get(`${BASE_URL}/${productId}/stock`, { skuId })
}

/**
 * 商品收藏
 */
export function favoriteProduct(productId: number | string): Promise<void> {
  return post(`${BASE_URL}/${productId}/favorite`)
}

/**
 * 取消商品收藏
 */
export function unfavoriteProduct(productId: number | string): Promise<void> {
  return del(`${BASE_URL}/${productId}/favorite`)
}

/**
 * 检查是否已收藏
 */
export function checkFavorite(productId: number | string): Promise<{ favorite: boolean }> {
  return get(`${BASE_URL}/${productId}/favorite`)
}

/**
 * 商品浏览记录
 */
export function addBrowseHistory(productId: number | string): Promise<void> {
  return post(`${BASE_URL}/${productId}/browse`)
}

/**
 * 获取商品分类
 */
export function getCategories(): Promise<{ id: number; name: string; parentId?: number; icon?: string }[]> {
  return get(`${BASE_URL}/categories`)
}

/**
 * 获取商品品牌
 */
export function getBrands(categoryId?: number): Promise<{ id: number; name: string; logo?: string }[]> {
  return get(`${BASE_URL}/brands`, { categoryId })
}

/**
 * 获取商品属性
 */
export function getProductAttributes(categoryId: number): Promise<{ id: number; name: string; values: string[] }[]> {
  return get(`${BASE_URL}/attributes`, { categoryId })
}

/**
 * 批量获取商品信息
 */
export function getProductsBatch(ids: number[]): Promise<Product[]> {
  return post(`${BASE_URL}/batch`, ids)
}

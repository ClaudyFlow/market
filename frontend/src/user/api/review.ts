/**
 * 评价相关 API
 */

import { get, post, put, del } from './request'
import type { Review, ReviewStats } from '@user/types/review'
import type { PageData, PageParams } from './request'

export type { Review, ReviewStats } from '@user/types/review'

const BASE_URL = '/review'

/**
 * 获取评价列表
 */
export function getReviewList(params?: PageParams & { productId?: number; userId?: number; score?: number }): Promise<PageData<Review>> {
  return get(BASE_URL, params)
}

/**
 * 获取评价详情
 */
export function getReviewDetail(reviewId: number | string): Promise<Review> {
  return get(`${BASE_URL}/${reviewId}`)
}

/**
 * 创建评价
 */
export function createReview(data: {
  orderId: number | string
  productId?: number | string
  score: number
  content?: string
  images?: string[]
  anonymous?: boolean
}): Promise<Review> {
  return post(BASE_URL, data)
}

/**
 * 更新评价
 */
export function updateReview(reviewId: number | string, data: { content?: string; images?: string[] }): Promise<Review> {
  return put(`${BASE_URL}/${reviewId}`, data)
}

/**
 * 删除评价
 */
export function deleteReview(reviewId: number | string): Promise<void> {
  return del(`${BASE_URL}/${reviewId}`)
}

/**
 * 点赞评价
 */
export function likeReview(reviewId: number | string): Promise<{ likeCount: number }> {
  return post(`${BASE_URL}/${reviewId}/like`)
}

/**
 * 取消点赞
 */
export function unlikeReview(reviewId: number | string): Promise<{ likeCount: number }> {
  return del(`${BASE_URL}/${reviewId}/like`)
}

/**
 * 回复评价
 */
export function replyReview(reviewId: number | string, content: string): Promise<{ id: number; content: string; replyTime: string }> {
  return post(`${BASE_URL}/${reviewId}/reply`, { content })
}

/**
 * 获取评价统计
 */
export function getReviewStats(productId?: number): Promise<ReviewStats> {
  return get(`${BASE_URL}/stats`, { productId })
}

/**
 * 获取评价标签
 */
export function getReviewTags(productId?: number): Promise<{ tag: string; count: number }[]> {
  return get(`${BASE_URL}/tags`, { productId })
}

/**
 * 获取带图评价
 */
export function getReviewsWithImages(productId?: number, limit?: number): Promise<Review[]> {
  return get(`${BASE_URL}/with-images`, { productId, limit })
}

/**
 * 获取追评
 */
export function getAdditionalReviews(productId?: number, limit?: number): Promise<Review[]> {
  return get(`${BASE_URL}/additional`, { productId, limit })
}

/**
 * 举报评价
 */
export function reportReview(reviewId: number | string, reason: string): Promise<void> {
  return post(`${BASE_URL}/${reviewId}/report`, { reason })
}

/**
 * 检查用户是否已评价商品
 */
export function checkReview(params: { userId?: number; productId?: number; orderId?: number }): Promise<{ reviewed: boolean }> {
  return get(`${BASE_URL}/check`, params)
}

/**
 * 添加评价（createReview 的别名）
 */
export const addReview = createReview

/**
 * 获取商品评价列表（getReviewList 的便捷方法）
 */
export function getProductReviews(productId: number, params?: PageParams): Promise<PageData<Review>> {
  return getReviewList({ productId, ...params })
}

/**
 * 获取商品平均评分
 */
export function getProductRating(productId: number): Promise<{ averageRating: number; reviewCount: number }> {
  return getReviewStats(productId).then(stats => ({
    averageRating: stats.averageRating,
    reviewCount: stats.totalReviews
  }))
}

/**
 * 评价信息类型别名（兼容旧代码）
 */
export type RatingInfo = {
  averageRating: number
  reviewCount: number
  ratingDistribution: Record<number, number>
}

/**
 * 评价商家
 */
export function createMerchantReview(data: {
  merchantId: number
  orderId?: number
  score: number
  serviceScore?: number
  deliveryScore?: number
  qualityScore?: number
  content?: string
  images?: string[]
  anonymous?: boolean
}): Promise<Review> {
  return post(`${BASE_URL}/merchant`, data)
}

/**
 * 获取商家评价列表
 */
export function getMerchantReviews(merchantId: number, params?: PageParams): Promise<PageData<Review>> {
  return get(`${BASE_URL}/merchant/${merchantId}`, params)
}

/**
 * 获取商家评价统计
 */
export function getMerchantReviewStats(merchantId: number): Promise<{
  averageScore: number
  serviceScore: number
  deliveryScore: number
  qualityScore: number
  totalReviews: number
}> {
  return get(`${BASE_URL}/merchant/${merchantId}/stats`)
}

/**
 * 评价服务（物流/客服）
 */
export function createServiceReview(data: {
  orderId: number
  type: 'logistics' | 'customer'
  score: number
  content?: string
}): Promise<void> {
  return post(`${BASE_URL}/service`, data)
}

/**
 * 获取服务评价详情
 */
export function getServiceReview(orderId: number, type: 'logistics' | 'customer'): Promise<{
  score: number
  content?: string
  createTime: string
}> {
  return get(`${BASE_URL}/service`, { orderId, type })
}

/**
 * 获取用户自己的评价列表
 */
export function getUserReviews(params?: PageParams): Promise<PageData<Review>> {
  return get(`${BASE_URL}/user`, params)
}

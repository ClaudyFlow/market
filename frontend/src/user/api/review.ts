/**
 * 评价相关 API
 */

import { get, post, put, del } from './request'
import type { Review, ReviewStats } from '@user/types/review'
import type { PageData, PageParams } from './request'

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

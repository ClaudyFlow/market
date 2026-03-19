import request from './request'

interface ApiResponse<T> {
  data: T
  message?: string
  code?: number
}

export interface Review {
  id: number
  userId: number
  productId: number
  rating: number
  content: string
  userName: string
  userAvatar?: string
  productName: string
  productImage?: string
  productPrice?: number
  createdAt?: string
  updatedAt?: string
}

export interface RatingInfo {
  averageRating: number
  reviewCount: number
  ratingDistribution: Record<number, number>
}

// 获取商品的所有评价
export function getProductReviews(productId: number) {
  return request<ApiResponse<Review[]>>({
    url: `/review/product/${productId}`,
    method: 'get'
  })
}

// 获取用户的所有评价
export function getUserReviews() {
  return request<ApiResponse<Review[]>>({
    url: '/review/user',
    method: 'get'
  })
}

// 添加评价
export function addReview(productId: number, rating: number, content: string) {
  return request({
    url: '/review',
    method: 'post',
    data: { productId, rating, content }
  })
}

// 更新评价
export function updateReview(productId: number, rating: number, content: string) {
  return request({
    url: `/review/${productId}`,
    method: 'put',
    data: { productId, rating, content }
  })
}

// 删除评价
export function deleteReview(productId: number) {
  return request({
    url: `/review/${productId}`,
    method: 'delete'
  })
}

// 获取商品评分信息
export function getProductRating(productId: number) {
  return request<ApiResponse<RatingInfo>>({
    url: `/review/product/${productId}/rating`,
    method: 'get'
  })
}

// 检查用户是否已评价
export function checkReview(productId: number) {
  return request<ApiResponse<{
    hasReviewed: boolean
    rating?: number
    content?: string
  }>>({
    url: `/review/product/${productId}/check`,
    method: 'get'
  })
}

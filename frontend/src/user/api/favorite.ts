import request from './request'

interface ApiResponse<T> {
  data: T
  message?: string
  code?: number
}

export interface Favorite {
  id: number
  productId: number
  productName: string
  productImage?: string
  productPrice: number
  createdAt?: string
}

// 获取收藏列表
export function getFavorites() {
  return request<ApiResponse<Favorite[]>>({
    url: '/favorites',
    method: 'get'
  })
}

// 添加收藏
export function addFavorite(productId: number) {
  return request({
    url: '/favorites',
    method: 'post',
    data: { productId }
  })
}

// 取消收藏
export function removeFavorite(productId: number) {
  return request({
    url: `/favorites/${productId}`,
    method: 'delete'
  })
}

// 切换收藏状态
export function toggleFavorite(productId: number) {
  return request({
    url: `/favorites/toggle/${productId}`,
    method: 'post'
  })
}

// 检查是否已收藏
export function checkFavorite(productId: number) {
  return request<ApiResponse<{ isFavorite: boolean }>>({
    url: `/favorites/check/${productId}`,
    method: 'get'
  })
}

// 获取收藏数量
export function getFavoriteCount() {
  return request<ApiResponse<{ count: number }>>({
    url: '/favorites/count',
    method: 'get'
  })
}

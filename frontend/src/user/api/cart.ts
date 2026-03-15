import request from './request'

export interface CartItem {
  id: number
  productId: number
  quantity: number
  price: number
  name: string
  image?: string
}

interface ApiResponse<T> {
  data: T
  message?: string
  code?: number
}

// 获取购物车
export function getCart() {
  return request<ApiResponse<CartItem[]>>({
    url: '/cart',
    method: 'get'
  })
}

// 添加到购物车
export function addToCart(productId: number, quantity: number) {
  return request({
    url: '/cart',
    method: 'post',
    data: { productId, quantity }
  })
}

// 更新购物车数量
export function updateQuantity(productId: number, quantity: number) {
  return request({
    url: `/cart/${productId}`,
    method: 'put',
    data: { quantity }
  })
}

// 从购物车移除
export function removeFromCart(productId: number) {
  return request({
    url: `/cart/${productId}`,
    method: 'delete'
  })
}

// 清空购物车
export function clearCart() {
  return request({
    url: '/cart/clear',
    method: 'post'
  })
}

import request from './request'

// 获取购物车
export function getCart() {
  return request({
    url: '/cart',
    method: 'get'
  })
}

// 添加商品到购物车
export function addToCart(productId, quantity) {
  return request({
    url: '/cart/add',
    method: 'post',
    params: { productId, quantity }
  })
}

// 更新购物车商品数量
export function updateCartItem(id, quantity) {
  return request({
    url: `/cart/update/${id}`,
    method: 'put',
    params: { quantity }
  })
}

// 删除购物车商品
export function removeCartItem(id) {
  return request({
    url: `/cart/remove/${id}`,
    method: 'delete'
  })
}

// 清空购物车
export function clearCart() {
  return request({
    url: '/cart/clear',
    method: 'delete'
  })
}

// 获取购物车统计
export function getCartStats() {
  return request({
    url: '/cart/total',
    method: 'get'
  })
}

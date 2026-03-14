import request from './request'

// 获取订单列表
export function getOrders() {
  return request({
    url: '/orders',
    method: 'get'
  })
}

// 获取订单详情
export function getOrderById(id) {
  return request({
    url: `/orders/${id}`,
    method: 'get'
  })
}

// 创建订单
export function createOrder(data) {
  return request({
    url: '/orders',
    method: 'post',
    params: { shippingAddress: data.shippingAddress }
  })
}

// 支付订单
export function payOrder(id) {
  return request({
    url: `/orders/${id}/pay`,
    method: 'post'
  })
}

// 取消订单
export function cancelOrder(id) {
  return request({
    url: `/orders/${id}/cancel`,
    method: 'post'
  })
}

// 获取订单统计
export function getOrderStats() {
  return request({
    url: '/orders/stats',
    method: 'get'
  })
}

import request from '@merchant/common/api/request'

// 获取订单列表
export function getOrderList(params) {
  return request({
    url: '/merchant/orders',
    method: 'get',
    params
  })
}

// 获取订单详情
export function getOrderDetail(id) {
  return request({
    url: `/merchant/orders/${id}`,
    method: 'get'
  })
}

// 发货
export function shipOrder(id, data) {
  return request({
    url: `/merchant/orders/${id}/ship`,
    method: 'post',
    data
  })
}

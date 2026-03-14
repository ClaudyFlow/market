import request from './request'

// 获取商品列表
export function getProducts(params) {
  return request({
    url: '/products',
    method: 'get',
    params
  })
}

// 获取商品详情
export function getProductById(id) {
  return request({
    url: `/products/${id}`,
    method: 'get'
  })
}

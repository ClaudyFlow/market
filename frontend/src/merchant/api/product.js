import request from '@merchant/common/api/request'

// 获取商品列表
export function getProductList(params) {
  return request({
    url: '/merchant/products',
    method: 'get',
    params
  })
}

// 创建商品
export function createProduct(data) {
  return request({
    url: '/merchant/products',
    method: 'post',
    data
  })
}

// 更新商品
export function updateProduct(id, data) {
  return request({
    url: `/merchant/products/${id}`,
    method: 'put',
    data
  })
}

// 删除商品
export function deleteProduct(id) {
  return request({
    url: `/merchant/products/${id}`,
    method: 'delete'
  })
}

// 上架/下架
export function toggleProductStatus(id, status) {
  return request({
    url: `/merchant/products/${id}/status`,
    method: 'put',
    data: { status }
  })
}

import request from '@merchant/api/request'

export interface Product {
  id: number
  name: string
  price: number
  originalPrice?: number
  stock?: number
  status?: number
  image?: string
  description?: string
  [key: string]: unknown
}

interface ApiResponse<T> {
  data: T
  message?: string
  code?: number
}

interface ProductListParams {
  page?: number
  size?: number
  keyword?: string
  status?: number
  [key: string]: unknown
}

// 获取商品列表
export function getProductList(params?: ProductListParams) {
  return request<ApiResponse<Product[]>>({
    url: '/merchant/products',
    method: 'get',
    params
  })
}

// 创建商品
export function createProduct(data: Partial<Product>) {
  return request({
    url: '/merchant/products',
    method: 'post',
    data
  })
}

// 更新商品
export function updateProduct(id: number, data: Partial<Product>) {
  return request({
    url: `/merchant/products/${id}`,
    method: 'put',
    data
  })
}

// 删除商品
export function deleteProduct(id: number) {
  return request({
    url: `/merchant/products/${id}`,
    method: 'delete'
  })
}

// 上架/下架
export function toggleProductStatus(id: number, status: number) {
  return request({
    url: `/merchant/products/${id}/status`,
    method: 'put',
    data: { status }
  })
}

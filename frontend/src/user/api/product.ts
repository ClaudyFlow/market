import request from './request'

export interface Product {
  id: number
  name: string
  price: number
  originalPrice: number
  image?: string
  rating?: number
  sales?: string
  type?: string
  [key: string]: unknown
}

interface ProductListParams {
  page?: number
  size?: number
  category?: string
  keyword?: string
  [key: string]: unknown
}

interface ApiResponse<T> {
  data: T
  message?: string
  code?: number
}

// 获取商品列表
export function getProducts(params?: ProductListParams) {
  return request<ApiResponse<Product[]>>({
    url: '/product',
    method: 'get',
    params
  })
}

// 获取商品详情
export function getProductById(id: number) {
  return request<ApiResponse<Product>>({
    url: `/product/${id}`,
    method: 'get'
  })
}

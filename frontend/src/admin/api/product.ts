import request from './request'

export interface Product {
  id: number
  name: string
  category: string
  price: number
  stock: number
  sales: number
  status: string
  image: string
  description?: string
  merchantId?: number
  merchantName?: string
  auditStatus?: string
  auditComment?: string
}

export interface ProductListParams {
  page?: number
  size?: number
  name?: string
  category?: string
  auditStatus?: string
  merchantId?: number
}

export interface ProductListResponse {
  list: Product[]
  total: number
}

/**
 * 获取待审核商品列表
 */
export function getAuditProductList(params: ProductListParams): Promise<ProductListResponse> {
  return request.get('/product/audit/list', { params })
}

/**
 * 审核商品
 */
export function auditProduct(id: number, data: { status: string; comment: string }): Promise<void> {
  return request.put(`/product/${id}/audit`, data)
}

/**
 * 获取商品详情
 */
export function getProductDetail(id: number): Promise<Product> {
  return request.get(`/product/${id}`)
}

/**
 * 下架商品
 */
export function removeProduct(id: number): Promise<void> {
  return request.delete(`/product/${id}`)
}

/**
 * 获取商品审核统计
 */
export function getProductAuditStats(): Promise<{
  total: number
  pending: number
  approved: number
  rejected: number
}> {
  return request.get('/product/audit/stats')
}

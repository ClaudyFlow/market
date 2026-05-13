import request from '@merchant/api/request'
import axios from 'axios'

export interface Order {
  id: number
  orderNo: string
  status: string // PENDING, PAID, SHIPPED, COMPLETED, CANCELLED, REFUNDING
  totalAmount: number
  customerName?: string
  customerPhone?: string
  customerAvatar?: string
  shippingAddress?: string
  trackingNo?: string
  carrier?: string
  items?: OrderItem[]
  createdAt?: string
  paidAt?: string
  shippedAt?: string
  completedAt?: string
  [key: string]: unknown
}

export interface OrderItem {
  id: number
  productId: number
  productName: string
  productImage?: string
  price: number
  quantity: number
  subtotal: number
}

interface ApiResponse<T> {
  code: number
  data: T
  message?: string
}

interface OrderListParams {
  page?: number
  size?: number
  status?: string
  orderNo?: string
  productName?: string
  startDate?: string
  endDate?: string
  [key: string]: unknown
}

interface OrderListResponse {
  list: Order[]
  total: number
  page: number
  size: number
}

interface OrderStats {
  total: number
  pending: number
  paid: number
  shipped: number
  completed: number
  cancelled: number
  refunding: number
}

interface ShipOrderData {
  trackingNo: string
  carrier: string
  remark?: string
}

// 获取订单列表
export function getOrderList(params?: OrderListParams): Promise<ApiResponse<OrderListResponse>> {
  return request.get('/order/list', { params })
}

// 获取订单详情
export function getOrderDetail(id: number): Promise<ApiResponse<Order>> {
  return request.get(`/order/${id}`)
}

// 获取订单统计
export function getOrderStats(): Promise<ApiResponse<OrderStats>> {
  return request.get('/order/stats')
}

// 发货
export function shipOrder(id: number, data: ShipOrderData): Promise<ApiResponse<Order>> {
  return request.post(`/order/${id}/ship`, data)
}

// 导出订单
export function exportOrders(params?: OrderListParams): Promise<Blob> {
  return request.get('/order/export', { 
    params,
    responseType: 'blob'
  })
}

// 获取退款申请列表
export function getRefundList(params?: { page?: number; size?: number }): Promise<ApiResponse<OrderListResponse>> {
  return request.get('/order/refund/list', { params })
}

// 处理退款申请
export function handleRefund(id: number, approved: boolean, reason?: string): Promise<ApiResponse<void>> {
  return request.post(`/order/${id}/refund`, { approved, reason })
}

// 获取销售趋势
export function getSalesTrend(days?: number): Promise<ApiResponse<{
  dates: string[]
  orderCounts: number[]
  orderAmounts: number[]
}>> {
  return request.get('/order/sales-trend', { params: { days } })
}

// 获取分类统计
export function getCategoryStats(): Promise<ApiResponse<Record<string, number>>> {
  return axios.get('http://localhost:8080/api/statistics/category-distribution', {
    headers: { Authorization: `Bearer ${localStorage.getItem('merchant_token')}` }
  }).then(res => res.data)
}

// 获取用户增长趋势
export function getUserGrowthTrend(days?: number): Promise<ApiResponse<{
  dates: string[]
  counts: number[]
}>> {
  return axios.get('http://localhost:8080/api/statistics/user-growth', {
    headers: { Authorization: `Bearer ${localStorage.getItem('merchant_token')}` },
    params: { days }
  }).then(res => res.data)
}

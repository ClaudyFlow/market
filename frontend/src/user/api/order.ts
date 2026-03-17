import request from './request'

export interface Order {
  id: number
  orderNo: string
  status: number
  totalAmount: number
  items: OrderItem[]
  createdAt?: string
  [key: string]: unknown
}

export interface OrderItem {
  productId: number
  productName: string
  price: number
  quantity: number
  image?: string
}

interface CreateOrderData {
  items: { productId: number; quantity: number }[]
  addressId: number
  couponId?: number
}

interface ApiResponse<T> {
  data: T
  message?: string
  code?: number
}

// 获取订单列表
export function getOrders(params?: { status?: number; page?: number; size?: number }) {
  return request<ApiResponse<Order[]>>({
    url: '/order',
    method: 'get',
    params
  })
}

// 获取订单详情
export function getOrderById(id: number) {
  return request<ApiResponse<Order>>({
    url: `/order/${id}`,
    method: 'get'
  })
}

// 创建订单
export function createOrder(data: CreateOrderData) {
  return request<ApiResponse<{ orderId: number }>>({
    url: '/order',
    method: 'post',
    data
  })
}

// 取消订单
export function cancelOrder(id: number) {
  return request({
    url: `/order/${id}/cancel`,
    method: 'post'
  })
}

// 删除订单
export function deleteOrder(id: number) {
  return request({
    url: `/order/${id}`,
    method: 'delete'
  })
}

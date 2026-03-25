import request from '@user/api/request'

export interface Order {
  id: number
  orderNo: string
  status: string // PENDING, PAID, SHIPPED, COMPLETED, CANCELLED, REFUNDING
  totalAmount: number
  shippingAddress: string
  items: OrderItem[]
  createdAt: string
  paidAt?: string
  shippedAt?: string
  completedAt?: string
  trackingNo?: string
  carrier?: string
  merchantName?: string
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
  [key: string]: unknown
}

interface OrderListResponse {
  list: Order[]
  total: number
  page: number
  size: number
}

interface CreateOrderData {
  items: { productId: number; quantity: number }[]
  addressId: number
  couponId?: number
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

// 获取订单列表
export function getOrderList(params?: OrderListParams): Promise<ApiResponse<OrderListResponse>> {
  return request.get('/order/list', { params })
}

// 获取订单详情
export function getOrderDetail(id: number): Promise<ApiResponse<Order>> {
  return request.get(`/order/${id}`)
}

// 创建订单
export function createOrder(data: CreateOrderData): Promise<ApiResponse<Order>> {
  return request.post('/order', data)
}

// 取消订单
export function cancelOrder(id: number): Promise<ApiResponse<void>> {
  return request.post(`/order/${id}/cancel`)
}

// 删除订单
export function deleteOrder(id: number): Promise<ApiResponse<void>> {
  return request.delete(`/order/${id}`)
}

// 支付订单
export function payOrder(id: number, paymentMethod: string): Promise<ApiResponse<Order>> {
  return request.post(`/order/${id}/pay`, { paymentMethod })
}

// 确认收货
export function confirmReceive(id: number): Promise<ApiResponse<Order>> {
  return request.post(`/order/${id}/confirm`)
}

// 申请退款
export function applyRefund(id: number, reason: string, images?: string[]): Promise<ApiResponse<void>> {
  return request.post(`/order/${id}/refund`, { reason, images })
}

// 获取订单统计
export function getOrderStats(): Promise<ApiResponse<OrderStats>> {
  return request.get('/order/stats')
}

// 获取物流信息
export function getTrackingInfo(id: number): Promise<ApiResponse<{ trackingNo: string; carrier: string; records: TrackingRecord[] }>> {
  return request.get(`/order/${id}/tracking`)
}

export interface TrackingRecord {
  time: string
  desc: string
}

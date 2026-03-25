import request from '@admin/api/request'

export interface Order {
  id: number
  orderNo: string
  userId: number
  userAvatar?: string
  userName: string
  userPhone?: string
  merchantId?: number
  merchantName?: string
  shopName?: string
  amount: number
  status: string // PENDING, PAID, SHIPPED, COMPLETED, CANCELLED, REFUNDING
  paymentMethod?: string // ALIPAY, WECHAT, CARD
  shippingAddress: string
  trackingNo?: string
  carrier?: string
  items: OrderItem[]
  createTime: string
  paidTime?: string
  shippedTime?: string
  completedTime?: string
  cancelledTime?: string
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

export interface OrderListParams {
  page?: number
  size?: number
  orderNo?: string
  status?: string
  userId?: number
  merchantId?: number
  shopName?: string
  productName?: string
  startDate?: string
  endDate?: string
  paymentMethod?: string
  [key: string]: unknown
}

export interface OrderListResponse {
  list: Order[]
  total: number
  page: number
  size: number
}

export interface OrderStats {
  total: number
  totalAmount: number
  pending: number
  paid: number
  shipped: number
  completed: number
  cancelled: number
  refunding: number
  todayOrders: number
  todayAmount: number
}

export interface OrderTrend {
  date: string
  orders: number
  amount: number
}

/**
 * 获取订单列表
 */
export function getOrderList(params: OrderListParams): Promise<OrderListResponse> {
  return request.get('/order/list', { params })
}

/**
 * 获取订单详情
 */
export function getOrderDetail(id: number): Promise<Order> {
  return request.get(`/order/${id}`)
}

/**
 * 更新订单状态
 */
export function updateOrderStatus(id: number, status: string, remark?: string): Promise<void> {
  return request.put(`/order/${id}/status`, { status, remark })
}

/**
 * 获取订单统计
 */
export function getOrderStats(): Promise<OrderStats> {
  return request.get('/order/stats')
}

/**
 * 获取订单趋势
 */
export function getOrderTrend(days?: number): Promise<OrderTrend[]> {
  return request.get('/order/trend', { params: { days } })
}

/**
 * 获取商品排行
 */
export function getProductRank(params?: { type?: string; limit?: number }): Promise<{ rank: number; productName: string; productImage?: string; sales: number; revenue: number; trend: number }[]> {
  return request.get('/order/product/rank', { params })
}

/**
 * 获取店铺排行
 */
export function getShopRank(params?: { limit?: number }): Promise<{ rank: number; shopName: string; revenue: number; trend: number }[]> {
  return request.get('/order/shop/rank', { params })
}

/**
 * 导出订单
 */
export function exportOrders(params?: OrderListParams): Promise<Blob> {
  return request.get('/order/export', { 
    params,
    responseType: 'blob'
  })
}

/**
 * 处理退款申请
 */
export function handleRefund(id: number, approved: boolean, reason?: string): Promise<void> {
  return request.post(`/order/${id}/refund`, { approved, reason })
}

/**
 * 获取退款申请列表
 */
export function getRefundList(params?: { page?: number; size?: number; status?: string }): Promise<OrderListResponse> {
  return request.get('/order/refund/list', { params })
}

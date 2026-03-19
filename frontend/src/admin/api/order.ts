import request from './request'

export interface Order {
  id: number
  orderNo: string
  userId: number
  userName: string
  merchantId: number
  merchantName: string
  amount: number
  status: string
  createTime: string
  items: OrderItem[]
}

export interface OrderItem {
  productId: number
  productName: string
  productImage: string
  price: number
  quantity: number
}

export interface OrderListParams {
  page?: number
  size?: number
  orderNo?: string
  status?: string
  merchantId?: number
  startDate?: string
  endDate?: string
}

export interface OrderListResponse {
  list: Order[]
  total: number
}

export interface OrderStats {
  total: number
  pending: number
  paid: number
  shipped: number
  completed: number
  cancelled: number
  refunding: number
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
export function updateOrderStatus(id: number, status: string): Promise<void> {
  return request.put(`/order/${id}/status`, { status })
}

/**
 * 获取订单统计
 */
export function getOrderStats(): Promise<OrderStats> {
  return request.get('/order/stats')
}

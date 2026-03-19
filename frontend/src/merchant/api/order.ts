import request from '@merchant/api/request'

export interface Order {
  id: number
  orderNo: string
  status: number
  totalAmount: number
  customerName?: string
  customerPhone?: string
  shippingAddress?: string
  items?: OrderItem[]
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

interface ApiResponse<T> {
  data: T
  message?: string
  code?: number
}

interface OrderListParams {
  page?: number
  size?: number
  status?: number
  keyword?: string
  [key: string]: unknown
}

interface ShipOrderData {
  trackingNo?: string
  carrier?: string
  [key: string]: unknown
}

// 获取订单列表
export function getOrderList(params?: OrderListParams) {
  return request<ApiResponse<Order[]>>({
    url: '/merchant/order',
    method: 'get',
    params
  })
}

// 获取订单详情
export function getOrderDetail(id: number) {
  return request<ApiResponse<Order>>({
    url: `/merchant/order/${id}`,
    method: 'get'
  })
}

// 发货
export function shipOrder(id: number, data: ShipOrderData) {
  return request({
    url: `/merchant/order/${id}/ship`,
    method: 'post',
    data
  })
}

/**
 * 订单相关类型定义
 */

import type { Address } from './common'
import type { ProductSku } from './product'

// 订单信息
export interface Order {
  id: number
  orderNo: string
  status: OrderStatus
  totalAmount: number
  payAmount: number
  freightAmount: number
  discountAmount: number
  itemCount: number
  createTime: string
  payTime?: string
  deliveryTime?: string
  receiveTime?: string
  shopId?: number
  shopName?: string
  items: OrderItem[]
}

// 订单状态
export type OrderStatus = 
  | 'pending_payment'     // 待支付
  | 'paid'                // 已支付
  | 'shipped'             // 已发货
  | 'delivered'           // 已送达
  | 'completed'           // 已完成
  | 'cancelled'           // 已取消
  | 'refunding'           // 退款中
  | 'refunded'            // 已退款
  | 'closed'              // 已关闭

// 订单项
export interface OrderItem {
  id: number
  orderId: number
  productId: number
  skuId?: number
  productName: string
  productImage: string
  specs?: string
  price: number
  quantity: number
  totalAmount: number
  reviewStatus?: 'not_reviewed' | 'reviewed' | 'reviewing'
}

// 订单详情
export interface OrderDetail extends Order {
  shippingAddress: Address
  paymentInfo?: PaymentInfo
  logistics?: OrderLogistics
  invoice?: InvoiceInfo
  remark?: string
  cancelReason?: string
  refundInfo?: RefundInfo
}

// 支付信息
export interface PaymentInfo {
  payType: 'alipay' | 'wechat' | 'card' | 'balance'
  payTime?: string
  payAmount: number
  transactionId?: string
}

// 物流信息
export interface OrderLogistics {
  company: string
  companyCode: string
  trackingNo: string
  status: string
  statusDesc: string
  traces: LogisticsTrace[]
  estimatedDelivery?: string
}

// 物流轨迹
export interface LogisticsTrace {
  time: string
  status: string
  description: string
  location?: string
}

// 发票信息
export interface InvoiceInfo {
  type: 'electronic' | 'paper' | 'none'
  title?: string
  taxNo?: string
  content?: string
  email?: string
}

// 退款信息
export interface RefundInfo {
  status: 'pending' | 'approved' | 'rejected' | 'success'
  amount: number
  reason: string
  images?: string[]
  applyTime: string
  handleTime?: string
  remark?: string
}

// 订单操作
export interface OrderAction {
  type: 'pay' | 'cancel' | 'confirm' | 'delete' | 'review' | 'refund'
  label: string
  enabled: boolean
}

// 订单统计
export interface OrderStats {
  unpaid: number
  unshipped: number
  unreceived: number
  reviewed: number
  totalAmount: number
}

// 创建订单参数
export interface CreateOrderParams {
  items?: { cartItemId?: number; productId?: number; skuId?: number; quantity: number }[]
  addressId: number
  couponId?: number
  remark?: string
  deliveryType?: 'express' | 'pickup' | 'virtual'
  invoice?: InvoiceInfo
}

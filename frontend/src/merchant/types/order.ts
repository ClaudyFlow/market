/**
 * 商家端订单相关类型定义
 */

/** 订单状态 */
export type OrderStatus = 'pending' | 'paid' | 'shipped' | 'completed' | 'cancelled' | 'refunding'

/** 订单项 */
export interface OrderItem {
  id?: number
  orderNo: string
  productImage: string
  productName: string
  customerName: string
  amount: number
  orderTime: string
  status: OrderStatus
}

/** 订单筛选表单 */
export interface OrderFilterForm {
  orderNo: string
  productName: string
  status: OrderStatus | ''
  dateRange: [Date, Date] | null
}

/** 订单统计 */
export interface OrderStats {
  all: number
  pending: number
  paid: number
  shipped: number
  completed: number
}

/** 分页参数 */
export interface Pagination {
  currentPage: number
  pageSize: number
  total: number
}

/** 发货表单 */
export interface ShippingForm {
  company: string
  trackingNo: string
  remark: string
}

/** 物流公司选项 */
export interface LogisticsCompany {
  value: string
  label: string
}

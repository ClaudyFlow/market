/**
 * 订单相关 API
 */

import { get, post, put, del } from './request'
import type { Order, OrderDetail, OrderItem, OrderLogistics } from '@user/types/order'
import type { PageData, PageParams } from './request'

const BASE_URL = '/order'

/**
 * 创建订单
 */
export function createOrder(data: {
  items?: { cartItemId?: number; productId?: number; skuId?: number; quantity: number }[]
  addressId: number
  couponId?: number
  remark?: string
  deliveryType?: 'express' | 'pickup' | 'virtual'
}): Promise<Order> {
  return post(BASE_URL, data)
}

/**
 * 获取订单列表
 */
export function getOrderList(params?: PageParams & { status?: string }): Promise<PageData<Order>> {
  return get(BASE_URL, params)
}

/**
 * 获取订单详情
 */
export function getOrderDetail(orderId: number | string): Promise<OrderDetail> {
  return get(`${BASE_URL}/${orderId}`)
}

/**
 * 取消订单
 */
export function cancelOrder(orderId: number | string, reason?: string): Promise<void> {
  return put(`${BASE_URL}/${orderId}/cancel`, { reason })
}

/**
 * 确认收货
 */
export function confirmReceive(orderId: number | string): Promise<void> {
  return put(`${BASE_URL}/${orderId}/confirm`)
}

/**
 * 删除订单
 */
export function deleteOrder(orderId: number | string): Promise<void> {
  return del(`${BASE_URL}/${orderId}`)
}

/**
 * 订单支付
 */
export function payOrder(orderId: number | string, payType?: 'alipay' | 'wechat' | 'card'): Promise<{ payUrl: string }> {
  return post(`${BASE_URL}/${orderId}/pay`, { payType })
}

/**
 * 获取订单支付状态
 */
export function getPayStatus(orderId: number | string): Promise<{ paid: boolean; payTime?: string }> {
  return get(`${BASE_URL}/${orderId}/pay-status`)
}

/**
 * 订单退款
 */
export function refundOrder(orderId: number | string, reason: string, images?: string[]): Promise<void> {
  return post(`${BASE_URL}/${orderId}/refund`, { reason, images })
}

/**
 * 获取退款详情
 */
export function getRefundDetail(orderId: number | string): Promise<{ status: string; amount: number; reason: string }> {
  return get(`${BASE_URL}/${orderId}/refund`)
}

/**
 * 订单评价
 */
export function submitReview(orderId: number | string, data: {
  score: number
  content?: string
  images?: string[]
  items?: { itemId: number; score: number; content?: string; images?: string[] }[]
}): Promise<void> {
  return post(`${BASE_URL}/${orderId}/review`, data)
}

/**
 * 检查是否已评价
 */
export function checkReviewed(orderId: number | string): Promise<{ reviewed: boolean }> {
  return get(`${BASE_URL}/${orderId}/reviewed`)
}

/**
 * 获取订单物流
 */
export function getOrderLogistics(orderId: number | string): Promise<OrderLogistics> {
  return get(`${BASE_URL}/${orderId}/logistics`)
}

/**
 * 修改订单地址
 */
export function updateOrderAddress(orderId: number | string, addressId: number): Promise<void> {
  return put(`${BASE_URL}/${orderId}/address`, { addressId })
}

/**
 * 订单备注
 */
export function updateOrderRemark(orderId: number | string, remark: string): Promise<void> {
  return put(`${BASE_URL}/${orderId}/remark`, { remark })
}

/**
 * 再次购买
 */
export function repurchase(orderId: number | string): Promise<void> {
  return post(`${BASE_URL}/${orderId}/repurchase`)
}

/**
 * 获取订单统计
 */
export function getOrderStats(): Promise<{
  unpaid: number
  unshipped: number
  unreceived: number
  reviewed: number
}> {
  return get(`${BASE_URL}/stats`)
}

/**
 * 模拟发货（测试用）
 */
export function mockShip(orderId: number | string): Promise<void> {
  return post(`${BASE_URL}/${orderId}/mock-ship`)
}

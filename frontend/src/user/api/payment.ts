/**
 * 支付系统 API
 */

import { get, post } from './request'

const BASE_URL = '/payment'

/**
 * 创建支付单
 */
export function createPayment(orderNo: string, paymentMethod: string): Promise<{
  paymentNo: string
  amount: number
  qrCode?: string
}> {
  return post(`${BASE_URL}/create`, { orderNo, paymentMethod })
}

/**
 * 模拟支付（测试用）
 */
export function mockPay(paymentNo: string): Promise<{ success: boolean }> {
  return post(`${BASE_URL}/mock-pay/${paymentNo}`)
}

/**
 * 查询支付状态
 */
export function getPaymentStatus(paymentNo: string): Promise<{
  paymentNo: string
  status: 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED'
  paidTime?: string
}> {
  return get(`${BASE_URL}/status/${paymentNo}`)
}

/**
 * 根据订单号查询支付单
 */
export function getPaymentByOrder(orderNo: string): Promise<{
  paymentNo: string
  amount: number
  status: string
  paymentMethod: string
}> {
  return get(`${BASE_URL}/order/${orderNo}`)
}

/**
 * 申请退款
 */
export function applyRefund(orderNo: string, reason: string): Promise<{
  refundNo: string
  amount: number
}> {
  return post(`${BASE_URL}/refund`, { orderNo, reason })
}

/**
 * 查询退款状态
 */
export function getRefundStatus(refundNo: string): Promise<{
  refundNo: string
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'REFUNDED'
}> {
  return get(`${BASE_URL}/refund/status/${refundNo}`)
}

/**
 * 获取用户退款列表
 */
export function getRefundList(params?: { page?: number; size?: number }): Promise<{
  list: any[]
  total: number
}> {
  return get(`${BASE_URL}/refund/list`, params)
}

/**
 * 审核退款（商家/管理员）
 */
export function approveRefund(refundNo: string, approved: boolean, remark?: string): Promise<void> {
  return post(`${BASE_URL}/refund/approve`, { refundNo, approved, remark })
}
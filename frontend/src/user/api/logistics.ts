/**
 * 物流系统 API
 */

import { get, post } from './request'

const BASE_URL = '/logistics'

/**
 * 获取快递公司列表
 */
export function getLogisticsCompanies(): Promise<{
  code: string
  name: string
}[]> {
  return get(`${BASE_URL}/companies`)
}

/**
 * 根据订单ID查询物流
 */
export function getLogisticsByOrder(orderId: number): Promise<{
  trackingNo: string
  companyCode: string
  companyName: string
  status: string
  estimatedDelivery?: string
}> {
  return get(`${BASE_URL}/order/${orderId}`)
}

/**
 * 查询物流轨迹
 */
export function getLogisticsTrack(trackingNo: string, companyCode?: string): Promise<{
  trackingNo: string
  companyName: string
  currentStatus: string
  tracks: {
    time: string
    location: string
    description: string
  }[]
}> {
  return post(`${BASE_URL}/track`, { trackingNo, companyCode })
}

/**
 * 生成模拟物流（测试用）
 */
export function generateMockLogistics(orderId: number): Promise<{
  trackingNo: string
  companyCode: string
}> {
  return post(`${BASE_URL}/mock-generate/${orderId}`)
}
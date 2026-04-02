/**
 * 统计相关 API
 */

import { get } from './request'

const BASE_URL = '/statistics'

/**
 * 获取首页统计概览
 */
export function getOverview(): Promise<{
  totalProducts: number
  totalShops: number
  totalUsers: number
  totalOrders: number
  todaySales: number
}> {
  return get(`${BASE_URL}/overview`)
}

/**
 * 获取商品统计
 */
export function getProductStats(): Promise<{
  totalProducts: number
  onSaleProducts: number
  offSaleProducts: number
  avgPrice: number
  hotProducts: { id: number; name: string; sales: number }[]
}> {
  return get(`${BASE_URL}/products`)
}

/**
 * 获取订单统计
 */
export function getOrderStats(): Promise<{
  totalOrders: number
  pendingOrders: number
  completedOrders: number
  cancelledOrders: number
  totalAmount: number
  todayAmount: number
}> {
  return get(`${BASE_URL}/orders`)
}

/**
 * 获取用户统计
 */
export function getUserStats(): Promise<{
  totalUsers: number
  todayNewUsers: number
  activeUsers: number
  vipUsers: number
}> {
  return get(`${BASE_URL}/users`)
}

/**
 * 获取销售趋势
 */
export function getSalesTrend(days?: number): Promise<{ date: string; amount: number; orders: number }[]> {
  return get(`${BASE_URL}/sales-trend`, { days })
}

/**
 * 获取类目占比
 */
export function getCategoryDistribution(): Promise<{ name: string; value: number }[]> {
  return get(`${BASE_URL}/category-distribution`)
}

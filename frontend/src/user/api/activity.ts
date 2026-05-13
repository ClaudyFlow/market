import { get } from './request'
import type { PageParams } from './request'

const BASE_URL = '/activity'

export interface Activity {
  id: number
  name: string
  type: string
  description?: string
  image?: string
  link?: string
  startTime: string
  endTime: string
  status: string
  discount?: number
  discountType?: string
  maxQuantity?: number
  usedQuantity?: number
  maxPerUser?: number
  productId?: number
  tags?: string
}

export interface ActivityListResponse {
  list: Activity[]
  total: number
  page: number
  size: number
}

export function getActivityList(params?: PageParams & { type?: string }): Promise<{ data: ActivityListResponse }> {
  return get(BASE_URL, params)
}

export function getActiveActivities(): Promise<{ data: Activity[] }> {
  return get(`${BASE_URL}/active`)
}

export function getActivityDetail(id: number): Promise<{ data: Activity }> {
  return get(`${BASE_URL}/${id}`)
}

export function getProductActivities(productId: number): Promise<{ data: Activity[] }> {
  return get(`${BASE_URL}/product/${productId}`)
}
import request from '@merchant/api/request'

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
  merchantId?: number
  createdAt?: string
  updatedAt?: string
}

interface ApiResponse<T> {
  code: number
  data: T
  message?: string
}

interface ActivityListParams {
  page?: number
  size?: number
  type?: string
  status?: string
  keyword?: string
}

interface ActivityListResponse {
  list: Activity[]
  total: number
  page: number
  size: number
}

export function getActivityList(params?: ActivityListParams): Promise<ApiResponse<ActivityListResponse>> {
  return request.get('/activity', { params })
}

export function getActivityDetail(id: number): Promise<ApiResponse<Activity>> {
  return request.get(`/activity/${id}`)
}

export function createActivity(data: Partial<Activity>): Promise<ApiResponse<Activity>> {
  return request.post('/activity', data)
}

export function updateActivity(id: number, data: Partial<Activity>): Promise<ApiResponse<Activity>> {
  return request.put(`/activity/${id}`, data)
}

export function deleteActivity(id: number): Promise<ApiResponse<void>> {
  return request.delete(`/activity/${id}`)
}

export function publishActivity(id: number): Promise<ApiResponse<Activity>> {
  return request.post(`/activity/${id}/publish`)
}

export function pauseActivity(id: number): Promise<ApiResponse<Activity>> {
  return request.post(`/activity/${id}/pause`)
}

export function endActivity(id: number): Promise<ApiResponse<Activity>> {
  return request.post(`/activity/${id}/end`)
}
import request from './request'

export interface Merchant {
  id: number
  shopName: string
  ownerName: string
  phone: string
  category: string
  joinTime: string
  status: string
  logo?: string
  description?: string
  businessLicense?: string
  license?: string
}

export interface MerchantListParams {
  page?: number
  size?: number
  merchantId?: string
  shopName?: string
  status?: string
  startDate?: string
  endDate?: string
}

export interface MerchantListResponse {
  list: Merchant[]
  total: number
}

export interface MerchantStats {
  total: number
  approved: number
  pending: number
  rejected: number
}

/**
 * 获取商家列表
 */
export function getMerchantList(params: MerchantListParams): Promise<MerchantListResponse> {
  return request.get('/merchant/list', { params })
}

/**
 * 获取商家详情
 */
export function getMerchantDetail(id: number): Promise<Merchant> {
  return request.get(`/merchant/${id}`)
}

/**
 * 审核商家
 */
export function auditMerchant(id: number, data: { status: string; comment: string }): Promise<void> {
  return request.put(`/merchant/${id}/audit`, data)
}

/**
 * 封禁商家
 */
export function banMerchant(id: number): Promise<void> {
  return request.put(`/merchant/${id}/ban`)
}

/**
 * 解封商家
 */
export function unbanMerchant(id: number): Promise<void> {
  return request.put(`/merchant/${id}/unban`)
}

/**
 * 获取商家统计
 */
export function getMerchantStats(): Promise<MerchantStats> {
  return request.get('/merchant/stats')
}

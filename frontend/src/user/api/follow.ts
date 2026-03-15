import request from './request'

interface ApiResponse<T> {
  data: T
  message?: string
  code?: number
}

export interface Follow {
  id: number
  shopId: number
  shopName: string
  shopAvatar?: string
  createdAt?: string
}

// 获取关注列表
export function getFollows() {
  return request<ApiResponse<Follow[]>>({
    url: '/follows',
    method: 'get'
  })
}

// 添加关注
export function addFollow(shopId: number, shopName: string, shopAvatar?: string) {
  return request({
    url: '/follows',
    method: 'post',
    data: { shopId, shopName, shopAvatar }
  })
}

// 取消关注
export function removeFollow(shopId: number) {
  return request({
    url: `/follows/${shopId}`,
    method: 'delete'
  })
}

// 切换关注状态
export function toggleFollow(shopId: number, shopName: string, shopAvatar?: string) {
  return request({
    url: `/follows/toggle/${shopId}`,
    method: 'post',
    data: { shopId, shopName, shopAvatar }
  })
}

// 检查是否已关注
export function checkFollow(shopId: number) {
  return request<ApiResponse<{ isFavorite: boolean }>>({
    url: `/follows/check/${shopId}`,
    method: 'get'
  })
}

// 获取关注数量
export function getFollowCount() {
  return request<ApiResponse<{ count: number }>>({
    url: '/follows/count',
    method: 'get'
  })
}

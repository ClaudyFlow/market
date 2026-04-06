/**
 * 收藏相关 API
 */

import { get, post, put, del } from './request'
import type { Favorite, FavoriteGroup } from '@user/types/favorite'
import type { PageData, PageParams } from './request'

const BASE_URL = '/favorite'

/**
 * 获取收藏列表
 */
export function getFavoriteList(params?: PageParams & { type?: 'product' | 'shop' | 'article' }): Promise<PageData<Favorite>> {
  return get(BASE_URL, params)
}

/**
 * 添加收藏
 */
export function addFavorite(data: {
  type: 'product' | 'shop' | 'article'
  itemId: number | string
  name: string
  image?: string
  groupId?: number
}): Promise<Favorite> {
  return post(BASE_URL, data)
}

/**
 * 取消收藏
 */
export function removeFavorite(favoriteId: number | string): Promise<void> {
  return del(`${BASE_URL}/${favoriteId}`)
}

/**
 * 批量取消收藏
 */
export function batchRemoveFavorites(favoriteIds: number[]): Promise<void> {
  return del(`${BASE_URL}/batch`, { ids: favoriteIds })
}

/**
 * 检查是否已收藏
 */
export function checkFavorite(type: 'product' | 'shop' | 'article', itemId: number | string): Promise<{ favorite: boolean; favoriteId?: number }> {
  return get(`${BASE_URL}/check`, { type, itemId })
}

/**
 * 获取收藏数
 */
export function getFavoriteCount(type?: 'product' | 'shop' | 'article'): Promise<{ count: number }> {
  return get(`${BASE_URL}/count`, { type })
}

/**
 * 创建收藏分组
 */
export function createGroup(name: string, description?: string): Promise<FavoriteGroup> {
  return post(`${BASE_URL}/group`, { name, description })
}

/**
 * 获取收藏分组列表
 */
export function getGroupList(): Promise<FavoriteGroup[]> {
  return get(`${BASE_URL}/groups`)
}

/**
 * 更新收藏分组
 */
export function updateGroup(groupId: number, data: { name?: string; description?: string }): Promise<void> {
  return put(`${BASE_URL}/group/${groupId}`, data)
}

/**
 * 删除收藏分组
 */
export function deleteGroup(groupId: number): Promise<void> {
  return del(`${BASE_URL}/group/${groupId}`)
}

/**
 * 移动收藏到分组
 */
export function moveToGroup(favoriteId: number, groupId?: number): Promise<void> {
  return put(`${BASE_URL}/${favoriteId}/group`, { groupId })
}

/**
 * 批量移动收藏
 */
export function batchMoveToFavorites(favoriteIds: number[], groupId?: number): Promise<void> {
  return put(`${BASE_URL}/batch-move`, { ids: favoriteIds, groupId })
}

/**
 * 获取分组收藏统计
 */
export function getGroupStats(groupId: number): Promise<{ total: number; products: number; shops: number }> {
  return get(`${BASE_URL}/group/${groupId}/stats`)
}

/**
 * 切换收藏状态（添加/取消）
 */
export async function toggleFavorite(type: 'product' | 'shop' | 'article', itemId: number | string): Promise<{ favorited: boolean }> {
  const { favorite } = await checkFavorite(type, itemId)
  
  if (favorite) {
    // 已收藏，取消收藏
    const list = await getFavoriteList({ type })
    const item = list.records.find(r => r.itemId === itemId)
    if (item) {
      await removeFavorite(item.id)
    }
    return { favorited: false }
  } else {
    // 未收藏，添加收藏
    await addFavorite({ type, itemId, name: '', image: '' })
    return { favorited: true }
  }
}

/**
 * 关注/粉丝相关 API
 */

import { get, post, del } from './request'
import type { Follow, Follower } from '@user/types/follow'
import type { PageData, PageParams } from './request'

const BASE_URL = '/follow'

/**
 * 获取关注列表
 */
export function getFollowingList(userId?: number | string, params?: PageParams): Promise<PageData<Follow>> {
  return get(`${BASE_URL}/following`, { userId, ...params })
}

/**
 * 获取粉丝列表
 */
export function getFollowerList(userId?: number | string, params?: PageParams): Promise<PageData<Follower>> {
  return get(`${BASE_URL}/followers`, { userId, ...params })
}

/**
 * 关注用户/店铺
 */
export function follow(targetId: number | string, type: 'user' | 'shop'): Promise<void> {
  return post(BASE_URL, { targetId, type })
}

/**
 * 取消关注
 */
export function unfollow(targetId: number | string, type: 'user' | 'shop'): Promise<void> {
  return del(BASE_URL, { targetId, type })
}

/**
 * 检查是否已关注
 */
export function checkFollowing(targetId: number | string, type: 'user' | 'shop'): Promise<{ following: boolean }> {
  return get(`${BASE_URL}/check`, { targetId, type })
}

/**
 * 获取关注数
 */
export function getFollowingCount(userId?: number | string): Promise<{ count: number }> {
  return get(`${BASE_URL}/following-count`, { userId })
}

/**
 * 获取粉丝数
 */
export function getFollowerCount(userId?: number | string): Promise<{ count: number }> {
  return get(`${BASE_URL}/follower-count`, { userId })
}

/**
 * 互相关注列表（互粉）
 */
export function getMutualFollowing(params?: PageParams): Promise<PageData<Follow>> {
  return get(`${BASE_URL}/mutual`, params)
}

/**
 * 可能认识的人
 */
export function getSuggestedFollows(limit?: number): Promise<Follow[]> {
  return get(`${BASE_URL}/suggestions`, { limit })
}

/**
 * 检查是否已关注（checkFollowing 的别名）
 */
export const checkFollow = checkFollowing

/**
 * 切换关注状态
 */
export async function toggleFollow(targetId: number | string, type: 'user' | 'shop'): Promise<{ followed: boolean }> {
  const { following } = await checkFollowing(targetId, type)
  
  if (following) {
    await unfollow(targetId, type)
    return { followed: false }
  } else {
    await follow(targetId, type)
    return { followed: true }
  }
}

/**
 * 关注/粉丝相关类型定义
 */

// 关注信息
export interface Follow {
  id: number
  type: 'user' | 'shop'
  targetId: number
  targetName: string
  targetAvatar?: string
  targetDescription?: string
  followerCount: number
  followingCount: number
  isFollowing: boolean
  isMutual: boolean
  followTime: string
  extra?: Record<string, any>
}

// 粉丝信息
export interface Follower {
  id: number
  userId: number
  userName: string
  userAvatar?: string
  userLevel?: number
  bio?: string
  following: boolean
  followTime: string
}

// 关注统计
export interface FollowStats {
  followingCount: number
  followerCount: number
  mutualCount: number
}

// 关注建议
export interface FollowSuggestion {
  id: number
  type: 'user' | 'shop'
  targetId: number
  targetName: string
  targetAvatar?: string
  reason: string
  commonFollowings?: number
}

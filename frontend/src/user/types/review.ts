/**
 * 评价相关类型定义
 */

import type { ImageInfo } from './common'

// 评价信息
export interface Review {
  id: number
  orderId: number
  productId: number
  productName?: string
  productImage?: string
  userId: number
  userName: string
  userAvatar?: string
  userLevel?: number
  score: number
  content?: string
  images?: ImageInfo[]
  videos?: ReviewVideo[]
  specs?: string
  anonymous: boolean
  status: 'pending' | 'approved' | 'rejected'
  createTime: string
  updateTime?: string
  likeCount: number
  replyCount: number
  reply?: ReviewReply
  additional?: AdditionalReview
  tags?: string[]
  useful: boolean
}

// 评价视频
export interface ReviewVideo {
  url: string
  cover: string
  duration: number
}

// 评价回复
export interface ReviewReply {
  id: number
  reviewId: number
  replierId: number
  replierName: string
  replierType: 'merchant' | 'admin' | 'user'
  content: string
  createTime: string
}

// 追评
export interface AdditionalReview {
  id: number
  reviewId: number
  content: string
  images?: ImageInfo[]
  createTime: string
  daysAfterPurchase: number
}

// 评价统计
export interface ReviewStats {
  totalCount: number
  averageScore: number
  scoreDistribution: {
    score: number
    count: number
    percentage: number
  }[]
  withImageCount: number
  withVideoCount: number
  additionalCount: number
  goodCount: number
  normalCount: number
  badCount: number
  goodRate: number
}

// 评价标签
export interface ReviewTag {
  tag: string
  count: number
  type: 'positive' | 'negative' | 'neutral'
}

// 评价参数
export interface CreateReviewParams {
  orderId: number
  productId?: number
  itemId?: number
  score: number
  content?: string
  images?: string[]
  videos?: string[]
  anonymous?: boolean
  tags?: string[]
}

// 评价列表查询参数
export interface ReviewQueryParams {
  productId?: number
  userId?: number
  orderId?: number
  score?: number
  hasImage?: boolean
  hasVideo?: boolean
  hasAdditional?: boolean
  tag?: string
  sort?: 'default' | 'time' | 'score' | 'useful'
}

/**
 * 论坛相关类型定义
 */

import type { ImageInfo } from './common'

// 论坛版块
export interface ForumBoard {
  id: number
  name: string
  description: string
  icon?: string
  banner?: string
  parentId?: number
  sortOrder: number
  postCount: number
  todayPostCount: number
  lastPost?: LastPostInfo
  moderators: ForumModerator[]
  children?: ForumBoard[]
}

// 最后发帖信息
export interface LastPostInfo {
  id: number
  title: string
  authorId: number
  authorName: string
  createTime: string
}

// 版主信息
export interface ForumModerator {
  id: number
  name: string
  avatar?: string
  level: 'super' | 'main' | 'assistant'
}

// 帖子信息
export interface ForumPost {
  id: number
  boardId: number
  boardName?: string
  title: string
  content: string
  authorId: number
  authorName: string
  authorAvatar?: string
  authorLevel?: number
  viewCount: number
  replyCount: number
  likeCount: number
  favoriteCount: number
  status: 'normal' | 'pinned' | 'essence' | 'locked' | 'deleted'
  tags?: string[]
  images?: ImageInfo[]
  createTime: string
  updateTime?: string
  lastReplyTime?: string
  lastReplyBy?: string
  isLiked: boolean
  isFavorited: boolean
}

// 帖子详情
export interface ForumPostDetail extends ForumPost {
  contentHtml?: string
  attachments?: ForumAttachment[]
  relatedPosts?: ForumPost[]
}

// 论坛评论
export interface ForumComment {
  id: number
  postId: number
  parentId?: number
  authorId: number
  authorName: string
  authorAvatar?: string
  authorLevel?: number
  content: string
  images?: ImageInfo[]
  likeCount: number
  replyCount: number
  status: 'normal' | 'deleted'
  createTime: string
  replies?: ForumComment[]
}

// 论坛附件
export interface ForumAttachment {
  id: number
  name: string
  type: 'image' | 'video' | 'file'
  url: string
  size: number
  downloadCount: number
}

// 帖子操作
export interface ForumAction {
  type: 'edit' | 'delete' | 'pin' | 'essence' | 'lock' | 'move'
  label: string
  enabled: boolean
}

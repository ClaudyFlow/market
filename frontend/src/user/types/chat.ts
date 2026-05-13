/**
 * 聊天相关类型定义
 */

// 聊天会话
export interface ChatSession {
  id: number
  type: 'user' | 'shop' | 'system'
  targetId: number
  targetName: string
  targetAvatar?: string
  targetOnline: boolean
  lastMessage?: ChatMessage
  unreadCount: number
  pinned: boolean
  updateTime: string
  extra?: Record<string, any>
}

// 聊天消息
export interface ChatMessage {
  id: number
  sessionId: number
  senderId: number
  senderName: string
  senderAvatar?: string
  receiverId: number
  type: 'text' | 'image' | 'file' | 'system' | 'order'
  content: string
  images?: string[]
  files?: ChatFile[]
  status: 'sent' | 'delivered' | 'read' | 'failed'
  createTime: string
  readTime?: string
  extra?: Record<string, any>
}

// 聊天文件
export interface ChatFile {
  name: string
  url: string
  size: number
  type: string
}

// 消息发送参数
export interface SendMessageParams {
  sessionId: number
  content: string
  type: 'text' | 'image' | 'file'
  images?: string[]
  files?: ChatFile[]
}

// 客服信息
export interface CustomerService {
  id: number
  name: string
  avatar: string
  status: 'online' | 'offline' | 'busy'
  greeting: string
  workingHours: string
  avgResponseTime: number
}

// 聊天记录查询参数
export interface ChatHistoryParams {
  sessionId: number
  startTime?: string
  endTime?: string
  type?: string
  page?: number
  size?: number
}

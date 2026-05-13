/**
 * 客服聊天 API
 */
import request from './request'

export interface ChatMessage {
  id?: number
  senderId: number
  receiverId: number
  content: string
  type: 'TEXT' | 'IMAGE' | 'SYSTEM' | 'FILE'
  isRead?: boolean
  createdAt?: string
}

export interface ChatMessageRequest {
  receiverId: number
  content: string
  type: string
}

export interface ChatMessageResponse {
  id: number
  senderId: number
  receiverId: number
  content: string
  type: string
  isRead: boolean
  createdAt: string
}

/**
 * 获取聊天记录
 */
export function getConversation(otherUserId: number, page = 0, size = 20) {
  return request.get<any, ChatMessageResponse[]>(`/api/chat/conversation/${otherUserId}`, {
    params: { page, size }
  })
}

/**
 * 获取未读消息
 */
export function getUnreadMessages() {
  return request.get<any, ChatMessageResponse[]>('/api/chat/unread')
}

/**
 * 获取未读消息数量
 */
export function getUnreadCount() {
  return request.get<any, number>('/api/chat/unread/count')
}

/**
 * 标记消息为已读
 */
export function markAsRead(senderId: number) {
  return request.post(`/api/chat/mark-read/${senderId}`)
}

/**
 * 发送消息 (HTTP 备用)
 */
export function sendMessage(data: ChatMessageRequest) {
  return request.post<any, ChatMessageResponse>('/api/chat/send', data)
}

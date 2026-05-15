/**
 * 聊天/客服相关 API
 */

import { get, post, put } from './request'
import type { ChatMessage, ChatSession } from '@user/types/chat'
import type { PageData, PageParams } from './request'

const BASE_URL = '/chat'

/**
 * 获取会话列表
 */
export function getChatSessions(params?: PageParams): Promise<PageData<ChatSession>> {
  return get(`${BASE_URL}/sessions`, params)
}

/**
 * 获取聊天记录
 */
export function getChatMessages(sessionId: number | string, params?: PageParams): Promise<PageData<ChatMessage>> {
  return get(`${BASE_URL}/sessions/${sessionId}/messages`, params)
}

/**
 * 发送消息
 */
export function sendMessage(sessionId: number | string, content: string, type?: 'text' | 'image' | 'file'): Promise<ChatMessage> {
  return post(`${BASE_URL}/sessions/${sessionId}/message`, { content, type })
}

/**
 * 创建会话（联系商家/用户）
 */
export function createChatSession(targetId: number | string, type: 'user' | 'shop' | 'system'): Promise<ChatSession> {
  return post(`${BASE_URL}/session`, { targetId, type })
}

/**
 * 删除会话
 */
export function deleteChatSession(sessionId: number | string): Promise<void> {
  return post(`${BASE_URL}/session/${sessionId}/delete`)
}

/**
 * 置顶会话
 */
export function pinChatSession(sessionId: number | string, pinned: boolean): Promise<void> {
  return put(`${BASE_URL}/session/${sessionId}/pin`, { pinned })
}

/**
 * 标记会话已读
 */
export function markSessionAsRead(sessionId: number | string): Promise<void> {
  return post(`${BASE_URL}/session/${sessionId}/read`)
}

/**
 * 获取未读消息数
 */
export function getUnreadMessageCount(): Promise<{ count: number }> {
  return get(`${BASE_URL}/unread-count`)
}

/**
 * 获取系统消息
 */
export function getSystemMessages(params?: PageParams): Promise<PageData<ChatMessage>> {
  return get(`${BASE_URL}/system-messages`, params)
}

/**
 * 商家客服聊天 API
 */
import axios from 'axios'

const chatRequest = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
})

chatRequest.interceptors.request.use(config => {
  const token = localStorage.getItem('merchant_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export interface ChatMessage {
  id?: number
  senderId: number
  receiverId: number
  content: string
  type: 'TEXT' | 'IMAGE' | 'SYSTEM' | 'FILE'
  isRead?: boolean
  createdAt?: string
}

export interface ChatSession {
  id: number
  name: string
  avatar?: string
  lastMessage?: string
  lastTime?: string
  unreadCount: number
  isOnline: boolean
}

export function getCustomerSessions() {
  return chatRequest.get<any, ChatSession[]>('/chat/sessions')
}

export function getConversation(customerId: number, page = 0, size = 50) {
  return chatRequest.get<any, ChatMessage[]>(`/chat/conversation/${customerId}`, {
    params: { page, size }
  })
}

export function sendMessage(receiverId: number, content: string, type = 'TEXT') {
  return chatRequest.post<any, ChatMessage>('/chat/send', { receiverId, content, type })
}

export function getUnreadCount() {
  return chatRequest.get<any, number>('/chat/unread/count')
}

export function markAsRead(customerId: number) {
  return chatRequest.post(`/chat/mark-read/${customerId}`)
}

export function joinChatRoom(shopId: number) {
  return chatRequest.post('/chat/join', { type: 'join', shopId })
}

export function leaveChatRoom(shopId: number) {
  return chatRequest.post('/chat/leave', { type: 'leave', shopId })
}
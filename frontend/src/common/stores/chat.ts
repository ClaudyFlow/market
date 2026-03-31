/**
 * 聊天消息状态管理
 * 管理客服聊天的消息状态和双端通信
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// 消息状态码定义
export const MessageStatus = {
  SENDING: 1000,      // 发送中
  SENT: 2000,         // 已发送
  DELIVERED: 3000,    // 已送达
  READ: 4000,         // 已读
  FAILED: 5000        // 发送失败
} as const

export type MessageStatusCode = typeof MessageStatus[keyof typeof MessageStatus]

export interface ChatMessage {
  id?: number
  senderId: number
  receiverId: number
  content: string
  type: 'TEXT' | 'IMAGE' | 'SYSTEM' | 'FILE'
  status: MessageStatusCode
  isRead: boolean
  createdAt?: string
  localId?: string // 本地消息 ID，用于乐观更新
  errorMessage?: string
}

export interface ChatState {
  messages: ChatMessage[]
  isConnected: boolean
  isTyping: boolean // 对方正在输入
  lastReadTime: number | null
  unreadCount: number
}

/**
 * 生成唯一本地 ID
 */
function generateLocalId(): string {
  return `msg_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
}

/**
 * 聊天消息 Store
 */
export const useChatStore = defineStore('chat', () => {
  // ========== State ==========
  const messages = ref<ChatMessage[]>([])
  const isConnected = ref(false)
  const isTyping = ref(false)
  const lastReadTime = ref<number | null>(null)
  const unreadCount = ref(0)
  const currentUserId = ref<number | null>(null)
  const currentReceiverId = ref<number | null>(null)

  // ========== Getters ==========
  /**
   * 获取显示用的消息列表（按时间排序）
   */
  const displayMessages = computed(() => {
    return [...messages.value].sort((a, b) => {
      const timeA = a.createdAt ? new Date(a.createdAt).getTime() : 0
      const timeB = b.createdAt ? new Date(b.createdAt).getTime() : 0
      return timeA - timeB
    })
  })

  /**
   * 获取发送中的消息
   */
  const sendingMessages = computed(() => {
    return messages.value.filter(m => m.status === MessageStatus.SENDING)
  })

  /**
   * 获取失败的消息
   */
  const failedMessages = computed(() => {
    return messages.value.filter(m => m.status === MessageStatus.FAILED)
  })

  /**
   * 获取未读消息
   */
  const unreadMessages = computed(() => {
    return messages.value.filter(m => !m.isRead && m.senderId !== currentUserId.value)
  })

  // ========== Actions ==========
  /**
   * 设置当前用户和聊天对象
   */
  function setCurrentChat(userId: number, receiverId: number) {
    currentUserId.value = userId
    currentReceiverId.value = receiverId
  }

  /**
   * 添加本地消息（乐观更新）
   */
  function addLocalMessage(content: string, type: 'TEXT' | 'IMAGE' | 'SYSTEM' | 'FILE' = 'TEXT'): ChatMessage {
    const localId = generateLocalId()
    const message: ChatMessage = {
      localId,
      senderId: currentUserId.value!,
      receiverId: currentReceiverId.value!,
      content,
      type,
      status: MessageStatus.SENDING,
      isRead: true,
      createdAt: new Date().toISOString()
    }
    messages.value.push(message)
    return message
  }

  /**
   * 更新消息状态（服务器确认）
   */
  function updateMessageStatus(localId: string, serverMessage: Partial<ChatMessage>) {
    const index = messages.value.findIndex(m => m.localId === localId)
    if (index !== -1) {
      messages.value[index] = {
        ...messages.value[index],
        ...serverMessage,
        localId // 保留本地 ID
      }
    }
  }

  /**
   * 标记消息为失败
   */
  function markMessageFailed(localId: string, errorMessage?: string) {
    const index = messages.value.findIndex(m => m.localId === localId)
    if (index !== -1) {
      messages.value[index].status = MessageStatus.FAILED
      messages.value[index].errorMessage = errorMessage || '发送失败'
    }
  }

  /**
   * 添加接收到的消息
   */
  function addReceivedMessage(message: ChatMessage) {
    // 检查是否已存在（避免重复）
    const exists = messages.value.some(m => 
      (m.id && m.id === message.id) || 
      (m.localId && m.localId === message.localId)
    )
    if (!exists) {
      messages.value.push({
        ...message,
        status: message.status || MessageStatus.DELIVERED,
        isRead: message.isRead || false
      })
      if (!message.isRead && message.senderId !== currentUserId.value) {
        unreadCount.value++
      }
    }
  }

  /**
   * 加载历史消息
   */
  function loadHistory(historyMessages: ChatMessage[]) {
    messages.value = [...historyMessages, ...messages.value]
  }

  /**
   * 标记所有消息为已读
   */
  function markAllAsRead() {
    messages.value.forEach(m => {
      if (!m.isRead && m.senderId !== currentUserId.value) {
        m.isRead = true
      }
    })
    lastReadTime.value = Date.now()
    unreadCount.value = 0
  }

  /**
   * 设置对方正在输入状态
   */
  function setTyping(typing: boolean) {
    isTyping.value = typing
  }

  /**
   * 更新连接状态
   */
  function setConnectionStatus(connected: boolean) {
    isConnected.value = connected
  }

  /**
   * 重试发送失败的消息
   */
  function retryMessage(localId: string): ChatMessage | null {
    const index = messages.value.findIndex(m => m.localId === localId)
    if (index !== -1 && messages.value[index].status === MessageStatus.FAILED) {
      messages.value[index].status = MessageStatus.SENDING
      messages.value[index].errorMessage = undefined
      return messages.value[index]
    }
    return null
  }

  /**
   * 清除聊天
   */
  function clearChat() {
    messages.value = []
    isTyping.value = false
    lastReadTime.value = null
    unreadCount.value = 0
  }

  return {
    // State
    messages,
    isConnected,
    isTyping,
    lastReadTime,
    unreadCount,
    currentUserId,
    currentReceiverId,
    // Getters
    displayMessages,
    sendingMessages,
    failedMessages,
    unreadMessages,
    // Actions
    setCurrentChat,
    addLocalMessage,
    updateMessageStatus,
    markMessageFailed,
    addReceivedMessage,
    loadHistory,
    markAllAsRead,
    setTyping,
    setConnectionStatus,
    retryMessage,
    clearChat
  }
})

export default useChatStore

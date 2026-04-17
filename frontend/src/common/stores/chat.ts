/**
 * 聊天 Store - 管理客服聊天状态
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { createWebSocketClient, WSStatus, type WSMessage } from '@common/api/websocket'
import * as chatApi from '@common/api/chat'

// 消息状态枚举
export enum MessageStatus {
  SENDING = 'sending',
  DELIVERED = 'delivered',
  READ = 'read',
  FAILED = 'failed'
}

export interface ChatSession {
  id: number
  name: string
  avatar?: string
  lastMessage?: string
  lastTime?: number
  unreadCount: number
  isOnline: boolean
}

export interface ChatState {
  messages: WSMessage[]
  sessions: ChatSession[]
  currentSessionId: number | null
  isConnected: boolean
  isTyping: boolean
  typingUserId: number | null
}

export const useChatStore = defineStore('chat', () => {
  // State
  const messages = ref<WSMessage[]>([])
  const sessions = ref<ChatSession[]>([])
  const currentSessionId = ref<number | null>(null)
  const isConnected = ref(false)
  const isTyping = ref(false)
  const typingUserId = ref<number | null>(null)
  const wsClient = ref<ReturnType<typeof createWebSocketClient> | null>(null)

  // Getters
  const currentMessages = computed(() => {
    if (!currentSessionId.value) return []
    return messages.value.filter(
      msg => msg.senderId === currentSessionId.value || msg.receiverId === currentSessionId.value
    )
  })

  const currentSession = computed(() => {
    return sessions.value.find(s => s.id === currentSessionId.value)
  })

  const unreadTotal = computed(() => {
    return sessions.value.reduce((sum, s) => sum + s.unreadCount, 0)
  })

  // Actions
  function initWebSocket(url?: string) {
    if (wsClient.value) return

    wsClient.value = createWebSocketClient(url)

    wsClient.value.on('status', (data) => {
      isConnected.value = data.status === WSStatus.CONNECTED
    })

    wsClient.value.on('message', (data) => {
      handleIncomingMessage(data)
    })

    wsClient.value.connect()
  }

  function disconnect() {
    wsClient.value?.disconnect()
    wsClient.value = null
    isConnected.value = false
  }

  function handleIncomingMessage(data: any) {
    switch (data.type) {
      case 'chat':
        const msg: WSMessage = {
          id: data.id || Date.now().toString(),
          type: data.messageType || 'text',
          content: data.content,
          senderId: data.senderId,
          senderName: data.senderName,
          senderAvatar: data.senderAvatar,
          receiverId: data.receiverId,
          timestamp: data.timestamp || Date.now(),
          isSelf: false
        }
        messages.value.push(msg)
        updateSessionLastMessage(msg.senderId, msg.content, msg.timestamp)
        break

      case 'typing':
        if (data.senderId === currentSessionId.value) {
          isTyping.value = data.isTyping
          typingUserId.value = data.isTyping ? data.senderId : null
        }
        break

      case 'system':
        messages.value.push({
          id: Date.now().toString(),
          type: 'system',
          content: data.content,
          senderId: 0,
          receiverId: 0,
          timestamp: Date.now()
        })
        break
    }
  }

  function sendMessage(content: string, type = 'text') {
    if (!currentSessionId.value || !wsClient.value) return false

    const msg: WSMessage = {
      id: Date.now().toString(),
      type: type as any,
      content,
      senderId: 0, // 当前用户ID
      receiverId: currentSessionId.value,
      timestamp: Date.now(),
      isSelf: true
    }

    wsClient.value.sendChatMessage(content, currentSessionId.value, type)
    messages.value.push(msg)
    updateSessionLastMessage(currentSessionId.value, content, msg.timestamp)
    return true
  }

  function sendTyping(isTyping: boolean) {
    if (!currentSessionId.value || !wsClient.value) return
    wsClient.value.sendTyping(currentSessionId.value, isTyping)
  }

  function setCurrentSession(sessionId: number | null) {
    currentSessionId.value = sessionId
    if (sessionId) {
      const session = sessions.value.find(s => s.id === sessionId)
      if (session) {
        session.unreadCount = 0
      }
    }
  }

  function updateSessionLastMessage(userId: number, content: string, timestamp: number) {
    const session = sessions.value.find(s => s.id === userId)
    if (session) {
      session.lastMessage = content
      session.lastTime = timestamp
      if (userId !== currentSessionId.value) {
        session.unreadCount++
      }
    }
  }

  async function loadSessions() {
    try {
      // 这里可以从 API 加载会话列表
      // const response = await chatApi.getSessions()
      // sessions.value = response
    } catch (error) {
      console.error('加载会话失败:', error)
    }
  }

  async function loadHistory(otherUserId: number) {
    try {
      const response = await chatApi.getConversation(otherUserId)
      // 转换历史消息格式
      const historyMessages: WSMessage[] = response.map((msg: any) => ({
        id: msg.id?.toString() || Date.now().toString(),
        type: (msg.type?.toLowerCase() as any) || 'text',
        content: msg.content,
        senderId: msg.senderId,
        receiverId: msg.receiverId,
        timestamp: new Date(msg.createdAt).getTime(),
        isSelf: false
      }))
      messages.value = [...historyMessages, ...messages.value]
    } catch (error) {
      console.error('加载历史消息失败:', error)
    }
  }

  function clearMessages() {
    messages.value = []
  }

  // 设置当前聊天
  function setCurrentChat(userId: number, csUserId: number) {
    currentSessionId.value = csUserId
  }

  // 设置连接状态
  function setConnectionStatus(connected: boolean) {
    isConnected.value = connected
  }

  // 添加本地消息
  function addLocalMessage(content: string, type: string) {
    const msg: WSMessage = {
      id: Date.now().toString(),
      type: type.toLowerCase() as any,
      content,
      senderId: 0,
      receiverId: currentSessionId.value || 0,
      timestamp: Date.now(),
      isSelf: true
    }
    messages.value.push(msg)
    return { localId: msg.id }
  }

  // 更新本地消息状态
  function updateLocalMessageStatus(localId: string, status: { id?: number; status: MessageStatus }) {
    const msg = messages.value.find(m => m.id === localId)
    if (msg) {
      if (status.id) msg.id = status.id.toString()
    }
  }

  // 添加消息
  function addMessage(message: WSMessage) {
    messages.value.push(message)
  }

  // 加载历史消息
  function loadHistory(history: WSMessage[]) {
    messages.value = [...history, ...messages.value]
  }

  // 标记所有为已读
  function markAllAsRead() {
    messages.value.forEach(msg => {
      if (!msg.isSelf) {
        msg.isSelf = false // 这里可以添加已读状态字段
      }
    })
  }

  return {
    messages,
    sessions,
    currentSessionId,
    isConnected,
    isTyping,
    typingUserId,
    currentMessages,
    currentSession,
    unreadTotal,
    initWebSocket,
    disconnect,
    sendMessage,
    sendTyping,
    setCurrentSession,
    loadSessions,
    loadHistory,
    clearMessages,
    setCurrentChat,
    setConnectionStatus,
    addLocalMessage,
    updateLocalMessageStatus,
    addMessage,
    markAllAsRead
  }
})

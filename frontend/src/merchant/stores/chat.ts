/**
 * 商家客服聊天 Store
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as chatApi from '@merchant/api/chat'
import type { ChatMessage, ChatSession } from '@merchant/api/chat'

export const useMerchantChatStore = defineStore('merchantChat', () => {
  const messages = ref<Map<number, ChatMessage[]>>(new Map())
  const sessions = ref<ChatSession[]>([])
  const currentCustomerId = ref<number | null>(null)
  const isConnected = ref(false)
  const isLoading = ref(false)

  const currentMessages = computed(() => {
    if (!currentCustomerId.value) return []
    return messages.value.get(currentCustomerId.value) || []
  })

  const totalUnread = computed(() => {
    return sessions.value.reduce((sum, s) => sum + s.unreadCount, 0)
  })

  async function loadSessions() {
    try {
      const data = await chatApi.getCustomerSessions()
      sessions.value = data || []
    } catch (error) {
      console.error('加载会话列表失败:', error)
    }
  }

  async function loadConversation(customerId: number) {
    isLoading.value = true
    currentCustomerId.value = customerId
    try {
      const data = await chatApi.getConversation(customerId)
      messages.value.set(customerId, data || [])
      await chatApi.markAsRead(customerId)
      updateSessionUnread(customerId, 0)
    } catch (error) {
      console.error('加载聊天记录失败:', error)
    } finally {
      isLoading.value = false
    }
  }

  async function sendMessage(content: string) {
    if (!currentCustomerId.value || !content.trim()) return

    const tempMessage: ChatMessage = {
      id: Date.now(),
      senderId: 0,
      receiverId: currentCustomerId.value,
      content: content.trim(),
      type: 'TEXT',
      createdAt: new Date().toISOString()
    }

    const msgs = messages.value.get(currentCustomerId.value) || []
    msgs.push(tempMessage)
    messages.value.set(currentCustomerId.value, msgs)

    try {
      const response = await chatApi.sendMessage(currentCustomerId.value, content)
      const idx = msgs.findIndex(m => m.id === tempMessage.id)
      if (idx !== -1) {
        msgs[idx] = response
        messages.value.set(currentCustomerId.value, msgs)
      }
    } catch (error) {
      console.error('发送消息失败:', error)
    }
  }

  function addMessage(message: ChatMessage) {
    const senderId = message.senderId
    const msgs = messages.value.get(senderId) || []
    msgs.push(message)
    messages.value.set(senderId, msgs)

    if (currentCustomerId.value !== senderId) {
      updateSessionUnread(senderId, (sessions.value.find(s => s.id === senderId)?.unreadCount || 0) + 1)
    }
  }

  function updateSessionUnread(customerId: number, count: number) {
    const session = sessions.value.find(s => s.id === customerId)
    if (session) {
      session.unreadCount = count
    }
  }

  function setCurrentCustomer(customerId: number | null) {
    currentCustomerId.value = customerId
  }

  function setConnectionStatus(connected: boolean) {
    isConnected.value = connected
  }

  return {
    messages,
    sessions,
    currentCustomerId,
    isConnected,
    isLoading,
    currentMessages,
    totalUnread,
    loadSessions,
    loadConversation,
    sendMessage,
    addMessage,
    setCurrentCustomer,
    setConnectionStatus
  }
})
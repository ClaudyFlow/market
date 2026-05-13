/**
 * 客服聊天组合式函数
 */
import { ref, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@user/stores/user'
import { useChatStore, MessageStatus } from '@common/stores/chat'
import { sendMessage as sendApiMessage, getConversation, markAsRead } from '@common/api/chat'
import { chatWS } from '@common/utils/chatWebSocket'

export function useCustomerChat() {
  const userStore = useUserStore()
  const chatStore = useChatStore()

  const showChat = ref(false)
  const inputMessage = ref('')
  const loadingHistory = ref(false)
  const messagesContainer = ref<HTMLElement | null>(null)

  const currentUserId = computed(() => userStore.user?.id || 1)
  const CS_USER_ID = 999999

  // 打开聊天
  const openChat = async () => {
    showChat.value = true
    await nextTick()

    chatStore.setCurrentChat(currentUserId.value, CS_USER_ID)
    scrollToBottom()

    if (!chatStore.isConnected) {
      try {
        await chatWS.connect(userStore.token)
        chatStore.setConnectionStatus(true)

        chatWS.onMessage(handleNewMessage)
        chatWS.onConnect(() => chatStore.setConnectionStatus(true))
        chatWS.onError(() => chatStore.setConnectionStatus(false))

        chatWS.joinChat()
        await loadHistory()
      } catch (err) {
        console.error('WebSocket 连接失败:', err)
        chatStore.setConnectionStatus(false)
        ElMessage.warning('连接客服失败，请稍后重试')
      }
    } else {
      await loadHistory()
    }
  }

  // 关闭聊天
  const closeChat = () => {
    showChat.value = false
  }

  // 加载历史消息
  const loadHistory = async () => {
    loadingHistory.value = true
    try {
      const res = await getConversation(CS_USER_ID, 0, 50)
      const historyMessages = res.map((item: any) => ({
        id: item.id,
        senderId: item.senderId,
        receiverId: item.receiverId,
        content: item.content,
        type: item.type,
        status: MessageStatus.DELIVERED,
        isRead: item.isRead,
        createdAt: item.createdAt
      }))
      chatStore.loadHistory(historyMessages)
      await markAsRead(CS_USER_ID)
      chatStore.markAllAsRead()
      await nextTick()
      scrollToBottom()
    } catch (err) {
      console.error('加载历史消息失败:', err)
    } finally {
      loadingHistory.value = false
    }
  }

  // 发送消息
  const sendMessage = async () => {
    if (!inputMessage.value.trim() || !chatStore.isConnected) return

    const content = inputMessage.value.trim()
    inputMessage.value = ''

    const localMessage = chatStore.addLocalMessage(content, 'TEXT')

    const sent = chatWS.sendChatMessage(CS_USER_ID, content, 'TEXT')
    if (!sent) {
      try {
        const response = await sendApiMessage({
          receiverId: CS_USER_ID,
          content,
          type: 'TEXT'
        })
        chatStore.updateLocalMessageStatus(localMessage.localId, {
          id: response.id,
          status: MessageStatus.DELIVERED
        })
      } catch (err) {
        chatStore.updateLocalMessageStatus(localMessage.localId, {
          status: MessageStatus.FAILED
        })
        ElMessage.error('消息发送失败')
      }
    }

    await nextTick()
    scrollToBottom()
  }

  // 重发消息
  const retryMessage = async (msg: any) => {
    if (!chatStore.isConnected) {
      ElMessage.warning('未连接到客服，请稍后重试')
      return
    }

    chatStore.updateLocalMessageStatus(msg.localId, { status: MessageStatus.SENDING })

    const sent = chatWS.sendChatMessage(CS_USER_ID, msg.content, 'TEXT')
    if (!sent) {
      try {
        const response = await sendApiMessage({
          receiverId: CS_USER_ID,
          content: msg.content,
          type: 'TEXT'
        })
        chatStore.updateLocalMessageStatus(msg.localId, {
          id: response.id,
          status: MessageStatus.DELIVERED
        })
        ElMessage.success('消息重发成功')
      } catch (err) {
        chatStore.updateLocalMessageStatus(msg.localId, { status: MessageStatus.FAILED })
        ElMessage.error('消息重发失败')
      }
    }

    await nextTick()
    scrollToBottom()
  }

  // 处理新消息
  const handleNewMessage = (data: any) => {
    const message = {
      id: data.id,
      senderId: data.senderId,
      receiverId: data.receiverId,
      content: data.content,
      type: data.type,
      status: MessageStatus.DELIVERED,
      isRead: false,
      createdAt: data.createdAt
    }
    chatStore.addMessage(message)

    if (data.senderId !== currentUserId.value) {
      chatWS.sendReadReceipt(data.senderId, data.id)
      chatStore.markAllAsRead()
    }

    nextTick(() => scrollToBottom())
  }

  // 滚动到底部
  const scrollToBottom = () => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  }

  // 格式化时间
  const formatTime = (date: string | Date) => {
    const d = new Date(date)
    const h = d.getHours().toString().padStart(2, '0')
    const m = d.getMinutes().toString().padStart(2, '0')
    return `${h}:${m}`
  }

  return {
    showChat,
    inputMessage,
    loadingHistory,
    messagesContainer,
    currentUserId,
    chatStore,
    MessageStatus,
    openChat,
    closeChat,
    sendMessage,
    retryMessage,
    formatTime
  }
}

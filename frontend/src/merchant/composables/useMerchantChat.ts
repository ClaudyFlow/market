/**
 * 商家客服聊天 Composable
 */
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { useMerchantChatStore } from '@merchant/stores/chat'

export function useMerchantChat() {
  const chatStore = useMerchantChatStore()
  const inputMessage = ref('')
  const messagesContainer = ref<HTMLElement | null>(null)

  const openChat = async (customerId: number) => {
    chatStore.setCurrentCustomer(customerId)
    await chatStore.loadConversation(customerId)
    await nextTick()
    scrollToBottom()
  }

  const sendMessage = async () => {
    if (!inputMessage.value.trim()) return

    const content = inputMessage.value.trim()
    inputMessage.value = ''

    await chatStore.sendMessage(content)
    await nextTick()
    scrollToBottom()
  }

  const scrollToBottom = () => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  }

  const formatTime = (dateStr: string | undefined) => {
    if (!dateStr) return ''
    const date = new Date(dateStr)
    const h = date.getHours().toString().padStart(2, '0')
    const m = date.getMinutes().toString().padStart(2, '0')
    return `${h}:${m}`
  }

  const loadSessionMessages = async (customerId: number) => {
    try {
      await chatStore.loadConversation(customerId)
    } catch (error) {
      ElMessage.error('加载聊天记录失败')
    }
  }

  onMounted(() => {
    chatStore.loadSessions()
  })

  return {
    inputMessage,
    messagesContainer,
    chatStore,
    openChat,
    sendMessage,
    loadSessionMessages,
    formatTime
  }
}
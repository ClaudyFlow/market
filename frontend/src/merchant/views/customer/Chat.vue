<template>
  <div class="chat-page">
    <div class="page-header">
      <h1 class="page-title">
        <el-icon><ChatDotRound /></el-icon>
        客服聊天
      </h1>
      <div class="header-info">
        <el-tag :type="isConnected ? 'success' : 'danger'" size="small">
          {{ isConnected ? '已连接' : '未连接' }}
        </el-tag>
      </div>
    </div>

    <div class="chat-container">
      <!-- 左侧会话列表 -->
      <div class="session-list">
        <div class="session-search">
          <el-input v-model="searchKeyword" placeholder="搜索用户" clearable prefix-icon="Search" />
        </div>

        <div class="session-items">
          <div
            v-for="session in filteredSessions"
            :key="session.id"
            class="session-item"
            :class="{ active: currentCustomerId === session.id }"
            @click="selectSession(session)"
          >
            <el-avatar :size="48" :src="session.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <div class="session-info">
              <div class="session-name">{{ session.name }}</div>
              <div class="session-last">{{ session.lastMessage || '暂无消息' }}</div>
            </div>
            <div class="session-meta">
              <div class="session-time">{{ formatSessionTime(session.lastTime) }}</div>
              <el-badge :value="session.unreadCount" :hidden="session.unreadCount === 0" class="unread-badge" />
            </div>
          </div>

          <el-empty v-if="sessions.length === 0" description="暂无会话" />
        </div>
      </div>

      <!-- 右侧聊天区域 -->
      <div class="chat-main">
        <template v-if="currentCustomerId">
          <!-- 聊天头部 -->
          <div class="chat-header">
            <div class="chat-customer">
              <el-avatar :size="40" :src="currentSession?.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="customer-info">
                <div class="customer-name">{{ currentSession?.name }}</div>
                <div class="customer-status">
                  <span class="online-dot" :class="{ online: currentSession?.isOnline }"></span>
                  {{ currentSession?.isOnline ? '在线' : '离线' }}
                </div>
              </div>
            </div>
            <el-button size="small" @click="clearCurrentChat">
              <el-icon><Delete /></el-icon>
              清空聊天
            </el-button>
          </div>

          <!-- 消息列表 -->
          <div class="message-list" ref="messagesContainer">
            <div v-if="currentMessages.length === 0" class="empty-chat">
              <el-empty description="暂无消息，发送一条消息开始对话吧" :image-size="80" />
            </div>

            <div
              v-for="msg in currentMessages"
              :key="msg.id"
              class="message-item"
              :class="{ 'message-me': isMyMessage(msg), 'message-other': !isMyMessage(msg) }"
            >
              <el-avatar :size="40" v-if="!isMyMessage(msg)">
                <el-icon><User /></el-icon>
              </el-avatar>

              <div class="message-content">
                <div class="message-bubble">
                  {{ msg.content }}
                </div>
                <div class="message-time">{{ formatTime(msg.createdAt) }}</div>
              </div>

              <el-avatar :size="40" v-if="isMyMessage(msg)">
                <el-icon><Shop /></el-icon>
              </el-avatar>
            </div>
          </div>

          <!-- 输入区域 -->
          <div class="chat-input">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="3"
              placeholder="输入消息..."
              @keyup.enter.ctrl="sendMessage"
            />
            <div class="input-actions">
              <el-button type="primary" :disabled="!inputMessage.trim()" @click="sendMessage">
                <el-icon><Promotion /></el-icon>
                发送
              </el-button>
            </div>
          </div>
        </template>

        <div v-else class="no-selection">
          <el-empty description="请选择会话开始聊天" :image-size="120" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { ChatDotRound, User, Shop, Delete, Promotion, Search } from '@element-plus/icons-vue'
import { useMerchantChatStore } from '@merchant/stores/chat'
import { useMerchantChat } from '@merchant/composables/useMerchantChat'
import { storeToRefs } from 'pinia'

const chatStore = useMerchantChatStore()
const {
  inputMessage,
  messagesContainer,
  openChat,
  sendMessage: doSendMessage,
  formatTime
} = useMerchantChat()

const { sessions, currentCustomerId, isConnected } = storeToRefs(chatStore)

const searchKeyword = ref('')
const messagesContainerRef = ref<HTMLElement | null>(null)

const currentMessages = computed(() => {
  return chatStore.currentMessages
})

const currentSession = computed(() => {
  return sessions.value.find(s => s.id === currentCustomerId.value)
})

const filteredSessions = computed(() => {
  if (!searchKeyword.value) return sessions.value
  const keyword = searchKeyword.value.toLowerCase()
  return sessions.value.filter(s =>
    s.name.toLowerCase().includes(keyword)
  )
})

const isMyMessage = (msg: any) => {
  return msg.senderId === 0
}

const formatSessionTime = (timeStr: string | undefined) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return `${Math.floor(diff / 86400000)}天前`
}

const selectSession = async (session: any) => {
  await openChat(session.id)
  await nextTick()
  if (messagesContainerRef.value) {
    messagesContainerRef.value.scrollTop = messagesContainerRef.value.scrollHeight
  }
}

const clearCurrentChat = () => {
  if (currentCustomerId.value) {
    chatStore.messages.set(currentCustomerId.value, [])
  }
}

const sendMessage = async () => {
  if (!inputMessage.value.trim()) return

  const content = inputMessage.value.trim()
  inputMessage.value = ''

  await chatStore.sendMessage(content)
  await nextTick()

  if (messagesContainerRef.value) {
    messagesContainerRef.value.scrollTop = messagesContainerRef.value.scrollHeight
  }
}

onMounted(() => {
  chatStore.loadSessions()
})
</script>

<style scoped>
.chat-page {
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  overflow: hidden;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.15);
}

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  margin: 0;
}

.page-title .el-icon {
  color: var(--mall-primary);
  font-size: 22px;
}

.chat-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.session-list {
  width: 300px;
  border-right: 1px solid rgba(0, 212, 255, 0.15);
  display: flex;
  flex-direction: column;
}

.session-search {
  padding: 15px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.1);
}

.session-items {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.session-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 8px;
}

.session-item:hover {
  background: rgba(0, 212, 255, 0.1);
}

.session-item.active {
  background: rgba(0, 212, 255, 0.15);
  border: 1px solid rgba(0, 212, 255, 0.3);
}

.session-info {
  flex: 1;
  min-width: 0;
}

.session-name {
  font-size: 14px;
  font-weight: 500;
  color: #fff;
  margin-bottom: 4px;
}

.session-last {
  font-size: 12px;
  color: #888;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.session-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.session-time {
  font-size: 11px;
  color: #666;
}

.unread-badge {
  --el-badge-size: 18px;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.1);
}

.chat-customer {
  display: flex;
  align-items: center;
  gap: 12px;
}

.customer-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.customer-name {
  font-size: 15px;
  font-weight: 500;
  color: #fff;
}

.customer-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #888;
}

.online-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #666;
}

.online-dot.online {
  background: #67c23a;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.empty-chat {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.message-me {
  flex-direction: row-reverse;
}

.message-content {
  max-width: 70%;
}

.message-bubble {
  padding: 10px 14px;
  border-radius: 12px;
  background: rgba(0, 212, 255, 0.1);
  color: #fff;
  word-break: break-word;
}

.message-me .message-bubble {
  background: var(--mall-primary);
  color: #fff;
}

.message-time {
  font-size: 11px;
  color: #666;
  margin-top: 4px;
}

.message-me .message-time {
  text-align: right;
}

.chat-input {
  padding: 15px 20px;
  border-top: 1px solid rgba(0, 212, 255, 0.15);
}

.input-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.no-selection {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
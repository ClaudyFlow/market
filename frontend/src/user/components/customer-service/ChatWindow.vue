<template>
  <Transition name="slide-fade">
    <div v-if="show" class="chat-section">
      <div class="chat-container">
        <!-- 聊天头部 -->
        <div class="chat-header">
          <div class="chat-title">
            <el-icon><ChatLineRound /></el-icon>
            <span>在线客服</span>
            <el-tag :type="chatStore.isConnected ? 'success' : 'danger'" size="small">
              {{ chatStore.isConnected ? '在线' : '离线' }}
            </el-tag>
          </div>
          <el-button size="small" @click="$emit('close')">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>

        <!-- 消息列表 -->
        <div class="chat-messages" ref="messagesContainer">
          <div v-if="chatStore.displayMessages.length === 0" class="empty-message">
            <el-empty description="暂无消息，发送一条消息开始对话吧" :image-size="80" />
          </div>

          <TransitionGroup name="message-list">
            <div
              v-for="msg in chatStore.displayMessages"
              :key="msg.localId || msg.id"
              class="message message-animation"
              :class="['message-' + (msg.senderId === currentUserId ? 'me' : 'other'), 'status-' + msg.status]"
            >
              <el-avatar :size="40" :src="msg.senderId === currentUserId ? undefined : 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'">
                <el-icon v-if="msg.senderId === currentUserId"><User /></el-icon>
                <el-icon v-else><Service /></el-icon>
              </el-avatar>

              <div class="message-content">
                <div class="message-bubble">
                  {{ msg.content }}
                  <span v-if="msg.senderId === currentUserId" class="message-status-icon">
                    <el-icon v-if="msg.status === 2"><Loading /></el-icon>
                    <el-icon v-else-if="msg.status === 3"><CircleCheck /></el-icon>
                    <el-icon v-else-if="msg.status === 4"><CircleCheckFilled /></el-icon>
                    <el-icon v-else-if="msg.status === 5"><View /></el-icon>
                    <el-icon v-else-if="msg.status === 6"><WarningFilled /></el-icon>
                  </span>
                </div>
                <div class="message-meta">
                  <span>{{ formatTime(msg.createdAt) }}</span>
                  <span v-if="msg.status === 6" class="retry-link" @click="$emit('retry-message', msg)">重发</span>
                </div>
              </div>
            </div>
          </TransitionGroup>

          <!-- 正在输入指示器 -->
          <Transition name="fade">
            <div v-if="chatStore.isTyping" class="typing-indicator">
              <el-avatar :size="40" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <div class="typing-dots"><span></span><span></span><span></span></div>
            </div>
          </Transition>

          <div v-loading="loading" class="loading-history" />
        </div>

        <!-- 输入区域 -->
        <div class="chat-input-area">
          <el-input
            :model-value="inputMessage"
            @update:model-value="$emit('update:inputMessage', $event)"
            placeholder="输入消息..."
            :disabled="!chatStore.isConnected"
            @keyup.enter="$emit('send')"
          >
            <template #prefix><el-icon><Edit /></el-icon></template>
          </el-input>
          <el-button type="primary" :disabled="!inputMessage.trim() || !chatStore.isConnected" @click="$emit('send')">
            <el-icon><Promotion /></el-icon>
            发送
          </el-button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { ChatLineRound, Close, User, Service, Loading, CircleCheck, CircleCheckFilled, View, WarningFilled, Edit, Promotion } from '@element-plus/icons-vue'
import type { ChatStore } from '@user/stores/chat'

interface Props {
  show: boolean
  inputMessage: string
  loading: boolean
  currentUserId: number | string
  chatStore: ChatStore
  messagesContainer?: HTMLElement | null
  formatTime: (date: string | Date) => string
}

defineProps<Props>()

defineEmits<{
  'update:inputMessage': [value: string]
  close: []
  send: []
  'retry-message': [msg: any]
}>()
</script>

<style scoped>
.chat-section {
  margin-bottom: 30px;
}

.chat-container {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.chat-messages {
  height: 400px;
  overflow-y: auto;
  padding: 20px;
}

.empty-message {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  animation: messageSlide 0.3s ease-out;
}

.message-me {
  flex-direction: row-reverse;
}

.message-bubble {
  padding: 10px 14px;
  border-radius: 12px;
  background: #f0f0f0;
  max-width: 60%;
  word-break: break-word;
  position: relative;
}

.message-me .message-bubble {
  background: #00d4ff;
  color: #fff;
}

.message-status-icon {
  margin-left: 6px;
  font-size: 14px;
}

.message-meta {
  display: flex;
  gap: 8px;
  margin-top: 4px;
  font-size: 12px;
  color: #999;
}

.retry-link {
  color: #00d4ff;
  cursor: pointer;
}

.typing-indicator {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 12px 0;
}

.typing-dots {
  display: flex;
  gap: 4px;
}

.typing-dots span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #ccc;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-dots span:nth-child(2) { animation-delay: 0.2s; }
.typing-dots span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-8px); }
}

@keyframes messageSlide {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.chat-input-area {
  display: flex;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
}

.loading-history {
  text-align: center;
  padding: 20px 0;
}

.slide-fade-enter-active, .slide-fade-leave-active { transition: all 0.3s ease; }
.slide-fade-enter-from, .slide-fade-leave-to { opacity: 0; transform: translateY(-10px); }
</style>

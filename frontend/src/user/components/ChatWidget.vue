<template>
  <div class="chat-widget" :class="{ 'is-open': isOpen }">
    <!-- 悬浮按钮 -->
    <div class="chat-float-btn" @click="toggleChat" v-if="!isOpen">
      <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="chat-badge">
        <div class="float-icon">
          <el-icon><ChatDotRound /></el-icon>
        </div>
      </el-badge>
      <span class="float-text">客服</span>
    </div>

    <!-- 聊天窗口 -->
    <div class="chat-window" v-show="isOpen">
      <!-- 头部 -->
      <div class="chat-header">
        <div class="header-info">
          <el-avatar :size="40" :src="customerServiceAvatar">
            <el-icon><Service /></el-icon>
          </el-avatar>
          <div class="header-text">
            <div class="header-title">在线客服</div>
            <div class="header-status">
              <span class="status-dot" :class="{ online: isConnected }"></span>
              <span>{{ isConnected ? '在线' : '连接中...' }}</span>
            </div>
          </div>
        </div>
        <div class="header-actions">
          <el-button text circle @click="clearMessages" title="清空记录">
            <el-icon><Delete /></el-icon>
          </el-button>
          <el-button text circle @click="toggleChat">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
      </div>

      <!-- 消息列表 -->
      <div class="chat-body" ref="messageContainer">
        <div class="message-list">
          <div
            v-for="msg in messages"
            :key="msg.id"
            class="message-item"
            :class="{ 'is-self': msg.isSelf, 'is-system': msg.type === 'system' }"
          >
            <!-- 系统消息 -->
            <div v-if="msg.type === 'system'" class="system-message">
              {{ msg.content }}
            </div>

            <!-- 普通消息 -->
            <template v-else>
              <el-avatar
                :size="36"
                :src="msg.isSelf ? userAvatar : customerServiceAvatar"
                class="message-avatar"
              >
                <el-icon><User v-if="msg.isSelf" /><Service v-else /></el-icon>
              </el-avatar>
              <div class="message-content">
                <div class="message-bubble">
                  <div class="message-text" v-if="msg.type === 'text'">{{ msg.content }}</div>
                  <div class="message-image" v-else-if="msg.type === 'image'">
                    <el-image :src="msg.content" fit="cover" />
                  </div>
                </div>
                <div class="message-time">{{ formatTime(msg.timestamp) }}</div>
              </div>
            </template>
          </div>

          <!-- 正在输入提示 -->
          <div v-if="isTyping" class="typing-indicator">
            <span>对方正在输入</span>
            <span class="typing-dots">
              <span></span>
              <span></span>
              <span></span>
            </span>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-footer">
        <div class="input-toolbar">
          <el-button text circle size="small" @click="showEmoji = !showEmoji">
            <el-icon><Orange /></el-icon>
          </el-button>
          <el-button text circle size="small" @click="triggerImageUpload">
            <el-icon><Picture /></el-icon>
          </el-button>
          <input
            type="file"
            ref="imageInput"
            accept="image/*"
            style="display: none"
            @change="handleImageUpload"
          />
        </div>

        <!-- 表情面板 -->
        <div class="emoji-panel" v-show="showEmoji">
          <span
            v-for="emoji in emojis"
            :key="emoji"
            class="emoji-item"
            @click="insertEmoji(emoji)"
          >{{ emoji }}</span>
        </div>

        <div class="input-area">
          <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="2"
            placeholder="请输入消息..."
            resize="none"
            @keydown.enter.prevent="handleEnter"
            @input="handleInput"
          />
          <el-button
            type="primary"
            class="send-btn"
            :disabled="!inputMessage.trim()"
            @click="sendMessage"
          >
            <el-icon><Promotion /></el-icon>
          </el-button>
        </div>

        <div class="input-hint">按 Enter 发送，Shift + Enter 换行</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useChatStore } from '@common/stores/chat'
import {
  ChatDotRound, Service, Close, Delete, User,
  Orange, Picture, Promotion
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const chatStore = useChatStore()

// State
const isOpen = ref(false)
const inputMessage = ref('')
const showEmoji = ref(false)
const messageContainer = ref<HTMLElement>()
const imageInput = ref<HTMLInputElement>()
const typingTimer = ref<ReturnType<typeof setTimeout> | null>(null)

// 表情列表
const emojis = ['😀', '😃', '😄', '😁', '😅', '😂', '🤣', '😊', '😇', '🙂',
  '😉', '😌', '😍', '🥰', '😘', '😗', '😙', '😚', '😋', '😛',
  '😝', '😜', '🤪', '🤨', '🧐', '🤓', '😎', '🥳', '😏', '😒',
  '👍', '👎', '👌', '✌️', '🤞', '🤟', '🤘', '🤙', '👈', '👉']

// 计算属性
const messages = computed(() => chatStore.currentMessages)
const isConnected = computed(() => chatStore.isConnected)
const isTyping = computed(() => chatStore.isTyping)
const unreadCount = computed(() => chatStore.unreadTotal)

// 头像
const customerServiceAvatar = 'https://via.placeholder.com/40x40/00d4ff/fff?text=KF'
const userAvatar = computed(() => {
  const user = localStorage.getItem('user')
  if (user) {
    const userData = JSON.parse(user)
    return userData.avatarUrl
  }
  return null
})

// 方法
function toggleChat() {
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    nextTick(() => {
      scrollToBottom()
    })
  }
}

function sendMessage() {
  const content = inputMessage.value.trim()
  if (!content) return

  if (!isConnected.value) {
    ElMessage.warning('连接未就绪，请稍后再试')
    return
  }

  chatStore.sendMessage(content)
  inputMessage.value = ''
  showEmoji.value = false
  chatStore.sendTyping(false)
  nextTick(() => scrollToBottom())
}

function handleEnter(e: KeyboardEvent) {
  if (e.shiftKey) {
    return
  }
  sendMessage()
}

function handleInput() {
  chatStore.sendTyping(true)
  if (typingTimer.value) {
    clearTimeout(typingTimer.value)
  }
  typingTimer.value = setTimeout(() => {
    chatStore.sendTyping(false)
  }, 1000)
}

function insertEmoji(emoji: string) {
  inputMessage.value += emoji
  showEmoji.value = false
}

function triggerImageUpload() {
  imageInput.value?.click()
}

function handleImageUpload(e: Event) {
  const target = e.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  // 这里可以实现图片上传逻辑
  const reader = new FileReader()
  reader.onload = (e) => {
    const imageUrl = e.target?.result as string
    chatStore.sendMessage(imageUrl, 'image')
    nextTick(() => scrollToBottom())
  }
  reader.readAsDataURL(file)
  target.value = ''
}

function clearMessages() {
  chatStore.clearMessages()
  ElMessage.success('聊天记录已清空')
}

function scrollToBottom() {
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight
  }
}

function formatTime(timestamp: number): string {
  const date = new Date(timestamp)
  const now = new Date()
  const isToday = date.toDateString() === now.toDateString()

  if (isToday) {
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  return date.toLocaleString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

// 监听新消息自动滚动
watch(() => messages.value.length, () => {
  if (isOpen.value) {
    nextTick(() => scrollToBottom())
  }
})

onMounted(() => {
  // 初始化 WebSocket 连接
  try {
    chatStore.initWebSocket()
    // 设置当前会话为客服
    chatStore.setCurrentSession(1) // 客服ID
  } catch (e) {
    console.warn('WebSocket 连接失败（后端未运行）:', e)
  }
})

onUnmounted(() => {
  if (typingTimer.value) {
    clearTimeout(typingTimer.value)
  }
})
</script>

<style scoped>
.chat-widget {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1000;
}

/* 悬浮按钮 */
.chat-float-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  transition: all 0.3s;
}

.chat-float-btn:hover {
  transform: scale(1.05);
}

.float-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.4);
  color: #000;
  font-size: 28px;
}

.float-text {
  font-size: 12px;
  color: #fff;
  background: rgba(0, 0, 0, 0.6);
  padding: 2px 8px;
  border-radius: 10px;
}

.chat-badge :deep(.el-badge__content) {
  background: linear-gradient(135deg, #ff6600, #ff8800);
  border: none;
}

/* 聊天窗口 */
.chat-window {
  width: 380px;
  height: 550px;
  background: linear-gradient(180deg, #1a2a4a 0%, #0a0e1a 100%);
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid rgba(0, 212, 255, 0.2);
}

/* 头部 */
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px;
  background: rgba(0, 0, 0, 0.3);
  border-bottom: 1px solid rgba(0, 212, 255, 0.1);
}

.header-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.header-title {
  font-size: 16px;
  font-weight: bold;
  color: #fff;
}

.header-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #888;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #888;
}

.status-dot.online {
  background: #00ff88;
  box-shadow: 0 0 8px rgba(0, 255, 136, 0.5);
}

.header-actions {
  display: flex;
  gap: 5px;
}

/* 消息区域 */
.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.message-item {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.message-item.is-self {
  flex-direction: row-reverse;
}

.message-item.is-system {
  justify-content: center;
}

.system-message {
  background: rgba(0, 0, 0, 0.4);
  color: #888;
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 12px;
}

.message-avatar {
  flex-shrink: 0;
}

.message-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-width: 70%;
}

.message-item.is-self .message-content {
  align-items: flex-end;
}

.message-bubble {
  padding: 10px 14px;
  border-radius: 12px;
  word-break: break-word;
}

.message-item:not(.is-self) .message-bubble {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  border-bottom-left-radius: 4px;
}

.message-item.is-self .message-bubble {
  background: linear-gradient(135deg, #00d4ff, #00a8cc);
  color: #000;
  border-bottom-right-radius: 4px;
}

.message-time {
  font-size: 11px;
  color: #666;
}

.message-image :deep(.el-image) {
  max-width: 200px;
  max-height: 150px;
  border-radius: 8px;
}

/* 正在输入 */
.typing-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 15px;
  color: #888;
  font-size: 12px;
}

.typing-dots {
  display: flex;
  gap: 4px;
}

.typing-dots span {
  width: 6px;
  height: 6px;
  background: #00d4ff;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing-dots span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-dots span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-10px); }
}

/* 输入区域 */
.chat-footer {
  padding: 10px 15px;
  background: rgba(0, 0, 0, 0.3);
  border-top: 1px solid rgba(0, 212, 255, 0.1);
}

.input-toolbar {
  display: flex;
  gap: 5px;
  margin-bottom: 8px;
}

.input-toolbar .el-button {
  color: #888;
}

.input-toolbar .el-button:hover {
  color: #00d4ff;
}

.emoji-panel {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  padding: 10px;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 8px;
  margin-bottom: 8px;
  max-height: 120px;
  overflow-y: auto;
}

.emoji-item {
  cursor: pointer;
  font-size: 20px;
  padding: 2px;
  transition: transform 0.2s;
}

.emoji-item:hover {
  transform: scale(1.2);
}

.input-area {
  display: flex;
  gap: 10px;
}

.input-area :deep(.el-textarea__inner) {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #fff;
  resize: none;
}

.input-area :deep(.el-textarea__inner:focus) {
  border-color: #00d4ff;
}

.send-btn {
  align-self: flex-end;
  height: 54px;
  width: 54px;
  background: linear-gradient(135deg, #00d4ff, #00a8cc);
  border: none;
}

.send-btn:disabled {
  background: rgba(255, 255, 255, 0.1);
}

.input-hint {
  font-size: 11px;
  color: #666;
  margin-top: 5px;
  text-align: center;
}

/* 滚动条 */
.chat-body::-webkit-scrollbar,
.emoji-panel::-webkit-scrollbar {
  width: 4px;
}

.chat-body::-webkit-scrollbar-thumb,
.emoji-panel::-webkit-scrollbar-thumb {
  background: rgba(0, 212, 255, 0.3);
  border-radius: 2px;
}
</style>

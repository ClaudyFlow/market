<template>
  <div class="customer-service-page">
    <header class="page-header">
      <el-icon><Headset /></el-icon>
      <h1>客服中心</h1>
    </header>

    <div class="service-body">
      <!-- 客服联系方式 -->
      <div class="service-cards">
        <div class="service-card" @click="openChat">
          <div class="card-icon">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <h3>在线客服</h3>
          <p>7x24 小时在线，即时回复</p>
          <el-button type="primary" round>立即咨询</el-button>
        </div>

        <div class="service-card" @click="contactPhone">
          <div class="card-icon phone">
            <el-icon><Phone /></el-icon>
          </div>
          <h3>电话咨询</h3>
          <p>工作日 9:00-18:00</p>
          <el-button type="success" round>400-123-4567</el-button>
        </div>

        <div class="service-card" @click="contactEmail">
          <div class="card-icon email">
            <el-icon><Message /></el-icon>
          </div>
          <h3>邮件联系</h3>
          <p>24 小时内回复</p>
          <el-button type="info" round>发送邮件</el-button>
        </div>

        <div class="service-card" @click="scrollToFAQ">
          <div class="card-icon faq">
            <el-icon><QuestionFilled /></el-icon>
          </div>
          <h3>常见问题</h3>
          <p>快速找到答案</p>
          <el-button type="warning" round>查看 FAQ</el-button>
        </div>
      </div>

      <!-- 聊天记录区域 -->
      <Transition name="slide-fade">
        <div v-if="showChat" class="chat-section">
          <div class="chat-container">
            <div class="chat-header">
              <div class="chat-title">
                <el-icon><ChatLineRound /></el-icon>
                <span>在线客服</span>
                <el-tag :type="chatStore.isConnected ? 'success' : 'danger'" size="small">
                  {{ chatStore.isConnected ? '在线' : '离线' }}
                </el-tag>
              </div>
              <el-button size="small" @click="closeChat">
                <el-icon><Close /></el-icon>
              </el-button>
            </div>

            <!-- 消息列表 -->
            <div class="chat-messages" ref="messagesContainer">
              <TransitionGroup name="message-list">
                <div v-if="chatStore.displayMessages.length === 0" key="empty" class="empty-message">
                  <el-empty description="暂无消息，发送一条消息开始对话吧" :image-size="80" />
                </div>
                
                <div
                  v-for="msg in chatStore.displayMessages"
                  :key="msg.localId || msg.id"
                  class="message message-animation"
                  :class="['message-' + (msg.senderId === currentUserId ? 'me' : 'other'), 'status-' + msg.status]"
                >
                  <el-avatar
                    :size="40"
                    :src="msg.senderId === currentUserId ? undefined : 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'"
                  >
                    <el-icon v-if="msg.senderId === currentUserId"><User /></el-icon>
                    <el-icon v-else><Service /></el-icon>
                  </el-avatar>
                  
                  <div class="message-content">
                    <div class="message-bubble">
                      {{ msg.content }}
                      <!-- 发送状态图标 -->
                      <span v-if="msg.senderId === currentUserId" class="message-status-icon">
                        <el-icon v-if="msg.status === MessageStatus.SENDING"><Loading /></el-icon>
                        <el-icon v-else-if="msg.status === MessageStatus.SENT"><CircleCheck /></el-icon>
                        <el-icon v-else-if="msg.status === MessageStatus.DELIVERED"><CircleCheckFilled /></el-icon>
                        <el-icon v-else-if="msg.status === MessageStatus.READ"><View /></el-icon>
                        <el-icon v-else-if="msg.status === MessageStatus.FAILED"><WarningFilled /></el-icon>
                      </span>
                    </div>
                    <div class="message-meta">
                      <span>{{ formatTime(msg.createdAt) }}</span>
                      <span v-if="msg.status === MessageStatus.FAILED" class="retry-link" @click="retryMessage(msg)">
                        重发
                      </span>
                    </div>
                  </div>
                </div>
              </TransitionGroup>

              <!-- 正在输入指示器 -->
              <Transition name="fade">
                <div v-if="chatStore.isTyping" class="typing-indicator">
                  <el-avatar :size="40" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
                  <div class="typing-dots">
                    <span></span>
                    <span></span>
                    <span></span>
                  </div>
                </div>
              </Transition>
              
              <div v-loading="loadingHistory" class="loading-history" />
            </div>

            <!-- 输入区域 -->
            <div class="chat-input-area">
              <el-input
                v-model="inputMessage"
                placeholder="输入消息..."
                :disabled="!chatStore.isConnected"
                @keyup.enter="sendMessage"
              >
                <template #prefix>
                  <el-icon><Edit /></el-icon>
                </template>
              </el-input>
              <el-button
                type="primary"
                :disabled="!inputMessage.trim() || !chatStore.isConnected"
                @click="sendMessage"
              >
                <el-icon><Promotion /></el-icon>
                发送
              </el-button>
            </div>
          </div>
        </div>
      </Transition>

      <!-- FAQ 区域 -->
      <section ref="faqSection" class="faq-section">
        <h2>
          <el-icon><Document /></el-icon>
          常见问题
        </h2>
        <div class="faq-list">
          <el-collapse v-model="activeFaqs" accordion>
            <el-collapse-item title="如何申请退款？" name="1">
              <div class="faq-answer">
                <p>1. 进入「我的订单」找到对应订单</p>
                <p>2. 点击「申请售后」选择退款类型</p>
                <p>3. 填写退款原因并提交</p>
                <p>4. 等待商家审核，审核通过后退款将原路返回</p>
              </div>
            </el-collapse-item>
            <el-collapse-item title="优惠券如何使用？" name="2">
              <div class="faq-answer">
                <p>1. 在商品详情页或购物车页面选择可用优惠券</p>
                <p>2. 结算时系统会自动计算最优优惠方案</p>
                <p>3. 部分优惠券可叠加使用，具体以页面显示为准</p>
              </div>
            </el-collapse-item>
            <el-collapse-item title="如何修改收货地址？" name="3">
              <div class="faq-answer">
                <p>1. 未发货订单：进入订单详情页点击「修改地址」</p>
                <p>2. 已发货订单：请联系客服协助处理</p>
                <p>3. 建议提前确认好收货地址，避免不必要的麻烦</p>
              </div>
            </el-collapse-item>
            <el-collapse-item title="VIP 会员有什么权益？" name="4">
              <div class="faq-answer">
                <p>• 专属折扣：部分商品享受会员价</p>
                <p>• 免邮券：每月赠送免邮券</p>
                <p>• 专属客服：优先响应</p>
                <p>• 生日礼包：生日当月赠送优惠券</p>
                <p>• 积分翻倍：购物享受双倍积分</p>
              </div>
            </el-collapse-item>
            <el-collapse-item title="如何联系客服？" name="5">
              <div class="faq-answer">
                <p>• 在线客服：点击页面右上角客服图标</p>
                <p>• 电话客服：400-123-4567（工作日 9:00-18:00）</p>
                <p>• 邮件客服：support@market.com</p>
                <p>• 微信公众号：关注「Market 商城」</p>
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </section>

      <!-- 意见反馈 -->
      <section class="feedback-section">
        <h2>
          <el-icon><EditPen /></el-icon>
          意见反馈
        </h2>
        <div class="feedback-form">
          <el-form :model="feedback" label-width="100px">
            <el-form-item label="反馈类型">
              <el-select v-model="feedback.type" placeholder="请选择类型" style="width: 100%">
                <el-option label="产品建议" value="suggestion" />
                <el-option label="功能问题" value="bug" />
                <el-option label="投诉建议" value="complaint" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
            <el-form-item label="联系方式">
              <el-input v-model="feedback.contact" placeholder="手机/邮箱（选填）" />
            </el-form-item>
            <el-form-item label="反馈内容">
              <el-input
                v-model="feedback.content"
                type="textarea"
                :rows="4"
                placeholder="请详细描述您的问题或建议"
                maxlength="1000"
                show-word-limit
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitFeedback" :loading="submitting">
                提交反馈
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Headset,
  ChatDotRound,
  ChatLineRound,
  Phone,
  Message,
  QuestionFilled,
  Document,
  EditPen,
  Close,
  User,
  Service,
  Loading,
  CircleCheck,
  CircleCheckFilled,
  View,
  WarningFilled,
  Edit,
  Promotion
} from '@element-plus/icons-vue'
import { useUserStore } from '@/common/stores/user'
import { useChatStore, type ChatMessage, MessageStatus } from '@/common/stores/chat'
import { sendMessage as sendApiMessage, getConversation, markAsRead } from '@/common/api/chat'
import { chatWS } from '@/common/utils/chatWebSocket'

const router = useRouter()
const userStore = useUserStore()
const chatStore = useChatStore()

const currentUserId = computed(() => userStore.user?.id || 1)
const CS_USER_ID = 999999 // 客服系统用户 ID

// FAQ
const activeFaqs = ref('')
const faqSection = ref<HTMLElement | null>(null)

// 反馈
const feedback = ref({
  type: '',
  contact: '',
  content: ''
})
const submitting = ref(false)

// 聊天
const showChat = ref(false)
const inputMessage = ref('')
const loadingHistory = ref(false)
const messagesContainer = ref<HTMLElement | null>(null)

// 打开聊天
const openChat = async () => {
  showChat.value = true
  await nextTick()
  
  // 设置当前聊天
  chatStore.setCurrentChat(currentUserId.value, CS_USER_ID)
  
  scrollToBottom()

  // 连接 WebSocket
  if (!chatStore.isConnected) {
    try {
      await chatWS.connect(userStore.token)
      chatStore.setConnectionStatus(true)

      // 监听新消息
      chatWS.onMessage(handleNewMessage)
      
      // 监听连接状态
      chatWS.onConnect(() => {
        chatStore.setConnectionStatus(true)
      })
      
      chatWS.onError(() => {
        chatStore.setConnectionStatus(false)
      })

      // 加入聊天
      chatWS.joinChat()

      // 加载历史消息
      loadHistory()
    } catch (err) {
      console.error('WebSocket 连接失败:', err)
      chatStore.setConnectionStatus(false)
      ElMessage.warning('连接客服失败，请稍后重试')
    }
  } else {
    loadHistory()
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

    // 标记为已读
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

  // 添加本地消息（乐观更新）
  const localMessage = chatStore.addLocalMessage(content, 'TEXT')

  // 通过 WebSocket 发送
  const sent = chatWS.sendChatMessage(CS_USER_ID, content, 'TEXT')

  if (!sent) {
    // WebSocket 发送失败，使用 HTTP 备用
    try {
      const response = await sendApiMessage({
        receiverId: CS_USER_ID,
        content,
        type: 'TEXT'
      })
      // 更新消息状态
      chatStore.updateMessageStatus(localMessage.localId!, {
        id: response.id,
        status: MessageStatus.SENT
      })
    } catch (err) {
      chatStore.markMessageFailed(localMessage.localId!, '发送失败，请重试')
      ElMessage.error('发送失败，请重试')
      inputMessage.value = content
    }
  } else {
    // 模拟服务器确认（实际应该由 WebSocket 返回）
    setTimeout(() => {
      chatStore.updateMessageStatus(localMessage.localId!, {
        status: MessageStatus.SENT
      })
    }, 500)
    
    setTimeout(() => {
      chatStore.updateMessageStatus(localMessage.localId!, {
        status: MessageStatus.DELIVERED
      })
    }, 1500)
  }
  
  await nextTick()
  scrollToBottom()
}

// 处理新消息
const handleNewMessage = (msg: ChatMessage) => {
  chatStore.addReceivedMessage(msg)
  
  // 如果是对方的消息，显示正在输入动画
  if (msg.senderId !== currentUserId.value) {
    chatStore.setTyping(true)
    setTimeout(() => {
      chatStore.setTyping(false)
    }, 1000)
  }
  
  nextTick(() => {
    scrollToBottom()
    // 如果聊天窗口打开，自动标记为已读
    if (showChat.value) {
      chatStore.markAllAsRead()
      markAsRead(msg.senderId)
    }
  })
}

// 重试发送失败的消息
const retryMessage = (msg: ChatMessage) => {
  const retried = chatStore.retryMessage(msg.localId!)
  if (retried) {
    sendMessageFromMessage(retried)
  }
}

// 从失败消息重新发送
const sendMessageFromMessage = async (message: ChatMessage) => {
  const sent = chatWS.sendChatMessage(CS_USER_ID, message.content, 'TEXT')
  
  if (!sent) {
    try {
      const response = await sendApiMessage({
        receiverId: CS_USER_ID,
        content: message.content,
        type: 'TEXT'
      })
      chatStore.updateMessageStatus(message.localId!, {
        id: response.id,
        status: MessageStatus.SENT
      })
      ElMessage.success('发送成功')
    } catch (err) {
      chatStore.markMessageFailed(message.localId!, '发送失败，请重试')
    }
  }
}

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 格式化时间
const formatTime = (time?: string) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  if (diff < 86400000) {
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }

  if (diff < 172800000) {
    return '昨天'
  }

  return date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' })
}

// 滚动到 FAQ
const scrollToFAQ = () => {
  faqSection.value?.scrollIntoView({ behavior: 'smooth' })
}

// 联系方式
const contactPhone = () => {
  window.open('tel:400-123-4567')
}

const contactEmail = () => {
  window.open('mailto:support@market.com')
}

// 提交反馈
const submitFeedback = async () => {
  if (!feedback.value.content) {
    ElMessage.warning('请填写反馈内容')
    return
  }

  submitting.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('反馈提交成功，我们会尽快处理')
    feedback.value = { type: '', contact: '', content: '' }
  } catch (err) {
    ElMessage.error('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 生命周期
onMounted(() => {
  // 监听页面可见性，隐藏时暂停动画
  document.addEventListener('visibilitychange', () => {
    if (document.hidden && showChat.value) {
      chatStore.setTyping(false)
    }
  })
})

onUnmounted(() => {
  // 不断开连接，保持全局可用
})
</script>

<style scoped>
/* ========== 页面基础样式 ========== */
.customer-service-page {
  min-height: 100vh;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.15) 0%, rgba(10, 14, 26, 0.8) 100%);
  padding: 40px 20px;
}

.page-header {
  max-width: 1200px;
  margin: 0 auto 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.page-header .el-icon {
  font-size: 32px;
  color: var(--mall-primary);
}

.page-header h1 {
  color: #fff;
  font-size: 32px;
  font-weight: bold;
}

.service-body {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* ========== 服务卡片 ========== */
.service-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
}

.service-card {
  background: rgba(0, 212, 255, 0.05);
  border: 1px solid rgba(0, 212, 255, 0.1);
  border-radius: 12px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.service-card:hover {
  background: rgba(0, 212, 255, 0.1);
  border-color: var(--mall-primary);
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 212, 255, 0.2);
}

.card-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 212, 255, 0.1);
  color: var(--mall-primary);
  font-size: 32px;
}

.card-icon.phone {
  background: rgba(102, 187, 106, 0.1);
  color: #66bb6a;
}

.card-icon.email {
  background: rgba(144, 147, 153, 0.1);
  color: #909399;
}

.card-icon.faq {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.service-card h3 {
  font-size: 16px;
  color: #fff;
  font-weight: 500;
}

.service-card p {
  font-size: 12px;
  color: #888;
}

/* ========== 聊天区域 ========== */
.chat-section {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 16px;
  overflow: hidden;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 600px;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: rgba(0, 212, 255, 0.1);
  border-bottom: 1px solid var(--mall-border);
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  color: #fff;
  font-weight: 500;
}

.chat-title .el-icon {
  font-size: 20px;
  color: var(--mall-primary);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.empty-message {
  display: flex;
  justify-content: center;
  align-items: center;
  flex: 1;
}

/* ========== 消息样式 ========== */
.message {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.message.message-me {
  flex-direction: row-reverse;
}

.message-content {
  max-width: 60%;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
  position: relative;
}

.message-other .message-bubble {
  background: rgba(0, 212, 255, 0.1);
  color: #fff;
  border-bottom-left-radius: 4px;
}

.message-me .message-bubble {
  background: var(--mall-primary);
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message-status-icon {
  margin-left: 6px;
  font-size: 14px;
  opacity: 0.8;
}

.message-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  color: #666;
}

.message-me .message-meta {
  justify-content: flex-end;
}

.retry-link {
  color: var(--mall-primary);
  cursor: pointer;
  text-decoration: underline;
}

/* 消息状态颜色 */
.message.status-1000 .message-bubble {
  opacity: 0.7;
}

.message.status-5000 .message-bubble {
  background: rgba(255, 77, 77, 0.3);
  border: 1px solid rgba(255, 77, 77, 0.5);
}

/* ========== 正在输入指示器 ========== */
.typing-indicator {
  display: flex;
  align-items: center;
  gap: 12px;
}

.typing-dots {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 12px 16px;
  background: rgba(0, 212, 255, 0.1);
  border-radius: 12px;
  border-bottom-left-radius: 4px;
}

.typing-dots span {
  width: 8px;
  height: 8px;
  background: var(--mall-primary);
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
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.4;
  }
  30% {
    transform: translateY(-10px);
    opacity: 1;
  }
}

.loading-history {
  padding: 10px;
}

/* ========== 输入区域 ========== */
.chat-input-area {
  display: flex;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid var(--mall-border);
  background: rgba(0, 0, 0, 0.1);
}

.chat-input-area .el-input {
  flex: 1;
}

/* ========== FAQ 和反馈 ========== */
.faq-section,
.feedback-section {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 16px;
  padding: 24px;
}

.faq-section h2,
.feedback-section h2 {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  color: #fff;
  margin-bottom: 20px;
}

.faq-section h2 .el-icon,
.feedback-section h2 .el-icon {
  color: var(--mall-primary);
  font-size: 22px;
}

.faq-answer {
  padding: 12px 16px;
  background: rgba(0, 212, 255, 0.05);
  border-radius: 8px;
}

.faq-answer p {
  font-size: 14px;
  color: #ccc;
  line-height: 1.8;
  margin: 4px 0;
}

.feedback-form {
  max-width: 600px;
}

/* ========== Vue 过渡动画 ========== */
/* Slide Fade - 聊天区域展开 */
.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.3s ease-out;
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(20px);
  opacity: 0;
}

/* Message List - 消息列表动画 */
.message-list-enter-active {
  animation: message-slide-in 0.4s ease-out;
}

.message-list-leave-active {
  transition: all 0.3s ease;
  position: absolute;
  width: 100%;
}

.message-list-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.message-list-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

@keyframes message-slide-in {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* Fade - 淡入淡出 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Message Animation - 消息动画 */
.message-animation {
  animation: message-pop 0.3s ease-out;
}

@keyframes message-pop {
  0% {
    opacity: 0;
    transform: scale(0.9);
  }
  50% {
    transform: scale(1.02);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}
</style>

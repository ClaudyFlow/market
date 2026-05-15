<template>
  <div class="customer-service-page">
    <header class="page-header">
      <el-icon><Headset /></el-icon>
      <h1>客服中心</h1>
    </header>

    <div class="service-body">
      <!-- 客服联系方式 -->
      <ServiceCards
        @open-chat="openChat"
        @contact-phone="contactPhone"
        @contact-email="contactEmail"
        @scroll-faq="scrollToFAQ"
      />

      <!-- 聊天窗口 -->
      <ChatWindow
        :show="showChat"
        v-model:input-message="inputMessage"
        :loading="loadingHistory"
        :current-user-id="currentUserId"
        :chat-store="chatStore"
        :messages-container="messagesContainer"
        :format-time="formatTime"
        @close="closeChat"
        @send="sendMessage"
        @retry-message="retryMessage"
      />

      <!-- FAQ 区域 -->
      <FaqSection ref="faqRef" />

      <!-- 意见反馈 -->
      <FeedbackForm ref="feedbackRef" @submit="handleFeedbackSubmit" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Headset } from '@element-plus/icons-vue'
import { useCustomerChat } from '@user/composables/useCustomerChat'
import { ServiceCards, ChatWindow, FaqSection, FeedbackForm } from '@user/components/customer-service'

// 使用客服聊天 composable
const {
  showChat,
  inputMessage,
  loadingHistory,
  messagesContainer,
  currentUserId,
  chatStore,
  openChat,
  closeChat,
  sendMessage,
  retryMessage,
  formatTime
} = useCustomerChat()

// FAQ 引用
const faqRef = ref<InstanceType<typeof FaqSection> | null>(null)
const feedbackRef = ref<InstanceType<typeof FeedbackForm> | null>(null)

// 滚动到 FAQ
const scrollToFAQ = () => {
  faqRef.value?.section?.scrollIntoView({ behavior: 'smooth' })
}

// 联系方式
const contactPhone = () => {
  window.open('tel:400-123-4567')
}

const contactEmail = () => {
  window.open('mailto:support@market.com')
}

// 反馈提交
const handleFeedbackSubmit = async (data: any) => {
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('反馈提交成功，我们会尽快处理')
    feedbackRef.value?.reset()
  } catch (err) {
    ElMessage.error('提交失败，请重试')
  }
}

// 生命周期
onMounted(() => {
  document.addEventListener('visibilitychange', () => {
    if (document.hidden && showChat.value) {
      chatStore.setTyping(false)
    }
  })
})
</script>

<style scoped>
.customer-service-page {
  min-height: 100vh;
  padding: 40px 20px;
  background: #f5f7fa;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 30px;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.page-header .el-icon {
  font-size: 28px;
  color: #00d4ff;
}

.page-header h1 {
  margin: 0;
  font-size: 24px;
  color: #1a1a1a;
}

.service-body {
  max-width: 1000px;
  margin: 0 auto;
}
</style>

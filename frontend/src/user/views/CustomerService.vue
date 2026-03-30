<template>
  <div class="customer-service-page">
    <header class="page-header">
      <i class="fas fa-headset"></i>
      <h1>客服中心</h1>
    </header>

    <div class="service-body">
      <div class="service-cards">
        <div class="service-card" @click="contactOnline">
          <div class="card-icon">
            <i class="fas fa-comments"></i>
          </div>
          <h3>在线客服</h3>
          <p>7x24 小时在线，即时回复</p>
          <el-button type="primary" round>立即咨询</el-button>
        </div>

        <div class="service-card" @click="contactPhone">
          <div class="card-icon phone">
            <i class="fas fa-phone"></i>
          </div>
          <h3>电话咨询</h3>
          <p>工作日 9:00-18:00</p>
          <el-button type="success" round>400-123-4567</el-button>
        </div>

        <div class="service-card" @click="contactEmail">
          <div class="card-icon email">
            <i class="fas fa-message"></i>
          </div>
          <h3>邮件联系</h3>
          <p>24 小时内回复</p>
          <el-button type="info" round>发送邮件</el-button>
        </div>

        <div class="service-card" @click="viewFAQ">
          <div class="card-icon faq">
            <i class="fas fa-question-circle"></i>
          </div>
          <h3>常见问题</h3>
          <p>快速找到答案</p>
          <el-button type="warning" round>查看 FAQ</el-button>
        </div>
      </div>

      <section class="faq-section">
        <h2>常见问题</h2>
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

      <section class="feedback-section">
        <h2>意见反馈</h2>
        <div class="feedback-form">
          <el-form :model="feedback" label-width="80px">
            <el-form-item label="反馈类型">
              <el-select v-model="feedback.type" placeholder="请选择类型" style="width: 100%">
                <el-option label="产品建议" value="suggestion" />
                <el-option label="功能问题" value="bug" />
                <el-option label="投诉建议" value="complaint" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
            <el-form-item label="联系方式" label-width="100px">
              <el-input v-model="feedback.contact" placeholder="手机/邮箱（选填）" />
            </el-form-item>
            <el-form-item label="反馈内容" label-width="100px">
              <el-input v-model="feedback.content" type="textarea" :rows="5" placeholder="请详细描述您的问题或建议" maxlength="1000" show-word-limit />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitFeedback">提交反馈</el-button>
            </el-form-item>
          </el-form>
        </div>
      </section>
    </div>

    <el-dialog v-model="showChatDialog" title="在线客服" width="800px" :close-on-click-modal="false" class="chat-dialog">
      <div class="chat-container">
        <div class="chat-messages" ref="messagesContainer">
          <div v-for="(msg, index) in chatMessages" :key="index" class="message" :class="msg.type">
            <div class="message-avatar">
              <el-avatar v-if="msg.type === 'agent'" :size="40" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <el-avatar v-else :size="40" />
            </div>
            <div class="message-content">
              <div class="message-bubble">{{ msg.content }}</div>
              <div class="message-time">{{ msg.time }}</div>
            </div>
          </div>
        </div>
        <div class="chat-input">
          <el-input v-model="chatInput" placeholder="输入消息..." @keyup.enter="sendMessage" />
          <el-button type="primary" @click="sendMessage">发送</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
// Font Awesome 图标直接使用类名，无需导入
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const activeFaqs = ref('')

const feedback = ref({
  type: '',
  contact: '',
  content: ''
})

const showChatDialog = ref(false)
const chatInput = ref('')
const chatMessages = ref([
  { type: 'agent', content: '您好，欢迎使用在线客服，请问有什么可以帮您？', time: '10:00' }
])

const contactOnline = () => {
  showChatDialog.value = true
}

const contactPhone = () => {
  window.open('tel:400-123-4567')
}

const contactEmail = () => {
  window.open('mailto:support@market.com')
}

const viewFAQ = () => {
  document.querySelector('.faq-section')?.scrollIntoView({ behavior: 'smooth' })
}

const submitFeedback = () => {
  if (!feedback.value.content) {
    ElMessage.warning('请填写反馈内容')
    return
  }
  
  ElMessage.success('反馈提交成功，我们会尽快处理')
  feedback.value = { type: '', contact: '', content: '' }
}

const sendMessage = () => {
  if (!chatInput.value.trim()) return
  
  const now = new Date()
  const time = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
  
  chatMessages.value.push({
    type: 'user',
    content: chatInput.value,
    time
  })
  
  const input = chatInput.value
  chatInput.value = ''
  
  setTimeout(() => {
    const responses = [
      '您好，请问具体是什么问题呢？',
      '明白了，我帮您查询一下',
      '请您提供一下订单号，我帮您查看',
      '这个问题需要核实，请您稍等'
    ]
    const randomResponse = responses[Math.floor(Math.random() * responses.length)]
    
    const now = new Date()
    const time = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
    
    chatMessages.value.push({
      type: 'agent',
      content: randomResponse,
      time
    })
  }, 1000)
}
</script>

<style scoped>
.customer-service-page {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: rgba(10, 15, 30, 0.95);
  border-radius: 12px;
  overflow: hidden;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
  background: rgba(0, 212, 255, 0.05);
}

.page-header i {
  font-size: 28px;
  color: var(--mall-primary);
}

.page-header h1 {
  font-size: 22px;
  
  color: #fff;
}

.service-body {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.service-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
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
}

.service-card p {
  font-size: 12px;
  color: #888;
  
}

.faq-section,
.feedback-section {
  background: rgba(0, 212, 255, 0.05);
  border: 1px solid rgba(0, 212, 255, 0.1);
  border-radius: 12px;
  padding: 24px;
}

.faq-section h2,
.feedback-section h2 {
  font-size: 18px;
  color: var(--mall-primary);
  
}

.faq-answer {
  padding: 12px 16px;
  background: rgba(0, 212, 255, 0.05);
  border-radius: 8px;
}

.faq-answer p {
  
  font-size: 14px;
  color: #ccc;
  line-height: 1.6;
}

.feedback-form {
  max-width: 600px;
}

.chat-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 500px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.message.agent {
  flex-direction: row;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
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
}

.message.agent .message-bubble {
  background: rgba(0, 212, 255, 0.1);
  color: #fff;
  border-bottom-left-radius: 4px;
}

.message.user .message-bubble {
  background: var(--mall-primary);
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message-time {
  font-size: 11px;
  color: #666;
  padding: 0 4px;
}

.message.agent .message-time {
  text-align: left;
}

.message.user .message-time {
  text-align: right;
}

.chat-input {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-top: 1px solid rgba(0, 212, 255, 0.1);
  background: rgba(0, 0, 0, 0.2);
}

.chat-input .el-input {
  flex: 1;
}
</style>

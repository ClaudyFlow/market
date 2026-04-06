<template>
  <div class="service-cards">
    <div class="service-card" @click="$emit('open-chat')">
      <div class="card-icon">
        <el-icon><ChatDotRound /></el-icon>
      </div>
      <h3>在线客服</h3>
      <p>7x24 小时在线，即时回复</p>
      <el-button type="primary" round>立即咨询</el-button>
    </div>

    <div class="service-card" @click="$emit('contact-phone')">
      <div class="card-icon phone">
        <el-icon><Phone /></el-icon>
      </div>
      <h3>电话咨询</h3>
      <p>工作日 9:00-18:00</p>
      <el-button type="success" round>400-123-4567</el-button>
    </div>

    <div class="service-card" @click="$emit('contact-email')">
      <div class="card-icon email">
        <el-icon><Message /></el-icon>
      </div>
      <h3>邮件联系</h3>
      <p>24 小时内回复</p>
      <el-button type="info" round>发送邮件</el-button>
    </div>

    <div class="service-card" @click="$emit('scroll-faq')">
      <div class="card-icon faq">
        <el-icon><QuestionFilled /></el-icon>
      </div>
      <h3>常见问题</h3>
      <p>快速找到答案</p>
      <el-button type="warning" round>查看 FAQ</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ChatDotRound, Phone, Message, QuestionFilled } from '@element-plus/icons-vue'

defineEmits<{
  'open-chat': []
  'contact-phone': []
  'contact-email': []
  'scroll-faq': []
}>()
</script>

<style scoped>
.service-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.service-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.service-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  background: linear-gradient(135deg, #00d4ff 0%, #00ff88 100%);
  color: #fff;
}

.card-icon.phone { background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%); }
.card-icon.email { background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%); }
.card-icon.faq { background: linear-gradient(135deg, #faad14 0%, #ffc53d 100%); }

.service-card h3 {
  margin: 0;
  font-size: 16px;
  color: #1a1a1a;
}

.service-card p {
  margin: 0;
  font-size: 13px;
  color: #666;
  text-align: center;
}
</style>

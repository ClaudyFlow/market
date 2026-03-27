<template>
  <section class="customer-service" aria-label="客服中心">
    <header class="service-header">
      <el-icon><Headset /></el-icon>
      <h3>客服中心</h3>
    </header>
    <div class="service-content">
      <button class="service-item" type="button" @click="contactOnline">
        <el-icon><ChatDotRound /></el-icon>
        <span>在线客服</span>
      </button>
      <button class="service-item" type="button" @click="contactPhone">
        <el-icon><Phone /></el-icon>
        <span>电话咨询</span>
      </button>
      <button class="service-item" type="button" @click="contactEmail">
        <el-icon><Message /></el-icon>
        <span>邮件联系</span>
      </button>
      <button class="service-item" type="button" @click="viewFAQ">
        <el-icon><QuestionFilled /></el-icon>
        <span>常见问题</span>
      </button>
    </div>
  </section>
</template>

<script setup lang="ts">
import { Headset, ChatDotRound, Phone, Message, QuestionFilled } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const contactOnline = () => {
  router.push('/chat')
}

const contactPhone = () => {
  window.open('tel:400-123-4567')
}

const contactEmail = () => {
  window.open('mailto:support@market.com')
}

const viewFAQ = () => {
  router.push('/faq')
}
</script>

<style scoped>
.customer-service {
  width: 100%;
  height: 100%;
  background: rgba(26, 31, 58, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  box-sizing: border-box;
}

.service-header {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--mall-primary);
}

.service-header h3 {
  font-size: 16px;
  
}

.service-header .el-icon {
  font-size: 20px;
}

.service-content {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  flex: 1;
}

.service-item {
  background: rgba(0, 212, 255, 0.1);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s;
  padding: 10px;
  
  font: inherit;
  color: inherit;
  width: 100%;
  text-align: center;
}

.service-item:hover {
  background: rgba(0, 212, 255, 0.2);
  border-color: var(--mall-primary);
  transform: translateY(-2px);
}

.service-item .el-icon {
  font-size: 24px;
  color: var(--mall-primary);
}

.service-item span {
  font-size: 12px;
  color: #ccc;
}
</style>

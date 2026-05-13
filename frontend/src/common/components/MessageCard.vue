<template>
  <div class="message-card" :class="'status-' + message.status">
    <!-- 订单卡片 -->
    <div v-if="type === 'order'" class="order-card">
      <div class="card-header">
        <el-icon><Document /></el-icon>
        <span class="card-title">订单消息</span>
        <el-tag :type="statusType" size="small">{{ statusText }}</el-tag>
      </div>
      <div class="card-body">
        <div class="info-row">
          <span class="label">订单号：</span>
          <span class="value">{{ data.orderNo }}</span>
        </div>
        <div v-if="data.amount" class="info-row">
          <span class="label">金额：</span>
          <span class="value amount">¥{{ data.amount.toFixed(2) }}</span>
        </div>
        <div v-if="data.trackingNo" class="info-row">
          <span class="label">快递单号：</span>
          <span class="value">{{ data.trackingNo }}</span>
        </div>
      </div>
      <div class="card-actions">
        <el-button size="small" @click="handleViewOrder">查看详情</el-button>
        <el-button v-if="showPay" type="primary" size="small" @click="handlePay">
          立即支付
        </el-button>
      </div>
    </div>

    <!-- 物流卡片 -->
    <div v-else-if="type === 'logistics'" class="logistics-card">
      <div class="card-header">
        <el-icon><Van /></el-icon>
        <span class="card-title">物流信息</span>
        <el-tag :type="statusType" size="small">{{ statusText }}</el-tag>
      </div>
      <div class="card-body">
        <div class="info-row">
          <span class="label">快递单号：</span>
          <span class="value">{{ data.trackingNo }}</span>
        </div>
        <div v-if="data.courierName" class="info-row">
          <span class="label">派送员：</span>
          <span class="value">{{ data.courierName }}</span>
        </div>
        <div v-if="data.courierPhone" class="info-row">
          <span class="label">联系电话：</span>
          <el-link type="primary" :href="'tel:' + data.courierPhone">
            {{ data.courierPhone }}
          </el-link>
        </div>
        <div v-if="data.location" class="info-row">
          <span class="label">当前位置：</span>
          <span class="value">{{ data.location }}</span>
        </div>
      </div>
      <div class="card-actions">
        <el-button size="small" @click="handleTrackLogistics">
          跟踪物流
        </el-button>
      </div>
    </div>

    <!-- 支付卡片 -->
    <div v-else-if="type === 'payment'" class="payment-card">
      <div class="card-header">
        <el-icon><Money /></el-icon>
        <span class="card-title">支付信息</span>
        <el-tag :type="statusType" size="small">{{ statusText }}</el-tag>
      </div>
      <div class="card-body">
        <div class="info-row">
          <span class="label">订单号：</span>
          <span class="value">{{ data.orderNo }}</span>
        </div>
        <div class="info-row highlight">
          <span class="label">支付金额：</span>
          <span class="value amount">¥{{ data.amount.toFixed(2) }}</span>
        </div>
        <div v-if="data.expireTime" class="info-row">
          <span class="label">支付截止：</span>
          <span class="value warning">{{ data.expireTime }}</span>
        </div>
      </div>
      <div class="card-actions">
        <el-button v-if="showPay" type="primary" size="small" @click="handlePay">
          立即支付
        </el-button>
        <el-button v-if="showRefund" size="small" @click="handleRefund">
          申请退款
        </el-button>
      </div>
    </div>

    <!-- 售后卡片 -->
    <div v-else-if="type === 'afterSales'" class="after-sales-card">
      <div class="card-header">
        <el-icon><Refresh /></el-icon>
        <span class="card-title">售后服务</span>
        <el-tag :type="statusType" size="small">{{ statusText }}</el-tag>
      </div>
      <div class="card-body">
        <div v-if="data.returnNo || data.exchangeNo" class="info-row">
          <span class="label">售后单号：</span>
          <span class="value">{{ data.returnNo || data.exchangeNo }}</span>
        </div>
        <div v-if="data.amount" class="info-row">
          <span class="label">退款金额：</span>
          <span class="value amount">¥{{ data.amount.toFixed(2) }}</span>
        </div>
      </div>
      <div class="card-actions">
        <el-button size="small" @click="handleViewAfterSales">查看详情</el-button>
      </div>
    </div>

    <!-- 优惠券卡片 -->
    <div v-else-if="type === 'coupon'" class="coupon-card">
      <div class="card-header">
        <el-icon><Ticket /></el-icon>
        <span class="card-title">优惠券</span>
        <el-tag :type="statusType" size="small">{{ statusText }}</el-tag>
      </div>
      <div class="card-body">
        <div class="coupon-amount">¥{{ data.amount }}</div>
        <div class="coupon-info">
          <div v-if="data.condition" class="info-row">
            <span class="label">使用条件：</span>
            <span class="value">{{ data.condition }}</span>
          </div>
          <div v-if="data.expireTime" class="info-row">
            <span class="label">有效期：</span>
            <span class="value">{{ data.expireTime }}</span>
          </div>
        </div>
      </div>
      <div class="card-actions">
        <el-button v-if="showUse" type="primary" size="small" @click="handleUseCoupon">
          立即使用
        </el-button>
        <el-button v-else size="small" @click="handleViewCoupon">
          查看详情
        </el-button>
      </div>
    </div>

    <!-- 评价卡片 -->
    <div v-else-if="type === 'evaluation'" class="evaluation-card">
      <div class="card-header">
        <el-icon><Star /></el-icon>
        <span class="card-title">服务评价</span>
      </div>
      <div class="card-body">
        <p class="evaluation-text">{{ message.content }}</p>
        <el-rate v-model="rating" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" />
      </div>
      <div class="card-actions">
        <el-button type="primary" size="small" @click="handleSubmitEvaluation">
          提交评价
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Van, Money, Refresh, Ticket, Star } from '@element-plus/icons-vue'
import { MessageStatusUtils } from '@/common/stores/messageStatus'
import type { ChatMessage } from '@/common/stores/chat'

interface Props {
  message: ChatMessage
  data?: any
}

const props = defineProps<Props>()

// 消息类型
const type = computed(() => {
  const content = props.message.content.toLowerCase()
  if (content.includes('订单')) return 'order'
  if (content.includes('物流') || content.includes('快递') || content.includes('配送')) return 'logistics'
  if (content.includes('支付') || content.includes('付款') || content.includes('退款')) return 'payment'
  if (content.includes('退货') || content.includes('换货') || content.includes('售后') || content.includes('投诉')) return 'afterSales'
  if (content.includes('优惠券')) return 'coupon'
  if (content.includes('评价')) return 'evaluation'
  return 'order' // 默认
})

// 数据
const data = computed(() => props.data || props.message)
const rating = ref(5)

// 状态相关
const statusType = computed(() => {
  const color = MessageStatusUtils.getColor(props.message.status)
  if (color === '#67c23a') return 'success'
  if (color === '#f56c6c') return 'danger'
  if (color === '#e6a23c') return 'warning'
  return 'info'
})

const statusText = computed(() => {
  return MessageStatusUtils.getDescription(props.message.status)
})

// 按钮显示
const showPay = computed(() => {
  return type.value === 'order' || type.value === 'payment'
})

const showRefund = computed(() => {
  return type.value === 'payment' && props.message.status >= 7002
})

const showUse = computed(() => {
  return type.value === 'coupon' && props.message.status === 10000
})

// 事件处理
const handleViewOrder = () => {
  ElMessage.info('查看订单详情')
}

const handlePay = () => {
  ElMessage.success('跳转到支付页面')
}

const handleTrackLogistics = () => {
  ElMessage.info('查看物流跟踪')
}

const handleRefund = () => {
  ElMessage.info('申请退款')
}

const handleViewAfterSales = () => {
  ElMessage.info('查看售后详情')
}

const handleUseCoupon = () => {
  ElMessage.success('优惠券已使用')
}

const handleViewCoupon = () => {
  ElMessage.info('查看优惠券详情')
}

const handleSubmitEvaluation = () => {
  ElMessage.success(`感谢您的评价，评分：${rating.value}星`)
}
</script>

<style scoped>
.message-card {
  width: 280px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

/* 卡片通用样式 */
.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: rgba(0, 0, 0, 0.2);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.card-title {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
  color: #fff;
}

.card-body {
  padding: 16px;
}

.info-row {
  display: flex;
  margin-bottom: 8px;
  font-size: 13px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-row.highlight {
  background: rgba(0, 212, 255, 0.1);
  padding: 8px;
  border-radius: 6px;
}

.label {
  color: #8899aa;
  white-space: nowrap;
}

.value {
  color: #fff;
  word-break: break-all;
}

.value.amount {
  color: #ff6b6b;
  font-weight: 600;
  font-size: 16px;
}

.value.warning {
  color: #ffd93d;
}

.card-actions {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  background: rgba(0, 0, 0, 0.1);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

/* 优惠券卡片特殊样式 */
.coupon-card .card-body {
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.2), rgba(255, 193, 7, 0.1));
}

.coupon-amount {
  font-size: 36px;
  font-weight: bold;
  color: #ff6b6b;
  text-align: center;
  padding: 16px 0;
}

.coupon-info {
  padding-top: 12px;
  border-top: 1px dashed rgba(255, 255, 255, 0.3);
}

/* 评价卡片特殊样式 */
.evaluation-card .evaluation-text {
  font-size: 14px;
  color: #ccc;
  margin-bottom: 16px;
  line-height: 1.5;
}

.evaluation-card :deep(.el-rate) {
  display: flex;
  justify-content: center;
}

/* 状态颜色 */
.message-card.status-1000 {
  opacity: 0.8;
}

.message-card.status-5000 {
  border-color: rgba(255, 77, 77, 0.5);
  background: rgba(255, 77, 77, 0.1);
}
</style>

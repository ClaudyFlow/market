<template>
  <div class="payment-result">
    <el-dialog
      v-model="dialogVisible"
      :title="resultType === 'success' ? '支付成功' : '支付失败'"
      width="450px"
      :close-on-click-modal="false"
      @close="handleClose"
    >
      <div class="result-content">
        <!-- 成功状态 -->
        <div class="result-status success" v-if="resultType === 'success'">
          <div class="status-icon">
            <el-icon :size="60" color="#67C23A"><CircleCheck /></el-icon>
          </div>
          <h3 class="status-title">支付成功</h3>
          <p class="status-desc">订单已支付成功，即将跳转到订单详情页</p>
        </div>

        <!-- 失败状态 -->
        <div class="result-status failed" v-else>
          <div class="status-icon">
            <el-icon :size="60" color="#F56C6C"><CircleClose /></el-icon>
          </div>
          <h3 class="status-title">支付失败</h3>
          <p class="status-desc">{{ failMessage || '支付过程中遇到问题，请重试' }}</p>
        </div>

        <!-- 支付信息 -->
        <div class="payment-info">
          <div class="info-row">
            <span class="label">订单编号：</span>
            <span class="value">{{ orderNo }}</span>
          </div>
          <div class="info-row">
            <span class="label">支付金额：</span>
            <span class="value amount">¥{{ amount }}</span>
          </div>
          <div class="info-row" v-if="paymentNo">
            <span class="label">支付流水号：</span>
            <span class="value">{{ paymentNo }}</span>
          </div>
          <div class="info-row" v-if="payTime">
            <span class="label">支付时间：</span>
            <span class="value">{{ payTime }}</span>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button v-if="resultType === 'failed'" @click="handleRetry">
            重新支付
          </el-button>
          <el-button @click="handleViewOrder">
            查看订单
          </el-button>
          <el-button type="primary" @click="handleComplete">
            完成
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  resultType: {
    type: String,
    default: 'success', // success | failed
    validator: (val: string) => ['success', 'failed'].includes(val)
  },
  orderNo: {
    type: String,
    default: ''
  },
  orderId: {
    type: Number,
    default: 0
  },
  amount: {
    type: [Number, String],
    default: ''
  },
  paymentNo: {
    type: String,
    default: ''
  },
  payTime: {
    type: String,
    default: ''
  },
  failMessage: {
    type: String,
    default: ''
  }
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'retry'): void
  (e: 'complete'): void
}>()

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 监听对话框打开
watch(() => props.modelValue, (val) => {
  if (val && props.resultType === 'success') {
    // 成功后 3 秒自动关闭
    setTimeout(() => {
      if (dialogVisible.value) {
        handleComplete()
      }
    }, 3000)
  }
})

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false
}

// 重新支付
const handleRetry = () => {
  dialogVisible.value = false
  emit('retry')
}

// 查看订单
const handleViewOrder = () => {
  dialogVisible.value = false
  router.push(`/user/order/${props.orderId}`)
}

// 完成
const handleComplete = () => {
  dialogVisible.value = false
  emit('complete')
  router.push('/user/orders')
}
</script>

<style scoped>
.result-content {
  padding: 10px 0;
}

/* 支付状态 */
.result-status {
  text-align: center;
  padding: 20px 0;
}

.result-status.success .status-icon {
  color: #67C23A;
}

.result-status.failed .status-icon {
  color: #F56C6C;
}

.status-icon {
  margin-bottom: 16px;
}

.status-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--mall-text-primary);
  margin-bottom: 8px;
}

.status-desc {
  font-size: 14px;
  color: var(--mall-text-secondary);
}

/* 支付信息 */
.payment-info {
  background: rgba(26, 31, 58, 0.5);
  padding: 16px;
  border-radius: 8px;
  margin: 20px 0;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-row .label {
  color: var(--mall-text-secondary);
}

.info-row .value {
  color: var(--mall-text-primary);
  font-weight: 500;
}

.info-row .value.amount {
  color: var(--mall-primary);
  font-size: 18px;
  font-weight: 600;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding-top: 10px;
}
</style>

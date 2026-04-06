<template>
  <div class="payment-dialog">
    <el-dialog
      v-model="dialogVisible"
      title="确认支付"
      width="500px"
      :close-on-click-modal="false"
      @close="handleClose"
    >
      <div class="payment-content" v-loading="loading">
        <!-- 订单信息 -->
        <div class="order-info">
          <div class="info-row">
            <span class="label">订单编号：</span>
            <span class="value">{{ orderNo }}</span>
          </div>
          <div class="info-row">
            <span class="label">支付金额：</span>
            <span class="value amount">¥{{ amount }}</span>
          </div>
        </div>

        <!-- 支付方式选择 -->
        <div class="payment-methods">
          <h4>选择支付方式</h4>
          <div class="method-list">
            <div
              class="method-item"
              :class="{ active: selectedMethod === 'ALIPAY' }"
              @click="selectedMethod = 'ALIPAY'"
            >
              <div class="method-icon">
                <el-icon :size="30"><Wallet /></el-icon>
              </div>
              <div class="method-name">
                <div class="name">支付宝</div>
                <div class="desc">推荐使用</div>
              </div>
            </div>

            <div
              class="method-item"
              :class="{ active: selectedMethod === 'WECHAT' }"
              @click="selectedMethod = 'WECHAT'"
            >
              <div class="method-icon">
                <el-icon :size="30"><ChatDotRound /></el-icon>
              </div>
              <div class="method-name">
                <div class="name">微信支付</div>
                <div class="desc">便捷支付</div>
              </div>
            </div>

            <div
              class="method-item"
              :class="{ active: selectedMethod === 'BANK' }"
              @click="selectedMethod = 'BANK'"
            >
              <div class="method-icon">
                <el-icon :size="30"><CreditCard /></el-icon>
              </div>
              <div class="method-name">
                <div class="name">银行卡支付</div>
                <div class="desc">对公转账</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 模拟支付提示 -->
        <el-alert
          title="测试环境提示"
          type="warning"
          :closable="false"
          show-icon
          class="test-tip"
        >
          <template #default>
            <div>当前为测试环境，点击支付将模拟支付流程，不会产生真实交易。</div>
          </template>
        </el-alert>

        <!-- 支付二维码（模拟） -->
        <div class="qr-code-area" v-if="showQrCode">
          <div class="qr-placeholder">
            <el-icon :size="80"><Picture /></el-icon>
            <p>模拟支付二维码</p>
            <p class="tip">请使用{{ selectedMethod === 'ALIPAY' ? '支付宝' : selectedMethod === 'WECHAT' ? '微信' : '银行 APP' }}扫码</p>
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleClose">取消支付</el-button>
          <el-button
            type="primary"
            @click="handlePay"
            :loading="paying"
            :disabled="!selectedMethod"
          >
            {{ paying ? '支付中...' : '确认支付' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { Wallet, ChatDotRound, CreditCard, Expand, Picture } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  orderNo: {
    type: String,
    required: true
  },
  orderId: {
    type: Number,
    required: true
  },
  amount: {
    type: [Number, String],
    required: true
  }
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'success', paymentNo: string): void
  (e: 'close'): void
}>()

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const loading = ref(false)
const paying = ref(false)
const selectedMethod = ref<'ALIPAY' | 'WECHAT' | 'BANK'>('ALIPAY')
const showQrCode = ref(false)
const paymentNo = ref('')

// 打开对话框时初始化
watch(() => props.modelValue, async (val) => {
  if (val) {
    await initPayment()
  }
})

// 初始化支付单
const initPayment = async () => {
  loading.value = true
  try {
    const response = await fetch('http://localhost:8080/api/payment/create', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        orderNo: props.orderNo,
        amount: props.amount,
        paymentMethod: selectedMethod.value
      })
    })

    const result = await response.json()
    if (result.code === 200) {
      paymentNo.value = result.data.paymentNo
      showQrCode.value = true
    } else {
      ElMessage.error(result.message || '创建支付单失败')
    }
  } catch (error) {
    console.error('创建支付单失败:', error)
  } finally {
    loading.value = false
  }
}

// 处理支付
const handlePay = async () => {
  if (!paymentNo.value) {
    ElMessage.warning('请先创建支付单')
    return
  }

  paying.value = true

  try {
    // 模拟支付（测试用）
    const response = await fetch(`http://localhost:8080/api/payment/mock-pay/${paymentNo.value}`, {
      method: 'POST'
    })

    const result = await response.json()
    if (result.code === 200) {
      ElMessage.success('支付成功')
      emit('success', paymentNo.value)
      dialogVisible.value = false
    } else {
      ElMessage.error(result.message || '支付失败')
    }
  } catch (error) {
    ElMessage.error('支付失败，请重试')
  } finally {
    paying.value = false
  }
}

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false
  emit('close')
}
</script>

<style scoped>
.payment-content {
  padding: 10px 0;
}

/* 订单信息 */
.order-info {
  background: rgba(26, 31, 58, 0.5);
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 20px;
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
  font-size: 20px;
  font-weight: 600;
}

/* 支付方式 */
.payment-methods h4 {
  color: var(--mall-text-primary);
  margin-bottom: 12px;
  font-size: 14px;
}

.method-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.method-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border: 2px solid var(--mall-border-light);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  background: rgba(26, 31, 58, 0.3);
}

.method-item:hover {
  border-color: var(--mall-primary);
  background: rgba(0, 212, 255, 0.05);
}

.method-item.active {
  border-color: var(--mall-primary);
  background: rgba(0, 212, 255, 0.1);
  box-shadow: 0 0 0 2px rgba(0, 212, 255, 0.2);
}

.method-icon {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 212, 255, 0.1);
  border-radius: 8px;
  color: var(--mall-primary);
}

.method-name {
  flex: 1;
}

.method-name .name {
  color: var(--mall-text-primary);
  font-weight: 500;
  font-size: 14px;
}

.method-name .desc {
  color: var(--mall-text-secondary);
  font-size: 12px;
  margin-top: 4px;
}

/* 测试提示 */
.test-tip {
  margin-top: 16px;
  background: rgba(230, 162, 60, 0.1);
  border-color: rgba(230, 162, 60, 0.3);
}

.test-tip :deep(.el-alert__content) {
  color: var(--mall-warning);
}

/* 二维码区域 */
.qr-code-area {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.qr-placeholder {
  width: 200px;
  height: 200px;
  border: 2px dashed var(--mall-border-light);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--mall-text-secondary);
  background: rgba(26, 31, 58, 0.3);
}

.qr-placeholder .el-icon {
  color: var(--mall-primary);
}

.qr-placeholder .tip {
  font-size: 12px;
  color: var(--mall-text-muted);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>

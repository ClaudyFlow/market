<template>
  <div class="payment-page">
    <div class="container">
      <!-- 头部 -->
      <div class="header">
        <i class="fas fa-shopping-cart"></i> 商场购物系统 - 结算支付
      </div>

      <!-- 内容区域 -->
      <div class="content">
        <!-- 左侧：支付表单 -->
        <div class="pay-form">
          <!-- 收货信息 -->
          <div class="address-form">
            <h3 class="section-title">收货信息</h3>
            <div class="form-group">
              <label>收货人姓名</label>
              <el-input v-model="formData.name" placeholder="请输入收货人姓名" />
            </div>
            <div class="form-group">
              <label>联系电话</label>
              <el-input v-model="formData.phone" placeholder="请输入手机号码" />
            </div>
            <div class="form-group">
              <label>收货地址</label>
              <el-input v-model="formData.address" placeholder="请输入详细收货地址" />
            </div>
          </div>

          <!-- 支付方式 -->
          <div class="pay-method">
            <h3 class="section-title">选择支付方式</h3>
            <div
              v-for="method in paymentMethods"
              :key="method.value"
              class="method-item"
              :class="{ active: selectedMethod === method.value }"
              @click="selectedMethod = method.value"
            >
              <div class="method-radio">
                <i v-if="selectedMethod === method.value" class="fas fa-check-circle"></i>
                <i v-else class="far fa-circle"></i>
              </div>
              <div class="method-icon-wrapper" :style="{ backgroundColor: method.bgColor }">
                <i :class="method.icon" :style="{ color: method.color }"></i>
              </div>
              <div class="method-info">
                <span class="method-name">{{ method.name }}</span>
                <span class="method-desc">{{ method.description }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：订单清单 -->
        <div class="order-info">
          <h3 class="section-title">订单清单</h3>
          <div class="goods-list">
            <div class="goods-item">
              <span class="goods-name">{{ orderInfo.productName }}</span>
              <span class="price">¥{{ formatPrice(orderInfo.totalAmount) }}</span>
            </div>
            <div class="goods-item" v-if="orderInfo.discountAmount > 0">
              <span class="goods-name">优惠折扣</span>
              <span class="price discount">- ¥{{ formatPrice(orderInfo.discountAmount) }}</span>
            </div>
          </div>

          <div class="total-price">
            <div class="total-row">
              <span>商品总价</span>
              <span>¥{{ formatPrice(orderInfo.totalAmount) }}</span>
            </div>
            <div class="total-row" v-if="orderInfo.discountAmount > 0">
              <span>优惠折扣</span>
              <span class="discount">- ¥{{ formatPrice(orderInfo.discountAmount) }}</span>
            </div>
            <div class="total-row">
              <span>运费</span>
              <span>¥0.00</span>
            </div>
            <div class="total-row final-total">
              <span>实付金额</span>
              <span>¥{{ formatPrice(paymentAmount) }}</span>
            </div>
            
            <!-- 积分信息 -->
            <div class="points-info">
              <div class="points-row">
                <span>可获得积分</span>
                <span class="points-value">+{{ earnPoints }} 积分</span>
              </div>
              <div class="points-formula">
                计算公式：floor(¥{{ formatPrice(paymentAmount) }}) = {{ earnPoints }}
              </div>
            </div>
          </div>

          <!-- 支付按钮 -->
          <button 
            class="pay-btn" 
            @click="handlePayment" 
            :disabled="isPaying"
            :style="{ background: getPayButtonGradient() }"
          >
            <i :class="selectedMethod === 'wechat' ? 'fab fa-weixin' : 
                       selectedMethod === 'alipay' ? 'fab fa-alipay' : 
                       selectedMethod === 'bankcard' ? 'fas fa-credit-card' : 'fas fa-truck'"></i>
            {{ selectedMethod === 'wechat' ? '微信支付' : 
               selectedMethod === 'alipay' ? '支付宝支付' : 
               selectedMethod === 'bankcard' ? '银行卡支付' : '确认付款' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 支付成功弹窗 -->
    <el-dialog
      v-model="showSuccessDialog"
      title="支付成功"
      width="400px"
      :close-on-click-modal="false"
      class="success-dialog"
    >
      <div class="success-content">
        <div class="success-icon">
          <i class="fas fa-check-circle" style="font-size: 60px; color: #67c23a;"></i>
        </div>
        <div class="success-text">支付成功！</div>
        <div class="success-detail">
          <p>支付金额：¥{{ formatPrice(paymentAmount) }}</p>
          <p>获得积分：<span class="points-highlight">+{{ earnPoints }} 积分</span></p>
        </div>
      </div>
      <template #footer>
        <el-button type="primary" @click="showSuccessDialog = false; goOrderList()">
          查看订单
        </el-button>
        <el-button @click="showSuccessDialog = false; continueShopping()">
          继续购物
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

// 支付方式
interface PaymentMethod {
  value: string
  name: string
  description: string
  icon: string
  color: string
  bgColor: string
}

const paymentMethods = ref<PaymentMethod[]>([
  {
    value: 'wechat',
    name: '微信支付',
    description: '推荐使用微信支付，安全快捷',
    icon: 'fab fa-weixin',
    color: '#07c160',
    bgColor: 'rgba(7,193,96,0.1)'
  },
  {
    value: 'alipay',
    name: '支付宝支付',
    description: '支付宝担保交易，购物无忧',
    icon: 'fab fa-alipay',
    color: '#1677ff',
    bgColor: 'rgba(22,119,255,0.1)'
  },
  {
    value: 'bankcard',
    name: '银行卡支付',
    description: '支持各大银行储蓄卡/信用卡',
    icon: 'fas fa-credit-card',
    color: '#ff8800',
    bgColor: 'rgba(255,136,0,0.1)'
  },
  {
    value: 'cod',
    name: '货到付款',
    description: '收到商品后再付款，更放心',
    icon: 'fas fa-truck',
    color: '#ff4d4f',
    bgColor: 'rgba(255,77,79,0.1)'
  }
])

// 选中的支付方式
const selectedMethod = ref<string>('wechat')

// 表单数据
const formData = reactive({
  name: '',
  phone: '',
  address: ''
})

// 订单信息
const orderInfo = ref({
  productName: '',
  quantity: 1,
  totalAmount: 0,
  discountAmount: 0
})

// 支付状态
const isPaying = ref<boolean>(false)
const showSuccessDialog = ref<boolean>(false)

// 计算实付金额
const paymentAmount = computed<number>(() => {
  const amount = orderInfo.value.totalAmount - orderInfo.value.discountAmount
  return Math.max(0, amount)
})

// 计算可获得积分（向下取整，只计整数）
const earnPoints = computed<number>(() => {
  return Math.floor(paymentAmount.value)
})

// 格式化价格
const formatPrice = (price: number): string => {
  return price.toFixed(2)
}

// 获取支付按钮渐变色
const getPayButtonGradient = (): string => {
  switch (selectedMethod.value) {
    case 'wechat':
      return 'linear-gradient(135deg, #07c160, #00ff88)'
    case 'alipay':
      return 'linear-gradient(135deg, #1677ff, #00d4ff)'
    case 'bankcard':
      return 'linear-gradient(135deg, #ff8800, #ffaa00)'
    case 'cod':
      return 'linear-gradient(135deg, #ff4d4f, #ff6b6b)'
    default:
      return 'linear-gradient(135deg, #00d4ff, #00ff88)'
  }
}

// 处理支付
const handlePayment = (): void => {
  // 验证表单
  if (!formData.name.trim()) {
    ElMessage.warning('请输入收货人姓名')
    return
  }
  if (!formData.phone.trim() || !/^1[3-9]\d{9}$/.test(formData.phone)) {
    ElMessage.warning('请输入正确的手机号码')
    return
  }
  if (!formData.address.trim()) {
    ElMessage.warning('请输入详细收货地址')
    return
  }
  if (!selectedMethod.value) {
    ElMessage.warning('请选择支付方式')
    return
  }

  isPaying.value = true

  // 模拟支付处理
  setTimeout(() => {
    isPaying.value = false
    showSuccessDialog.value = true
    ElMessage.success(`支付成功！获得 ${earnPoints.value} 积分`)
  }, 1500)
}

// 跳转到订单列表
const goOrderList = (): void => {
  router.push('/user/orders')
}

// 继续购物
const continueShopping = (): void => {
  router.push('/')
}

// 加载订单数据
onMounted(async (): Promise<void> => {
  const query = route.query
  
  orderInfo.value = {
    productName: (query.productName as string) || 'Apple iPhone 15 Pro Max 256GB',
    quantity: Number(query.quantity) || 1,
    totalAmount: Number(query.amount) || 9999.00,
    discountAmount: 0
  }
})
</script>

<style scoped>
.payment-page {
  min-height: 100vh;
  background: linear-gradient(180deg, rgba(0,212,255,0.15) 0%, rgba(10,14,26,0.8) 100%);
  padding: 20px;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  background: rgba(26,31,58,0.8);
  border-radius: 12px;
  border: 1px solid rgba(0,212,255,0.2);
  box-shadow: 0 4px 20px rgba(0,212,255,0.1);
  overflow: hidden;
}

/* 头部 */
.header {
  background: linear-gradient(135deg, rgba(0,212,255,0.3), rgba(0,255,136,0.2));
  color: #fff;
  padding: 20px;
  text-align: center;
  font-size: 22px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-bottom: 1px solid rgba(0,212,255,0.3);
}

.header .el-icon {
  font-size: 24px;
  color: #00d4ff;
}

/* 内容区域 */
.content {
  display: flex;
  flex-wrap: wrap;
  padding: 25px;
  gap: 25px;
}

/* 左侧：支付表单 */
.pay-form {
  flex: 1;
  min-width: 300px;
}

.section-title {
  font-size: 18px;
  margin-bottom: 15px;
  color: #fff;
  border-left: 4px solid #00d4ff;
  padding-left: 10px;
}

/* 收货信息 */
.address-form {
  margin-bottom: 25px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #ccc;
}

.form-group :deep(.el-input__wrapper) {
  border-radius: 6px;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(0,212,255,0.2);
}

.form-group :deep(.el-input__inner) {
  color: #fff;
}

.form-group :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(0,212,255,0.3);
  border-color: #00d4ff;
}

/* 支付方式 */
.pay-method {
  margin-bottom: 25px;
}

.method-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 10px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: 0.3s;
  gap: 12px;
  background: rgba(255,255,255,0.02);
}

.method-item:hover {
  border-color: #00d4ff;
  background: rgba(0,212,255,0.08);
}

.method-item.active {
  border-color: #00d4ff;
  background: rgba(0,212,255,0.12);
  box-shadow: 0 0 20px rgba(0,212,255,0.25);
}

.method-radio {
  font-size: 20px;
  color: #888;
}

.method-radio .fa-check-circle {
  color: #00d4ff;
}

.method-icon-wrapper {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.method-icon-wrapper i {
  font-size: 28px;
}

.method-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.method-name {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
}

.method-desc {
  font-size: 12px;
  color: #888;
}

/* 支付按钮 */
.pay-btn {
  width: 100%;
  height: 55px;
  font-size: 18px;
  font-weight: bold;
  margin-top: 20px;
  border: none;
  color: #000;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  cursor: pointer;
  transition: 0.3s;
}

.pay-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.pay-btn i {
  font-size: 22px;
}

.pay-btn:hover {
  opacity: 0.9;
  box-shadow: 0 0 25px rgba(0,212,255,0.4);
  transform: translateY(-2px);
}

/* 右侧：订单清单 */
.order-info {
  flex: 1;
  min-width: 300px;
  background: rgba(0,0,0,0.2);
  padding: 20px;
  border-radius: 10px;
  border: 1px solid rgba(0,212,255,0.15);
}

.goods-list {
  margin-bottom: 20px;
}

.goods-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px dashed rgba(255,255,255,0.1);
}

.goods-name {
  font-weight: 500;
  color: #fff;
}

.price {
  color: #00ff88;
  font-weight: bold;
}

.price.discount {
  color: #00d4ff;
}

/* 价格总计 */
.total-price {
  padding: 15px 0;
  border-top: 1px solid rgba(255,255,255,0.1);
}

.total-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.total-row .discount {
  color: #00d4ff;
}

.final-total {
  font-size: 20px;
  color: #00ff88;
  font-weight: bold;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px dashed rgba(0,212,255,0.3);
}

/* 积分信息 */
.points-info {
  margin-top: 15px;
  padding: 15px;
  background: rgba(0,255,136,0.05);
  border-radius: 8px;
  border: 1px dashed rgba(0,255,136,0.3);
}

.points-row {
  display: flex;
  justify-content: space-between;
  font-size: 16px;
  margin-bottom: 8px;
}

.points-value {
  color: #00ff88;
  font-weight: bold;
  font-size: 18px;
}

.points-formula {
  font-size: 12px;
  color: #888;
  font-family: monospace;
}

/* 成功弹窗 */
.success-dialog {
  background: rgba(26,31,58,0.95);
  border: 1px solid rgba(0,212,255,0.3);
}

.success-dialog :deep(.el-dialog__title) {
  color: #fff;
}

.success-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(0,212,255,0.2);
}

.success-content {
  text-align: center;
}

.success-icon {
  margin-bottom: 20px;
}

.success-text {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 20px;
}

.success-detail {
  padding: 20px;
  background: rgba(0,0,0,0.2);
  border-radius: 8px;
  border: 1px solid rgba(0,212,255,0.1);
}

.success-detail p {
  margin: 8px 0;
  font-size: 16px;
  color: #ccc;
}

.success-detail .points-highlight {
  font-size: 20px;
  font-weight: 700;
  color: #00ff88;
}

/* 响应式 */
@media (max-width: 768px) {
  .content {
    flex-direction: column;
  }
  
  .pay-form,
  .order-info {
    min-width: 100%;
  }
}
</style>

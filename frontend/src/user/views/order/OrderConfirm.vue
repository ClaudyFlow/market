<template>
  <div class="order-page">
    <div class="container">
      <h1 class="page-title">订单确认</h1>

      <div class="order-content">
        <!-- 收货地址 -->
        <div class="order-section">
          <div class="section-title">收货地址</div>
          <div class="address-list">
            <div class="address-item active">
              <div class="address-header">
                <span class="name">张三</span>
                <span class="phone">138****8888</span>
              </div>
              <div class="address-detail">北京市朝阳区 xxx 街道 xxx 小区 1 号楼 1 单元 101 室</div>
              <div class="address-tag">默认地址</div>
            </div>
            <div class="address-item">
              <el-icon><Plus /></el-icon> 添加新地址
            </div>
          </div>
        </div>

        <!-- 商品清单 -->
        <div class="order-section">
          <div class="section-title">商品清单</div>
          <div class="order-items">
            <div class="order-item" v-for="item in cart.cartItems" :key="item.id">
              <img v-lazyload="item.image" :alt="item.name" />
              <div class="item-info">
                <div class="item-name">{{ item.name }}</div>
                <div class="item-spec">颜色:{{ item.selectedColor || '默认' }} | 版本:{{ item.selectedVersion || '默认' }}</div>
              </div>
              <div class="item-price">¥{{ item.price || 0 }}</div>
              <div class="item-quantity">x{{ item.quantity || 0 }}</div>
              <div class="item-total">¥{{ ((item.price || 0) * (item.quantity || 0)).toFixed(2) }}</div>
            </div>
          </div>
        </div>

        <!-- 配送方式 -->
        <div class="order-section">
          <div class="section-title">配送方式</div>
          <div class="delivery-options">
            <div class="delivery-item active">
              <el-radio v-model="deliveryMethod" label="express">京东快递</el-radio>
              <span class="delivery-price">免运费</span>
              <span class="delivery-time">预计明天送达</span>
            </div>
            <div class="delivery-item">
              <el-radio v-model="deliveryMethod" label="self">自提</el-radio>
              <span class="delivery-price">免运费</span>
              <span class="delivery-time">预计 2 小时后可取</span>
            </div>
          </div>
        </div>

        <!-- 支付方式 -->
        <div class="order-section">
          <div class="section-title">支付方式</div>
          <div class="payment-options">
            <div class="payment-item" :class="{ active: paymentMethod === 'wechat' }" @click="paymentMethod = 'wechat'">
              <el-icon size="24" color="#07c160"><ChatDotRound /></el-icon>
              <span>微信支付</span>
            </div>
            <div class="payment-item" :class="{ active: paymentMethod === 'alipay' }" @click="paymentMethod = 'alipay'">
              <el-icon size="24" color="#1677ff"><Alipay /></el-icon>
              <span>支付宝</span>
            </div>
            <div class="payment-item" :class="{ active: paymentMethod === 'card' }" @click="paymentMethod = 'card'">
              <el-icon size="24" color="#e1251b"><CreditCard /></el-icon>
              <span>银行卡</span>
            </div>
            <div class="payment-item" :class="{ active: paymentMethod === 'cod' }" @click="paymentMethod = 'cod'">>
              <el-icon size="24" color="#666"><Money /></el-icon>
              <span>货到付款</span>
            </div>
          </div>
        </div>

        <!-- 订单备注 -->
        <div class="order-section">
          <div class="section-title">订单备注</div>
          <el-input
            v-model="orderRemark"
            type="textarea"
            placeholder="选填:对本订单的说明(如:送货时间要求等)"
            :rows="3"
          />
        </div>

        <!-- 结算栏 -->
        <div class="order-footer">
          <div class="footer-left">
            <div class="amount-item">
              <span>商品总额:</span>
              <span class="amount">¥{{ totalAmount.toFixed(2) }}</span>
            </div>
            <div class="amount-item">
              <span>运费:</span>
              <span class="amount free">免运费</span>
            </div>
          </div>
          <div class="footer-right">
            <div class="total-label">应付总额:</div>
            <div class="total-price">¥{{ totalAmount.toFixed(2) }}</div>
            <el-button type="danger" size="large" @click="submitOrder">提交订单</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@user/stores/cart'
import { ElMessage } from 'element-plus'

const router = useRouter()
const cart = useCartStore()

const deliveryMethod = ref('express')
const paymentMethod = ref('wechat')
const orderRemark = ref('')

// 安全计算总价,避免 undefined 错误
const totalAmount = computed(() => {
  return cart.totalPrice || 0
})

const submitOrder = () => {
  ElMessage.success('订单提交成功!')
  setTimeout(() => {
    cart.clearCart()
    router.push('/')
  }, 1500)
}
</script>

<style scoped>
@import '@user/assets/mall-style.css';

.order-page {
  min-height: 100vh;
  padding: 30px 0;
  background: linear-gradient(180deg, rgba(0,212,255,0.15) 0%, rgba(10,14,26,0.8) 100%);
}

.container {
  max-width: 1000px;
  
  padding: 0 20px;
}

.page-title {
  font-size: 28px;
  color: #fff;
  
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.order-content {
  background: rgba(26,31,58,0.8);
  border: 1px solid rgba(0,212,255,0.2);
  border-radius: 12px;
  padding: 20px;
}

.order-section {
  
  padding-bottom: 25px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.order-section:last-of-type {
  border-bottom: none;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: var(--mall-primary);
  
}

/* 收货地址 */
.address-list {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.address-item {
  width: 300px;
  padding: 15px;
  border: 2px solid rgba(255,255,255,0.1);
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  background: rgba(0,0,0,0.2);
  transition: all 0.3s;
}

.address-item:hover {
  border-color: rgba(0,212,255,0.3);
}

.address-item.active {
  border-color: var(--mall-primary);
  background: rgba(0,212,255,0.1);
}

.address-item:last-child {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #888;
}

.address-item:last-child:hover {
  background: rgba(0,212,255,0.1);
}

.address-header {
  
}

.address-header .name {
  font-weight: bold;
  color: #fff;
  
}

.address-header .phone {
  color: #ccc;
}

.address-detail {
  font-size: 14px;
  color: #ccc;
  line-height: 1.6;
}

.address-tag {
  position: absolute;
  top: 10px;
  right: 10px;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  color: #000;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

/* 商品清单 */
.order-items {
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 8px;
  overflow: hidden;
}

.order-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid rgba(255,255,255,0.05);
  gap: 15px;
  background: rgba(0,0,0,0.1);
}

.order-item:last-child {
  border-bottom: none;
}

.order-item img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}

.item-info {
  flex: 1;
}

.item-name {
  font-size: 14px;
  color: var(--mall-text-primary, #eee);
  
}

.item-spec {
  font-size: 12px;
  color: var(--mall-text-muted);
}

.item-price {
  width: 100px;
  text-align: center;
  color: var(--mall-text-secondary);
}

.item-quantity {
  width: 60px;
  text-align: center;
  color: var(--mall-text-muted);
}

.item-total {
  width: 100px;
  text-align: right;
  color: var(--mall-accent);
  font-weight: bold;
}

/* 配送方式 */
.delivery-options {
  display: flex;
  gap: 20px;
}

.delivery-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px 20px;
  border: 2px solid rgba(255,255,255,0.1);
  border-radius: 8px;
  cursor: pointer;
  background: rgba(0,0,0,0.2);
  transition: all 0.3s;
}

.delivery-item:hover {
  border-color: rgba(0,212,255,0.3);
}

.delivery-item.active {
  border-color: var(--mall-primary);
  background: rgba(0,212,255,0.1);
}

.delivery-price {
  color: var(--mall-primary);
  font-weight: bold;
}

.delivery-time {
  color: #888;
  font-size: 13px;
}

/* 支付方式 */
.payment-options {
  display: flex;
  gap: 15px;
}

.payment-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 15px 25px;
  border: 2px solid rgba(255,255,255,0.1);
  border-radius: 8px;
  cursor: pointer;
  background: rgba(0,0,0,0.2);
  transition: all 0.3s;
}

.payment-item:hover {
  border-color: rgba(0,212,255,0.3);
}

.payment-item.active {
  border-color: var(--mall-primary);
  background: rgba(0,212,255,0.1);
}

/* 订单备注 */
.order-section textarea {
  width: 100%;
  background: rgba(0,0,0,0.2);
  border: 1px solid rgba(255,255,255,0.1);
  color: #fff;
}

/* 结算栏 */
.order-footer {
  background: rgba(0,0,0,0.2);
  padding: 20px;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid rgba(255,255,255,0.1);
}

.footer-left {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.amount-item {
  font-size: 14px;
  color: #ccc;
}

.amount-item .amount {
  color: #fff;
  font-weight: bold;
}

.amount-item .amount.free {
  color: var(--mall-primary);
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.total-label {
  font-size: 14px;
  color: #ccc;
}

.total-price {
  color: var(--mall-accent);
  font-size: 28px;
  font-weight: bold;
}

.submit-btn {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  color: #000;
  font-weight: bold;
  padding: 12px 40px;
  font-size: 16px;
  box-shadow: 0 0 15px rgba(0,212,255,0.4);
}

.submit-btn:hover {
  box-shadow: 0 0 25px rgba(0,212,255,0.6);
}
</style>

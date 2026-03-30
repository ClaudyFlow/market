<template>
  <div class="shopping-cart-component">
    <!-- 购物车标题 -->
    <div class="cart-header">
      <div class="header-left">
        <i class="fas fa-shopping-cart"></i>
        <span>购物车</span>
        <span class="cart-count" v-if="cartCount > 0">{{ cartCount }}</span>
      </div>
      <button class="clear-cart-btn" @click="clearCart" type="button" v-if="cartCount > 0">
        <i class="fas fa-trash-alt"></i>
        <span>清空</span>
      </button>
    </div>
    
    <!-- 购物车商品列表 -->
    <div class="cart-items" v-if="cartItems.length > 0">
      <div
        v-for="item in cartItems"
        :key="item.id"
        class="cart-item"
      >
        <!-- 商品图片 -->
        <div class="item-image-wrapper">
          <img :src="item.image" :alt="item.name" class="item-image" />
          <span class="item-quantity">×{{ item.quantity }}</span>
        </div>
        
        <!-- 商品信息 -->
        <div class="item-info">
          <div class="item-name">{{ item.name }}</div>
          <div class="item-spec" v-if="item.spec">{{ item.spec }}</div>
          <div class="item-price-row">
            <span class="item-price">¥{{ formatPrice(item.price) }}</span>
            <span class="item-total">¥{{ formatPrice(item.price * item.quantity) }}</span>
          </div>
        </div>
        
        <!-- 删除按钮 -->
        <button class="remove-btn" @click="removeItem(item.id)" type="button" aria-label="删除商品">
          <i class="fas fa-times"></i>
        </button>
      </div>
    </div>
    
    <!-- 空购物车 -->
    <div class="empty-cart" v-else>
      <div class="empty-icon">
        <i class="fas fa-shopping-cart"></i>
      </div>
      <p class="empty-text">购物车空空如也</p>
      <button class="go-shopping-btn" @click="goShopping" type="button">
        <i class="fas fa-store"></i>
        <span>去购物</span>
      </button>
    </div>
    
    <!-- 购物车底部 -->
    <div class="cart-footer" v-if="cartItems.length > 0">
      <div class="cart-total">
        <span class="total-label">合计：</span>
        <span class="total-price">¥{{ formatPrice(totalAmount) }}</span>
      </div>
      <button class="checkout-btn" @click="goToCheckout" type="button">
        <span>去结算</span>
        <i class="fas fa-arrow-right"></i>
      </button>
    </div>
    
    <!-- 购物车标签（可固定在页面角落） -->
    <div class="cart-badge" @click="toggleCartPanel" v-if="cartCount > 0">
      <div class="badge-icon">
        <i class="fas fa-shopping-cart"></i>
        <span class="badge-count">{{ cartCount > 99 ? '99+' : cartCount }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCartStore } from '@user/stores/cart'

const router = useRouter()
const cartStore = useCartStore()

// 购物车商品
const cartItems = ref(cartStore.cartItems)

// 购物车数量
const cartCount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

// 购物车总额
const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

// 格式化价格
const formatPrice = (price: number): string => {
  return price.toFixed(2)
}

// 删除商品
const removeItem = (id: number): void => {
  ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    cartStore.removeFromCart(id)
    cartItems.value = cartStore.cartItems
    ElMessage.success('已删除')
  }).catch(() => {})
}

// 清空购物车
const clearCart = (): void => {
  ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    cartStore.clearCart()
    cartItems.value = []
    ElMessage.success('购物车已清空')
  }).catch(() => {})
}

// 去购物
const goShopping = (): void => {
  router.push('/')
}

// 去结算
const goToCheckout = (): void => {
  if (cartItems.value.length === 0) {
    ElMessage.warning('购物车是空的')
    return
  }
  
  router.push({
    path: '/payment',
    query: {
      amount: totalAmount.value.toFixed(2),
      quantity: cartCount.value
    }
  })
}

// 切换购物车面板
const toggleCartPanel = (): void => {
  // 可以在这里实现购物车面板的展开/收起
  ElMessage.info('点击了购物车标签')
}

// 监听购物车变化
onMounted(() => {
  cartItems.value = cartStore.cartItems
})
</script>

<style scoped>
.shopping-cart-component {
  width: 100%;
  background: rgba(26, 31, 58, 0.8);
  border-radius: 12px;
  padding: 20px;
  border: 1px solid rgba(0, 212, 255, 0.2);
  position: relative;
}

/* 购物车标题 */
.cart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-left i {
  color: #00d4ff;
  font-size: 20px;
}

.header-left span {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
}

.cart-count {
  background: linear-gradient(135deg, #ff6b6b, #ff8888);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 12px;
  min-width: 20px;
  text-align: center;
}

.clear-cart-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: transparent;
  border: 1px solid rgba(255, 107, 107, 0.3);
  border-radius: 12px;
  color: #ff6b6b;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.clear-cart-btn:hover {
  background: rgba(255, 107, 107, 0.1);
  border-color: #ff6b6b;
}

/* 购物车商品列表 */
.cart-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 400px;
  overflow-y: auto;
  padding-right: 8px;
}

.cart-items::-webkit-scrollbar {
  width: 6px;
}

.cart-items::-webkit-scrollbar-thumb {
  background: rgba(0, 212, 255, 0.3);
  border-radius: 3px;
}

.cart-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 10px;
  transition: all 0.3s;
}

.cart-item:hover {
  background: rgba(0, 212, 255, 0.08);
  border-color: rgba(0, 212, 255, 0.2);
}

/* 商品图片 */
.item-image-wrapper {
  position: relative;
  flex-shrink: 0;
}

.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid rgba(0, 212, 255, 0.2);
}

.item-quantity {
  position: absolute;
  top: -8px;
  right: -8px;
  background: rgba(0, 0, 0, 0.8);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 10px;
  border: 1px solid rgba(0, 212, 255, 0.3);
}

/* 商品信息 */
.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  overflow: hidden;
}

.item-name {
  font-size: 14px;
  font-weight: 500;
  color: #fff;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.item-spec {
  font-size: 12px;
  color: #888;
  background: rgba(255, 255, 255, 0.05);
  padding: 2px 8px;
  border-radius: 8px;
  align-self: flex-start;
}

.item-price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.item-price {
  font-size: 16px;
  font-weight: 700;
  color: #00ff88;
}

.item-total {
  font-size: 13px;
  color: #aaa;
}

/* 删除按钮 */
.remove-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background: transparent;
  border: none;
  color: #666;
  cursor: pointer;
  border-radius: 50%;
  transition: all 0.3s;
  flex-shrink: 0;
}

.remove-btn:hover {
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
}

/* 空购物车 */
.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.empty-icon {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 212, 255, 0.1);
  border-radius: 50%;
  margin-bottom: 16px;
}

.empty-icon i {
  font-size: 40px;
  color: #00d4ff;
}

.empty-text {
  font-size: 14px;
  color: #888;
  margin-bottom: 20px;
}

.go-shopping-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 24px;
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  border-radius: 20px;
  color: #000;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.go-shopping-btn:hover {
  background: linear-gradient(135deg, #00ff88, #00d4ff);
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.4);
  transform: translateY(-2px);
}

/* 购物车底部 */
.cart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(0, 212, 255, 0.2);
}

.cart-total {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.total-label {
  font-size: 14px;
  color: #aaa;
}

.total-price {
  font-size: 22px;
  font-weight: 700;
  color: #00ff88;
}

.checkout-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 24px;
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  border-radius: 20px;
  color: #000;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.checkout-btn:hover {
  background: linear-gradient(135deg, #00ff88, #00d4ff);
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.4);
  transform: translateY(-2px);
}

.checkout-btn i {
  font-size: 14px;
  transition: transform 0.3s;
}

.checkout-btn:hover i {
  transform: translateX(4px);
}

/* 购物车标签（悬浮角标） */
.cart-badge {
  position: absolute;
  top: -10px;
  right: -10px;
  cursor: pointer;
  transition: transform 0.3s;
}

.cart-badge:hover {
  transform: scale(1.1);
}

.badge-icon {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border-radius: 50%;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.4);
}

.badge-icon i {
  color: #000;
  font-size: 22px;
}

.badge-count {
  position: absolute;
  top: -5px;
  right: -5px;
  background: linear-gradient(135deg, #ff6b6b, #ff8888);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
  border: 2px solid rgba(26, 31, 58, 0.8);
}
</style>

<template>
  <div class="cart-page">
    <div class="container">
      <h1 class="page-title">我的购物车</h1>

      <div v-if="cartItems.length > 0" class="cart-content">
        <!-- 购物车列表 -->
        <div class="cart-table">
          <div class="cart-header">
            <div class="col-check">
              <el-checkbox v-model="selectAll" @change="toggleSelectAll" /> 全选
            </div>
            <div class="col-product">商品信息</div>
            <div class="col-price">单价</div>
            <div class="col-quantity">数量</div>
            <div class="col-total">小计</div>
            <div class="col-action">操作</div>
          </div>

          <div class="cart-body">
            <div class="cart-item" v-for="item in cartItems" :key="item.id">
              <div class="col-check">
                <el-checkbox v-model="item.selected" />
              </div>
              <div class="col-product">
                <div class="product-info">
                  <img :src="item.image" :alt="item.name" />
                  <div class="product-name">{{ item.name }}</div>
                </div>
              </div>
              <div class="col-price">¥{{ item.price }}</div>
              <div class="col-quantity">
                <el-input-number v-model="item.quantity" :min="1" :max="99" size="small" @change="updateQuantity(item)" />
              </div>
              <div class="col-total">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
              <div class="col-action">
                <el-button type="danger" text size="small" @click="removeItem(item.id)">删除</el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 结算栏 -->
        <div class="cart-footer">
          <div class="footer-left">
            <el-button @click="clearCart">清空购物车</el-button>
            <el-button @click="continueShopping">继续购物</el-button>
          </div>
          <div class="footer-right">
            <div class="selected-info">
              已选 <span class="highlight">{{ selectedCount }}</span> 件商品
            </div>
            <div class="total-info">
              <span class="label">合计:</span>
              <span class="total-price">¥{{ selectedTotal.toFixed(2) }}</span>
            </div>
            <el-button type="danger" size="large" @click="goToCheckout">去结算</el-button>
          </div>
        </div>
      </div>

      <!-- 空购物车 -->
      <div v-else class="empty-cart">
        <el-empty description="购物车空空如也">
          <el-button type="danger" @click="continueShopping">去购物</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useLocalCartStore } from '@user/stores/cart-local'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ShoppingCart } from '@element-plus/icons-vue'

const router = useRouter()
const cartStore = useLocalCartStore()

const selectAll = ref(true)

const selectedCount = computed(() => {
  return cartStore.selectedCount
})

const selectedTotal = computed(() => {
  return cartStore.totalAmount
})

const cartItems = computed(() => {
  return cartStore.cartItems
})

const toggleSelectAll = () => {
  cartStore.selectAll(selectAll.value)
}

const updateQuantity = (item: any) => {
  cartStore.updateQuantity(item.id, item.quantity)
}

const removeItem = (id: number) => {
  ElMessageBox.confirm('确定要删除该商品吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    cartStore.removeItem(id)
    ElMessage.success('已删除')
  }).catch(() => {})
}

const clearCart = () => {
  ElMessageBox.confirm('确定要清空购物车吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    cartStore.clearCart()
    ElMessage.success('购物车已清空')
  }).catch(() => {})
}

const continueShopping = () => {
  router.push('/item')
}

const goToCheckout = () => {
  if (selectedCount.value === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  router.push({
    path: '/payment',
    query: {
      amount: selectedTotal.value.toFixed(2),
      quantity: selectedCount.value
    }
  })
}

onMounted(() => {
  // 初始化全选状态
  selectAll.value = cartStore.allSelected
})
</script>

<style scoped>
@import '@user/assets/mall-style.css';

.cart-page {
  min-height: 100vh;
  padding: 30px 0;
  background: linear-gradient(180deg, rgba(0,212,255,0.15) 0%, rgba(10,14,26,0.8) 100%);
}

.container {
  max-width: 1200px;
  
  padding: 0 20px;
}

.page-title {
  font-size: 28px;
  color: #fff;
  
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 购物车表格 */
.cart-table {
  background: rgba(26,31,58,0.8);
  border: 1px solid rgba(0,212,255,0.2);
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 20px;
}

.cart-header {
  display: flex;
  background: rgba(0,212,255,0.15);
  padding: 15px 20px;
  font-size: 14px;
  color: #fff;
  font-weight: bold;
}

.cart-body {
  border-top: 1px solid rgba(255,255,255,0.05);
}

.cart-item {
  display: flex;
  padding: 20px;
  border-bottom: 1px solid rgba(255,255,255,0.05);
  align-items: center;
  transition: all 0.3s;
  background: rgba(255,255,255,0.02);
}

.cart-item:hover {
  background: rgba(0,212,255,0.08);
}

.cart-item:last-child {
  border-bottom: none;
}

.col-check {
  width: 80px;
}

.col-product {
  flex: 1;
}

.col-price {
  width: 120px;
  text-align: center;
  color: #ccc;
}

.col-quantity {
  width: 150px;
  text-align: center;
}

.col-total {
  width: 120px;
  text-align: center;
  color: var(--mall-accent);
  font-weight: bold;
  font-size: 16px;
}

.col-action {
  width: 80px;
  text-align: center;
}

.product-info {
  display: flex;
  gap: 15px;
  align-items: center;
}

.product-info img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid rgba(0,212,255,0.2);
}

.product-name {
  font-size: 14px;
  color: #fff;
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* 结算栏 */
.cart-footer {
  background: rgba(26,31,58,0.8);
  border: 1px solid rgba(0,212,255,0.2);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-left {
  display: flex;
  gap: 10px;
}

.footer-left .el-button {
  background: rgba(26,31,58,0.6);
  border: 1px solid rgba(0,212,255,0.3);
  color: var(--mall-primary);
}

.footer-left .el-button:hover {
  background: rgba(0,212,255,0.1);
  border-color: var(--mall-primary);
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.selected-info {
  color: #ccc;
}

.selected-info .highlight {
  color: var(--mall-primary);
  font-weight: bold;
}

.total-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.total-info .label {
  font-size: 14px;
  color: #ccc;
}

.total-price {
  color: var(--mall-accent);
  font-size: 24px;
  font-weight: bold;
}

/* 空购物车 */
.empty-cart {
  background: rgba(26,31,58,0.8);
  border: 1px solid rgba(0,212,255,0.2);
  border-radius: 12px;
  padding: 60px 20px;
  text-align: center;
}

.empty-cart :deep(.el-empty__description) {
  color: #ccc;
}
</style>

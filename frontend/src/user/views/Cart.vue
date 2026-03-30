<template>
  <div class="cart-page">
    <div class="container">
      <h1 class="page-title">我的购物车</h1>

      <div v-if="购物车.cartItems.length > 0" class="cart-content">
        <!-- 购物车列表 -->
        <div class="cart-table">
          <div class="cart-header">
            <div class="col-check">
              <el-checkbox v-model="全选" @change="切换全选" /> 全选
            </div>
            <div class="col-product">商品信息</div>
            <div class="col-price">单价</div>
            <div class="col-quantity">数量</div>
            <div class="col-total">小计</div>
            <div class="col-action">操作</div>
          </div>

          <div class="cart-body">
            <div class="cart-item" v-for="item in 购物车.cartItems" :key="item.id">
              <div class="col-check">
                <el-checkbox v-model="item.selected" />
              </div>
              <div class="col-product">
                <div class="product-info">
                  <img v-lazyload="item.image" :alt="item.name" />
                  <div class="product-name">{{ item.name }}</div>
                </div>
              </div>
              <div class="col-price">¥{{ item.price }}</div>
              <div class="col-quantity">
                <el-input-number v-model="item.quantity" :min="1" :max="99" size="small" @change="更新数量 (item)" />
              </div>
              <div class="col-total">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
              <div class="col-action">
                <el-button type="danger" text size="small" @click="删除商品 (item.id)">删除</el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 结算栏 -->
        <div class="cart-footer">
          <div class="footer-left">
            <el-button @click="清空购物车">清空购物车</el-button>
            <el-button @click="继续购物">继续购物</el-button>
          </div>
          <div class="footer-right">
            <div class="selected-info">
              已选 <span class="highlight">{{ 已选数量 }}</span> 件商品
            </div>
            <div class="total-info">
              <span class="label">合计:</span>
              <span class="total-price">¥{{ 已选总额.toFixed (2) }}</span>
            </div>
            <el-button type="danger" size="large" @click="去结算">去结算</el-button>
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

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@user/stores/cart'
import { ElMessage, ElMessageBox } from 'element-plus'

const 路由 = useRouter()
const 购物车 = useCartStore()

const 全选 = ref(true)

const 已选数量 = computed(() => {
  return 购物车.cartItems.filter(商品 => 商品.selected !== false).length
})

const 已选总额 = computed(() => {
  return 购物车.cartItems
    .filter(商品 => 商品.selected !== false)
    .reduce((总计,商品) => 总计 + 商品.price * 商品.quantity, 0)
})

const 切换全选 = () => {
  购物车.cartItems.forEach(商品 => {
    商品.selected = 全选.value
  })
}

const 更新数量 = (商品) => {
  购物车.updateQuantity(商品.id, 商品.quantity)
}

const 删除商品 = (id) => {
  ElMessageBox.confirm('确定要删除该商品吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    购物车.removeFromCart(id)
    ElMessage.success('已删除')
  }).catch(() => {})
}

const 清空购物车 = () => {
  ElMessageBox.confirm('确定要清空购物车吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    购物车.clearCart()
    ElMessage.success('购物车已清空')
  }).catch(() => {})
}

const 继续购物 = () => {
  路由.push('/products')
}

// 别名用于模板
const continueShopping = 继续购物

const 去结算 = () => {
  if (已选数量.value === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  console.log('跳转支付页面', {
    path: '/payment',
    query: {
      amount: 已选总额.value.toFixed(2),
      quantity: 已选数量.value
    }
  })
  // 跳转到支付页面
  路由.push({
    path: '/payment',
    query: {
      amount: 已选总额.value.toFixed(2),
      quantity: 已选数量.value
    }
  })
}
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

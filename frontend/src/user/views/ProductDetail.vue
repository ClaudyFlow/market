<template>
  <div class="product-detail">
    <div class="container">
      <div class="detail-content">
        <!-- 商品图片 -->
        <div class="product-gallery">
          <div class="main-image">
            <img v-lazyload="商品.image" :alt="商品.name" />
          </div>
          <div class="thumbnail-list">
            <div class="thumbnail" v-for="i in 4" :key="i" :class="{ active: i === 1 }">
              <img v-lazyload="商品.image" alt="" />
            </div>
          </div>
        </div>

        <!-- 商品信息 -->
        <div class="product-info">
          <h1 class="product-title">{{ 商品.name }}</h1>
          <p class="product-subtitle">{{ 商品.description }}</p>

          <div class="price-section">
            <span class="current-price">¥{{ 商品.price }}</span>
            <span class="original-price">¥{{ 商品.originalPrice || 商品.price * 1.2 }}</span>
          </div>

          <div class="product-specs">
            <div class="spec-item">
              <span class="spec-label">颜色</span>
              <div class="spec-options">
                <el-radio-group v-model="选中颜色" size="small">
                  <el-radio-button v-for="color in 商品.colors" :key="color" :label="color">{{ color }}</el-radio-button>
                </el-radio-group>
              </div>
            </div>
            <div class="spec-item">
              <span class="spec-label">版本</span>
              <div class="spec-options">
                <el-radio-group v-model="选中版本" size="small">
                  <el-radio-button v-for="ver in 商品.versions" :key="ver" :label="ver">{{ ver }}</el-radio-button>
                </el-radio-group>
              </div>
            </div>
            <div class="spec-item">
              <span class="spec-label">数量</span>
              <el-input-number v-model="数量" :min="1" :max="99" size="small" />
            </div>
          </div>

          <div class="action-buttons">
            <el-button type="danger" size="large" @click="加入购物车">
              <el-icon><ShoppingCart /></el-icon> 加入购物车
            </el-button>
            <el-button type="warning" size="large" @click="立即购买">
              <el-icon><CreditCard /></el-icon> 立即购买
            </el-button>
          </div>

          <div class="service-promise">
            <div class="service-item">
              <el-icon><CircleCheck /></el-icon> 正品保障
            </div>
            <div class="service-item">
              <el-icon><Van /></el-icon> 极速配送
            </div>
            <div class="service-item">
              <el-icon><RefreshLeft /></el-icon> 7 天无理由退换
            </div>
          </div>
        </div>
      </div>

      <!-- 评价表单弹窗 -->
      <ReviewForm
        v-if="showReviewForm"
        :productId="商品.id"
        :product="{
          name: 商品.name,
          price: 商品.price,
          image: 商品.image
        }"
        @close="showReviewForm = false"
        @success="isPurchased = false"
      />

      <!-- 商品详情 -->
      <div class="detail-section">
        <div class="detail-tabs">
          <el-tabs v-model="当前标签">
            <el-tab-pane label="商品详情" name="detail">
              <div class="detail-content-text">
                <img :src="商品.image" alt="商品详情图" style="width: 100%; max-width: 800px; display: block; " />
                <p style="text-align: center; color: #999; padding: 40px;">商品详情图片展示区域</p>
              </div>
            </el-tab-pane>
            <el-tab-pane label="规格参数" name="specs">
              <table class="specs-table">
                <tr><td>品牌</td><td>Apple</td></tr>
                <tr><td>型号</td><td>{{ 商品.name }}</td></tr>
                <tr><td>产地</td><td>中国</td></tr>
                <tr><td>保修期</td><td>1 年</td></tr>
              </table>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>

      <!-- 用户评价 -->
      <div class="reviews-section">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon><ChatDotRound /></el-icon>
            用户评价
          </h2>
          <el-button type="primary" @click="checkLoginAndReview" :disabled="!isPurchased">
            <el-icon><Edit /></el-icon> {{ isPurchased ? '发表评价' : '购买后可评价' }}
          </el-button>
        </div>
        <!-- 评价面板组件 -->
        <ReviewPanel :productId="商品.id" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCartStore } from '@user/stores/cart'
import { ElMessage } from 'element-plus'
import { ChatDotRound, Edit, ShoppingCart, CreditCard, CircleCheck, Van, RefreshLeft } from '@element-plus/icons-vue'
import ReviewPanel from '@user/components/ReviewPanel.vue'
import ReviewForm from '@user/components/ReviewForm.vue'
import { checkReview } from '@user/api/review'

const 路由 = useRouter()
const 路由参数 = useRoute()
const 购物车 = useCartStore()

const 当前标签 = ref('detail')
const 选中颜色 = ref('深空黑')
const 选中版本 = ref('256GB')
const 数量 = ref(1)
const showReviewForm = ref(false)
const isPurchased = ref(true) // TODO: 根据实际订单状态判断

// 监听路由变化,跳转到页面顶部
watch(() => 路由参数.fullPath, () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}, { immediate: true })

// 组件挂载时滚动到顶部
onMounted(() => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
})

// 检查登录状态并打开评价表单
const checkLoginAndReview = () => {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录后再评价')
    // 跳转到登录页,并传递返回地址
    路由.push(`/login?redirect=${路由.currentRoute.value.fullPath}`)
    return
  }
  showReviewForm.value = true
}

const 商品 = computed(() => {
  const id = parseInt(路由参数.params.id)
  return {
    id,
    name: `商品${id} - Apple iPhone 15 Pro Max`,
    description: '256GB / 钛金属 / A17 Pro 芯片 / 5G 手机',
    price: 9999,
    originalPrice: 10999,
    image: `https://via.placeholder.com/500x500/f5f5f5/333?text=Product${id}`,
    colors: ['深空黑', '钛金属', '白色', '蓝色'],
    versions: ['128GB', '256GB', '512GB', '1TB']
  }
})

const 加入购物车 = () => {
  购物车.addToCart({
    ...商品.value,
    selectedColor: 选中颜色.value,
    selectedVersion: 选中版本.value
  })
  ElMessage.success('已加入购物车')
}

const 立即购买 = () => {
  路由.push('/order')
}
</script>

<style scoped>
@import '@user/assets/mall-style.css';

.product-detail {
  min-height: 100vh;
  padding: 20px 0;
  background: linear-gradient(180deg, rgba(0,212,255,0.15) 0%, rgba(10,14,26,0.8) 100%);
}

.container {
  max-width: 1200px;
  padding: 0 20px;
}

/* 详情内容 */
.detail-content {
  display: grid;
  grid-template-columns: 500px 1fr;
  gap: 30px;
  background: rgba(26,31,58,0.8);
  border: 1px solid rgba(0,212,255,0.2);
  padding: 30px;
  border-radius: 12px;
  
}

/* 商品图片 */
.product-gallery {
  border-right: 1px solid rgba(255,255,255,0.1);
  padding-right: 30px;
}

.main-image {
  
}

.main-image img {
  width: 100%;
  border-radius: 8px;
  border: 1px solid rgba(0,212,255,0.2);
}

.thumbnail-list {
  display: flex;
  gap: 10px;
}

.thumbnail {
  width: 80px;
  height: 80px;
  border: 2px solid rgba(255,255,255,0.1);
  border-radius: 8px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s;
}

.thumbnail:hover {
  border-color: var(--mall-primary);
}

.thumbnail.active {
  border-color: var(--mall-primary);
  box-shadow: 0 0 10px rgba(0,212,255,0.3);
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 商品信息 */
.product-info {
  padding-left: 20px;
}

.product-title {
  font-size: 20px;
  color: #fff;
  
}

.product-subtitle {
  font-size: 14px;
  color: #888;
  
}

.price-section {
  background: rgba(0,0,0,0.2);
  padding: 20px;
  border-radius: 8px;
  
  border: 1px solid rgba(0,212,255,0.1);
}

.current-price {
  color: var(--mall-accent);
  font-size: 32px;
  font-weight: bold;
  
}

.original-price {
  color: #666;
  font-size: 16px;
  text-decoration: line-through;
}

/* 规格选择 */
.product-specs {
  
}

.spec-item {
  display: flex;
  align-items: center;
  
}

.spec-label {
  width: 60px;
  color: #888;
  font-size: 14px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 15px;
  
}

.action-buttons .el-button {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  color: #000;
  font-weight: bold;
  box-shadow: 0 0 10px rgba(0,212,255,0.3);
}

.action-buttons .el-button:hover {
  box-shadow: 0 0 20px rgba(0,212,255,0.6);
}

/* 服务承诺 */
.service-promise {
  display: flex;
  gap: 20px;
  padding-top: 20px;
  border-top: 1px solid rgba(255,255,255,0.1);
}

.service-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #888;
  font-size: 13px;
}

.service-item .el-icon {
  color: var(--mall-primary);
}

/* 详情区块 */
.detail-section {
  background: rgba(26,31,58,0.8);
  border: 1px solid rgba(0,212,255,0.2);
  border-radius: 12px;
  padding: 20px;
  
  box-shadow: 0 0 20px rgba(0,212,255,0.1);
}

/* 选项卡样式 - 更显眼 */
.detail-tabs {
  background: rgba(0,0,0,0.2);
  border-radius: 8px;
  padding: 10px;
}

.detail-tabs :deep(.el-tabs__header) {
  
}

.detail-tabs :deep(.el-tabs__item) {
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(0,212,255,0.1);
  transition: all 0.3s ease;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.detail-tabs :deep(.el-tabs__item:hover) {
  background: rgba(0,212,255,0.1);
  border-color: rgba(0,212,255,0.3);
  color: var(--mall-primary);
}

.detail-tabs :deep(.el-tabs__item.is-active) {
  background: linear-gradient(135deg, rgba(0,212,255,0.3), rgba(0,255,136,0.2));
  border-color: var(--mall-primary);
  color: #fff;
  box-shadow: 0 0 15px rgba(0,212,255,0.4);
}

.detail-tabs :deep(.el-tabs__active-bar) {
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-accent));
  height: 3px;
  box-shadow: 0 0 10px rgba(0,212,255,0.5);
}

.detail-tabs :deep(.el-tab-pane) {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.detail-content-text {
  padding: 40px;
}

.specs-table {
  width: 100%;
  max-width: 600px;
  border-collapse: collapse;
}

.specs-table td {
  padding: 12px;
  border-bottom: 1px solid rgba(255,255,255,0.05);
  color: #ccc;
}

.specs-table td:first-child {
  width: 120px;
  color: #888;
  background: rgba(0,0,0,0.2);
}

/* 评价 */
.review-summary {
  padding: 20px;
  background: rgba(0,0,0,0.2);
  border-radius: 8px;
  
}

.rating-score {
  display: flex;
  align-items: center;
  gap: 15px;
}

.score {
  font-size: 48px;
  color: var(--mall-accent);
  font-weight: bold;
}

.stars {
  color: var(--mall-accent);
  display: flex;
}

.review-count {
  color: #888;
}

.review-item {
  display: flex;
  gap: 15px;
  padding: 15px 0;
  border-bottom: 1px solid rgba(255,255,255,0.05);
}

.reviewer {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: #888;
}

.review-content p {
  
  color: #ccc;
}

.review-stars {
  color: #ff9900;
  display: flex;
}

.write-review-btn {
  
  text-align: center;
}

/* 评价区块 */
.reviews-section {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  padding: 24px;
  
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  padding-bottom: 16px;
  border-bottom: 1px solid var(--mall-border-light);
}

.section-header .section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: bold;
  color: var(--mall-text-primary);
}

.section-header .section-title .el-icon {
  font-size: 24px;
  color: var(--mall-primary);
}
</style>

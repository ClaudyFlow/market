<template>
  <div class="product-detail">
    <div class="container">
      <!-- 面包屑 -->
      <div class="breadcrumb">
        <router-link to="/">首页</router-link>
        <span>/</span>
        <router-link to="/products">商品列表</router-link>
        <span>/</span>
        <span>{{ product.name }}</span>
      </div>

      <div class="detail-content">
        <!-- 商品图片 -->
        <div class="product-gallery">
          <div class="main-image">
            <img :src="product.image" :alt="product.name" />
          </div>
          <div class="thumbnail-list">
            <div class="thumbnail" v-for="i in 4" :key="i" :class="{ active: i === 1 }">
              <img :src="product.image" alt="" />
            </div>
          </div>
        </div>

        <!-- 商品信息 -->
        <div class="product-info">
          <h1 class="product-title">{{ product.name }}</h1>
          <p class="product-subtitle">{{ product.description }}</p>

          <div class="price-section">
            <span class="current-price">¥{{ product.price }}</span>
            <span class="original-price">¥{{ product.originalPrice || product.price * 1.2 }}</span>
          </div>

          <div class="product-specs">
            <div class="spec-item">
              <span class="spec-label">颜色</span>
              <div class="spec-options">
                <el-radio-group v-model="selectedColor" size="small">
                  <el-radio-button v-for="color in product.colors" :key="color" :label="color">{{ color }}</el-radio-button>
                </el-radio-group>
              </div>
            </div>
            <div class="spec-item">
              <span class="spec-label">版本</span>
              <div class="spec-options">
                <el-radio-group v-model="selectedVersion" size="small">
                  <el-radio-button v-for="ver in product.versions" :key="ver" :label="ver">{{ ver }}</el-radio-button>
                </el-radio-group>
              </div>
            </div>
            <div class="spec-item">
              <span class="spec-label">数量</span>
              <el-input-number v-model="quantity" :min="1" :max="99" size="small" />
            </div>
          </div>

          <div class="action-buttons">
            <el-button type="danger" size="large" @click="addToCart">
              <el-icon><ShoppingCart /></el-icon> 加入购物车
            </el-button>
            <el-button type="warning" size="large" @click="buyNow">
              <el-icon><CreditCard /></el-icon> 立即购买
            </el-button>
          </div>

          <div class="service-promise">
            <div class="service-item">
              <el-icon><CircleCheck /></el-icon> 正品保障
            </div>
            <div class="service-item">
              <el-icon><Truck /></el-icon> 极速配送
            </div>
            <div class="service-item">
              <el-icon><RefreshLeft /></el-icon> 7 天无理由退换
            </div>
          </div>
        </div>
      </div>

      <!-- 商品详情 -->
      <div class="detail-section">
        <div class="detail-tabs">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="商品详情" name="detail">
              <div class="detail-content-text">
                <img :src="product.image" alt="商品详情图" style="width: 100%; max-width: 800px; display: block; margin: 0 auto;" />
                <p style="text-align: center; color: #999; padding: 40px;">商品详情图片展示区域</p>
              </div>
            </el-tab-pane>
            <el-tab-pane label="规格参数" name="specs">
              <table class="specs-table">
                <tr><td>品牌</td><td>Apple</td></tr>
                <tr><td>型号</td><td>{{ product.name }}</td></tr>
                <tr><td>产地</td><td>中国</td></tr>
                <tr><td>保修期</td><td>1 年</td></tr>
              </table>
            </el-tab-pane>
            <el-tab-pane label="用户评价" name="reviews">
              <div class="reviews-section">
                <div class="review-summary">
                  <div class="rating-score">
                    <span class="score">4.9</span>
                    <div class="stars">
                      <el-icon v-for="i in 5" :key="i"><StarFilled /></el-icon>
                    </div>
                    <span class="review-count">累计评价 10 万+</span>
                  </div>
                </div>
                <div class="review-list">
                  <div class="review-item" v-for="i in 3" :key="i">
                    <div class="reviewer">
                      <el-avatar :size="40">用</el-avatar>
                      <span>用户***{{ i }}</span>
                    </div>
                    <div class="review-content">
                      <div class="review-stars">
                        <el-icon v-for="j in 5" :key="j"><StarFilled /></el-icon>
                      </div>
                      <p>商品质量很好，物流也很快，包装完整，非常满意！</p>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCartStore } from '@user/stores/cart'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()

const activeTab = ref('detail')
const selectedColor = ref('深空黑')
const selectedVersion = ref('256GB')
const quantity = ref(1)

const product = computed(() => {
  const id = parseInt(route.params.id)
  return {
    id,
    name: `商品 ${id} - Apple iPhone 15 Pro Max`,
    description: '256GB / 钛金属 / A17 Pro 芯片 / 5G 手机',
    price: 9999,
    originalPrice: 10999,
    image: `https://via.placeholder.com/500x500/f5f5f5/333?text=Product${id}`,
    colors: ['深空黑', '钛金属', '白色', '蓝色'],
    versions: ['128GB', '256GB', '512GB', '1TB']
  }
})

const addToCart = () => {
  cartStore.addToCart({
    ...product.value,
    selectedColor: selectedColor.value,
    selectedVersion: selectedVersion.value
  })
  ElMessage.success('已加入购物车')
}

const buyNow = () => {
  router.push('/order')
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
  margin: 0 auto;
  padding: 0 20px;
}

.breadcrumb {
  margin-bottom: 20px;
  font-size: 13px;
  color: #888;
}

.breadcrumb a {
  color: var(--mall-primary);
  text-decoration: none;
}

.breadcrumb a:hover {
  color: var(--mall-secondary);
}

.breadcrumb span {
  margin: 0 10px;
  color: #666;
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
  margin-bottom: 20px;
}

/* 商品图片 */
.product-gallery {
  border-right: 1px solid rgba(255,255,255,0.1);
  padding-right: 30px;
}

.main-image {
  margin-bottom: 15px;
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
  margin-bottom: 10px;
}

.product-subtitle {
  font-size: 14px;
  color: #888;
  margin-bottom: 20px;
}

.price-section {
  background: rgba(0,0,0,0.2);
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 25px;
  border: 1px solid rgba(0,212,255,0.1);
}

.current-price {
  color: var(--mall-accent);
  font-size: 32px;
  font-weight: bold;
  margin-right: 15px;
}

.original-price {
  color: #666;
  font-size: 16px;
  text-decoration: line-through;
}

/* 规格选择 */
.product-specs {
  margin-bottom: 30px;
}

.spec-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
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
  margin-bottom: 25px;
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
  margin-bottom: 20px;
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
  margin-top: 8px;
  color: #ccc;
}

.review-stars {
  color: #ff9900;
  display: flex;
}
</style>

<template>
  <div class="product-info-panel">
    <!-- 商品标题 -->
    <div class="product-title">
      <h1>{{ product.name }}</h1>
      <p class="product-subtitle">{{ product.description }}</p>
    </div>

    <!-- 价格区域 -->
    <div class="price-section">
      <span class="current-price">{{ formatPrice(product.price) }}</span>
      <span class="original-price">{{ formatPrice(product.originalPrice) }}</span>
      <span class="discount-badge">{{ product.discount }}% OFF</span>
    </div>

    <!-- 促销信息 -->
    <div class="promotion-section" v-if="product.promotions?.length">
      <div class="promotion-item" v-for="(promo, index) in product.promotions" :key="index">
        <el-tag type="danger" size="small">{{ promo }}</el-tag>
      </div>
    </div>

    <!-- 规格选择 - 颜色 -->
    <div class="specs-section" v-if="product.colors?.length">
      <div class="spec-label">颜色</div>
      <div class="spec-options">
        <div
          v-for="color in product.colors"
          :key="color"
          class="spec-option"
          :class="{ active: selectedSpecs.color === color }"
          @click="$emit('update:selectedSpecs', { ...selectedSpecs, color })"
        >
          {{ color }}
        </div>
      </div>
    </div>

    <!-- 规格选择 - 版本 -->
    <div class="specs-section" v-if="product.versions?.length">
      <div class="spec-label">版本</div>
      <div class="spec-options">
        <div
          v-for="version in product.versions"
          :key="version"
          class="spec-option"
          :class="{ active: selectedSpecs.version === version }"
          @click="$emit('update:selectedSpecs', { ...selectedSpecs, version })"
        >
          {{ version }}
        </div>
      </div>
    </div>

    <!-- 数量选择 -->
    <div class="quantity-section">
      <div class="spec-label">数量</div>
      <el-input-number
        :model-value="quantity"
        @update:model-value="$emit('update:quantity', $event)"
        :min="1"
        :max="product.stock"
        size="small"
      />
      <span class="stock-info">库存 {{ product.stock }} 件</span>
    </div>

    <!-- 已选规格 -->
    <div class="selected-specs" v-if="selectedSpecs.color || selectedSpecs.version">
      <span class="specs-label">已选</span>
      <span class="specs-value">{{ selectedSpecsText }}</span>
    </div>

    <!-- 服务承诺 -->
    <div class="service-promise" v-if="product.services?.length">
      <div class="service-item" v-for="(service, index) in product.services" :key="index">
        <el-icon><Check /></el-icon>
        <span>{{ service }}</span>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <button class="add-cart-btn" @click="$emit('add-to-cart')">
        <el-icon><ShoppingCart /></el-icon>
        加入购物车
      </button>
      <button class="buy-now-btn" @click="$emit('buy-now')">
        立即购买
      </button>
    </div>

    <!-- 社交操作 -->
    <div class="social-actions">
      <button class="social-btn" @click="$emit('toggle-favorite')">
        <el-icon><Star :class="{ 'is-favorited': isFavorited }" /></el-icon>
        <span>收藏 {{ favoriteCount }}</span>
      </button>
      <button class="social-btn" @click="$emit('share')">
        <el-icon><Share /></el-icon>
        <span>分享</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Check, ShoppingCart, Star, Share } from '@element-plus/icons-vue'
import type { Product, SelectedSpecs } from '@user/types/product'

interface Props {
  product: Product
  selectedSpecs: SelectedSpecs
  quantity: number
  formatPrice: (price: number) => string
  selectedSpecsText: string
  isFavorited?: boolean
  favoriteCount?: number
}

defineProps<Props>()

defineEmits<{
  'update:selectedSpecs': [specs: SelectedSpecs]
  'update:quantity': [qty: number]
  'add-to-cart': []
  'buy-now': []
  'toggle-favorite': []
  'share': []
}>()
</script>

<style scoped>
.product-info-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.product-title h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1.4;
}

.product-subtitle {
  margin: 8px 0 0;
  color: #666;
  font-size: 14px;
}

.price-section {
  display: flex;
  align-items: baseline;
  gap: 12px;
  padding: 16px;
  background: linear-gradient(135deg, #fff5f5 0%, #fff 100%);
  border-radius: 12px;
}

.current-price {
  font-size: 32px;
  font-weight: 700;
  color: #ff4444;
}

.original-price {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
}

.discount-badge {
  padding: 4px 8px;
  background: #ff4444;
  color: #fff;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.promotion-section {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.promotion-item {
  flex-shrink: 0;
}

.specs-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.spec-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.spec-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.spec-option {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}

.spec-option:hover {
  border-color: var(--mall-primary, #00d4ff);
}

.spec-option.active {
  border-color: var(--mall-primary, #00d4ff);
  background: rgba(0, 212, 255, 0.1);
  color: var(--mall-primary, #00d4ff);
  font-weight: 500;
}

.quantity-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stock-info {
  font-size: 13px;
  color: #999;
}

.selected-specs {
  display: flex;
  gap: 8px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  font-size: 14px;
}

.specs-label {
  color: #666;
  font-weight: 500;
}

.specs-value {
  color: #1a1a1a;
}

.service-promise {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.service-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #666;
}

.service-item .el-icon {
  color: #52c41a;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.add-cart-btn,
.buy-now-btn {
  flex: 1;
  padding: 14px 24px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s;
}

.add-cart-btn {
  background: linear-gradient(135deg, #00d4ff 0%, #00ff88 100%);
  color: #fff;
}

.add-cart-btn:hover {
  box-shadow: 0 8px 20px rgba(0, 212, 255, 0.3);
}

.buy-now-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff4444 100%);
  color: #fff;
}

.buy-now-btn:hover {
  box-shadow: 0 8px 20px rgba(255, 68, 68, 0.3);
}

.social-actions {
  display: flex;
  gap: 12px;
}

.social-btn {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
  transition: all 0.2s;
}

.social-btn:hover {
  border-color: var(--mall-primary, #00d4ff);
  color: var(--mall-primary, #00d4ff);
}

.is-favorited {
  color: #ffd700;
}
</style>

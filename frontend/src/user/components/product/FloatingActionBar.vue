<template>
  <div class="floating-action-bar" v-show="visible">
    <div class="floating-actions">
      <!-- 收藏按钮 -->
      <button class="floating-item" @click="$emit('toggle-favorite')">
        <el-icon>
          <Star :class="{ 'is-favorited': isFavorited }" />
        </el-icon>
        <span class="item-label">收藏</span>
        <span class="item-count" v-if="favoriteCount > 0">{{ favoriteCount }}</span>
      </button>

      <!-- 回到顶部 -->
      <button class="floating-item" @click="$emit('scroll-to-top')">
        <el-icon><Top /></el-icon>
        <span class="item-label">顶部</span>
      </button>

      <!-- 购物车 -->
      <button class="floating-item cart-item" @click="$emit('add-to-cart')">
        <el-icon><ShoppingCart /></el-icon>
        <span class="item-label">购物车</span>
        <el-badge :value="cartCount" :hidden="cartCount === 0" class="cart-badge" />
      </button>
    </div>

    <!-- 立即购买按钮 -->
    <button class="floating-buy-btn" @click="$emit('buy-now')">
      立即购买
    </button>
  </div>
</template>

<script setup lang="ts">
import { Star, Top, ShoppingCart } from '@element-plus/icons-vue'

interface Props {
  isFavorited?: boolean
  favoriteCount?: number
  cartCount?: number
  visible?: boolean
}

withDefaults(defineProps<Props>(), {
  isFavorited: false,
  favoriteCount: 0,
  cartCount: 0,
  visible: true
})

defineEmits<{
  'toggle-favorite': []
  'scroll-to-top': []
  'add-to-cart': []
  'buy-now': []
}>()
</script>

<style scoped>
.floating-action-bar {
  position: fixed;
  bottom: 80px;
  right: 20px;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: flex-end;
}

.floating-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  padding: 8px;
}

.floating-item {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 10px 12px;
  border: none;
  background: transparent;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 60px;
}

.floating-item:hover {
  background: #f5f5f5;
}

.floating-item .el-icon {
  font-size: 20px;
  color: #666;
}

.item-label {
  font-size: 12px;
  color: #666;
}

.item-count {
  position: absolute;
  top: 4px;
  right: 4px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  background: #ff4444;
  color: #fff;
  border-radius: 8px;
  font-size: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.is-favorited {
  color: #ffd700 !important;
}

.cart-badge {
  position: absolute;
  top: 0;
  right: 0;
}

.floating-buy-btn {
  padding: 14px 32px;
  border: none;
  border-radius: 24px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff4444 100%);
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba(255, 68, 68, 0.4);
  transition: all 0.2s;
}

.floating-buy-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(255, 68, 68, 0.5);
}

.floating-buy-btn:active {
  transform: translateY(0);
}

@media (max-width: 768px) {
  .floating-action-bar {
    bottom: 60px;
    right: 10px;
  }

  .floating-item {
    min-width: 50px;
    padding: 8px 10px;
  }

  .floating-buy-btn {
    padding: 12px 24px;
    font-size: 14px;
  }
}
</style>

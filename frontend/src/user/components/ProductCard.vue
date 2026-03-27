<template>
  <article class="product-card" @click="handleClick">
    <div class="sale-image">
      <img v-lazyload="product.image" :alt="product.name" />
      <FavoriteButton
        :productId="product.id"
        circle
        :showText="false"
        class="favorite-overlay"
      />
    </div>
    <div class="sale-info">
      <div class="sale-content">
        <h3 class="item-name">{{ product.name }}</h3>
        <div class="item-meta">
          <div class="item-rating">
            <el-icon><StarFilled /></el-icon>
            <span>{{ (product.rating || 5.0).toFixed(1) }}</span>
            <span class="item-sales">销量 {{ product.sales || '10 万+' }}</span>
          </div>
        </div>
        <div class="item-type-tag" :class="product.type">
          {{ typeText(product.type) }}
        </div>
        <div class="price-row">
          <div class="price-wrapper">
            <span class="current-price">¥{{ product.price }}</span>
            <span v-if="discount" class="original-price">¥{{ product.originalPrice }}</span>
          </div>
          <span v-if="discount" class="sale-tag">
            <span class="discount">{{ discount }}折</span>
          </span>
        </div>
        <div class="progress-container" :style="{ '--progress-color': 获取进度颜色 (remainingPercent) }">
          <el-progress
            :percentage="remainingPercent"
            :format="percent => percent.toFixed(2) + '%'"
            :color="获取进度颜色 (remainingPercent)"
            :stroke-width="10"
            class="full-width-progress"
          />
        </div>
        <span class="sold-info">仅剩{{ remaining }}件</span>
      </div>
      <div class="sale-action">
        <el-button
          v-if="remaining > 0"
          type="primary"
          size="small"
          @click.stop="handleAddToCart"
        >
          <el-icon><ShoppingCart /></el-icon> 加入购物车
        </el-button>
        <el-button
          v-else
          disabled
          size="small"
          class="sold-out-btn"
        >
          <el-icon><CircleClose /></el-icon> 售罄
        </el-button>
      </div>
    </div>
  </article>
</template>

<script setup lang="ts">
import { ShoppingCart, CircleClose, StarFilled } from '@element-plus/icons-vue'
import { computed } from 'vue'
import { 计算折扣 as calculateDiscount, 获取进度颜色 as getProgressColor } from '@user/util/discount'

interface Product {
  id: number
  name: string
  price: number
  originalPrice: number
  type?: string
  rating?: number
  sales?: string
  image?: string
  remainCount?: number
  soldCount?: number
  salesPercent?: number
  remaining?: number
  [key: string]: unknown
}

const props = defineProps<{
  product: Product
}>()

const emit = defineEmits<{
  click: [product: Product]
  'add-to-cart': [product: Product]
}>()

// 计算折扣率
const discount = computed(() => {
  return calculateDiscount(props.product.price, props.product.originalPrice)
})

// 计算总数量 = 剩余 + 已售
const totalCount = computed(() => {
  if (props.product.remainCount !== undefined && props.product.soldCount !== undefined) {
    return props.product.remainCount + props.product.soldCount
  }
  return null
})

// 计算已售百分比
const soldPercent = computed(() => {
  if (props.product.salesPercent !== undefined) {
    return props.product.salesPercent
  }
  if (props.product.soldCount !== undefined && totalCount.value > 0) {
    return Math.round((props.product.soldCount / totalCount.value) * 100)
  }
  return 70
})

// 计算剩余数量
const remaining = computed(() => {
  if (props.product.remaining !== undefined) {
    return props.product.remaining
  }
  if (props.product.remainCount !== undefined) {
    return props.product.remainCount
  }
  if (totalCount.value !== null) {
    return Math.round(totalCount.value * (1 - soldPercent.value / 100))
  }
  return 0
})

// 计算剩余百分比
const remainingPercent = computed(() => {
  return 100 - soldPercent.value
})

const typeText = (type: string | undefined) => {
  const map: Record<string, string> = { digital: '数码', appliance: '家电', fashion: '服饰', beauty: '美妆' }
  return map[type || ''] || '商品'
}

const handleClick = () => {
  emit('click', props.product)
}

const handleAddToCart = () => {
  emit('add-to-cart', props.product)
}

// 模板中使用的中文函数别名
const 计算折扣 = calculateDiscount
const 获取进度颜色 = getProgressColor
</script>

<style scoped>
.product-card {
  background: rgba(26, 31, 58, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.product-card:hover {
  transform: translateY(-8px);
  border-color: var(--mall-primary);
  box-shadow: 0 10px 40px rgba(0, 212, 255, 0.2);
}

.sale-image {
  position: relative;
  padding: 20px;
  background: rgba(0, 0, 0, 0.3);
  aspect-ratio: 16 / 9;
}

.sale-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.sale-info {
  display: block;
  padding: 15px;
}

.sale-content {
  width: 100%;
}

.item-name {
  font-size: 14px;
  color: #fff;
  
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.item-meta {
  
}

.item-rating {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--mall-accent);
  font-size: 13px;
}

.item-rating .el-icon {
  color: var(--mall-accent);
}

.item-rating .item-sales {
  font-size: 12px;
  color: #888;
}

.item-type-tag {
  display: inline-block;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: bold;
  
}

.item-type-tag.digital {
  background: rgba(0, 212, 255, 0.8);
  color: #fff;
}

.item-type-tag.appliance {
  background: rgba(0, 255, 136, 0.8);
  color: #000;
}

.item-type-tag.fashion {
  background: rgba(255, 51, 102, 0.8);
  color: #fff;
}

.item-type-tag.beauty {
  background: rgba(163, 53, 238, 0.8);
  color: #fff;
}

.price-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  
}

.price-wrapper {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.current-price {
  color: var(--mall-accent);
  font-size: 18px;
  font-weight: bold;
  white-space: nowrap;
}

.original-price {
  color: #666;
  font-size: 12px;
  text-decoration: line-through;
  white-space: nowrap;
}

.sale-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--mall-accent), #ff8800);
  padding: 3px 8px;
  border-radius: 12px;
  height: 22px;
  flex-shrink: 0;
}

.sale-tag .discount {
  color: #fff;
  font-size: 11px;
  font-weight: bold;
  
  white-space: nowrap;
}

.progress-container {
  
}

.progress-container :deep(.el-progress__text) {
  text-align: right;
  font-size: 12px !important;
  color: var(--progress-color) !important;
  font-weight: bold;
}

.full-width-progress {
  width: 100%;
}

.sold-info {
  font-size: 12px;
  color: #888;
}

.sale-action {
  
}

.sale-action .el-button {
  width: 100%;
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  color: #000;
  font-weight: bold;
  box-shadow: 0 0 10px rgba(0, 212, 255, 0.3);
}

.sale-action .el-button:hover {
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.6);
}

.sale-action .sold-out-btn {
  width: 100%;
  background: #cccccc;
  border: none;
  color: #000000;
  cursor: default;
  box-shadow: none;
  pointer-events: none;
}

.sale-action .sold-out-btn:hover {
  background: #cccccc;
  box-shadow: none;
  transform: none;
}
</style>

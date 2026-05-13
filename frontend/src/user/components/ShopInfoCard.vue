<template>
  <div class="shop-info-card" :class="{ 'is-collapsed': collapsed }">
    <!-- 店铺头像和基本信息 -->
    <div class="shop-header">
      <div class="shop-avatar">
        <img :src="shopInfo.logo || '/images/shop-logo-default.jpg'" alt="店铺 logo" />
        <div class="avatar-glow"></div>
      </div>
      <div class="shop-basic">
        <div class="shop-name-row">
          <h3 class="shop-name">{{ shopInfo.name }}</h3>
          <el-tag 
            v-if="shopInfo.certified" 
            size="small" 
            type="success"
            class="certified-tag"
          >
            <i class="fas fa-check-circle"></i>
            已认证
          </el-tag>
        </div>
        <div class="shop-slogan" v-if="shopInfo.slogan">{{ shopInfo.slogan }}</div>
        <div class="shop-rating-row">
          <el-rate 
            v-model="shopInfo.rating" 
            disabled 
            :colors="['#ffd700', '#ffd700', '#ffd700']"
            size="small"
          />
          <span class="rating-score">{{ shopInfo.rating }}分</span>
          <span class="divider">|</span>
          <span class="followers">
            <i class="fas fa-heart"></i>
            {{ formatNumber(shopInfo.followers) }}人关注
          </span>
        </div>
      </div>
    </div>

    <!-- 店铺统计信息 -->
    <div class="shop-stats">
      <div class="stat-item" v-for="stat in stats" :key="stat.label">
        <div class="stat-value">
          <count-up :end="stat.value" :duration="1" />
        </div>
        <div class="stat-label">{{ stat.label }}</div>
      </div>
    </div>

    <!-- 店铺标签 -->
    <div class="shop-tags" v-if="shopInfo.tags && shopInfo.tags.length > 0">
      <el-tag 
        v-for="tag in shopInfo.tags" 
        :key="tag"
        size="small"
        class="tag-item"
      >
        {{ tag }}
      </el-tag>
    </div>

    <!-- 操作按钮 -->
    <div class="shop-actions">
      <el-button 
        :type="isFollowing ? 'primary' : 'default'"
        class="action-btn follow-btn"
        @click="handleFollow"
        :loading="followingLoading"
      >
        <i :class="isFollowing ? 'fas fa-heart' : 'fas fa-heart-o'"></i>
        {{ isFollowing ? '已关注' : '关注' }}
      </el-button>
      <el-button 
        class="action-btn"
        @click="handleContact"
      >
        <i class="fas fa-comments"></i>
        联系
      </el-button>
      <el-button 
        class="action-btn"
        @click="handleShare"
      >
        <i class="fas fa-share-alt"></i>
        分享
      </el-button>
    </div>

    <!-- 展开/收起 -->
    <div class="collapse-btn" @click="collapsed = !collapsed">
      <i :class="collapsed ? 'fas fa-chevron-down' : 'fas fa-chevron-up'"></i>
      <span>{{ collapsed ? '展开' : '收起' }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface ShopInfo {
  id: number | string
  name: string
  logo?: string
  slogan?: string
  rating: number
  followers: number
  productCount: number
  positiveRate: number
  openYears: number
  certified: boolean
  tags?: string[]
  description?: string
}

interface Props {
  shopInfo: ShopInfo
  isFollowing?: boolean
  collapsed?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  isFollowing: false,
  collapsed: false
})

const emit = defineEmits<{
  follow: []
  contact: []
  share: []
}>()

const followingLoading = ref(false)

// 统计信息
const stats = computed(() => [
  { label: '商品', value: props.shopInfo.productCount },
  { label: '好评率', value: props.shopInfo.positiveRate },
  { label: '开店时长', value: props.shopInfo.openYears, suffix: '年' }
])

// 格式化数字
const formatNumber = (num: number): string => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

// 关注店铺
const handleFollow = async () => {
  if (followingLoading.value) return
  followingLoading.value = true
  try {
    emit('follow')
  } finally {
    followingLoading.value = false
  }
}

// 联系商家
const handleContact = () => {
  emit('contact')
}

// 分享店铺
const handleShare = () => {
  emit('share')
}

// 简单的计数组件
const CountUp = {
  props: ['end', 'duration'],
  data() {
    return {
      displayValue: 0
    }
  },
  mounted() {
    this.animate()
  },
  methods: {
    animate() {
      const start = 0
      const end = Number(this.end)
      const duration = (this.duration || 1) * 1000
      const startTime = performance.now()

      const step = (currentTime: number) => {
        const progress = Math.min((currentTime - startTime) / duration, 1)
        this.displayValue = Math.floor(progress * (end - start) + start)
        
        if (progress < 1) {
          requestAnimationFrame(step)
        }
      }
      
      requestAnimationFrame(step)
    }
  },
  render() {
    return this.displayValue
  }
}
</script>

<style scoped>
.shop-info-card {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.shop-info-card.is-collapsed {
  padding-bottom: 10px;
}

/* ==================== 店铺头部 ==================== */
.shop-header {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.shop-avatar {
  position: relative;
  width: 80px;
  height: 80px;
  flex-shrink: 0;
}

.shop-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 12px;
  border: 2px solid var(--mall-primary);
}

.avatar-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 12px;
  box-shadow: 0 0 20px var(--mall-glow);
  animation: glow-pulse 2s ease-in-out infinite;
}

@keyframes glow-pulse {
  0%, 100% {
    opacity: 0.5;
  }
  50% {
    opacity: 1;
  }
}

.shop-basic {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow: hidden;
}

.shop-name-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.shop-name {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  margin: 0;
}

.certified-tag {
  --el-tag-bg-color: rgba(0, 255, 136, 0.1);
  --el-tag-border-color: rgba(0, 255, 136, 0.3);
  --el-tag-text-color: #00ff88;
}

.shop-slogan {
  font-size: 12px;
  color: var(--mall-text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.shop-rating-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.shop-rating-row :deep(.el-rate) {
  font-size: 14px;
}

.rating-score {
  font-size: 12px;
  color: #ffd700;
}

.divider {
  color: var(--mall-text-muted);
  font-size: 12px;
}

.followers {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--mall-text-secondary);
}

.followers i {
  font-size: 10px;
  color: #ff6666;
}

/* ==================== 统计信息 ==================== */
.shop-stats {
  display: flex;
  justify-content: space-around;
  padding: 15px 0;
  margin-bottom: 15px;
  border-top: 1px solid var(--mall-border);
  border-bottom: 1px solid var(--mall-border);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: var(--mall-primary);
}

.stat-label {
  font-size: 12px;
  color: var(--mall-text-muted);
}

/* ==================== 店铺标签 ==================== */
.shop-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 15px;
}

.tag-item {
  --el-tag-bg-color: rgba(0, 0, 0, 0.6);
  --el-tag-border-color: rgba(255, 255, 255, 0.2);
  --el-tag-text-color: #fff;
}

/* ==================== 操作按钮 ==================== */
.shop-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  flex: 1;
}

.follow-btn {
  --el-button-hover-border-color: var(--mall-primary);
}

/* ==================== 展开/收起按钮 ==================== */
.collapse-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid var(--mall-border);
  font-size: 12px;
  color: var(--mall-text-muted);
  cursor: pointer;
  transition: color 0.3s ease;
}

.collapse-btn:hover {
  color: var(--mall-primary);
}

.collapse-btn i {
  font-size: 10px;
}

/* ==================== 响应式 ==================== */
@media (max-width: 768px) {
  .shop-info-card {
    padding: 15px;
  }

  .shop-avatar {
    width: 60px;
    height: 60px;
  }

  .shop-name {
    font-size: 16px;
  }

  .shop-stats {
    padding: 10px 0;
  }

  .stat-value {
    font-size: 16px;
  }

  .shop-actions {
    flex-direction: column;
  }

  .action-btn {
    width: 100%;
  }
}
</style>

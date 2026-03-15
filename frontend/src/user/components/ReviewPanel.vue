<template>
  <div class="review-panel">
    <!-- 评分汇总 -->
    <div class="review-summary" v-if="ratingInfo">
      <div class="rating-score">
        <span class="score">{{ ratingInfo.averageRating.toFixed(1) }}</span>
        <div class="stars">
          <RatingStars :model-value="Math.round(ratingInfo.averageRating)" readonly />
        </div>
        <span class="review-count">{{ ratingInfo.reviewCount }}条评价</span>
      </div>
      <!-- 评分分布 -->
      <div class="rating-distribution">
        <div
          v-for="star in [5, 4, 3, 2, 1]"
          :key="star"
          class="rating-bar"
        >
          <span class="star-label">{{ star }}星</span>
          <div class="bar-container">
            <div
              class="bar-fill"
              :style="{ width: getPercentage(star) }"
            ></div>
          </div>
          <span class="bar-count">{{ ratingInfo.ratingDistribution[star] || 0 }}</span>
        </div>
      </div>
    </div>

    <!-- 评价列表 -->
    <div class="review-list">
      <div
        v-for="review in reviews"
        :key="review.id"
        class="review-item"
      >
        <div class="reviewer-info">
          <img
            :src="review.userAvatar || '/default-avatar.png'"
            :alt="review.userName"
            class="reviewer-avatar"
          />
          <div class="reviewer-details">
            <span class="reviewer-name">{{ review.userName }}</span>
            <div class="review-meta">
              <RatingStars :model-value="review.rating" readonly size="small" />
              <span class="review-time">{{ formatTime(review.createdAt) }}</span>
            </div>
          </div>
        </div>
        <div class="review-content">
          <p>{{ review.content }}</p>
        </div>
        <div class="review-product" v-if="review.productImage">
          <img :src="review.productImage" :alt="review.productName" class="product-thumb" />
          <span class="product-name">{{ review.productName }}</span>
          <span class="product-price">¥{{ review.productPrice }}</span>
        </div>
      </div>

      <div v-if="reviews.length === 0" class="empty-reviews">
        暂无评价
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getProductReviews, getProductRating, type Review, type RatingInfo } from '@/user/api/review'
import RatingStars from './RatingStars.vue'

const props = defineProps<{
  productId: number
}>()

const reviews = ref<Review[]>([])
const ratingInfo = ref<RatingInfo | null>(null)

const loadReviews = async () => {
  try {
    const res = await getProductReviews(props.productId)
    reviews.value = res.data.data || []
  } catch (error) {
    console.error('加载评价失败:', error)
  }
}

const loadRating = async () => {
  try {
    const res = await getProductRating(props.productId)
    ratingInfo.value = res.data.data
  } catch (error) {
    console.error('加载评分失败:', error)
  }
}

const getPercentage = (star: number): string => {
  if (!ratingInfo.value || !ratingInfo.value.reviewCount) return '0%'
  const count = ratingInfo.value.ratingDistribution[star] || 0
  return `${(count / ratingInfo.value.reviewCount) * 100}%`
}

const formatTime = (timeStr?: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  if (days < 30) return `${Math.floor(days / 7)}周前`
  if (days < 365) return `${Math.floor(days / 30)}个月前`
  return `${Math.floor(days / 365)}年前`
}

onMounted(() => {
  loadReviews()
  loadRating()
})
</script>

<style scoped>
.review-panel {
  padding: 20px;
}

.review-summary {
  padding: 20px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 8px;
  margin-bottom: 20px;
}

.rating-score {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.score {
  font-size: 48px;
  color: var(--mall-accent);
  font-weight: bold;
}

.stars {
  color: var(--mall-accent);
}

.review-count {
  color: #888;
  font-size: 14px;
}

.rating-distribution {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rating-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}

.star-label {
  width: 30px;
  color: #888;
  font-size: 12px;
}

.bar-container {
  flex: 1;
  height: 8px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--mall-accent), #ffc107);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.bar-count {
  width: 30px;
  color: #888;
  font-size: 12px;
  text-align: right;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.review-item {
  padding: 15px;
  background: rgba(0, 0, 0, 0.15);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.reviewer-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.reviewer-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.reviewer-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.reviewer-name {
  font-weight: 500;
  color: #fff;
}

.review-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.review-time {
  color: #888;
  font-size: 12px;
}

.review-content {
  color: #ddd;
  line-height: 1.6;
}

.review-product {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-top: 10px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.product-thumb {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  object-fit: cover;
}

.product-name {
  flex: 1;
  color: #888;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  color: var(--mall-accent);
  font-weight: 500;
}

.empty-reviews {
  text-align: center;
  padding: 40px;
  color: #888;
}
</style>

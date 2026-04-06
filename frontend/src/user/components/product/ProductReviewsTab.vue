<template>
  <div class="product-reviews-tab">
    <!-- 评价统计 -->
    <div class="review-summary">
      <div class="rating-overview">
        <div class="rating-score">
          <span class="score-number">{{ averageRating }}</span>
          <div class="rating-stars">
            <el-rate :model-value="averageRating" disabled />
          </div>
        </div>
        <div class="rating-stats">
          <div class="stat-item">
            <span class="stat-value">{{ reviewCount }}</span>
            <span class="stat-label">总评价数</span>
          </div>
        </div>
      </div>

      <!-- 评价标签 -->
      <div class="review-tags">
        <el-tag
          v-for="tag in reviewTags"
          :key="tag.label"
          size="small"
          class="review-tag"
        >
          {{ tag.label }} ({{ tag.count }})
        </el-tag>
      </div>
    </div>

    <!-- 评价列表 -->
    <div class="review-list">
      <div class="review-item" v-for="review in reviews" :key="review.id">
        <div class="review-header">
          <div class="review-user">
            <el-avatar :size="32" :src="review.user.avatar">
              {{ review.user.name.charAt(0) }}
            </el-avatar>
            <span class="user-name">{{ review.user.name }}</span>
          </div>
          <div class="review-meta">
            <el-rate :model-value="review.rating" disabled size="small" />
            <span class="review-date">{{ review.createdAt }}</span>
          </div>
        </div>

        <div class="review-content">
          <p>{{ review.content }}</p>
          <div class="review-images" v-if="review.images?.length">
            <el-image
              v-for="(img, index) in review.images"
              :key="index"
              :src="img"
              :preview-src-list="review.images"
              :initial-index="index"
              fit="cover"
              class="review-image"
            />
          </div>
          <div class="review-specs" v-if="review.specs">
            {{ review.specs }}
          </div>
        </div>
      </div>
    </div>

    <!-- 发表评价按钮 -->
    <div class="review-action">
      <el-button type="primary" @click="$emit('open-review')">
        发表评价
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Review, ReviewTag } from '@user/types/product'

interface Props {
  reviewCount: number
  averageRating: number
  reviewTags: ReviewTag[]
  reviews: Review[]
}

defineProps<Props>()

defineEmits<{
  'open-review': []
}>()
</script>

<style scoped>
.product-reviews-tab {
  padding: 20px 0;
}

.review-summary {
  margin-bottom: 24px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
}

.rating-overview {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 16px;
}

.rating-score {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.score-number {
  font-size: 32px;
  font-weight: 700;
  color: #ff8800;
}

.rating-stars {
  margin-top: 4px;
}

.rating-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
}

.stat-label {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

.review-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.review-tag {
  cursor: pointer;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.review-user {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  font-size: 14px;
  color: #666;
}

.review-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.review-date {
  font-size: 12px;
  color: #999;
}

.review-content p {
  margin: 0 0 12px;
  font-size: 14px;
  line-height: 1.6;
  color: #333;
}

.review-images {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 8px;
}

.review-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  cursor: pointer;
}

.review-specs {
  font-size: 12px;
  color: #999;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 4px;
}

.review-action {
  margin-top: 20px;
  text-align: center;
}
</style>

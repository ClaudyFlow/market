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
        v-for="review in review"
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

      <div v-if="review.length === 0" class="empty-review">
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

const review = ref<Review[]>([])
const ratingInfo = ref<RatingInfo | null>(null)

// 模拟评价数据
const mockReviews: Review[] = [
  {
    id: 1,
    userId: 1001,
    userName: '张***3',
    userAvatar: 'https://via.placeholder.com/40x40/00d4ff/fff?text=张',
    productId: props.productId,
    productName: 'iPhone 15 Pro Max',
    productPrice: 9999,
    productImage: 'https://via.placeholder.com/40x40/f5f5f5/333?text=Product',
    rating: 5,
    content: '非常好用，系统流畅，拍照效果也很棒！钛金属手感一流，值得购买。',
    images: ['https://via.placeholder.com/100x100/00d4ff/fff?text=1'],
    createdAt: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
    reply: '感谢您的好评！祝您使用愉快~',
    replyTime: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000).toISOString()
  },
  {
    id: 2,
    userId: 1002,
    userName: '李***8',
    userAvatar: 'https://via.placeholder.com/40x40/ff8800/fff?text=李',
    productId: props.productId,
    productName: 'iPhone 15 Pro Max',
    productPrice: 9999,
    productImage: 'https://via.placeholder.com/40x40/f5f5f5/333?text=Product',
    rating: 5,
    content: '物流很快，包装完好。A17 Pro 芯片性能强劲，玩游戏很流畅。',
    images: [],
    createdAt: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(),
    reply: '',
    replyTime: undefined
  },
  {
    id: 3,
    userId: 1003,
    userName: '王***5',
    userAvatar: 'https://via.placeholder.com/40x40/a335ee/fff?text=王',
    productId: props.productId,
    productName: 'iPhone 15 Pro Max',
    productPrice: 9999,
    productImage: 'https://via.placeholder.com/40x40/f5f5f5/333?text=Product',
    rating: 4,
    content: '整体不错，就是价格有点贵。但是苹果的品质还是值得信赖的。',
    images: ['https://via.placeholder.com/100x100/ff8800/fff?text=1', 'https://via.placeholder.com/100x100/a335ee/fff?text=2'],
    createdAt: new Date(Date.now() - 10 * 24 * 60 * 60 * 1000).toISOString(),
    reply: '感谢您的支持，我们会继续努力提供更好的产品和服务！',
    replyTime: new Date(Date.now() - 9 * 24 * 60 * 60 * 1000).toISOString()
  },
  {
    id: 4,
    userId: 1004,
    userName: '刘***2',
    userAvatar: 'https://via.placeholder.com/40x40/00ff88/fff?text=刘',
    productId: props.productId,
    productName: 'iPhone 15 Pro Max',
    productPrice: 9999,
    productImage: 'https://via.placeholder.com/40x40/f5f5f5/333?text=Product',
    rating: 5,
    content: '拍照效果真的很好，夜景模式特别强大。电池续航也不错，一天一充足够了。',
    images: [],
    createdAt: new Date(Date.now() - 15 * 24 * 60 * 60 * 1000).toISOString(),
    reply: '',
    replyTime: undefined
  },
  {
    id: 5,
    userId: 1005,
    userName: '陈***7',
    userAvatar: 'https://via.placeholder.com/40x40/ff3366/fff?text=陈',
    productId: props.productId,
    productName: 'iPhone 15 Pro Max',
    productPrice: 9999,
    productImage: 'https://via.placeholder.com/40x40/f5f5f5/333?text=Product',
    rating: 5,
    content: '钛金属材质很轻，手感好。120Hz 高刷很流畅，Face ID 解锁也很快。',
    images: ['https://via.placeholder.com/100x100/00d4ff/fff?text=晒图'],
    createdAt: new Date(Date.now() - 20 * 24 * 60 * 60 * 1000).toISOString(),
    reply: '感谢亲的认可~',
    replyTime: new Date(Date.now() - 19 * 24 * 60 * 60 * 1000).toISOString()
  }
]

// 模拟评分数据
const mockRatingInfo: RatingInfo = {
  averageRating: 4.8,
  reviewCount: 1258,
  ratingDistribution: {
    5: 980,
    4: 200,
    3: 50,
    2: 18,
    1: 10
  }
}

const loadReviews = async () => {
  try {
    const res = await getProductReviews(props.productId)
    review.value = res.data?.data || mockReviews
  } catch (error) {
    console.error('加载评价失败，使用模拟数据:', error)
    // 使用模拟数据
    review.value = mockReviews
  }
}

const loadRating = async () => {
  try {
    const res = await getProductRating(props.productId)
    ratingInfo.value = res.data?.data
  } catch (error) {
    console.error('加载评分失败，使用模拟数据:', error)
    // 使用模拟数据
    ratingInfo.value = mockRatingInfo
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

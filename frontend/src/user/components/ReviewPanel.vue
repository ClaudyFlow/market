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
      <!-- 评分分布（可点击筛选） -->
      <div class="rating-distribution">
        <div
          v-for="star in [5, 4, 3, 2, 1]"
          :key="star"
          class="rating-bar"
          :class="{ active: filterScore === star }"
          @click="toggleFilter(star)"
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

    <!-- 筛选工具栏 -->
    <div class="filter-toolbar">
      <div class="filter-left">
        <el-tag
          v-for="tag in reviewTags"
          :key="tag.tag"
          :type="activeTag === tag.tag ? '' : 'info'"
          effect="plain"
          round
          size="small"
          class="tag-filter"
          @click="activeTag = activeTag === tag.tag ? '' : tag.tag"
        >
          {{ tag.tag }}
        </el-tag>
      </div>
      <div class="filter-right">
        <el-radio-group v-model="sortType" size="small">
          <el-radio-button value="time_desc">最新</el-radio-button>
          <el-radio-button value="score_desc">好评优先</el-radio-button>
          <el-radio-button value="like_desc">最有用</el-radio-button>
          <el-radio-button value="has_image">有图</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 筛选提示 -->
    <div class="filter-hint" v-if="filterScore || activeTag">
      <span>当前筛选：</span>
      <el-tag v-if="filterScore" closable @close="filterScore = undefined">
        {{ filterScore }}星评价
      </el-tag>
      <el-tag v-if="activeTag" type="success" closable @close="activeTag = ''">
        {{ activeTag }}
      </el-tag>
      <el-button text type="primary" size="small" @click="clearFilters">清除全部</el-button>
    </div>

    <!-- 评价列表 -->
    <div class="review-list" v-loading="listLoading">
      <div
        v-for="review in filteredReviews"
        :key="review.id"
        class="review-item"
      >
        <div class="reviewer-info">
          <img
            :src="review.userAvatar || ''"
            :alt="review.userName"
            class="reviewer-avatar"
          />
          <div class="reviewer-details">
            <span class="reviewer-name">{{ review.userName }}</span>
            <div class="review-meta">
              <RatingStars :model-value="review.rating" readonly size="small" />
              <span class="review-time">{{ formatTime(review.createdAt) }}</span>
              <span v-if="(review as any).specs" class="review-specs">· {{ (review as any).specs }}</span>
            </div>
          </div>
          <!-- 点赞按钮 -->
          <div class="like-btn" :class="{ liked: (review as any).liked }" @click="handleLike(review)">
            <el-icon><StarFilled /></el-icon>
            <span>{{ (review as any).likeCount || 0 }}</span>
          </div>
        </div>
        <div class="review-content">
          <p>{{ review.content }}</p>
          <!-- 评价图片（可预览） -->
          <div class="review-images" v-if="review.images?.length">
            <el-image
              v-for="(img, idx) in review.images"
              :key="idx"
              :src="img"
              :preview-src-list="review.images as string[]"
              :initial-index="idx"
              fit="cover"
              class="review-img"
            />
          </div>
          <!-- 标签 -->
          <div class="review-tags-inline" v-if="(review as any).tags?.length">
            <el-tag v-for="t in (review as any).tags" :key="t" size="small" effect="plain" round>{{ t }}</el-tag>
          </div>
        </div>
        <!-- 商品信息 -->
        <div class="review-product" v-if="review.productImage">
          <img :src="review.productImage" :alt="review.productName" class="product-thumb" />
          <span class="product-name">{{ review.productName }}</span>
          <span class="product-price">¥{{ review.productPrice }}</span>
        </div>
        <!-- 商家回复 -->
        <div class="merchant-reply" v-if="review.reply">
          <div class="reply-header"><el-icon><ChatDotRound /></el-icon> 商家回复</div>
          <p class="reply-content">{{ review.reply }}</p>
          <span class="reply-time">{{ formatTime(review.replyTime) }}</span>
        </div>
        <!-- 追加评价 -->
        <div class="additional-review" v-if="(review as any).additionalContent">
          <div class="additional-header"><el-icon><EditPen /></el-icon> 追加评价</div>
          <p class="additional-content">{{ (review as any).additionalContent }}</p>
          <div class="additional-images" v-if="(review as any).additionalImages?.length">
            <el-image
              v-for="(img, idx) in (review as any).additionalImages"
              :key="idx"
              :src="img"
              :preview-src-list="(review as any).additionalImages as string[]"
              :initial-index="idx"
              fit="cover"
              class="additional-img"
            />
          </div>
          <span class="additional-time">{{ formatTime((review as any).additionalTime) }}</span>
        </div>
      </div>

      <div v-if="filteredReviews.length === 0 && !listLoading" class="empty-review">
        <el-empty description="暂无符合条件的评价" :image-size="80" />
      </div>
    </div>

    <!-- 加载更多 -->
    <div class="load-more" v-if="hasMore && reviews.length > 0">
      <el-button plain @click="loadMore" :loading="loadingMore">加载更多</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { StarFilled, ChatDotRound, EditPen } from '@element-plus/icons-vue'
import {
  getProductReviews, getProductRating,
  likeReview, unlikeReview, getReviewTags,
  type Review, type RatingInfo
} from '@user/api/review'
import RatingStars from './RatingStars.vue'

const props = defineProps<{
  productId: number
}>()

const listLoading = ref(false)
const loadingMore = ref(false)
const reviews = ref<Review[]>([])
const ratingInfo = ref<RatingInfo | null>(null)
const reviewTags = ref<{ tag: string; count: number }[]>([])

// 筛选状态
const filterScore = ref<number | undefined>()
const activeTag = ref('')
const sortType = ref<string>('time_desc')

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const hasMore = ref(true)

// 模拟数据
const mockReviews: Review[] = [
  {
    id: 1, userId: 1001, userName: '张***3', userAvatar: '',
    productId: props.productId, productName: '示例商品', productPrice: 9999, productImage: '',
    rating: 5,
    content: '非常好用，系统流畅，拍照效果也很棒！钛金属手感一流，值得购买。',
    images: [],
    createdAt: new Date(Date.now() - 2 * 86400000).toISOString(),
    reply: '感谢您的好评！祝您使用愉快~',
    replyTime: new Date(Date.now() - 1 * 86400000).toISOString()
  },
  {
    id: 2, userId: 1002, userName: '李***8', userAvatar: '',
    productId: props.productId, productName: '示例商品', productPrice: 9999, productImage: '',
    rating: 4,
    content: '整体不错，就是价格有点贵。但是品质还是值得信赖的。',
    images: [],
    createdAt: new Date(Date.now() - 5 * 86400000).toISOString(),
    additionalContent: '使用一周后来追评：电池续航确实不错，一天一充没问题',
    additionalImages: [],
    additionalTime: new Date(Date.now() - 1 * 86400000).toISOString()
  },
  {
    id: 3, userId: 1003, userName: '王***5', userAvatar: '',
    productId: props.productId, productName: '示例商品', productPrice: 9999, productImage: '',
    rating: 5,
    content: '拍照效果真的很好，夜景模式特别强大。电池续航也不错。',
    images: [],
    createdAt: new Date(Date.now() - 10 * 86400000).toISOString(),
    reply: '感谢亲的认可~',
    replyTime: new Date(Date.now() - 9 * 86400000).toISOString()
  }
]

const mockRatingInfo: RatingInfo = {
  averageRating: 4.7,
  reviewCount: 1258,
  ratingDistribution: { 5: 900, 4: 220, 3: 80, 2: 35, 1: 23 }
}

// 增强模拟数据
type EnhancedReview = Review & {
  likeCount?: number
  liked?: boolean
  tags?: string[]
  specs?: string
  additionalContent?: string
  additionalImages?: string[]
  additionalTime?: string
}

const enhancedReviews = computed((): EnhancedReview[] =>
  reviews.value.map(r => ({
    ...r,
    likeCount: (r as any).likeCount ?? Math.floor(Math.random() * 50),
    liked: (r as any).liked ?? false,
    tags: (r as any).tags ?? [],
    specs: (r as any).specs ?? ''
  }))
)

// 筛选 + 排序
const filteredReviews = computed(() => {
  let result = [...enhancedReviews.value] as any[]

  if (filterScore.value) result = result.filter((r: any) => r.rating === filterScore.value)
  if (activeTag.value) result = result.filter((r: any) => r.tags?.includes(activeTag.value))

  switch (sortType.value) {
    case 'time_desc':
      result.sort((a: any, b: any) => new Date(b.createdAt || '').getTime() - new Date(a.createdAt || '').getTime())
      break
    case 'score_desc':
      result.sort((a: any, b: any) => b.rating - a.rating)
      break
    case 'like_desc':
      result.sort((a: any, b: any) => (b.likeCount || 0) - (a.likeCount || 0))
      break
    case 'has_image':
      result = result.filter((r: any) => r.images?.length)
      break
  }
  return result
})

// 数据加载
const loadReviews = async () => {
  listLoading.value = true
  try {
    const res = await getProductReviews(props.productId)
    const data = res.records || []
    reviews.value = data.length > 0
      ? data.map((r: Review) => ({ ...r }))
      : [...mockReviews]
  } catch {
    reviews.value = [...mockReviews]
  } finally {
    listLoading.value = false
  }
}

const loadRating = async () => {
  try { ratingInfo.value = await getProductRating(props.productId) }
  catch { ratingInfo.value = mockRatingInfo }
}

const loadTags = async () => {
  try { reviewTags.value = await getReviewTags(props.productId) }
  catch {
    reviewTags.value = [
      { tag: '质量好', count: 520 }, { tag: '物流快', count: 480 },
      { tag: '性价比高', count: 350 }, { tag: '包装完好', count: 290 },
      { tag: '推荐购买', count: 680 }, { tag: '拍照好', count: 200 }
    ]
  }
}

// 筛选操作
const toggleFilter = (star: number) => {
  filterScore.value = filterScore.value === star ? undefined : star
}
const clearFilters = () => { filterScore.value = undefined; activeTag.value = ''; sortType.value = 'time_desc' }

// 点赞
const handleLike = async (review: Review) => {
  const er = review as unknown as EnhancedReview
  try {
    if (er.liked) {
      await unlikeReview(review.id)
      er.liked = false
      er.likeCount = Math.max(0, (er.likeCount || 1) - 1)
    } else {
      await likeReview(review.id)
      er.liked = true
      er.likeCount = (er.likeCount || 0) + 1
    }
  } catch { ElMessage.error('操作失败') }
}

// 加载更多
const loadMore = async () => {
  loadingMore.value = true
  try {
    currentPage.value++
    const res = await getProductReviews(props.productId, { current: currentPage.value, size: pageSize.value })
    const more = res.records || []
    if (more.length > 0) reviews.value.push(...more)
    else hasMore.value = false
  } finally { loadingMore.value = false }
}

// 辅助方法
const getPercentage = (star: number): string => {
  if (!ratingInfo.value || !ratingInfo.value.reviewCount) return '0%'
  return `${((ratingInfo.value.ratingDistribution[star] || 0) / ratingInfo.value.reviewCount) * 100}%`
}

const formatTime = (timeStr?: string): string => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / 86400000)
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  if (days < 30) return `${Math.floor(days / 7)}周前`
  if (days < 365) return `${Math.floor(days / 30)}个月前`
  return `${Math.floor(days / 365)}年前`
}

onMounted(() => { loadReviews(); loadRating(); loadTags() })
</script>

<style scoped>
.review-panel { padding: 20px; }

/* 评分汇总 */
.review-summary {
  padding: 24px; background: rgba(255,255,255,0.04);
  border: 1px solid var(--mall-border); border-radius: 12px;
}
.rating-score { display: flex; align-items: center; gap: 16px; margin-bottom: 22px; }
.score { font-size: 44px; color: #ffc107; font-weight: bold; line-height: 1; }
.stars { color: #ffc107; }
.review-count { color: var(--mall-text-muted); font-size: 14px; }

/* 评分分布 */
.rating-distribution { display: flex; flex-direction: column; gap: 10px; }
.rating-bar {
  display: flex; align-items: center; gap: 10px;
  cursor: pointer; padding: 3px 8px;
  border-radius: 6px; transition: background 0.15s;
}
.rating-bar:hover, .rating-bar.active { background: rgba(255,193,7,0.08); }
.star-label { width: 32px; font-size: 13px; color: var(--mall-text-secondary); }
.bar-container { flex: 1; height: 8px; background: rgba(255,255,255,0.06); border-radius: 4px; overflow: hidden; }
.bar-fill {
  height: 100%;
  border-radius: 4px; transition: width 0.35s ease;
  background: linear-gradient(90deg, #ff6b35, #ffc107);
}
.bar-count { width: 36px; font-size: 12px; text-align: right; color: var(--mall-text-muted); }

/* 工具栏 */
.filter-toolbar {
  display: flex; justify-content: space-between; align-items: center;
  flex-wrap: wrap; gap: 12px; margin-top: 18px; padding: 14px 18px;
  background: var(--mall-bg-card); border: 1px solid var(--mall-border); border-radius: 10px;
}
.filter-left { display: flex; flex-wrap: wrap; gap: 6px; }
.tag-filter { cursor: pointer; transition: all 0.15s; }
.filter-right { flex-shrink: 0; }

/* 筛选提示 */
.filter-hint {
  display: flex; align-items: center; gap: 8px;
  font-size: 13px; color: var(--mall-text-secondary); margin-top: 10px; padding: 8px 14px;
  background: rgba(64,158,255,0.06); border-radius: 8px;
}

/* 列表 */
.review-list { display: flex; flex-direction: column; gap: 14px; margin-top: 16px; }

.review-item {
  padding: 18px; background: var(--mall-bg-card);
  border: 1px solid var(--mall-border); border-radius: 10px;
  display: flex; flex-direction: column; gap: 12px;
  transition: box-shadow 0.2s;
}
.review-item:hover { box-shadow: 0 2px 12px rgba(0,0,0,0.12); }

.reviewer-info {
  display: flex; align-items: center; gap: 12px;
}
.reviewer-avatar {
  width: 40px; height: 40px; border-radius: 50%; object-fit: cover;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex; align-items: center; justify-content: center; color: #fff; font-weight: bold;
}
.reviewer-details { flex: 1; display: flex; flex-direction: column; gap: 3px; }
.reviewer-name { font-weight: 500; font-size: 14px; color: #eee; }
.review-meta { display: flex; align-items: center; gap: 8px; }
.review-time { font-size: 12px; color: var(--mall-text-muted); }
.review-specs { font-size: 11px; color: var(--mall-text-muted); max-width: 120px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

/* 点赞 */
.like-btn {
  display: flex; align-items: center; gap: 4px;
  padding: 4px 10px; border-radius: 14px;
  cursor: pointer; font-size: 13px; color: var(--mall-text-muted);
  transition: all 0.2s; flex-shrink: 0;
}
.like-btn:hover { color: #ffc107; background: rgba(255,193,7,0.08); }
.like-btn.liked { color: #ffc107; }
.like-btn .el-icon { transition: transform 0.2s; }
.like-btn.liked .el-icon { transform: scale(1.15); }

/* 内容 */
.review-content p { margin: 0; color: #ddd; line-height: 1.65; font-size: 14px; }
.review-images { display: flex; gap: 8px; flex-wrap: wrap; margin-top: 8px; }
.review-img { width: 72px; height: 72px; border-radius: 8px; cursor: pointer; }
.review-tags-inline { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 8px; }

/* 商品 */
.review-product {
  display: flex; align-items: center; gap: 10px;
  padding-top: 10px; border-top: 1px solid rgba(255,255,255,0.05);
}
.product-thumb { width: 40px; height: 40px; border-radius: 6px; object-fit: cover; }
.product-name { flex: 1; font-size: 13px; color: var(--mall-text-secondary); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-price { color: var(--mall-primary); font-weight: 600; font-size: 13px; }

/* 商家回复 */
.merchant-reply {
  margin-top: 8px; padding: 12px 14px; background: rgba(64,158,255,0.05);
  border-radius: 8px; border-left: 3px solid #409eff;
}
.reply-header { display: flex; align-items: center; gap: 4px; font-size: 13px; font-weight: 500; color: #409eff; margin-bottom: 6px; }
.reply-content { margin: 0; font-size: 13px; color: var(--mall-text-secondary); line-height: 1.55; }
.reply-time { font-size: 11px; color: var(--mall-text-muted); margin-top: 4px; display: block; }

/* 追评 */
.additional-review {
  margin-top: 8px; padding: 12px 14px; background: rgba(103,194,58,0.05);
  border-radius: 8px; border-left: 3px solid #67c23a;
}
.additional-header { display: flex; align-items: center; gap: 4px; font-size: 13px; font-weight: 500; color: #67c23a; margin-bottom: 6px; }
.additional-content { margin: 0; font-size: 13px; color: var(--mall-text-secondary); line-height: 1.55; }
.additional-images { display: flex; gap: 6px; flex-wrap: wrap; margin-top: 8px; }
.additional-img { width: 60px; height: 60px; border-radius: 6px; cursor: pointer; }
.additional-time { font-size: 11px; color: var(--mall-text-muted); margin-top: 4px; display: block; }

.empty-review { text-align: center; padding: 40px; }
.load-more { text-align: center; padding: 20px 0; }
</style>

<template>
  <div class="review-records">
    <div class="page-header">
      <h1><el-icon><ChatDotRound /></el-icon> 我的评价</h1>
    </div>

    <!-- 评价统计 -->
    <div class="stats-cards" v-if="stats">
      <div class="stat-item">
        <span class="stat-value">{{ stats.total || 0 }}</span>
        <span class="stat-label">全部评价</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ stats.withImages || 0 }}</span>
        <span class="stat-label">图文评价</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ stats.anonymous || 0 }}</span>
        <span class="stat-label">匿名评价</span>
      </div>
    </div>

    <!-- 评价列表 -->
    <div class="review-list" v-loading="loading">
      <el-empty v-if="!loading && records.length === 0" description="暂无评价记录" />

      <el-timeline v-else>
        <el-timeline-item
          v-for="(record, index) in records"
          :key="record.id"
          :timestamp="record.createdAt"
          :color="getColor(record.rating)"
          :hollow="index !== 0"
          placement="top"
        >
          <el-card class="review-card">
            <!-- 商品信息 -->
            <div class="review-header">
              <div class="product-info" @click="goToProduct(record.productId)">
                <el-image :src="record.productImage" fit="cover" class="product-image">
                  <template #error>
                    <div class="image-error"><el-icon><PictureFilled /></el-icon></div>
                  </template>
                </el-image>
                <div class="product-detail">
                  <span class="product-name">{{ record.productName }}</span>
                  <span class="product-price">¥{{ record.price?.toFixed(2) }}</span>
                </div>
              </div>
              <el-rate
                v-model="record.rating"
                disabled
                :colors="['#ff4757', '#ff6b35', '#ffc107']"
                size="small"
              />
            </div>

            <!-- 评价内容 -->
            <div class="review-content" v-if="record.content">
              <p>{{ record.content }}</p>
            </div>

            <!-- 图片 -->
            <div class="review-images" v-if="record.images && record.images.length">
              <el-image
                v-for="(img, imgIndex) in record.images"
                :key="imgIndex"
                :src="img"
                fit="cover"
                class="review-image"
                :preview-src-list="record.images"
                preview-teleported
              />
            </div>

            <!-- 评价元信息 -->
            <div class="review-meta">
              <span class="review-shop" v-if="record.shopName">
                <el-icon><Shop /></el-icon> {{ record.shopName }}
              </span>
              <span class="review-order" v-if="record.orderNo">
                订单：{{ record.orderNo.substring(0, 12) }}...
              </span>
              <span class="review-verified">
                <el-icon><Key /></el-icon>
                <el-tag type="success" size="small">✓ 已发布</el-tag>
              </span>
              <span class="review-anonymous" v-if="record.isAnonymous">
                <el-tag size="small" type="info">匿名</el-tag>
              </span>
            </div>

            <!-- 操作按钮 -->
            <div class="review-actions">
              <el-button text size="small" @click="goToProduct(record.productId)">
                <el-icon><View /></el-icon> 查看商品
              </el-button>
              <el-button text size="small" @click="goToOrder(record.orderId)">
                <el-icon><Document /></el-icon> 查看订单
              </el-button>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 20]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="loadRecords"
        @current-change="loadRecords"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ChatDotRound, Shop, Key, View, Document, PictureFilled
} from '@element-plus/icons-vue'
import * as reviewApi from '@user/api/review'

const router = useRouter()

const loading = ref(false)
const records = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const stats = ref<{ total?: number; withImages?: number; anonymous?: number } | null>(null)

interface Record {
  id: number
  productId?: number
  productName?: string
  productImage?: string
  price?: number
  rating: number
  content?: string
  images?: string[]
  shopName?: string
  orderId?: number
  orderNo?: string
  isAnonymous?: boolean
  createdAt: string
}

const getColor = (rating: number) => {
  if (rating >= 4) return '#67C23A'
  if (rating >= 3) return '#E6A23C'
  return '#F56C6C'
}

const loadRecords = async () => {
  loading.value = true
  try {
    const result = await reviewApi.getUserReviews({
      current: currentPage.value,
      size: pageSize.value
    })
    records.value = result.records || []
    total.value = result.total || 0

    // 计算统计数据
    if (records.value.length > 0) {
      stats.value = {
        total: total.value,
        withImages: records.value.filter(r => r.images?.length > 0).length,
        anonymous: records.value.filter(r => r.isAnonymous).length
      }
    }
  } catch (error) {
    console.error('加载评价记录失败:', error)
    ElMessage.error('加载评价记录失败')
  } finally {
    loading.value = false
  }
}

const goToProduct = (productId?: number) => {
  if (productId) {
    router.push(`/item/${productId}`)
  }
}

const goToOrder = (orderId?: number) => {
  if (orderId) {
    router.push(`/user/orders/${orderId}`)
  }
}

onMounted(() => {
  loadRecords()
})
</script>

<style scoped>
.review-records {
  max-width: 960px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}

.stat-item {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 10px;
  padding: 20px;
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 28px;
  font-weight: bold;
  color: var(--mall-primary);
}

.stat-label {
  display: block;
  font-size: 13px;
  color: var(--mall-text-muted);
  margin-top: 4px;
}

/* 评价卡片 */
.review-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  transition: all 0.2s;
}

.review-card:hover {
  border-color: rgba(64, 158, 255, 0.3);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
}

.product-info {
  display: flex;
  gap: 12px;
  cursor: pointer;
  flex: 1;
  min-width: 0;
}

.product-image {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.06);
}

.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--mall-text-muted);
}

.product-detail {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 4px;
  min-width: 0;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #eee;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 13px;
  color: var(--mall-text-muted);
}

.review-content {
  margin-bottom: 12px;
}

.review-content p {
  font-size: 14px;
  color: var(--mall-text-secondary);
  line-height: 1.6;
  margin: 0;
}

.review-images {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.review-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  cursor: pointer;
}

.review-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 12px;
  color: var(--mall-text-muted);
  flex-wrap: wrap;
  margin-bottom: 8px;
}

.review-shop,
.review-order {
  display: flex;
  align-items: center;
  gap: 4px;
}

.review-verified {
  display: flex;
  align-items: center;
  gap: 4px;
}

.review-actions {
  display: flex;
  gap: 8px;
  padding-top: 8px;
  border-top: 1px solid rgba(255, 255, 255, 0.04);
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-top: 20px;
}

:deep(.el-timeline-item__node) {
  background-color: var(--mall-primary);
}

:deep(.el-timeline-item__wrapper) {
  padding-left: 30px;
}

@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: repeat(3, 1fr);
  }

  .review-header {
    flex-direction: column;
  }

  .product-info {
    width: 100%;
  }
}
</style>
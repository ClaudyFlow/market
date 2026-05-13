<template>
  <div class="activity-detail-page">
    <div class="container">
      <div v-if="loading" class="loading">
        <el-skeleton :rows="8" animated />
      </div>

      <div v-else-if="!activity" class="empty-state">
        <el-empty description="活动不存在或已结束" />
      </div>

      <template v-else>
        <div class="activity-header">
          <img v-if="activity.image" :src="activity.image" :alt="activity.name" class="activity-banner" />
          <div v-else class="activity-banner placeholder"></div>
        </div>

        <div class="activity-info">
          <div class="info-main">
            <h1 class="activity-name">{{ activity.name }}</h1>
            <p class="activity-desc">{{ activity.description }}</p>

            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">活动类型</span>
                <span class="info-value">{{ getTypeName(activity.type) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">活动状态</span>
                <span class="info-value status" :class="activity.status">
                  {{ getStatusText(activity.status) }}
                </span>
              </div>
              <div class="info-item">
                <span class="info-label">开始时间</span>
                <span class="info-value">{{ formatTime(activity.startTime) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">结束时间</span>
                <span class="info-value">{{ formatTime(activity.endTime) }}</span>
              </div>
              <div v-if="activity.maxQuantity" class="info-item">
                <span class="info-label">总名额</span>
                <span class="info-value">{{ activity.maxQuantity }}</span>
              </div>
              <div v-if="activity.usedQuantity" class="info-item">
                <span class="info-label">已参与</span>
                <span class="info-value">{{ activity.usedQuantity }}</span>
              </div>
            </div>

            <div v-if="activity.discount" class="discount-section">
              <div class="discount-value">
                <span v-if="activity.discountType === 'PERCENT'" class="big">
                  {{ (activity.discount * 10).toFixed(1) }}折
                </span>
                <span v-else class="big">立减 ¥{{ activity.discount }}</span>
              </div>
            </div>

            <div class="action-section">
              <el-button
                v-if="activity.status === 'ACTIVE'"
                type="primary"
                size="large"
                @click="handleJoin"
              >
                立即参与
              </el-button>
              <el-button v-else disabled size="large">
                {{ activity.status === 'PENDING' ? '活动未开始' : '活动已结束' }}
              </el-button>
            </div>
          </div>
        </div>

        <div class="activity-products">
          <h2 class="section-title">相关商品</h2>
          <div v-if="products.length === 0" class="empty-products">
            <el-empty description="暂无相关商品" />
          </div>
          <div v-else class="product-grid">
            <div v-for="product in products" :key="product.id" class="product-card" @click="goToProduct(product.id)">
              <img v-lazyload="product.image || '/placeholder.png'" :alt="product.name" />
              <div class="product-info">
                <h3 class="product-name">{{ product.name }}</h3>
                <div class="product-price">
                  <span class="current">¥{{ product.price }}</span>
                  <span v-if="product.originalPrice" class="original">¥{{ product.originalPrice }}</span>
                </div>
                <div v-if="product.flashPrice" class="product-flash">
                  秒杀价: ¥{{ product.flashPrice }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>

      <div class="back-btn">
        <el-button @click="goBack">返回活动列表</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getActivityDetail, getProductActivities } from '@user/api/activity'

interface Activity {
  id: number
  name: string
  type: string
  description?: string
  image?: string
  startTime: string
  endTime: string
  status: string
  discount?: number
  discountType?: string
  maxQuantity?: number
  usedQuantity?: number
  maxPerUser?: number
}

interface Product {
  id: number
  name: string
  price: number
  originalPrice?: number
  image?: string
  flashPrice?: number
}

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const activity = ref<Activity | null>(null)
const products = ref<Product[]>([])

const getTypeName = (type: string) => {
  const names: Record<string, string> = {
    FLASH_SALE: '秒杀',
    DISCOUNT: '折扣',
    FULL_REDUCE: '满减',
    GROUP_BUY: '拼团',
    LOTTERY: '抽奖',
    VIP_DAY: '会员日'
  }
  return names[type] || type
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    ACTIVE: '进行中',
    PENDING: '即将开始',
    ENDED: '已结束'
  }
  return texts[status] || status
}

const formatTime = (time: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

const goBack = () => {
  router.push('/activity')
}

const goToProduct = (id: number) => {
  router.push(`/item/${id}`)
}

const handleJoin = () => {
  ElMessage.success('参与活动成功')
}

const loadActivity = async () => {
  const id = Number(route.params.id)
  if (!id) return

  loading.value = true
  try {
    const res = await getActivityDetail(id)
    const data = res.data || res
    activity.value = {
      id: data.id,
      name: data.name,
      type: data.type,
      description: data.description,
      image: data.image,
      startTime: data.startTime,
      endTime: data.endTime,
      status: data.status,
      discount: data.discount,
      discountType: data.discountType,
      maxQuantity: data.maxQuantity,
      usedQuantity: data.usedQuantity,
      maxPerUser: data.maxPerUser
    }

    if (data.productId) {
      const productRes = await getProductActivities(data.productId)
      products.value = ((productRes.data || []) as any[]).map(p => ({
        id: p.id,
        name: p.name,
        price: p.price || p.flashPrice,
        originalPrice: p.originalPrice,
        image: p.image,
        flashPrice: p.flashPrice
      }))
    }
  } catch (error) {
    console.error('加载活动详情失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadActivity()
})
</script>

<style scoped>
.activity-detail-page {
  min-height: 100vh;
  background: linear-gradient(180deg, rgba(0,212,255,0.15) 0%, rgba(10,14,26,0.8) 100%);
  padding: 20px;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
}

.activity-header {
  margin-bottom: 20px;
}

.activity-banner {
  width: 100%;
  height: 300px;
  border-radius: 16px;
  overflow: hidden;
  background: rgba(26,31,58,0.8);
}

.activity-banner.placeholder {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
}

.activity-banner img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.activity-info {
  background: rgba(26,31,58,0.8);
  border: 1px solid rgba(0,212,255,0.2);
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
}

.info-main {
  margin-bottom: 20px;
}

.activity-name {
  font-size: 24px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 12px;
}

.activity-desc {
  font-size: 14px;
  color: #aaa;
  margin-bottom: 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 12px;
  color: #666;
}

.info-value {
  font-size: 14px;
  color: #fff;
}

.info-value.status.ACTIVE { color: #00d4ff; }
.info-value.status.PENDING { color: #ff8800; }
.info-value.status.ENDED { color: #888; }

.discount-section {
  background: linear-gradient(135deg, rgba(255,68,68,0.2), rgba(255,102,102,0.2));
  border: 1px solid rgba(255,68,68,0.3);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  margin-bottom: 20px;
}

.discount-value .big {
  font-size: 32px;
  font-weight: 700;
  color: #ff4444;
}

.action-section {
  text-align: center;
}

.activity-products {
  background: rgba(26,31,58,0.8);
  border: 1px solid rgba(0,212,255,0.2);
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 20px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.product-card {
  background: rgba(10,20,50,0.5);
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0,212,255,0.2);
}

.product-card img {
  width: 100%;
  height: 120px;
  object-fit: cover;
}

.product-info {
  padding: 12px;
}

.product-name {
  font-size: 14px;
  color: #fff;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  display: flex;
  gap: 8px;
  align-items: center;
}

.product-price .current {
  font-size: 16px;
  font-weight: 600;
  color: #ff4444;
}

.product-price .original {
  font-size: 12px;
  color: #666;
  text-decoration: line-through;
}

.product-flash {
  font-size: 12px;
  color: #ff8800;
  margin-top: 4px;
}

.empty-products {
  padding: 40px;
  text-align: center;
}

.back-btn {
  text-align: center;
}

.loading, .empty-state {
  background: rgba(26,31,58,0.8);
  border: 1px solid rgba(0,212,255,0.2);
  border-radius: 12px;
  padding: 40px;
  text-align: center;
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
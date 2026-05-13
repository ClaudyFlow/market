<template>
  <div class="activity-page">
    <div class="container">
      <h1 class="page-title">活动中心</h1>

      <div class="activity-tabs">
        <div
          v-for="tab in tabs"
          :key="tab.value"
          class="tab-item"
          :class="{ active: activeTab === tab.value }"
          @click="activeTab = tab.value"
        >
          {{ tab.label }}
        </div>
      </div>

      <div v-if="loading" class="loading">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="currentActivities.length === 0" class="empty-state">
        <el-empty description="暂无相关活动" />
      </div>

      <div v-else class="activity-grid">
        <div
          v-for="activity in currentActivities"
          :key="activity.id"
          class="activity-card"
          :class="activity.status"
          @click="goToDetail(activity)"
        >
          <div class="activity-image">
            <img v-lazyload="activity.image || '/placeholder-activity.png'" :alt="activity.name" />
            <div class="activity-badge" :class="activity.type">{{ getTypeName(activity.type) }}</div>
          </div>
          <div class="activity-content">
            <h3 class="activity-name">{{ activity.name }}</h3>
            <p class="activity-desc">{{ activity.description }}</p>
            <div class="activity-meta">
              <span class="activity-time">
                {{ formatTime(activity.startTime) }} ~ {{ formatTime(activity.endTime) }}
              </span>
              <span class="activity-status" :class="activity.status">
                {{ getStatusText(activity.status) }}
              </span>
            </div>
            <div v-if="activity.discount" class="activity-discount">
              <span v-if="activity.discountType === 'PERCENT'">{{ (activity.discount * 10).toFixed(1) }}折</span>
              <span v-else>立减 ¥{{ activity.discount }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getActiveActivities } from '@user/api/activity'

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

const router = useRouter()
const loading = ref(false)
const activeTab = ref('all')
const activities = ref<Activity[]>([])

const tabs = [
  { label: '全部', value: 'all' },
  { label: '秒杀', value: 'FLASH_SALE' },
  { label: '折扣', value: 'DISCOUNT' },
  { label: '满减', value: 'FULL_REDUCE' },
  { label: '拼团', value: 'GROUP_BUY' },
  { label: '抽奖', value: 'LOTTERY' }
]

const currentActivities = computed(() => {
  if (activeTab.value === 'all') {
    return activities.value
  }
  return activities.value.filter(a => a.type === activeTab.value)
})

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
  return new Date(time).toLocaleDateString('zh-CN')
}

const goToDetail = (activity: Activity) => {
  router.push(`/activity/${activity.id}`)
}

const loadActivities = async () => {
  loading.value = true
  try {
    const res = await getActiveActivities()
    activities.value = (res.data || []).map((item: any) => ({
      id: item.id,
      name: item.name,
      type: item.type,
      description: item.description,
      image: item.image,
      startTime: item.startTime,
      endTime: item.endTime,
      status: item.status,
      discount: item.discount,
      discountType: item.discountType,
      maxQuantity: item.maxQuantity,
      usedQuantity: item.usedQuantity,
      maxPerUser: item.maxPerUser
    }))
  } catch (error) {
    console.error('加载活动失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.activity-page {
  min-height: 100vh;
  background: linear-gradient(180deg, rgba(0,212,255,0.15) 0%, rgba(10,14,26,0.8) 100%);
  padding: 20px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 24px;
  text-align: center;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.activity-tabs {
  display: flex;
  background: rgba(26,31,58,0.8);
  border: 1px solid rgba(0,212,255,0.2);
  border-radius: 12px;
  padding: 4px;
  margin-bottom: 24px;
  gap: 4px;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 12px;
  cursor: pointer;
  border-radius: 8px;
  color: #aaa;
  font-weight: 500;
  transition: all 0.3s;
}

.tab-item:hover {
  background: rgba(0,212,255,0.1);
}

.tab-item.active {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  color: #000;
}

.activity-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.activity-card {
  background: rgba(26,31,58,0.8);
  border: 1px solid rgba(0,212,255,0.15);
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.activity-card:hover {
  transform: translateY(-4px);
  border-color: var(--mall-primary);
  box-shadow: 0 8px 24px rgba(0,212,255,0.2);
}

.activity-card.ENDED {
  opacity: 0.6;
}

.activity-image {
  position: relative;
  height: 160px;
  background: rgba(10,20,50,0.5);
}

.activity-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.activity-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
}

.activity-badge.FLASH_SALE { background: linear-gradient(135deg, #ff4444, #ff6666); }
.activity-badge.DISCOUNT { background: linear-gradient(135deg, #ff8800, #ffaa00); }
.activity-badge.FULL_REDUCE { background: linear-gradient(135deg, #00d4ff, #00ff88); }
.activity-badge.GROUP_BUY { background: linear-gradient(135deg, #aa00ff, #cc44ff); }
.activity-badge.LOTTERY { background: linear-gradient(135deg, #ffd700, #ffaa00); }

.activity-content {
  padding: 16px;
}

.activity-name {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 8px;
}

.activity-desc {
  font-size: 13px;
  color: #888;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.activity-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
}

.activity-status {
  padding: 2px 8px;
  border-radius: 4px;
}

.activity-status.ACTIVE {
  background: rgba(0,212,255,0.2);
  color: #00d4ff;
}

.activity-status.PENDING {
  background: rgba(255,136,0,0.2);
  color: #ff8800;
}

.activity-status.ENDED {
  background: rgba(128,128,128,0.2);
  color: #888;
}

.activity-discount {
  font-size: 14px;
  font-weight: 600;
  color: #ff4444;
}

.loading, .empty-state {
  background: rgba(26,31,58,0.8);
  border: 1px solid rgba(0,212,255,0.2);
  border-radius: 12px;
  padding: 40px;
  text-align: center;
}

@media (max-width: 768px) {
  .activity-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .activity-grid {
    grid-template-columns: 1fr;
  }
}
</style>
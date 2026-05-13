<template>
  <div class="coupon-page">
    <div class="container">
      <h1 class="page-title">我的优惠券</h1>

      <!-- 选项卡切换 -->
      <div class="tab-container">
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'available' }"
          @click="activeTab = 'available'"
        >
          可用优惠券
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'used' }"
          @click="activeTab = 'used'"
        >
          已使用
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'expired' }"
          @click="activeTab = 'expired'"
        >
          已过期
        </div>
      </div>

      <!-- 可领取优惠券 -->
      <div v-if="可领取优惠券.length > 0" class="claim-section">
        <h2 class="section-title">可领取优惠券</h2>
        <div class="coupon-list">
          <div 
            v-for="coupon in 可领取优惠券" 
            :key="coupon.id" 
            class="coupon-card claimable"
          >
            <div class="coupon-left">
              <div class="coupon-amount">
                <span class="symbol">¥</span>
                <span class="value">{{ coupon.value }}</span>
              </div>
              <div v-if="coupon.threshold > 0" class="coupon-threshold">
                满{{ coupon.threshold }}可用
              </div>
            </div>
            <div class="coupon-right">
              <div class="coupon-name">{{ coupon.name }}</div>
              <div class="coupon-desc">{{ coupon.description }}</div>
              <div class="coupon-info">
                <span>有效期：{{ formatDate(coupon.validFrom) }} - {{ formatDate(coupon.validTo) }}</span>
                <span>剩余：{{ coupon.remainCount }}张</span>
              </div>
              <el-button 
                type="primary" 
                size="small"
                @click="领取优惠券 (coupon.id)"
                :disabled="coupon.remainCount === 0"
              >
                {{ coupon.remainCount === 0 ? '已领完' : '立即领取' }}
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 优惠券列表 -->
      <div class="coupon-section">
        <div v-if="loading" class="loading">
          <el-skeleton :rows="5" animated />
        </div>
        
        <div v-else-if="currentList.length === 0" class="empty-state">
          <el-empty :description="getEmptyDescription()" />
        </div>

        <div v-else class="coupon-list">
          <div 
            v-for="coupon in currentList" 
            :key="coupon.id" 
            class="coupon-card"
            :class="coupon.status"
          >
            <div class="coupon-left">
              <div class="coupon-amount">
                <span class="symbol">¥</span>
                <span class="value">{{ coupon.value }}</span>
              </div>
              <div v-if="coupon.threshold > 0" class="coupon-threshold">
                满{{ coupon.threshold }}可用
              </div>
            </div>
            <div class="coupon-right">
              <div class="coupon-name">{{ coupon.name }}</div>
              <div class="coupon-desc">{{ coupon.description }}</div>
              <div class="coupon-info">
                <span>有效期：{{ formatDate(coupon.validFrom) }} - {{ formatDate(coupon.validTo) }}</span>
                <span v-if="coupon.status === 'used'">已使用</span>
                <span v-else-if="coupon.status === 'expired'">已过期</span>
              </div>
              <el-button 
                v-if="coupon.status === 'available'"
                type="primary" 
                size="small"
                @click="使用优惠券 (coupon.id)"
              >
                立即使用
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyCoupons, takeCoupon } from '@user/api/coupon'

interface Coupon {
  id: number
  name: string
  value: number
  threshold: number
  description: string
  validFrom: string
  validTo: string
  status: 'available' | 'used' | 'expired'
  remainCount?: number
}

const loading = ref(false)
const activeTab = ref<'available' | 'used' | 'expired'>('available')

const 优惠券列表 = ref<Coupon[]>([])
const 可领取优惠券 = ref<Coupon[]>([])

const currentList = computed(() => {
  return 优惠券列表.value.filter(c => c.status === activeTab.value)
})

const getEmptyDescription = () => {
  switch (activeTab.value) {
    case 'available': return '暂无可用优惠券，快去领取吧~'
    case 'used': return '暂无已使用优惠券'
    case 'expired': return '暂无已过期优惠券'
    default: return '暂无数据'
  }
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

const 加载优惠券 = async () => {
  loading.value = true
  try {
    const res = await getMyCoupons()
    if (res.code === 200) {
      优惠券列表.value = (res.data.list || []).map((item: any) => ({
        id: item.id,
        name: item.name,
        value: item.discountValue,
        threshold: item.minPurchase || 0,
        description: item.description || '',
        validFrom: item.validFrom || '',
        validTo: item.validTo || '',
        status: (item.status || 'available').toLowerCase() as 'available' | 'used' | 'expired'
      }))
    }
  } catch (error) {
    ElMessage.error('加载优惠券失败')
  } finally {
    loading.value = false
  }
}

const 加载可领取优惠券 = async () => {
  try {
    const res = await getCouponTemplates({ page: 1, size: 100 })
    if (res.code === 200 && res.data) {
      可领取优惠券.value = (res.data.list || []).map((item: any) => ({
        id: item.id,
        name: item.name,
        value: item.discountValue,
        threshold: item.minPurchase || 0,
        description: item.description || '',
        validFrom: item.validFrom || '',
        validTo: item.validTo || '',
        status: 'available',
        remainCount: item.remainCount ?? item.totalCount - item.usedCount
      }))
    }
  } catch (error) {
    console.error('加载可领取优惠券失败', error)
  }
}

const 领取优惠券 = async (couponId: number) => {
  try {
    const res = await takeCoupon(couponId)
    if (res.code === 200) {
      ElMessage.success('领取成功')
      // 刷新列表
      加载可领取优惠券 ()
      加载优惠券 ()
    } else {
      ElMessage.error(res.message || '领取失败')
    }
  } catch (error) {
    ElMessage.error('领取失败')
  }
}

const 使用优惠券 = (couponId: number) => {
  // 跳转到商品列表或订单页面
  ElMessage.info('前往使用优惠券')
  // 这里可以跳转到商品列表页
  // router.push('/item')
}

onMounted(() => {
  加载优惠券 ()
  加载可领取优惠券 ()
})
</script>

<style scoped>
@import '@user/assets/mall-style.css';

.coupon-page {
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

/* 选项卡 */
.tab-container {
  display: flex;
  background: rgba(26,31,58,0.8);
  border: 1px solid rgba(0,212,255,0.2);
  border-radius: 12px;
  padding: 4px;
  margin-bottom: 24px;
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

/* 可领取区域 */
.claim-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 16px;
}

/* 优惠券列表 */
.coupon-list {
  display: grid;
  gap: 16px;
}

.coupon-card {
  display: flex;
  background: rgba(26,31,58,0.8);
  border: 1px solid rgba(0,212,255,0.15);
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
}

.coupon-card:hover {
  transform: translateY(-2px);
  border-color: var(--mall-primary);
  box-shadow: 0 8px 16px rgba(0,212,255,0.2);
}

.coupon-card.claimable {
  border: 2px solid #00d4ff;
}

.coupon-card.used,
.coupon-card.expired {
  opacity: 0.7;
}

.coupon-left {
  width: 140px;
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  color: #000;
}

.coupon-amount {
  display: flex;
  align-items: baseline;
}

.coupon-amount .symbol {
  font-size: 20px;
  margin-right: 4px;
}

.coupon-amount .value {
  font-size: 42px;
  font-weight: 700;
}

.coupon-threshold {
  font-size: 14px;
  margin-top: 8px;
  opacity: 0.9;
}

.coupon-right {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.coupon-name {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

.coupon-desc {
  font-size: 14px;
  color: #aaa;
  margin: 8px 0;
}

.coupon-info {
  font-size: 12px;
  color: #888;
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.loading,
.empty-state {
  background: rgba(26,31,58,0.8);
  border: 1px solid rgba(0,212,255,0.2);
  border-radius: 12px;
  padding: 40px;
  text-align: center;
}

/* 响应式 */
@media (max-width: 768px) {
  .coupon-card {
    flex-direction: column;
  }

  .coupon-left {
    width: 100%;
    padding: 16px;
  }

  .coupon-amount .value {
    font-size: 32px;
  }
}
</style>

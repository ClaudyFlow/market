<template>
  <div class="page-container">
    <PageHeader title="数据统计" :icon="DataAnalysis" />

    <StatCards :cards="statCards" />

    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <SciCard variant="gradient">
          <div class="chart-section">
            <h3>销售趋势</h3>
            <LineChart v-if="salesTrend.length > 0" :data="salesTrend" :height="280" />
            <div v-else class="chart-empty">暂无数据</div>
          </div>
        </SciCard>
      </el-col>
      <el-col :span="12">
        <SciCard variant="gradient">
          <div class="chart-section">
            <h3>订单统计</h3>
            <BarChart v-if="orderChartData.length > 0" :data="orderChartData" :height="280" />
            <div v-else class="chart-empty">暂无数据</div>
          </div>
        </SciCard>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <SciCard variant="gradient">
          <div class="chart-section">
            <h3>商品分类占比</h3>
            <PieChart v-if="categoryData.length > 0" :data="categoryData" :height="280" />
            <div v-else class="chart-empty">暂无数据</div>
          </div>
        </SciCard>
      </el-col>
      <el-col :span="12">
        <SciCard variant="gradient">
          <div class="chart-section">
            <h3>用户增长</h3>
            <div class="chart-empty">数据加载中...</div>
          </div>
        </SciCard>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { DataAnalysis, TrendCharts, ShoppingCart, User, Money } from '@element-plus/icons-vue'
import { PageHeader, StatCards, SciCard } from '@merchant/components'
import { getOrderStats, getSalesTrend, getCategoryStats } from '@merchant/api/order'
import { getCouponStats } from '@merchant/api/coupon'
import LineChart from '@merchant/components/charts/LineChart.vue'
import BarChart from '@merchant/components/charts/BarChart.vue'
import PieChart from '@merchant/components/charts/PieChart.vue'

const loading = ref(false)
const orderStats = ref({ total: 0, pending: 0, paid: 0, shipped: 0, completed: 0, cancelled: 0, refunding: 0 })
const couponStats = ref({ total: 0, active: 0, totalDiscount: 0 })
const sales = ref(0)
const revenue = ref(0)
const salesTrend = ref<{ label: string; value: number }[]>([])
const categoryData = ref<{ label: string; value: number }[]>([])

const statCards = computed(() => [
  { value: `¥${sales.value.toLocaleString()}`, label: '今日销售额', type: 'primary' as const, icon: Money },
  { value: orderStats.value.total, label: '总订单数', type: 'success' as const, icon: ShoppingCart },
  { value: couponStats.value.active, label: '活跃优惠券', type: 'warning' as const, icon: User },
  { value: `¥${revenue.value.toLocaleString()}`, label: '本月营收', type: 'info' as const, icon: TrendCharts }
])

const orderChartData = computed(() => [
  { label: '待付款', value: orderStats.value.pending, color: '#ff8800' },
  { label: '已支付', value: orderStats.value.paid, color: '#00d4ff' },
  { label: '已发货', value: orderStats.value.shipped, color: '#00ff88' },
  { label: '已完成', value: orderStats.value.completed, color: '#ffd700' },
  { label: '已取消', value: orderStats.value.cancelled, color: '#888888' }
])

const loadStats = async () => {
  loading.value = true
  try {
    const [orderRes, couponRes, trendRes, categoryRes] = await Promise.all([
      getOrderStats(),
      getCouponStats(),
      getSalesTrend(7),
      getCategoryStats()
    ])

    if (orderRes.code === 200) {
      orderStats.value = orderRes.data
      revenue.value = orderRes.data.revenue || 0
    }

    if (couponRes.code === 200) {
      couponStats.value = couponRes.data
    }

    if (trendRes.code === 200 && trendRes.data) {
      const data = trendRes.data
      const amounts = Array.isArray(data.orderAmounts) ? data.orderAmounts : []
      const dates = Array.isArray(data.dates) ? data.dates : []
      salesTrend.value = dates.map((date: string, i: number) => ({
        label: date.slice(5),
        value: Number(amounts[i]) || 0
      }))
      if (amounts.length > 0) {
        sales.value = Number(amounts[amounts.length - 1]) || 0
      }
    }

    if (categoryRes.code === 200 && categoryRes.data) {
      const catData = categoryRes.data
      categoryData.value = Object.keys(catData).map((key) => ({
        label: key,
        value: Number(catData[key]) || 0
      }))
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.05) 0%, transparent 100%);
  min-height: 100vh;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-section {
  padding: 16px;
}

.chart-section h3 {
  margin: 0 0 16px;
  color: #fff;
  font-size: 16px;
}

.chart-empty {
  height: 280px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #88aacc;
  background: rgba(10, 20, 50, 0.5);
  border-radius: 8px;
}
</style>

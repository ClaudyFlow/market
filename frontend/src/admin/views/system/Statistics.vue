<template>
  <div class="page-container">
    <header class="page-header">
      <h1 class="page-title">
        <el-icon><DataAnalysis /></el-icon>
        数据统计
      </h1>
      <div class="header-actions">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="handleDateChange"
          style="width: 240px"
        />
      </div>
    </header>

    <!-- 核心数据卡片 -->
    <section class="stats-cards">
      <el-row :gutter="15">
        <el-col :span="6">
          <div class="stat-card primary">
            <div class="stat-icon"><el-icon><User /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.userCount }}</div>
              <div class="stat-label">用户总数</div>
              <div class="stat-trend">
                <el-icon><Top /></el-icon>
                <span>+12.5%</span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card success">
            <div class="stat-icon"><el-icon><Shop /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.merchantCount }}</div>
              <div class="stat-label">商家总数</div>
              <div class="stat-trend">
                <el-icon><Top /></el-icon>
                <span>+8.3%</span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card warning">
            <div class="stat-icon"><el-icon><ShoppingCart /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.orderCount }}</div>
              <div class="stat-label">订单总数</div>
              <div class="stat-trend">
                <el-icon><Top /></el-icon>
                <span>+25.7%</span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card danger">
            <div class="stat-icon"><el-icon><Money /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.revenue }}</div>
              <div class="stat-label">平台营收</div>
              <div class="stat-trend">
                <el-icon><Top /></el-icon>
                <span>+18.2%</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </section>

    <!-- 图表区域 -->
    <section class="chart-section">
      <el-row :gutter="15">
        <el-col :span="12">
          <div class="chart-card">
            <div class="card-header">
              <el-icon><TrendCharts /></el-icon>
              <span class="card-title">订单趋势</span>
            </div>
            <div class="chart-container" ref="orderChartRef"></div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="chart-card">
            <div class="card-header">
              <el-icon><PieChart /></el-icon>
              <span class="card-title">订单状态分布</span>
            </div>
            <div class="chart-container" ref="orderStatusChartRef"></div>
          </div>
        </el-col>
      </el-row>
    </section>

    <section class="chart-section">
      <el-row :gutter="15">
        <el-col :span="12">
          <div class="chart-card">
            <div class="card-header">
              <el-icon><DataLine /></el-icon>
              <span class="card-title">销售额趋势</span>
            </div>
            <div class="chart-container" ref="revenueChartRef"></div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="chart-card">
            <div class="card-header">
              <el-icon><Histogram /></el-icon>
              <span class="card-title">商品分类占比</span>
            </div>
            <div class="chart-container" ref="categoryChartRef"></div>
          </div>
        </el-col>
      </el-row>
    </section>

    <!-- 排行榜 -->
    <section class="rank-section">
      <el-row :gutter="15">
        <el-col :span="12">
          <div class="rank-card">
            <div class="card-header">
              <el-icon><Trophy /></el-icon>
              <span class="card-title">商家营收排行</span>
            </div>
            <el-table :data="merchantRankList" class="sci-table" style="width: 100%">
              <el-table-column prop="rank" label="排名" width="80">
                <template #default="{ row }">
                  <span class="rank-badge" :class="'rank-' + row.rank">{{ row.rank }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="shopName" label="店铺名称" min-width="150" />
              <el-table-column prop="revenue" label="营收 (元)" width="120">
                <template #default="{ row }">
                  <span class="revenue-text">¥{{ row.revenue }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="trend" label="趋势" width="80">
                <template #default="{ row }">
                  <el-icon v-if="row.trend > 0" color="#00ff88"><Top /></el-icon>
                  <el-icon v-else color="#ff6666"><Bottom /></el-icon>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="rank-card">
            <div class="card-header">
              <el-icon><Trophy /></el-icon>
              <span class="card-title">商品销量排行</span>
            </div>
            <el-table :data="productRankList" class="sci-table" style="width: 100%">
              <el-table-column prop="rank" label="排名" width="80">
                <template #default="{ row }">
                  <span class="rank-badge" :class="'rank-' + row.rank">{{ row.rank }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="productName" label="商品名称" min-width="150" />
              <el-table-column prop="sales" label="销量" width="100" />
              <el-table-column prop="trend" label="趋势" width="80">
                <template #default="{ row }">
                  <el-icon v-if="row.trend > 0" color="#00ff88"><Top /></el-icon>
                  <el-icon v-else color="#ff6666"><Bottom /></el-icon>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import {
  DataAnalysis,
  TrendCharts,
  PieChart,
  DataLine,
  Histogram,
  Trophy,
  User,
  Shop,
  ShoppingCart,
  Money,
  Top,
  Bottom
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'

interface Stats {
  userCount: string
  merchantCount: string
  orderCount: string
  revenue: string
}

interface RankItem {
  rank: number
  shopName?: string
  productName?: string
  revenue?: string
  sales?: number
  trend: number
}

const dateRange = ref<[Date, Date]>()
const orderChartRef = ref<HTMLElement>()
const orderStatusChartRef = ref<HTMLElement>()
const revenueChartRef = ref<HTMLElement>()
const categoryChartRef = ref<HTMLElement>()

const stats = ref<Stats>({
  userCount: '12,580',
  merchantCount: '356',
  orderCount: '89,760',
  revenue: '1,258.6 万'
})

const merchantRankList = ref<RankItem[]>([
  { rank: 1, shopName: '品质优选店', revenue: '125.6 万', trend: 15 },
  { rank: 2, shopName: '数码港湾', revenue: '98.5 万', trend: 8 },
  { rank: 3, shopName: '电器城', revenue: '87.3 万', trend: -3 },
  { rank: 4, shopName: '时尚衣橱', revenue: '76.2 万', trend: 12 },
  { rank: 5, shopName: '家居生活馆', revenue: '65.8 万', trend: 5 }
])

const productRankList = ref<RankItem[]>([
  { rank: 1, productName: '无线蓝牙耳机', sales: 10240, trend: 20 },
  { rank: 2, productName: '智能手环', sales: 8960, trend: 15 },
  { rank: 3, productName: '机械键盘', sales: 7680, trend: -5 },
  { rank: 4, productName: '空气净化器', sales: 5120, trend: 8 },
  { rank: 5, productName: '运动跑鞋', sales: 4860, trend: 3 }
])

const handleDateChange = () => {
  console.log('日期范围变化:', dateRange.value)
  // 重新加载数据
}

const initCharts = () => {
  nextTick(() => {
    // 订单趋势图
    if (orderChartRef.value) {
      const chart = echarts.init(orderChartRef.value)
      const option: EChartsOption = {
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['3-12', '3-13', '3-14', '3-15', '3-16', '3-17', '3-18'],
          axisLine: { lineStyle: { color: 'rgba(0,212,255,0.3)' } },
          axisLabel: { color: '#888' }
        },
        yAxis: {
          type: 'value',
          splitLine: { lineStyle: { color: 'rgba(255,255,255,0.05)' } },
          axisLabel: { color: '#888' }
        },
        series: [{
          name: '订单数',
          type: 'line',
          smooth: true,
          data: [820, 932, 901, 1234, 1290, 1330, 1420],
          itemStyle: { color: '#00d4ff' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(0,212,255,0.3)' },
              { offset: 1, color: 'rgba(0,212,255,0)' }
            ])
          }
        }]
      }
      chart.setOption(option)
    }

    // 订单状态分布
    if (orderStatusChartRef.value) {
      const chart = echarts.init(orderStatusChartRef.value)
      const option: EChartsOption = {
        tooltip: { trigger: 'item' },
        legend: { top: '5%', left: 'center', textStyle: { color: '#888' } },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          itemStyle: {
            borderRadius: 10,
            borderColor: '#1a1f3a',
            borderWidth: 2
          },
          label: { color: '#fff' },
          data: [
            { value: 3580, name: '已完成', itemStyle: { color: '#00ff88' } },
            { value: 1290, name: '待发货', itemStyle: { color: '#00d4ff' } },
            { value: 860, name: '待付款', itemStyle: { color: '#ffaa00' } },
            { value: 512, name: '已取消', itemStyle: { color: '#666' } },
            { value: 120, name: '退款中', itemStyle: { color: '#ff6666' } }
          ]
        }]
      }
      chart.setOption(option)
    }

    // 销售额趋势
    if (revenueChartRef.value) {
      const chart = echarts.init(revenueChartRef.value)
      const option: EChartsOption = {
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['3-12', '3-13', '3-14', '3-15', '3-16', '3-17', '3-18'],
          axisLine: { lineStyle: { color: 'rgba(0,212,255,0.3)' } },
          axisLabel: { color: '#888' }
        },
        yAxis: {
          type: 'value',
          splitLine: { lineStyle: { color: 'rgba(255,255,255,0.05)' } },
          axisLabel: { color: '#888' }
        },
        series: [{
          name: '销售额',
          type: 'line',
          smooth: true,
          data: [12.5, 15.8, 18.2, 22.3, 19.8, 25.6, 28.9],
          itemStyle: { color: '#00ff88' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(0,255,136,0.3)' },
              { offset: 1, color: 'rgba(0,255,136,0)' }
            ])
          }
        }]
      }
      chart.setOption(option)
    }

    // 商品分类占比
    if (categoryChartRef.value) {
      const chart = echarts.init(categoryChartRef.value)
      const option: EChartsOption = {
        tooltip: { trigger: 'item' },
        legend: { top: '5%', left: 'center', textStyle: { color: '#888' } },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          itemStyle: {
            borderRadius: 10,
            borderColor: '#1a1f3a',
            borderWidth: 2
          },
          label: { color: '#fff' },
          data: [
            { value: 2580, name: '手机数码', itemStyle: { color: '#00d4ff' } },
            { value: 1890, name: '电脑办公', itemStyle: { color: '#00ff88' } },
            { value: 1520, name: '家用电器', itemStyle: { color: '#ffaa00' } },
            { value: 1280, name: '服装鞋包', itemStyle: { color: '#ff6699' } },
            { value: 980, name: '美妆护肤', itemStyle: { color: '#a335ee' } },
            { value: 650, name: '其他', itemStyle: { color: '#666' } }
          ]
        }]
      }
      chart.setOption(option)
    }
  })
}

onMounted(() => {
  initCharts()
  window.addEventListener('resize', () => {
    echarts.getInstanceByDom(orderChartRef.value!)?.resize()
    echarts.getInstanceByDom(orderStatusChartRef.value!)?.resize()
    echarts.getInstanceByDom(revenueChartRef.value!)?.resize()
    echarts.getInstanceByDom(categoryChartRef.value!)?.resize()
  })
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
}

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: bold;
  color: #fff;
}

.page-title .el-icon {
  color: var(--mall-primary);
  font-size: 24px;
}

.stats-cards {
  
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--glow-color), transparent);
}

.stat-card:hover {
  transform: translateY(-3px);
  border-color: var(--glow-color);
  box-shadow: 0 8px 30px rgba(0, 212, 255, 0.15);
}

.stat-card.primary { --glow-color: #00d4ff; }
.stat-card.success { --glow-color: #00ff88; }
.stat-card.warning { --glow-color: #ffaa00; }
.stat-card.danger { --glow-color: #ff6666; }

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  color: #fff;
  flex-shrink: 0;
}

.stat-card.primary .stat-icon {
  background: linear-gradient(135deg, #00d4ff, #00a8cc);
}

.stat-card.success .stat-icon {
  background: linear-gradient(135deg, #00ff88, #00cc6a);
}

.stat-card.warning .stat-icon {
  background: linear-gradient(135deg, #ffaa00, #ff8800);
}

.stat-card.danger .stat-icon {
  background: linear-gradient(135deg, #ff6666, #ff4444);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 26px;
  font-weight: bold;
  color: #fff;
}

.stat-label {
  font-size: 13px;
  color: #888;
  
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #00ff88;
  
}

.chart-section {
  
}

.chart-card {
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.15);
}

.card-header .el-icon {
  color: var(--mall-primary);
  font-size: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
  color: #fff;
}

.chart-container {
  height: 300px;
  width: 100%;
}

.rank-section {
  
}

.rank-card {
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 20px;
}

.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: bold;
  color: #fff;
}

.rank-badge.rank-1 {
  background: linear-gradient(135deg, #ffd700, #ffaa00);
  box-shadow: 0 0 12px rgba(255, 215, 0, 0.5);
}

.rank-badge.rank-2 {
  background: linear-gradient(135deg, #c0c0c0, #999);
  box-shadow: 0 0 12px rgba(192, 192, 192, 0.5);
}

.rank-badge.rank-3 {
  background: linear-gradient(135deg, #cd7f32, #b87333);
  box-shadow: 0 0 12px rgba(205, 127, 50, 0.5);
}

.rank-badge.rank-4,
.rank-badge.rank-5 {
  background: rgba(255, 255, 255, 0.15);
}

.revenue-text {
  color: var(--mall-primary);
  font-weight: bold;
}

.sci-table :deep(.el-table__header th) {
  background: rgba(0, 212, 255, 0.08);
  color: var(--mall-primary);
  font-size: 13px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.15);
}

.sci-table :deep(.el-table__body td) {
  background: transparent;
  color: #aaa;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  font-size: 13px;
}

.sci-table :deep(.el-table__row:hover) {
  background: rgba(0, 212, 255, 0.05);
}
</style>

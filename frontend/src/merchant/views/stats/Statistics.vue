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
            <div class="stat-icon"><el-icon><ShoppingCart /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayOrders }}</div>
              <div class="stat-label">今日订单</div>
              <div class="stat-trend">
                <el-icon><Top /></el-icon>
                <span>+15.3%</span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card success">
            <div class="stat-icon"><el-icon><Money /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.todayRevenue }}</div>
              <div class="stat-label">今日销售额</div>
              <div class="stat-trend">
                <el-icon><Top /></el-icon>
                <span>+22.5%</span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card warning">
            <div class="stat-icon"><el-icon><Goods /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.productViews }}</div>
              <div class="stat-label">商品浏览</div>
              <div class="stat-trend">
                <el-icon><Top /></el-icon>
                <span>+8.7%</span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card danger">
            <div class="stat-icon"><el-icon><User /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.newCustomers }}</div>
              <div class="stat-label">新增客户</div>
              <div class="stat-trend">
                <el-icon><Top /></el-icon>
                <span>+12.1%</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </section>

    <!-- 图表区域 -->
    <section class="chart-section">
      <el-row :gutter="15">
        <el-col :span="16">
          <div class="chart-card">
            <div class="card-header">
              <el-icon><TrendCharts /></el-icon>
              <span class="card-title">销售趋势</span>
            </div>
            <div class="chart-container" ref="revenueChartRef"></div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="chart-card">
            <div class="card-header">
              <el-icon><PieChart /></el-icon>
              <span class="card-title">订单状态</span>
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
              <el-icon><Histogram /></el-icon>
              <span class="card-title">商品销量排行</span>
            </div>
            <div class="rank-list">
              <div
                v-for="(item, index) in productRankList"
                :key="item.id"
                class="rank-item"
              >
                <span class="rank-number" :class="'rank-' + (index + 1)">{{ index + 1 }}</span>
                <el-avatar :size="40" :src="item.image" class="product-image" />
                <div class="rank-info">
                  <span class="rank-name">{{ item.name }}</span>
                  <span class="rank-sales">销量：{{ item.sales }}</span>
                </div>
                <span class="rank-revenue">¥{{ item.revenue }}</span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="chart-card">
            <div class="card-header">
              <el-icon><DataLine /></el-icon>
              <span class="card-title">访客分析</span>
            </div>
            <div class="visitor-stats">
              <div class="visitor-item">
                <div class="visitor-label">今日访客</div>
                <div class="visitor-value">{{ visitorStats.today }}</div>
                <div class="visitor-trend up">
                  <el-icon><Top /></el-icon>
                  <span>+18.5%</span>
                </div>
              </div>
              <div class="visitor-item">
                <div class="visitor-label">昨日访客</div>
                <div class="visitor-value">{{ visitorStats.yesterday }}</div>
              </div>
              <div class="visitor-item">
                <div class="visitor-label">本周累计</div>
                <div class="visitor-value">{{ visitorStats.week }}</div>
                <div class="visitor-trend up">
                  <el-icon><Top /></el-icon>
                  <span>+25.3%</span>
                </div>
              </div>
              <div class="visitor-item">
                <div class="visitor-label">本月累计</div>
                <div class="visitor-value">{{ visitorStats.month }}</div>
                <div class="visitor-trend up">
                  <el-icon><Top /></el-icon>
                  <span>+32.8%</span>
                </div>
              </div>
            </div>
            <div class="chart-container" ref="visitorChartRef" style="height: 200px; margin-top: 20px;"></div>
          </div>
        </el-col>
      </el-row>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import {
  DataAnalysis,
  TrendCharts,
  PieChart,
  Histogram,
  DataLine,
  ShoppingCart,
  Money,
  Goods,
  User,
  Top
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'

interface Stats {
  todayOrders: number
  todayRevenue: string
  productViews: number
  newCustomers: number
}

interface ProductRank {
  id: number
  name: string
  image: string
  sales: number
  revenue: string
}

interface VisitorStats {
  today: number
  yesterday: number
  week: number
  month: number
}

const dateRange = ref<[Date, Date]>()
const revenueChartRef = ref<HTMLElement>()
const orderStatusChartRef = ref<HTMLElement>()
const visitorChartRef = ref<HTMLElement>()

const stats = ref<Stats>({
  todayOrders: 128,
  todayRevenue: '9,876',
  productViews: 3580,
  newCustomers: 56
})

const productRankList = ref<ProductRank[]>([
  { id: 1, name: '无线蓝牙耳机', image: '', sales: 1024, revenue: '20.4 万' },
  { id: 2, name: '智能手环', image: '', sales: 896, revenue: '12.9 万' },
  { id: 3, name: '机械键盘', image: '', sales: 768, revenue: '25.2 万' },
  { id: 4, name: '空气净化器', image: '', sales: 512, revenue: '51.2 万' },
  { id: 5, name: '运动跑鞋', image: '', sales: 486, revenue: '14.5 万' }
])

const visitorStats = ref<VisitorStats>({
  today: 1256,
  yesterday: 1058,
  week: 8520,
  month: 35680
})

const handleDateChange = () => {
  console.log('日期范围变化:', dateRange.value)
}

const initCharts = () => {
  nextTick(() => {
    // 销售趋势图
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
        series: [
          {
            name: '销售额',
            type: 'line',
            smooth: true,
            data: [8200, 9320, 9010, 12340, 12900, 13300, 14200],
            itemStyle: { color: '#00d4ff' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(0,212,255,0.3)' },
                { offset: 1, color: 'rgba(0,212,255,0)' }
              ])
            }
          },
          {
            name: '订单数',
            type: 'line',
            smooth: true,
            data: [82, 93, 90, 123, 129, 133, 142],
            itemStyle: { color: '#00ff88' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(0,255,136,0.3)' },
                { offset: 1, color: 'rgba(0,255,136,0)' }
              ])
            }
          }
        ]
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
            { value: 688, name: '已完成', itemStyle: { color: '#00ff88' } },
            { value: 358, name: '已发货', itemStyle: { color: '#00d4ff' } },
            { value: 124, name: '待发货', itemStyle: { color: '#ffaa00' } },
            { value: 86, name: '待付款', itemStyle: { color: '#666' } },
            { value: 12, name: '退款中', itemStyle: { color: '#ff6666' } }
          ]
        }]
      }
      chart.setOption(option)
    }

    // 访客趋势
    if (visitorChartRef.value) {
      const chart = echarts.init(visitorChartRef.value)
      const option: EChartsOption = {
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
        xAxis: {
          type: 'category',
          data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
          axisLine: { lineStyle: { color: 'rgba(0,212,255,0.3)' } },
          axisLabel: { color: '#888' }
        },
        yAxis: {
          type: 'value',
          splitLine: { lineStyle: { color: 'rgba(255,255,255,0.05)' } },
          axisLabel: { color: '#888' }
        },
        series: [{
          name: '访客数',
          type: 'bar',
          barWidth: '40%',
          data: [1058, 1120, 1256, 1189, 1350, 1420, 1256],
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#00d4ff' },
              { offset: 1, color: 'rgba(0,212,255,0.2)' }
            ]),
            borderRadius: [8, 8, 0, 0]
          }
        }]
      }
      chart.setOption(option)
    }
  })
}

onMounted(() => {
  initCharts()
  window.addEventListener('resize', () => {
    echarts.getInstanceByDom(revenueChartRef.value!)?.resize()
    echarts.getInstanceByDom(orderStatusChartRef.value!)?.resize()
    echarts.getInstanceByDom(visitorChartRef.value!)?.resize()
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
  margin-bottom: 20px;
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
  margin-bottom: 20px;
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
  margin-top: 4px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #00ff88;
  margin-top: 6px;
}

.chart-section {
  margin-bottom: 15px;
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
  margin-bottom: 20px;
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

.rank-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.rank-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  background: rgba(0, 212, 255, 0.05);
  border: 1px solid rgba(0, 212, 255, 0.1);
  border-radius: 8px;
  transition: all 0.3s;
}

.rank-item:hover {
  background: rgba(0, 212, 255, 0.1);
  border-color: var(--mall-primary);
}

.rank-number {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: bold;
  color: #fff;
  flex-shrink: 0;
}

.rank-number.rank-1 {
  background: linear-gradient(135deg, #ffd700, #ffaa00);
  box-shadow: 0 0 10px rgba(255, 215, 0, 0.4);
}

.rank-number.rank-2 {
  background: linear-gradient(135deg, #c0c0c0, #999);
  box-shadow: 0 0 10px rgba(192, 192, 192, 0.4);
}

.rank-number.rank-3 {
  background: linear-gradient(135deg, #cd7f32, #b87333);
  box-shadow: 0 0 10px rgba(205, 127, 50, 0.4);
}

.rank-number.rank-4,
.rank-number.rank-5 {
  background: rgba(255, 255, 255, 0.15);
}

.product-image {
  border-radius: 8px;
}

.rank-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.rank-name {
  color: #fff;
  font-size: 14px;
  font-weight: 500;
}

.rank-sales {
  color: #888;
  font-size: 12px;
}

.rank-revenue {
  color: var(--mall-primary);
  font-weight: bold;
  font-size: 15px;
}

.visitor-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.visitor-item {
  padding: 15px;
  background: rgba(0, 212, 255, 0.05);
  border: 1px solid rgba(0, 212, 255, 0.1);
  border-radius: 8px;
}

.visitor-label {
  font-size: 12px;
  color: #888;
  margin-bottom: 8px;
}

.visitor-value {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
}

.visitor-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  margin-top: 6px;
}

.visitor-trend.up {
  color: #00ff88;
}
</style>

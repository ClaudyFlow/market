<template>
  <div class="page-container">
    <PageHeader title="数据统计" :icon="DataAnalysis" />

    <StatCards :cards="statCards" />

    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <SciCard variant="gradient">
          <div class="chart-placeholder">
            <h3>销售趋势</h3>
            <div id="sales-chart" class="chart-container"></div>
          </div>
        </SciCard>
      </el-col>
      <el-col :span="12">
        <SciCard variant="gradient">
          <div class="chart-placeholder">
            <h3>订单统计</h3>
            <div id="order-chart" class="chart-container"></div>
          </div>
        </SciCard>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <SciCard variant="gradient">
          <div class="chart-placeholder">
            <h3>商品分类占比</h3>
            <div id="category-chart" class="chart-container"></div>
          </div>
        </SciCard>
      </el-col>
      <el-col :span="12">
        <SciCard variant="gradient">
          <div class="chart-placeholder">
            <h3>用户增长</h3>
            <div id="user-chart" class="chart-container"></div>
          </div>
        </SciCard>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { DataAnalysis, TrendCharts, ShoppingCart, User, Money } from '@element-plus/icons-vue'
import { PageHeader, StatCards } from '@merchant/components'

const stats = { sales: 125680, orders: 1256, users: 856, revenue: 98520 }

const statCards = computed(() => [
  { value: `¥${stats.sales.toLocaleString()}`, label: '销售额', type: 'primary' as const, icon: Money },
  { value: stats.orders, label: '订单数', type: 'success' as const, icon: ShoppingCart },
  { value: stats.users, label: '新增用户', type: 'warning' as const, icon: User },
  { value: `¥${stats.revenue.toLocaleString()}`, label: '营收', type: 'info' as const, icon: TrendCharts }
])
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

.chart-placeholder {
  padding: 16px;
}

.chart-placeholder h3 {
  margin: 0 0 16px;
  color: #fff;
  font-size: 16px;
}

.chart-container {
  height: 300px;
  background: rgba(10, 20, 50, 0.5);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #88aacc;
}
</style>

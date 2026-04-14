<template>
  <div class="operations-dashboard">
    <el-page-header @back="$router.push('/dashboard')" content="运营数据分析" />

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 漏斗分析 -->
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>用户行为转化漏斗 (近{{ funnelDays }}天)</span>
              <el-radio-group v-model="funnelDays" size="small" @change="loadFunnelAnalysis">
                <el-radio-button :label="7">近7天</el-radio-button>
                <el-radio-button :label="30">近30天</el-radio-button>
                <el-radio-button :label="90">近90天</el-radio-button>
              </el-radio-group>
            </div>
          </template>

          <div class="funnel-container">
            <el-row :gutter="10" class="funnel-steps">
              <el-col :span="4" v-for="(step, index) in funnelSteps" :key="step.key">
                <div class="funnel-step">
                  <div class="funnel-value">{{ funnelData[step.key] || 0 }}</div>
                  <div class="funnel-label">{{ step.label }}</div>
                  <div v-if="index < funnelSteps.length - 1" class="funnel-rate">
                    转化率: {{ funnelData[step.rateKey] || 0 }}%
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>

      <!-- 留存分析 -->
      <el-col :span="12" style="margin-top: 20px;">
        <el-card>
          <template #header>用户留存分析</template>
          <div class="stats-grid">
            <el-statistic title="总用户数" :value="retentionData.totalUsers || 0" />
            <el-statistic title="近7天活跃" :value="retentionData.activeUsers7d || 0" />
            <el-statistic title="近30天活跃" :value="retentionData.activeUsers30d || 0" />
            <el-statistic title="7天活跃率" :value="retentionData.activeRate7d || 0" suffix="%" />
            <el-statistic title="30天活跃率" :value="retentionData.activeRate30d || 0" suffix="%" />
          </div>

          <el-table :data="retentionData.dailyRetention || []" style="margin-top: 20px;" max-height="300">
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="newUsers" label="新增用户" width="100" />
            <el-table-column prop="nextDayRetention" label="次日留存" width="100">
              <template #default="{ row }">{{ row.nextDayRetention }}%</template>
            </el-table-column>
            <el-table-column prop="day3Retention" label="3日留存" width="100">
              <template #default="{ row }">{{ row.day3Retention }}%</template>
            </el-table-column>
            <el-table-column prop="day7Retention" label="7日留存" width="100">
              <template #default="{ row }">{{ row.day7Retention }}%</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 内容审核统计 -->
      <el-col :span="12" style="margin-top: 20px;">
        <el-card>
          <template #header>内容审核统计</template>
          <el-tabs>
            <el-tab-pane label="评价审核">
              <div class="stats-grid">
                <el-statistic title="总评价数" :value="reviewAuditStats.totalReviews || 0" />
                <el-statistic title="已通过" :value="reviewAuditStats.approved || 0" />
                <el-statistic title="已拒绝" :value="reviewAuditStats.rejected || 0" />
                <el-statistic title="已过滤" :value="reviewAuditStats.filtered || 0" />
                <el-statistic title="待审核" :value="reviewAuditStats.pending || 0" />
                <el-statistic title="通过率" :value="reviewAuditStats.approvalRate || 0" suffix="%" />
              </div>
              <el-button type="primary" style="margin-top: 20px;" @click="$router.push('/operations/review-audit')">
                查看待审核评价
              </el-button>
            </el-tab-pane>

            <el-tab-pane label="论坛审核">
              <div class="stats-grid">
                <el-statistic title="总帖子数" :value="forumAuditStats.totalPosts || 0" />
                <el-statistic title="已通过" :value="forumAuditStats.approved || 0" />
                <el-statistic title="已拒绝" :value="forumAuditStats.rejected || 0" />
                <el-statistic title="已过滤" :value="forumAuditStats.filtered || 0" />
                <el-statistic title="待审核" :value="forumAuditStats.pending || 0" />
                <el-statistic title="通过率" :value="forumAuditStats.approvalRate || 0" suffix="%" />
              </div>
              <el-button type="primary" style="margin-top: 20px;" @click="$router.push('/operations/forum-audit')">
                查看待审核帖子
              </el-button>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>

      <!-- 敏感词统计 -->
      <el-col :span="24" style="margin-top: 20px;">
        <el-card>
          <template #header>敏感词库统计</template>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-statistic title="敏感词总数" :value="sensitiveWordStats.totalWords || 0" />
            </el-col>
            <el-col :span="8">
              <div class="chart-container">
                <h4>类型分布</h4>
                <div v-for="(count, type) in sensitiveWordStats.typeDistribution" :key="type" class="dist-item">
                  <el-tag>{{ type }}</el-tag>
                  <span>{{ count }} 个</span>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="chart-container">
                <h4>Top 10 高频敏感词</h4>
                <div v-for="word in sensitiveWordStats.topWords" :key="word.word" class="top-word">
                  <span class="word-text">{{ word.word }}</span>
                  <el-tag size="small" type="danger">{{ word.matchCount }} 次</el-tag>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>

      <!-- 商品转化率 Top 10 -->
      <el-col :span="24" style="margin-top: 20px;">
        <el-card>
          <template #header>商品转化率排行 Top 10</template>
          <el-table :data="productConversionData" style="width: 100%">
            <el-table-column type="index" label="排名" width="60" />
            <el-table-column prop="productName" label="商品名称" min-width="150" />
            <el-table-column prop="category" label="分类" width="100" />
            <el-table-column prop="views" label="浏览量" width="100" />
            <el-table-column prop="carts" label="加购数" width="100" />
            <el-table-column prop="orders" label="订单数" width="100" />
            <el-table-column prop="completedOrders" label="成交数" width="100" />
            <el-table-column prop="viewToCartRate" label="浏览→加购" width="120">
              <template #default="{ row }">{{ row.viewToCartRate }}%</template>
            </el-table-column>
            <el-table-column prop="viewToOrderRate" label="浏览→下单" width="120">
              <template #default="{ row }">{{ row.viewToOrderRate }}%</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const funnelDays = ref(30)
const funnelData = ref<any>({})
const retentionData = ref<any>({})
const reviewAuditStats = ref<any>({})
const forumAuditStats = ref<any>({})
const sensitiveWordStats = ref<any>({})
const productConversionData = ref<any[]>([])

const funnelSteps = [
  { key: 'visitors', label: '访问用户', rateKey: 'visitToCartRate' },
  { key: 'cartUsers', label: '加购用户', rateKey: 'cartToFavoriteRate' },
  { key: 'favoriteUsers', label: '收藏用户', rateKey: 'favoriteToOrderRate' },
  { key: 'orderUsers', label: '下单用户', rateKey: 'orderToPaidRate' },
  { key: 'paidUsers', label: '支付用户', rateKey: 'paidToCompletedRate' },
  { key: 'completedUsers', label: '完成订单', rateKey: 'overallConversionRate' }
]

const loadFunnelAnalysis = async () => {
  try {
    const res = await axios.get(`/api/operations/funnel?days=${funnelDays.value}`)
    funnelData.value = res.data.data
  } catch (error) {
    ElMessage.error('加载漏斗分析失败')
  }
}

const loadRetentionAnalysis = async () => {
  try {
    const res = await axios.get('/api/operations/retention?days=30')
    retentionData.value = res.data.data
  } catch (error) {
    ElMessage.error('加载留存分析失败')
  }
}

const loadReviewAuditStats = async () => {
  try {
    const res = await axios.get('/api/operations/review-audit/stats')
    reviewAuditStats.value = res.data.data
  } catch (error) {
    ElMessage.error('加载评价审核统计失败')
  }
}

const loadForumAuditStats = async () => {
  try {
    const res = await axios.get('/api/operations/forum-audit/stats')
    forumAuditStats.value = res.data.data
  } catch (error) {
    ElMessage.error('加载论坛审核统计失败')
  }
}

const loadSensitiveWordStats = async () => {
  try {
    const res = await axios.get('/api/operations/sensitive-words/stats')
    sensitiveWordStats.value = res.data.data
  } catch (error) {
    ElMessage.error('加载敏感词统计失败')
  }
}

const loadProductConversion = async () => {
  try {
    const res = await axios.get('/api/operations/product-conversion?limit=10')
    productConversionData.value = res.data.data
  } catch (error) {
    ElMessage.error('加载商品转化率失败')
  }
}

onMounted(() => {
  loadFunnelAnalysis()
  loadRetentionAnalysis()
  loadReviewAuditStats()
  loadForumAuditStats()
  loadSensitiveWordStats()
  loadProductConversion()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.funnel-container {
  padding: 20px 0;
}

.funnel-steps {
  text-align: center;
}

.funnel-step {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: white;
}

.funnel-value {
  font-size: 28px;
  font-weight: bold;
}

.funnel-label {
  font-size: 14px;
  margin-top: 8px;
}

.funnel-rate {
  font-size: 12px;
  margin-top: 8px;
  opacity: 0.9;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.chart-container {
  padding: 10px;
}

.chart-container h4 {
  margin-bottom: 15px;
}

.dist-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  padding: 8px;
  background: #f5f7fa;
  border-radius: 4px;
}

.top-word {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  padding: 8px;
  background: #fff5f5;
  border-radius: 4px;
}

.word-text {
  font-weight: 500;
  color: #f56c6c;
}
</style>

<template>
  <div class="page-container">
    <PageHeader title="商家工作台">
      <template #actions>
        <SciButton type="primary" @click="refresh">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </SciButton>
      </template>
    </PageHeader>

    <StatCards :cards="statCards" />

    <el-row :gutter="20">
      <el-col :span="12">
        <SciCard variant="gradient">
          <template #header><h3>待处理订单</h3></template>
          <div class="pending-list">
            <div v-for="order in pendingOrders" :key="order.orderNo" class="pending-item">
              <span>{{ order.orderNo }}</span>
              <SciTag>{{ order.status }}</SciTag>
              <SciButton type="primary" size="small" text @click="handleOrder(order)">处理</SciButton>
            </div>
          </div>
        </SciCard>
      </el-col>
      <el-col :span="12">
        <SciCard variant="gradient">
          <template #header><h3>最新评价</h3></template>
          <div class="review-list">
            <div v-for="review in recentReviews" :key="review.id" class="review-item">
              <span>{{ review.userName }}</span>
              <span class="review-content">{{ review.content }}</span>
              <SciButton type="success" size="small" text @click="replyReview(review)">回复</SciButton>
            </div>
          </div>
        </SciCard>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, ShoppingCart, ChatDotRound, Warning, Check } from '@element-plus/icons-vue'
import { PageHeader, StatCards } from '@merchant/components'
import { SciButton, SciTag } from '@merchant/components/ui'

const stats = { orders: 12, reviews: 36, warnings: 5, completed: 156 }

const statCards = computed(() => [
  { value: stats.orders, label: '待处理订单', type: 'warning' as const, icon: ShoppingCart },
  { value: stats.reviews, label: '待回复评价', type: 'info' as const, icon: ChatDotRound },
  { value: stats.warnings, label: '库存预警', type: 'danger' as const, icon: Warning },
  { value: stats.completed, label: '今日已完成', type: 'success' as const, icon: Check }
])

const pendingOrders = ref([
  { orderNo: 'DD202603180001', status: '待发货' },
  { orderNo: 'DD202603180002', status: '待付款' },
  { orderNo: 'DD202603170003', status: '退款中' }
])

const recentReviews = ref([
  { id: 1, userName: '张先生', content: '商品很好，物流很快' },
  { id: 2, userName: '李女士', content: '包装精美，质量不错' },
  { id: 3, userName: '王先生', content: '性价比高，推荐购买' }
])

const refresh = () => ElMessage.success('数据已刷新')
const handleOrder = (o: any) => ElMessage.info(`处理订单: ${o.orderNo}`)
const replyReview = (r: any) => ElMessage.info(`回复评价: ${r.userName}`)
</script>

<style scoped>
.page-container {
  padding: 20px;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.05) 0%, transparent 100%);
  min-height: 100vh;
}

.pending-list, .review-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.pending-item, .review-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: rgba(10, 20, 50, 0.5);
  border-radius: 8px;
}

.review-content {
  flex: 1;
  color: #88aacc;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>

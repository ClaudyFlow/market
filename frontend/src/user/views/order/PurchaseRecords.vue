<template>
  <div class="purchase-records">
    <el-timeline v-if="records.length">
      <el-timeline-item
        v-for="(record, index) in records"
        :key="index"
        :timestamp="record.timestamp"
        :color="getColor(record.status)"
        :hollow="index !== 0"
        placement="top"
      >
        <el-card class="record-card">
          <div class="record-header">
            <el-tag :type="getTagType(record.status)" effect="dark" round>
              {{ getStatusText(record.status) }}
            </el-tag>
            <span class="record-hash" v-if="record.hash">
              <el-icon><Key /></el-icon>
              {{ record.hash.substring(0, 16) }}...
            </span>
          </div>
          <div class="record-content" v-if="record.description">
            <p>{{ record.description }}</p>
          </div>
          <div class="record-meta">
            <span class="record-location" v-if="record.location">
              <el-icon><Location /></el-icon> {{ record.location }}
            </span>
            <span class="record-verified" v-if="record.verified">
              <el-icon><CircleCheck /></el-icon>
              <el-tag type="success" size="small">✓ 已验证</el-tag>
            </span>
          </div>
        </el-card>
      </el-timeline-item>
    </el-timeline>
    <el-empty v-else description="暂无购物记录" />
  </div>
</template>

<script setup lang="ts">
import { Key, Location, CircleCheck } from '@element-plus/icons-vue'

interface Record {
  status: string
  timestamp: string
  description?: string
  location?: string
  hash?: string
  verified?: boolean
}

defineProps<{
  records: Record[]
}>()

const getColor = (status: string) => {
  const colors: Record<string, string> = {
    CREATE: '#409EFF',
    PAYMENT: '#E6A23C',
    PAID: '#E6A23C',
    SHIPPED: '#67C23A',
    DELIVERED: '#67C23A',
    COMPLETED: '#67C23A',
    CANCELLED: '#909399',
    REFUNDING: '#F56C6C',
    REFUNDED: '#F56C6C'
  }
  return colors[status] || '#909399'
}

const getTagType = (status: string) => {
  const types: Record<string, string> = {
    CREATE: 'primary',
    PAYMENT: 'warning',
    PAID: 'warning',
    SHIPPED: 'success',
    DELIVERED: 'success',
    COMPLETED: 'success',
    CANCELLED: 'info',
    REFUNDING: 'danger',
    REFUNDED: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    CREATE: '已创建',
    PAYMENT: '待支付',
    PAID: '已支付',
    PAYMENT_COMPLETED: '支付完成',
    SHIPPED: '已发货',
    IN_TRANSIT: '运输中',
    DELIVERED: '已送达',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
    REFUNDING: '退款中',
    REFUNDED: '已退款'
  }
  return texts[status] || status
}
</script>

<style scoped>
.purchase-records {
  padding: 10px 0;
}

.record-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  transition: all 0.2s;
}

.record-card:hover {
  border-color: rgba(64, 158, 255, 0.3);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.record-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.record-hash {
  font-family: 'SF Mono', Consolas, monospace;
  font-size: 12px;
  color: var(--mall-text-muted);
  display: flex;
  align-items: center;
  gap: 4px;
}

.record-content {
  margin: 8px 0;
}

.record-content p {
  font-size: 14px;
  color: var(--mall-text-secondary);
  margin: 0;
  line-height: 1.5;
}

.record-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 8px;
  font-size: 12px;
  color: var(--mall-text-muted);
}

.record-location {
  display: flex;
  align-items: center;
  gap: 4px;
}

.record-verified {
  display: flex;
  align-items: center;
  gap: 4px;
}

:deep(.el-timeline-item__node) {
  background-color: var(--mall-primary);
}

:deep(.el-timeline-item__wrapper) {
  padding-left: 30px;
}
</style>
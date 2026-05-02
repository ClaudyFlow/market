<template>
  <div class="logistics-dialog">
    <el-dialog
      v-model="dialogVisible"
      title="物流信息"
      width="600px"
      :close-on-click-modal="false"
      @close="handleClose"
    >
      <div class="logistics-content" v-loading="loading">
        <!-- 物流概览 -->
        <div class="logistics-overview" v-if="logisticsInfo.trackingNo">
          <div class="overview-row">
            <span class="label">运单号：</span>
            <span class="value">{{ logisticsInfo.trackingNo }}</span>
            <el-button link type="primary" @click="copyTrackingNo">
              <el-icon><CopyDocument /></el-icon>
              复制
            </el-button>
          </div>
          <div class="overview-row">
            <span class="label">快递公司：</span>
            <span class="value">{{ logisticsInfo.companyName }}</span>
          </div>
          <div class="overview-row">
            <span class="label">当前状态：</span>
            <el-tag :type="getStatusType(logisticsInfo.status)" size="large">
              {{ getStatusText(logisticsInfo.status) }}
            </el-tag>
          </div>
          <div class="overview-row" v-if="logisticsInfo.estimatedDelivery">
            <span class="label">预计送达：</span>
            <span class="value">{{ formatDate(logisticsInfo.estimatedDelivery) }}</span>
          </div>
        </div>

        <!-- 暂无物流信息 -->
        <div class="no-logistics" v-else-if="!loading">
          <el-empty description="暂无物流信息">
            <el-button type="primary" @click="handleRefresh">刷新</el-button>
          </el-empty>
        </div>

        <!-- 物流轨迹 -->
        <div class="logistics-tracks" v-if="tracks.length > 0">
          <h4 class="tracks-title">物流轨迹</h4>
          <el-timeline class="tracks-timeline">
            <el-timeline-item
              v-for="(track, index) in tracks"
              :key="index"
              :timestamp="formatTrackTime(track.time)"
              placement="top"
              :type="getTrackType(track.status)"
              :size="index === 0 ? 'large' : 'normal'"
            >
              <el-card class="track-card" :class="{ 'latest': index === 0 }">
                <div class="track-content">
                  <div class="track-description">{{ track.description }}</div>
                  <div class="track-location" v-if="track.location">
                    <el-icon><Location /></el-icon>
                    {{ track.location }}
                  </div>
                  <div class="track-time">{{ formatTrackTime(track.time) }}</div>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>

        <!-- 模拟物流（测试用） -->
        <div class="mock-logistics" v-if="showMockButton">
          <el-divider />
          <el-alert
            title="测试环境"
            type="info"
            :closable="false"
            show-icon
            class="mock-tip"
          >
            <template #default>
              <div class="mock-actions">
                <span>当前无真实物流数据，可生成模拟轨迹用于测试：</span>
                <el-button type="primary" size="small" @click="handleGenerateMock">
                  生成模拟物流
                </el-button>
              </div>
            </template>
          </el-alert>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleClose">关闭</el-button>
          <el-button @click="handleRefresh">刷新</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { CopyDocument, Location } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getLogisticsByOrder, generateMockLogistics } from '@user/api/logistics'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  orderId: {
    type: Number,
    required: true
  },
  trackingNo: {
    type: String,
    default: ''
  },
  showMockButton: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'refresh'): void
}>()

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const loading = ref(false)
const logisticsInfo = reactive({
  trackingNo: '',
  companyCode: '',
  companyName: '',
  status: '',
  estimatedDelivery: ''
})

const tracks = ref<any[]>([])

// 监听对话框打开
watch(() => props.modelValue, (val) => {
  if (val) {
    fetchLogistics()
  }
})

// 获取物流信息
const fetchLogistics = async () => {
  loading.value = true
  tracks.value = []

  try {
    const res = await getLogisticsByOrder(props.orderId)
    if (res.trackingNo) {
      logisticsInfo.trackingNo = res.trackingNo
      logisticsInfo.companyCode = res.companyCode
      logisticsInfo.companyName = res.companyName
      logisticsInfo.status = res.status
      logisticsInfo.estimatedDelivery = res.estimatedDelivery || ''
    } else {
      logisticsInfo.trackingNo = ''
    }
  } catch (error) {
    console.error('获取物流信息失败:', error)
  } finally {
    loading.value = false
  }
}

// 复制运单号
const copyTrackingNo = () => {
  if (logisticsInfo.trackingNo) {
    navigator.clipboard.writeText(logisticsInfo.trackingNo)
    ElMessage.success('运单号已复制')
  }
}

// 生成模拟物流（测试用）
const handleGenerateMock = async () => {
  loading.value = true

  try {
    await generateMockLogistics(props.orderId)
    ElMessage.success('模拟物流已生成')
    await fetchLogistics()
  } catch (error: any) {
    ElMessage.error(error.message || '生成失败，请重试')
  } finally {
    loading.value = false
  }
}

// 刷新
const handleRefresh = () => {
  fetchLogistics()
  emit('refresh')
}

// 关闭
const handleClose = () => {
  dialogVisible.value = false
}

// 格式化日期
const formatDate = (date: string | Date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

// 格式化轨迹时间
const formatTrackTime = (time: string | Date) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取状态类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, any> = {
    'PENDING': 'info',
    'IN_TRANSIT': 'warning',
    'DELIVERED': 'success',
    'EXCEPTION': 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    'PENDING': '待发货',
    'IN_TRANSIT': '运输中',
    'DELIVERED': '已签收',
    'EXCEPTION': '异常'
  }
  return textMap[status] || status
}

// 获取轨迹类型
const getTrackType = (status: string) => {
  if (status === 'DELIVERED') return 'success'
  if (status === 'EXCEPTION') return 'danger'
  return 'primary'
}
</script>

<style scoped>
.logistics-content {
  padding: 10px 0;
}

/* 物流概览 */
.logistics-overview {
  background: rgba(26, 31, 58, 0.5);
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.overview-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  font-size: 14px;
}

.overview-row:last-child {
  margin-bottom: 0;
}

.overview-row .label {
  color: var(--mall-text-secondary);
  min-width: 80px;
}

.overview-row .value {
  color: var(--mall-text-primary);
  font-weight: 500;
}

/* 暂无物流 */
.no-logistics {
  padding: 40px 0;
}

/* 物流轨迹 */
.logistics-tracks {
  margin-top: 20px;
}

.tracks-title {
  color: var(--mall-text-primary);
  font-size: 14px;
  margin-bottom: 16px;
}

.tracks-timeline {
  padding: 0 10px;
}

.track-card {
  background: rgba(26, 31, 58, 0.5);
  border-color: var(--mall-border-light);
  transition: all 0.3s;
}

.track-card.latest {
  border-color: var(--mall-primary);
  box-shadow: 0 0 0 2px rgba(0, 212, 255, 0.2);
}

.track-content {
  padding: 4px 0;
}

.track-description {
  color: var(--mall-text-primary);
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
}

.track-location {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--mall-text-secondary);
  font-size: 12px;
  margin-bottom: 4px;
}

.track-location .el-icon {
  color: var(--mall-primary);
}

.track-time {
  color: var(--mall-text-muted);
  font-size: 12px;
}

/* 模拟物流 */
.mock-logistics {
  margin-top: 20px;
}

.mock-tip {
  background: rgba(0, 198, 255, 0.1);
  border-color: rgba(0, 198, 255, 0.3);
}

.mock-tip :deep(.el-alert__content) {
  color: var(--mall-info);
}

.mock-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>

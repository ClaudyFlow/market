<template>
  <span class="status-dot" :class="status" :title="statusText" aria-label="状态指示">
    <span class="dot"></span>
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue'

type StatusType = 'loading' | 'stuck' | 'success' | 'timeout' | 'error'

const props = defineProps({
  status: {
    type: String as () => StatusType,
    default: 'loading'
  }
})

const statusTextMap: Record<StatusType, string> = {
  loading: '获取中',
  stuck: '定位卡顿',
  success: '定位成功',
  timeout: '定位超时',
  error: '定位失败'
}

const statusText = computed(() => statusTextMap[props.status] || '')
</script>

<style scoped>
.status-dot {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-left: 4px;
}

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: currentColor;
}

/* 获取中 - 蓝色脉动 */
.status-dot.loading .dot {
  color: #00d4ff;
  animation: pulse 1.5s ease-in-out infinite;
}

/* 卡顿 - 黄色慢闪 */
.status-dot.stuck .dot {
  color: #ffdd00;
  animation: pulse 2s ease-in-out infinite;
}

/* 成功 - 绿色常亮 */
.status-dot.success .dot {
  color: #00ff88;
  box-shadow: 0 0 8px rgba(0, 255, 136, 0.6);
}

/* 超时 - 橙色慢闪 */
.status-dot.timeout .dot {
  color: #ff8800;
  animation: pulse 2s ease-in-out infinite;
}

/* 失败/断联 - 红色慢闪 */
.status-dot.error .dot {
  color: #ff3366;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.4;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.2);
  }
}
</style>

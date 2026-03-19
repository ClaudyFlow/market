<template>
  <div class="top-info-bar" aria-label="顶部信息栏">
    <!-- 左侧:地址和在线人数 -->
    <div class="left-section">
      <LocationInfo />
      <div class="online-info">
        <el-icon><Connection /></el-icon>
        <span class="online-count">在线人数 {{ onlineCount.toLocaleString() }}</span>
        <StatusDot status="success" />
      </div>
    </div>

    <!-- 中间:时间 -->
    <div class="center-section">
      <TimeInfo />
    </div>

    <!-- 右侧:用户信息和积分 -->
    <div class="right-section">
      <slot name="user-info">
        <div class="user-info">
          <span class="username">
            <el-avatar :size="24" icon="User" />
            <span>{{ userText }}</span>
          </span>
        </div>
      </slot>
      <div class="credit-info">
        <el-icon><Trophy /></el-icon>
        <span class="credit-count">{{ userCredit }} 积分</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { Connection, Trophy } from '@element-plus/icons-vue'
import TimeInfo from './TimeInfo.vue'
import LocationInfo from './LocationInfo.vue'
import StatusDot from './StatusDot.vue'

const props = defineProps({
  // 用户文本
  userText: {
    type: String,
    default: '用户'
  },
  // 用户积分
  userCredit: {
    type: Number,
    default: 0
  },
  // 是否显示在线人数
  showOnline: {
    type: Boolean,
    default: true
  },
  // 自定义在线人数
  onlineCount: {
    type: Number,
    default: () => Math.floor(Math.random() * 1000) + 10000
  }
})

const internalOnlineCount = ref(props.onlineCount)

let onlineTimer = null

// 模拟在线人数更新
const updateOnlineCount = () => {
  if (props.showOnline) {
    const change = Math.floor((Math.random() - 0.5) * 100)
    internalOnlineCount.value = Math.max(10000, internalOnlineCount.value + change)
  }
}

onMounted(() => {
  if (props.showOnline) {
    updateOnlineCount()
    onlineTimer = setInterval(updateOnlineCount, 2000)
  }
})

onUnmounted(() => {
  if (onlineTimer) clearInterval(onlineTimer)
})
</script>

<style scoped>
.top-info-bar {
  display: grid;
  grid-template-columns: 1fr 2fr 1fr;
  align-items: center;
  position: relative;
  width: 100%;
  padding: 8px 16px;
}

/* 左侧区域 - 地址和在线人数 */
.left-section {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  gap: 4px;
  color: var(--mall-primary);
}

.online-info {
  display: flex;
  align-items: center;
  gap: 5px;
}

.online-count {
  color: var(--mall-secondary);
  font-size: 12px;
}

/* 中间区域 - 时间居中 */
.center-section {
  display: flex;
  align-items: center;
  justify-content: center;
}

.center-section :deep(.time-display) {
  justify-content: center;
}

/* 右侧区域 - 用户信息和积分 */
.right-section {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: center;
  gap: 4px;
  color: #fff;
  font-size: 13px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.username {
  display: flex;
  align-items: center;
  gap: 6px;
}

.credit-info {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  background: linear-gradient(135deg, rgba(255, 102, 0, 0.2), rgba(255, 136, 0, 0.2));
  border: 1px solid rgba(255, 136, 0, 0.4);
  border-radius: 12px;
  color: #ffa500;
  font-size: 12px;
  font-weight: bold;
  box-shadow: 0 0 8px rgba(255, 136, 0, 0.2);
}

.credit-info .el-icon {
  font-size: 14px;
  color: #ffa500;
}

.credit-count {
  color: #ffa500;
}
</style>

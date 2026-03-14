<template>
  <div class="top-info-bar" aria-label="顶部信息栏">
    <div class="server-info">
      <LocationInfo />
      <span class="divider">|</span>
      <div class="online-info">
        <el-icon><Connection /></el-icon>
        <span class="online-count">在线人数 {{ onlineCount.toLocaleString() }}</span>
      </div>
    </div>
    <TimeInfo />
    <div class="user-section">
      <slot name="user-info">
        <div class="user-info">
          <span class="username">
            <el-avatar :size="24" icon="User" />
            <span>{{ userText }}</span>
          </span>
        </div>
      </slot>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { Connection } from '@element-plus/icons-vue'
import TimeInfo from './TimeInfo.vue'
import LocationInfo from './LocationInfo.vue'

const props = defineProps({
  // 用户文本，如 '尊敬的会员' 或 '商家账号' 或 '管理员'
  userText: {
    type: String,
    default: '用户'
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  width: 100%;
}

.server-info {
  display: flex;
  align-items: center;
  gap: 15px;
  color: var(--mall-primary);
}

.time-display {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.user-section {
  display: flex;
  justify-content: flex-end;
}

.divider {
  color: rgba(255, 255, 255, 0.3);
  font-size: 12px;
}

.online-info {
  display: flex;
  align-items: center;
  gap: 5px;
}

.online-count {
  color: var(--mall-secondary);
  font-size: 13px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  font-size: 13px;
}

.username {
  display: flex;
  align-items: center;
  gap: 6px;
}
</style>

<template>
  <div class="top-info">
    <div class="container">
      <TopInfoBar user-text="尊敬的会员" :user-credit="0">
        <template #user-info>
          <div class="user-info">
            <div class="user-profile" @click="handleAuthClick" style="cursor: pointer;">
              <el-avatar :size="32" :src="isLoggedIn && currentUser?.avatarUrl ? currentUser.avatarUrl : `https://via.placeholder.com/32x32/00d4ff/fff?text=${userDisplayName ? userDisplayName[0].toUpperCase() : 'U'}`">
                <el-icon v-if="!isLoggedIn"><User /></el-icon>
              </el-avatar>
              <div class="user-details">
                <span class="user-name">{{ isLoggedIn && userDisplayName ? userDisplayName : '登录/注册' }}</span>
                <div class="user-status">
                  <span class="status-dot" :class="{ 'online': isLoggedIn }"></span>
                  <span class="status-text">{{ isLoggedIn ? '已连接' : '未登录' }}</span>
                </div>
              </div>
            </div>
          </div>
        </template>
      </TopInfoBar>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@user/stores/user'
import TopInfoBar from '@common/components/TopInfoBar.vue'
import { User } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = ref(false)
const currentUser = ref(null)

// 检查登录状态
const checkLoginStatus = () => {
  const token = localStorage.getItem('token')
  const user = localStorage.getItem('user')
  if (token && user) {
    isLoggedIn.value = true
    currentUser.value = JSON.parse(user)
  } else {
    isLoggedIn.value = false
    currentUser.value = null
  }
}

// 获取用户显示名称
const userDisplayName = computed(() => {
  if (isLoggedIn.value && currentUser.value) {
    return currentUser.value.name || currentUser.value.username || '用户'
  }
  return null
})

// 处理登录/注册点击
const handleAuthClick = () => {
  if (isLoggedIn.value) {
    router.push('/user')
  } else {
    router.push('/login')
  }
}

// 用户积分
const 用户积分 = computed(() => userStore.userCredit)

// 组件挂载时检查登录状态
onMounted(() => {
  checkLoginStatus()
  window.addEventListener('storage', checkLoginStatus)
})
</script>

<style scoped>
/* 顶部信息栏 */
.top-info {
  background: rgba(10,14,26,1);
  padding: 0;
  font-size: 13px;
}

.top-info > .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  width: 100%;
  padding: 0 2%;
  height: 40px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 10px;
  transition: all 0.3s ease;
}

.user-profile:hover {
  transform: translateX(4px);
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  font-weight: 600;
  font-size: 14px;
  color: #fff;
  white-space: nowrap;
}

.user-status {
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #888;
  transition: all 0.3s ease;
}

.status-dot.online {
  background-color: #00ff88;
  box-shadow: 0 0 8px rgba(0, 255, 136, 0.5);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.1);
  }
}

.status-text {
  font-size: 12px;
  color: #888;
}

.status-dot.online + .status-text {
  color: #00ff88;
}
</style>

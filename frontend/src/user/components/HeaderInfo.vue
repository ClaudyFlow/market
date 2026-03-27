<template>
  <div class="top-info">
    <div class="container">
      <TopInfoBar user-text="尊敬的会员" :user-credit="用户积分">
        <template #user-info>
          <div class="user-info">
            <span class="username" @click="handleAuthClick" style="cursor: pointer;">
              <el-avatar :size="24" :src="isLoggedIn && currentUser?.avatarUrl ? currentUser.avatarUrl : `https://via.placeholder.com/24x24/00d4ff/fff?text=${userDisplayName ? userDisplayName[0].toUpperCase() : 'U'}`">
                <el-icon v-if="!isLoggedIn"><User /></el-icon>
              </el-avatar>
              <span class="auth-text">{{ isLoggedIn && userDisplayName ? userDisplayName : '登录/注册' }}</span>
            </span>
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
  padding: 8px 0;
  font-size: 13px;
}

.top-info > .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  max-width: 1400px;
  padding: 0 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.username {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  transition: all 0.3s ease;
  cursor: pointer;
}

.username:hover {
  color: var(--mall-primary);
  text-shadow: 0 0 10px rgba(0,212,255,0.5);
}

.auth-text {
  font-weight: 500;
  font-size: 14px;
}
</style>

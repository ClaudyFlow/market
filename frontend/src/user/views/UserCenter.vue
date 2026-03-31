<template>
  <div class="page-container">
    <header class="page-header">
      <h1 class="page-title">
        <el-icon><User /></el-icon>
        个人中心
      </h1>
    </header>

    <section class="user-menu">
      <div class="menu-grid">
        <div class="menu-item" @click="navigateTo('/user/order')">
          <div class="menu-icon"><el-icon><Document /></el-icon></div>
          <span class="menu-text">我的订单</span>
        </div>

        <div class="menu-item" @click="navigateTo('/user/favorite')">
          <div class="menu-icon"><el-icon><Star /></el-icon></div>
          <span class="menu-text">我的收藏</span>
        </div>

        <div class="menu-item" @click="navigateTo('/user/follow')">
          <div class="menu-icon"><el-icon><User /></el-icon></div>
          <span class="menu-text">我的关注</span>
        </div>

        <div class="menu-item" @click="navigateTo('/user/address')">
          <div class="menu-icon"><el-icon><Location /></el-icon></div>
          <span class="menu-text">地址管理</span>
        </div>

        <div class="menu-item" @click="navigateTo('/user/credit')">
          <div class="menu-icon"><el-icon><Trophy /></el-icon></div>
          <span class="menu-text">我的积分</span>
        </div>

        <div class="menu-item" @click="navigateTo('/user/coupon')">
          <div class="menu-icon"><el-icon><Ticket /></el-icon></div>
          <span class="menu-text">优惠券</span>
        </div>

        <div class="menu-item" @click="navigateTo('/user/settings')">
          <div class="menu-icon"><el-icon><Setting /></el-icon></div>
          <span class="menu-text">设置</span>
        </div>

        <div class="menu-item vip-card" @click="navigateTo('/user/vip')">
          <div class="menu-icon"><el-icon><Star /></el-icon></div>
          <div class="vip-content">
            <div class="vip-title">
              <span class="menu-text">VIP 会员中心</span>
              <el-tag type="warning" size="small">HOT</el-tag>
            </div>
            <div class="vip-info">
              <div class="info-item">
                <span class="info-label">当前等级</span>
                <span class="info-value">{{ vipLevelName }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">我的积分</span>
                <span class="info-value">{{ userCredit?.credit || 0 }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">成长值</span>
                <span class="info-value">{{ userCredit?.growthValue || 0 }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { User, Document, Star, Location, Trophy, Ticket, Setting } from '@element-plus/icons-vue'
import { useUserStore } from '@/common/stores/user'
import request from '@/common/api/request'

const router = useRouter()
const userStore = useUserStore()

const userCredit = ref(null)

const vipLevelName = computed(() => {
  const levels = ['普通会员', '白银会员', '黄金会员', '铂金会员', '钻石会员', '至尊会员']
  return levels[userStore.user?.vipLevel || 0]
})

const navigateTo = (path) => {
  router.push(path)
}

const loadUserCredit = async () => {
  try {
    const res = await request.get('/user/credit')
    userCredit.value = res.data || res
  } catch (error) {
    console.error('加载积分信息失败', error)
  }
}

onMounted(() => {
  loadUserCredit()
})
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.15) 0%, rgba(10, 14, 26, 0.8) 100%);
  padding: 40px 20px;
}

.page-header {
  max-width: 1200px;
  margin: 0 auto 40px;
}

.page-title {
  color: #fff;
  font-size: 32px;
  text-align: center;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.user-menu {
  max-width: 1200px;
  margin: 0 auto;
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 16px;
  padding: 40px;
  backdrop-filter: blur(10px);
}

.menu-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 20px 24px;
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  transition: all 0.3s;
  height: 80px;
  cursor: pointer;
}

.menu-item:hover {
  transform: translateX(8px);
  border-color: var(--mall-primary);
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.3);
  background: linear-gradient(90deg, rgba(0, 212, 255, 0.1), rgba(0, 255, 136, 0.05));
}

.menu-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--mall-primary), var(--mall-secondary));
  color: #fff;
  font-size: 24px;
  margin-right: 20px;
  flex-shrink: 0;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.4);
}

.menu-text {
  font-size: 16px;
  color: var(--mall-text-secondary);
  font-weight: 500;
  flex: 1;
}

/* VIP 卡片 */
.menu-item.vip-card {
  background: linear-gradient(90deg, rgba(255, 215, 0, 0.15), rgba(255, 170, 0, 0.1));
  border: 2px solid rgba(255, 215, 0, 0.4);
  height: 100px;
  padding: 16px 24px;
}

.menu-item.vip-card:hover {
  border-color: #ffd700;
  box-shadow: 0 4px 30px rgba(255, 215, 0, 0.5);
}

.menu-item.vip-card .menu-icon {
  background: linear-gradient(135deg, #ffd700, #ffaa00);
  box-shadow: 0 4px 20px rgba(255, 215, 0, 0.5);
}

.vip-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
  margin-left: 16px;
}

.vip-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.vip-title .menu-text {
  font-size: 18px;
  font-weight: bold;
  color: #ffd700;
}

.vip-info {
  display: flex;
  gap: 24px;
}

.vip-info .info-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
}

.vip-info .info-label {
  font-size: 12px;
  color: #88aacc;
}

.vip-info .info-value {
  font-size: 16px;
  font-weight: bold;
  color: #fff;
}
</style>

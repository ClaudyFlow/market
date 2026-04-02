<template>
  <div class="merchant-dashboard">
    <!-- 顶部导航 -->
    <header class="merchant-header">
      <div class="logo">
        <el-icon><Shop /></el-icon>
        <span>商家后台管理系统</span>
      </div>
      <div class="header-actions">
        <el-badge :value="unreadNotifications" :hidden="unreadNotifications === 0">
          <el-button circle @click="goToNotifications">
            <el-icon><Bell /></el-icon>
          </el-button>
        </el-badge>
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-avatar :size="32" :src="merchantInfo.avatar" />
            <span class="username">{{ merchantInfo.name }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="shop">店铺管理</el-dropdown-item>
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <div class="main-container">
      <!-- 侧边栏导航 -->
      <aside class="sidebar">
        <el-menu :default-active="activeMenu" router background-color="#1a1f3a" text-color="#b0d4ff"
          active-text-color="#00d4ff">
          <el-menu-item index="/merchant/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据概览</span>
          </el-menu-item>
          <el-sub-menu index="product">
            <template #title>
              <el-icon><Goods /></el-icon>
              <span>商品管理</span>
            </template>
            <el-menu-item index="/merchant/product/list">商品列表</el-menu-item>
            <el-menu-item index="/merchant/product/add">添加商品</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="order">
            <template #title>
              <el-icon><ShoppingCart /></el-icon>
              <span>订单管理</span>
            </template>
            <el-menu-item index="/merchant/order/list">订单列表</el-menu-item>
            <el-menu-item index="/merchant/order/refund">退款处理</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="coupon">
            <template #title>
              <el-icon><Ticket /></el-icon>
              <span>优惠券管理</span>
            </template>
            <el-menu-item index="/merchant/coupon/list">优惠券列表</el-menu-item>
            <el-menu-item index="/merchant/coupon/add">添加优惠券</el-menu-item>
          </el-sub-menu>
          <el-menu-item index="/merchant/review/list">
            <el-icon><Comment /></el-icon>
            <span>评价管理</span>
          </el-menu-item>
          <el-menu-item index="/merchant/shop/info">
            <el-icon><Shop /></el-icon>
            <span>店铺管理</span>
          </el-menu-item>
          <el-menu-item index="/merchant/stats">
            <el-icon><TrendCharts /></el-icon>
            <span>数据统计</span>
          </el-menu-item>
          <el-menu-item index="/merchant/customer/chat">
            <el-icon><ChatDotRound /></el-icon>
            <span>客服聊天</span>
          </el-menu-item>
        </el-menu>
      </aside>

      <!-- 主内容区 -->
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Shop, Bell, DataAnalysis, Goods, ShoppingCart, Ticket, Comment, TrendCharts, ChatDotRound
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const activeMenu = ref(route.path)
const unreadNotifications = ref(0)

const merchantInfo = ref({
  name: '商家用户',
  avatar: '/images/avatar-default.png',
  shopName: '我的店铺'
})

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('merchantInfo')
    router.push('/merchant/login')
    ElMessage.success('已退出登录')
  } else if (command === 'shop') {
    router.push('/merchant/shop/info')
  } else if (command === 'profile') {
    router.push('/merchant/profile')
  }
}

// 跳转到通知
const goToNotifications = () => {
  router.push('/merchant/notifications')
}

// 获取未读消息数
const fetchUnreadCount = async () => {
  // TODO: 调用 API 获取未读消息数
  unreadNotifications.value = 0
}

onMounted(() => {
  fetchUnreadCount()
})
</script>

<style scoped>
.merchant-dashboard {
  min-height: 100vh;
  background: #0a0f1a;
}

.merchant-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  background: #1a1f3a;
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: bold;
  color: #00d4ff;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.username {
  color: #fff;
  font-size: 14px;
}

.main-container {
  display: flex;
  height: calc(100vh - 60px);
}

.sidebar {
  width: 220px;
  background: #1a1f3a;
  border-right: 1px solid rgba(0, 212, 255, 0.1);
  overflow-y: auto;
}

.sidebar :deep(.el-menu) {
  border-right: none;
}

.sidebar :deep(.el-sub-menu__title:hover),
.sidebar :deep(.el-menu-item:hover) {
  background: rgba(0, 212, 255, 0.1) !important;
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #0a0f1a;
}
</style>

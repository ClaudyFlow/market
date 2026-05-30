<template>
  <div class="admin-dashboard">
    <!-- 顶部导航 -->
    <header class="admin-header">
      <div class="logo">
        <el-icon><Monitor /></el-icon>
        <span>平台管理后台</span>
      </div>
      <div class="header-actions">
        <el-badge :value="unreadNotifications" :hidden="unreadNotifications === 0">
          <el-button circle @click="goToNotifications">
            <el-icon><Bell /></el-icon>
          </el-button>
        </el-badge>
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-avatar :size="32" :src="adminInfo.avatar" />
            <span class="username">{{ adminInfo.name }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
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
          <el-menu-item index="/">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据概览</span>
          </el-menu-item>
          <el-sub-menu index="user">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/user">用户列表</el-menu-item>
            <el-menu-item index="/user/credit">用户积分</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="merchant">
            <template #title>
              <el-icon><Shop /></el-icon>
              <span>商家管理</span>
            </template>
            <el-menu-item index="/merchant">商家列表</el-menu-item>
            <el-menu-item index="/merchant/audit">入驻审核</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="product">
            <template #title>
              <el-icon><Goods /></el-icon>
              <span>商品管理</span>
            </template>
            <el-menu-item index="/product">商品列表</el-menu-item>
            <el-menu-item index="/product/audit">商品审核</el-menu-item>
            <el-menu-item index="/product/category">分类管理</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="order">
            <template #title>
              <el-icon><ShoppingCart /></el-icon>
              <span>订单管理</span>
            </template>
            <el-menu-item index="/order">订单列表</el-menu-item>
            <el-menu-item index="/order/refund">退款管理</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="coupon">
            <template #title>
              <el-icon><Ticket /></el-icon>
              <span>优惠券管理</span>
            </template>
            <el-menu-item index="/coupon">优惠券列表</el-menu-item>
            <el-menu-item index="/coupon/template">模板管理</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="content">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>内容管理</span>
            </template>
            <el-menu-item index="/content/notice">公告管理</el-menu-item>
            <el-menu-item index="/content/banner">轮播图管理</el-menu-item>
            <el-menu-item index="/content/review">评价审核</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="marketing">
            <template #title>
              <el-icon><Promotion /></el-icon>
              <span>营销管理</span>
            </template>
            <el-menu-item index="/marketing/activity">活动管理</el-menu-item>
            <el-menu-item index="/marketing/lottery">抽奖管理</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统设置</span>
            </template>
            <el-menu-item index="/system/settings">系统配置</el-menu-item>
            <el-menu-item index="/system/log">操作日志</el-menu-item>
            <el-menu-item index="/system/permission">权限管理</el-menu-item>
          </el-sub-menu>
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
  Monitor, Bell, DataAnalysis, User, Shop, Goods, ShoppingCart, Ticket, Document, Promotion, Setting
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const activeMenu = ref(route.path)
const unreadNotifications = ref(0)

const adminInfo = ref({
  name: '管理员',
  avatar: '/images/avatar-default.png'
})

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('adminInfo')
    router.push('/login')
    ElMessage.success('已退出登录')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}

// 跳转到通知
const goToNotifications = () => {
  router.push('/notifications')
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
@import '@admin/assets/mall-style.css';

.admin-dashboard {
  min-height: 100vh;
  background: #0a0f1a;
}

.admin-header {
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

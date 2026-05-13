<template>
  <header class="header">
    <!-- 顶部信息栏 -->
    <div class="top-info">
      <div class="container">
        <TopInfoBar user-text="管理员" :show-online="false" />
      </div>
    </div>

    <!-- 主导航 -->
    <div class="main-nav">
      <div class="container">
        <router-link to="/" class="logo">
          <div class="logo-icon">
            <el-icon><Monitor /></el-icon>
          </div>
          <div class="logo-text">
            <span class="logo-title">平台管理</span>
            <span class="logo-subtitle">
              <span>智慧运营</span>
              <span>高效治理</span>
            </span>
          </div>
        </router-link>

        <nav class="nav-links">
          <router-link to="/" class="nav-item" active-class="active">
            <el-icon><DataBoard /></el-icon>
            <span>平台概览</span>
          </router-link>
          <router-link to="/user" class="nav-item" active-class="active">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </router-link>
          <router-link to="/merchant" class="nav-item" active-class="active">
            <el-icon><Shop /></el-icon>
            <span>商家管理</span>
          </router-link>
          <router-link to="/product" class="nav-item" active-class="active">
            <el-icon><Goods /></el-icon>
            <span>商品审核</span>
          </router-link>
          <router-link to="/order" class="nav-item" active-class="active">
            <el-icon><List /></el-icon>
            <span>订单监控</span>
          </router-link>
          <router-link to="/statistic" class="nav-item" active-class="active">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据统计</span>
          </router-link>
        </nav>

        <div class="user-actions">
          <el-dropdown>
            <span class="el-dropdown-link">
              <el-avatar :size="36" :src="管理员信息?.头像 || `https://via.placeholder.com/36x36/00d4ff/fff?text=${管理员名称?.[0] || '管'}`">
                <el-icon v-if="!管理员信息"><User /></el-icon>
              </el-avatar>
              <span class="username">{{ 管理员名称 || '管理员' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="跳转页面 ('/setting')">个人设置</el-dropdown-item>
                <el-dropdown-item divided @click="处理退出">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Monitor, DataBoard, User, Shop, Goods, List, DataAnalysis } from '@element-plus/icons-vue'
import TopInfoBar from '@common/components/TopInfoBar.vue'
import { ElMessage } from 'element-plus'

interface 管理员信息类型 {
  姓名:string
  角色:string
  头像:string
}

const router = useRouter()

const 管理员信息 = ref<管理员信息类型 | null>(null)

// 获取管理员名称
const 管理员名称 = computed(() => {
  return 管理员信息.value?.姓名 || null
})

// 加载管理员信息
const 加载管理员信息 = () => {
  const 存储信息 = localStorage.getItem('adminInfo')
  if (存储信息) {
    管理员信息.value = JSON.parse(存储信息)
  } else {
    // 模拟数据
    管理员信息.value = {
      姓名:'系统管理员',
      角色:'超级管理员',
      头像:''
    }
  }
}

// 跳转页面
const 跳转页面 = (路径:string) => {
  router.push(路径)
}

// 处理退出
const 处理退出 = () => {
  localStorage.removeItem('adminToken')
  localStorage.removeItem('adminInfo')
  ElMessage.success('已退出登录')
  router.push('/login')
}

// 组件挂载时加载信息
onMounted(() => {
  加载管理员信息 ()
})
</script>

<style scoped>
.header {
  background: linear-gradient(180deg, #1a2a4a 0%, #0d1a2a 100%);
  border-bottom: 1px solid rgba(0, 212, 255, 0.3);
  position: sticky;
  top: 0;
  z-index: 1000;
}

/* 顶部信息栏 */
.top-info {
  background: rgba(10, 14, 26, 1);
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

/* 主导航 */
.main-nav {
  padding: 15px 0;
}

.main-nav > .container {
  display: flex;
  align-items: center;
  gap: 30px;
  max-width: 1400px;
  
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
  color: #fff;
}

.logo-icon {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, var(--mall-primary), var(--mall-secondary));
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.5);
}

.logo-text {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.logo-title {
  font-size: 22px;
  font-weight: bold;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: 2px;
  text-align: left;
}

.logo-subtitle {
  display: flex;
  flex-direction: column;
  font-size: 11px;
  color: #888;
  letter-spacing: 1px;
  line-height: 1.4;
}

.nav-links {
  display: flex;
  gap: 8px;
  flex: 1;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 14px;
  color: #fff;
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
  white-space: nowrap;
}

.nav-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.2), rgba(0, 255, 136, 0.2));
  opacity: 0;
  transition: opacity 0.3s;
}

.nav-item:hover {
  color: var(--mall-primary);
}

.nav-item:hover::before {
  opacity: 1;
}

.nav-item.active {
  color: var(--mall-primary);
  background: rgba(0, 212, 255, 0.15);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.3);
}

.nav-item .el-icon {
  font-size: 18px;
}

.user-actions {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  color: #fff;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s;
}

.el-dropdown-link:hover {
  background: rgba(0, 212, 255, 0.1);
}

.username {
  color: #fff;
  font-size: 14px;
}
</style>

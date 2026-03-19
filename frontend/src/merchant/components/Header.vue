<template>
  <header class="header">
    <!-- 顶部信息栏 -->
    <div class="top-info">
      <div class="container">
        <TopInfoBar user-text="商家账号" :show-online="false" />
      </div>
    </div>

    <!-- 主导航 -->
    <div class="main-nav">
      <div class="container">
        <router-link to="/merchant/dashboard" class="logo">
          <div class="logo-icon">
            <el-icon><Shop /></el-icon>
          </div>
          <div class="logo-text">
            <span class="logo-title">商家管理</span>
            <span class="logo-subtitle">
              <span>智慧经营</span>
              <span>数据驱动</span>
            </span>
          </div>
        </router-link>

        <nav class="nav-links">
          <router-link to="/merchant/dashboard" class="nav-item" active-class="active">
            <el-icon><DataAnalysis /></el-icon>
            <span>首页看板</span>
          </router-link>
          <router-link to="/merchant/product" class="nav-item" active-class="active">
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </router-link>
          <router-link to="/merchant/order" class="nav-item" active-class="active">
            <el-icon><ShoppingCart /></el-icon>
            <span>订单管理</span>
          </router-link>
          <router-link to="/merchant/shop" class="nav-item" active-class="active">
            <el-icon><Shop /></el-icon>
            <span>店铺管理</span>
          </router-link>
          <router-link to="/merchant/statistic" class="nav-item" active-class="active">
            <el-icon><TrendCharts /></el-icon>
            <span>数据统计</span>
          </router-link>
          <router-link to="/merchant/chat" class="nav-item" active-class="active">
            <el-icon><ChatDotRound /></el-icon>
            <span>消息中心</span>
          </router-link>
        </nav>

        <div class="user-actions">
          <el-dropdown>
            <span class="el-dropdown-link">
              <el-avatar :size="36" :src="商家信息?.头像 || `https://via.placeholder.com/36x36/00d4ff/fff?text=${商家名称?.[0] || '商'}`">
                <el-icon v-if="!商家信息"><Shop /></el-icon>
              </el-avatar>
              <span class="username">{{ 商家名称 || '商家账号' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="跳转页面 ('/merchant/shop')">店铺设置</el-dropdown-item>
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
import { Shop, DataAnalysis, Goods, ShoppingCart, TrendCharts, ChatDotRound } from '@element-plus/icons-vue'
import TopInfoBar from '@common/components/TopInfoBar.vue'
import { ElMessage } from 'element-plus'

interface 商家信息类型 {
  店铺名称:string
  店铺等级:string
  营业状态:string
  头像:string
}

const router = useRouter()

const 商家信息 = ref<商家信息类型 | null>(null)

// 获取商家名称
const 商家名称 = computed(() => {
  return 商家信息.value?.店铺名称 || null
})

// 加载商家信息
const 加载商家信息 = () => {
  const 存储信息 = localStorage.getItem('merchantInfo')
  if (存储信息) {
    商家信息.value = JSON.parse(存储信息)
  } else {
    // 模拟数据
    商家信息.value = {
      店铺名称:'品质优选店',
      店铺等级:'金牌商家',
      营业状态:'营业中',
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
  localStorage.removeItem('merchantToken')
  localStorage.removeItem('merchantInfo')
  ElMessage.success('已退出登录')
  router.push('/merchant/login')
}

// 组件挂载时加载信息
onMounted(() => {
  加载商家信息 ()
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
  margin: 0 auto;
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
  margin: 0 auto;
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

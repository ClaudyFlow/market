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
        <div class="logo">
          <el-icon><Monitor /></el-icon>
          <span>平台管理</span>
        </div>
        <nav class="nav">
          <router-link to="/admin/dashboard" class="nav-item">
            <el-icon><DataAnalysis /></el-icon>
            <span>平台概览</span>
          </router-link>
          <router-link to="/admin/user" class="nav-item">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </router-link>
          <router-link to="/admin/product" class="nav-item">
            <el-icon><Goods /></el-icon>
            <span>商品审核</span>
          </router-link>
          <router-link to="/admin/order" class="nav-item">
            <el-icon><ShoppingCart /></el-icon>
            <span>订单监控</span>
          </router-link>
        </nav>
        <div class="user-actions">
          <el-dropdown>
            <span class="el-dropdown-link">
              <el-avatar :size="32" icon="User" />
              <span class="username">管理员</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人设置</el-dropdown-item>
                <el-dropdown-item divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import TopInfoBar from '@common/components/TopInfoBar.vue'
</script>

<style scoped>
.header {
  background: rgba(26, 42, 74, 0.95);
  border-bottom: 1px solid var(--mall-border);
  box-shadow: 0 2px 20px rgba(0, 212, 255, 0.1);
  backdrop-filter: blur(10px);
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

.server-info {
  display: flex;
  align-items: center;
  gap: 15px;
  color: var(--mall-primary);
  flex: 1;
}

.time-center {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  white-space: nowrap;
}

.time-standard {
  color: #00d4ff;
  font-family: 'Courier New', monospace;
  font-size: 13px;
}

.time-local {
  color: #00ff88;
  font-family: 'Courier New', monospace;
  font-size: 13px;
}

.location-info {
  display: flex;
  align-items: center;
  gap: 5px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
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
  gap: 10px;
  font-size: 18px;
  font-weight: bold;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav {
  display: flex;
  gap: 5px;
  flex: 1;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  color: var(--mall-text-secondary);
  text-decoration: none;
  border-radius: 6px;
  transition: all 0.3s;
}

.nav-item:hover {
  background: rgba(24, 144, 255, 0.1);
  color: var(--mall-primary);
}

.nav-item.router-link-active {
  background: rgba(24, 144, 255, 0.15);
  color: var(--mall-primary);
}

.user-actions {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: var(--mall-text-primary);
}

.username {
  color: var(--mall-text-primary);
}
</style>

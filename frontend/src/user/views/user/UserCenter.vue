<template>
  <div class="user-center-page">
    <div class="container">
      <h1 class="page-title">个人中心</h1>
      
      <div class="user-menu">
        <div class="menu-grid">
          <router-link to="/user/orders" class="menu-item">
            <div class="menu-icon">
              <el-icon><Document /></el-icon>
            </div>
            <span class="menu-text">我的订单</span>
          </router-link>
          
          <router-link to="/user/favorites" class="menu-item">
            <div class="menu-icon">
              <el-icon><StarFilled /></el-icon>
            </div>
            <span class="menu-text">我的收藏</span>
          </router-link>
          
          <router-link to="/user/follows" class="menu-item">
            <div class="menu-icon">
              <el-icon><UserFilled /></el-icon>
            </div>
            <span class="menu-text">我的关注</span>
          </router-link>
          
          <router-link to="/user/address" class="menu-item">
            <div class="menu-icon">
              <el-icon><Location /></el-icon>
            </div>
            <span class="menu-text">地址管理</span>
          </router-link>
          
          <router-link to="/user/credit" class="menu-item">
            <div class="menu-icon">
              <el-icon><Trophy /></el-icon>
            </div>
            <span class="menu-text">我的积分</span>
          </router-link>
          
          <router-link to="/user/coupons" class="menu-item">
            <div class="menu-icon">
              <el-icon><Ticket /></el-icon>
            </div>
            <span class="menu-text">优惠券</span>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Document, StarFilled, UserFilled, Location, Trophy, Ticket } from '@element-plus/icons-vue'
</script>

<style scoped>
.user-center-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  padding: 40px 20px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  color: #fff;
  font-size: 28px;
  margin-bottom: 30px;
  text-align: center;
}

.user-menu {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 40px;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 24px;
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  text-decoration: none;
}

.menu-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.menu-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 28px;
  margin-bottom: 16px;
}

.menu-text {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}
</style>

<template>
  <div class="user-center-page">
    <div class="container">
      <h1 class="page-title">个人中心</h1>

      <div class="user-menu">
        <div class="menu-grid">
          <router-link to="/user/orders" class="menu-item">
            <div class="menu-icon">
              <i class="fas fa-file-alt"></i>
            </div>
            <span class="menu-text">订单中心</span>
          </router-link>

          <router-link to="/user/order" class="menu-item">
            <div class="menu-icon">
              <i class="fas fa-list"></i>
            </div>
            <span class="menu-text">我的订单</span>
          </router-link>

          <router-link to="/user/favorite" class="menu-item">
            <div class="menu-icon">
              <i class="fas fa-star"></i>
            </div>
            <span class="menu-text">我的收藏</span>
          </router-link>

          <router-link to="/user/follow" class="menu-item">
            <div class="menu-icon">
              <i class="fas fa-user-friends"></i>
            </div>
            <span class="menu-text">我的关注</span>
          </router-link>

          <router-link to="/user/address" class="menu-item">
            <div class="menu-icon">
              <i class="fas fa-map-marker-alt"></i>
            </div>
            <span class="menu-text">地址管理</span>
          </router-link>

          <router-link to="/user/credit" class="menu-item">
            <div class="menu-icon">
              <i class="fas fa-trophy"></i>
            </div>
            <span class="menu-text">我的积分</span>
          </router-link>

          <router-link to="/user/coupon" class="menu-item">
            <div class="menu-icon">
              <i class="fas fa-ticket-alt"></i>
            </div>
            <span class="menu-text">优惠券</span>
          </router-link>

          <router-link to="/vip" class="menu-item vip-card">
            <div class="menu-icon">
              <i class="fas fa-crown"></i>
            </div>
            <div class="vip-content">
              <div class="vip-title">
                <span class="menu-text">VIP 会员中心</span>
                <el-tag type="warning" size="small" class="vip-tag">HOT</el-tag>
              </div>
              <div class="vip-info">
                <div class="info-item">
                  <span class="info-label">当前等级</span>
                  <span class="info-value">普通会员</span>
                </div>
                <div class="info-item">
                  <span class="info-label">我的积分</span>
                  <span class="info-value">0</span>
                </div>
                <div class="info-item">
                  <span class="info-label">成长值</span>
                  <span class="info-value">0</span>
                </div>
              </div>
            </div>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// Font Awesome 图标直接使用类名，无需导入
</script>

<style scoped>
@import '@user/assets/mall-style.css';

.user-center-page {
  min-height: 100vh;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.15) 0%, rgba(10, 14, 26, 0.8) 100%);
  padding: 40px 20px;
}

.container {
  max-width: 1200px;
  
}

.page-title {
  color: var(--mall-text-primary);
  font-size: 32px;
  
  text-align: center;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: bold;
}

.user-menu {
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
  text-decoration: none;
  height: 80px;
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
  
  flex-shrink: 0;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.4);
}

.menu-text {
  font-size: 16px;
  color: var(--mall-text-secondary);
  font-weight: 500;
  flex: 1;
}

/* VIP 卡片特殊样式 */
.menu-item.vip-card {
  background: linear-gradient(90deg, rgba(255, 215, 0, 0.15), rgba(255, 170, 0, 0.1));
  border: 2px solid rgba(255, 215, 0, 0.4);
  position: relative;
  height: 100px;
  padding: 16px 24px;
}

.menu-item.vip-card:hover {
  border-color: #ffd700;
  box-shadow: 0 4px 30px rgba(255, 215, 0, 0.5);
  background: linear-gradient(90deg, rgba(255, 215, 0, 0.25), rgba(255, 170, 0, 0.15));
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

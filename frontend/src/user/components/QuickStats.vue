<template>
  <div class="quick-stats">
    <div class="stats-header">
      <el-icon><DataAnalysis /></el-icon>
      <span>我的数据</span>
    </div>
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon cart">
          <el-icon><ShoppingCart /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">0</span>
          <span class="stat-label">购物车</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon favorite">
          <el-icon><Star /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">0</span>
          <span class="stat-label">收藏</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon order">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">0</span>
          <span class="stat-label">订单</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon coupon">
          <el-icon><Ticket /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">0</span>
          <span class="stat-label">优惠券</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ShoppingCart, Star, Document, Ticket, DataAnalysis } from '@element-plus/icons-vue'
</script>

<style scoped>
.quick-stats {
  height: 100%;
  background: rgba(26, 31, 58, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stats-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: bold;
  color: #00d4ff;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.15);
}

.stats-header .el-icon {
  font-size: 18px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  flex: 1;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 8px;
  transition: all 0.3s;
}

.stat-card:hover {
  border-color: var(--mall-primary);
  background: rgba(26, 31, 58, 0.8);
  transform: translateY(-2px);
}

.stat-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;
  flex-shrink: 0;
}

.stat-icon.cart {
  background: linear-gradient(135deg, #ff6600, #ff8800);
}

.stat-icon.favorite {
  background: linear-gradient(135deg, #ff3366, #ff5588);
}

.stat-icon.order {
  background: linear-gradient(135deg, #00d4ff, #00a8cc);
}

.stat-icon.coupon {
  background: linear-gradient(135deg, #00ff88, #00cc6a);
}

.stat-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
}

.stat-label {
  font-size: 12px;
  color: #888;
}
</style>

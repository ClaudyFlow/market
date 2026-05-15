<template>
  <section class="stats-cards">
    <el-row :gutter="15">
      <el-col :span="6">
        <div class="stat-card primary" @click="$emit('filter-status', '')">
          <div class="stat-icon"><el-icon><List /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.all }}</div>
            <div class="stat-label">全部订单</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card warning" @click="$emit('filter-status', 'pending')">
          <div class="stat-icon"><el-icon><Clock /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pending }}</div>
            <div class="stat-label">待付款</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card info" @click="$emit('filter-status', 'paid')">
          <div class="stat-icon"><el-icon><ShoppingCart /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.paid }}</div>
            <div class="stat-label">待发货</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card success" @click="$emit('filter-status', 'shipped')">
          <div class="stat-icon"><el-icon><Van /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.shipped }}</div>
            <div class="stat-label">已发货</div>
          </div>
        </div>
      </el-col>
    </el-row>
  </section>
</template>

<script setup lang="ts">
import { List, Clock, ShoppingCart, Van } from '@element-plus/icons-vue'
import type { OrderStats } from '../types/order'

defineProps<{
  stats: OrderStats
}>()

defineEmits<{
  'filter-status': [status: string]
}>()
</script>

<style scoped>
.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.8), rgba(26, 31, 58, 0.6));
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  transition: all 0.3s;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--glow-color), transparent);
}

.stat-card:hover {
  transform: translateY(-3px);
  border-color: var(--glow-color);
  box-shadow: 0 8px 30px rgba(0, 212, 255, 0.15);
}

.stat-card.primary { --glow-color: #00d4ff; }
.stat-card.warning { --glow-color: #ffaa00; }
.stat-card.info { --glow-color: #00d4ff; }
.stat-card.success { --glow-color: #00ff88; }

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  flex-shrink: 0;
}

.stat-card.primary .stat-icon {
  background: linear-gradient(135deg, #00d4ff, #00a8cc);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
}

.stat-card.warning .stat-icon {
  background: linear-gradient(135deg, #ffaa00, #ff8800);
  box-shadow: 0 0 15px rgba(255, 170, 0, 0.4);
}

.stat-card.info .stat-icon {
  background: linear-gradient(135deg, #00d4ff, #0088cc);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
}

.stat-card.success .stat-icon {
  background: linear-gradient(135deg, #00ff88, #00cc6a);
  box-shadow: 0 0 15px rgba(0, 255, 136, 0.4);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
}

.stat-label {
  font-size: 13px;
  color: #888;
}
</style>

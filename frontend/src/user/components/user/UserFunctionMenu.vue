<template>
  <div class="function-menu">
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in menuItems" :key="item.title">
        <div class="menu-card" @click="$emit('navigate', item.path)">
          <div class="menu-icon"><el-icon><component :is="item.icon" /></el-icon></div>
          <div class="menu-title">{{ item.title }}</div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ShoppingCart, Star, Ticket, Location } from '@element-plus/icons-vue'

defineEmits<{ navigate: [path: string] }>()

const menuItems = [
  { icon: ShoppingCart, title: '我的订单', path: '/order-center' },
  { icon: Star, title: '我的收藏', path: '/favorite' },
  { icon: Ticket, title: '我的优惠券', path: '/coupon' },
  { icon: Location, title: '地址管理', path: '/address' }
]
</script>

<style scoped>
.function-menu { margin-bottom: 20px; }

.menu-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.menu-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12); }

.menu-icon {
  width: 50px; height: 50px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #00d4ff 0%, #00ff88 100%);
  color: #fff; font-size: 24px;
}

.menu-title { font-size: 14px; color: #1a1a1a; font-weight: 500; }
</style>

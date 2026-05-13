<template>
  <nav class="quick-actions-bar" aria-label="快捷操作">
    <button class="action-item" @click="router.push('/item')" aria-label="浏览商品">
      <el-icon><Box /></el-icon>
      <span>商品</span>
    </button>
    <button class="action-item" @click="router.push('/order')" aria-label="查看订单">
      <el-icon><Document /></el-icon>
      <span>订单</span>
    </button>
    <button class="action-item" @click="router.push('/coupon')" aria-label="查看优惠券">
      <el-icon><Ticket /></el-icon>
      <span>优惠券</span>
    </button>
    <button class="action-item" @click="router.push('/address')" aria-label="管理地址">
      <el-icon><Location /></el-icon>
      <span>地址</span>
    </button>
    <button class="action-item vip-action" @click="router.push('/vip')" aria-label="VIP 中心">
      <el-icon><StarFilled /></el-icon>
      <span>VIP 中心</span>
    </button>
    <button class="action-item" @click="router.push('/user/favorite')" aria-label="我的收藏">
      <el-icon><Star /></el-icon>
      <span>收藏</span>
    </button>
    <button class="action-item" @click="router.push('/lottery')" aria-label="幸运抽奖">
      <el-icon><Coin /></el-icon>
      <span>抽奖</span>
    </button>
    <button class="action-item" @click="router.push('/user')" aria-label="个人中心">
      <el-icon><User /></el-icon>
      <span>我的</span>
    </button>
  </nav>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Box, Document, Ticket, Location, StarFilled, Star, Coin, User } from '@element-plus/icons-vue'

const router = useRouter()
</script>

<style scoped>
/* 快捷操作栏 */
.quick-actions-bar {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 15px;
  padding: 15px;
  
  background: rgba(26, 31, 58, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  box-sizing: border-box;
}

.action-item {
  position: relative;
  overflow: hidden;
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s;
  width: 100%;
  height: 100%;
}

.action-item:hover {
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.2), rgba(0, 255, 136, 0.2));
  box-shadow: 0 5px 15px rgba(0, 212, 255, 0.3);
}

.action-item:hover .el-icon {
  font-size: 42px;
}

.action-item:hover span {
  font-size: 21px;
}

.action-item .el-icon {
  font-size: 28px;
  color: var(--mall-primary);
}

.action-item span {
  font-size: 14px;
  color: #ccc;
}

/* VIP 中心按钮特殊样式 */
.action-item.vip-action {
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, rgba(255, 215, 0, 0.2), rgba(255, 170, 0, 0.15));
  border: 1px solid rgba(255, 215, 0, 0.5);
}

.action-item.vip-action .el-icon,
.action-item.vip-action span {
  color: #ffd700;
}

.action-item.vip-action:hover {
  background: linear-gradient(135deg, rgba(255, 215, 0, 0.35), rgba(255, 170, 0, 0.3));
  border-color: rgba(255, 215, 0, 0.8);
  box-shadow: 0 0 20px rgba(255, 215, 0, 0.5), 0 5px 15px rgba(255, 215, 0, 0.3);
}

.action-item.vip-action:hover .el-icon,
.action-item.vip-action:hover span {
  color: #ffd700;
}
</style>

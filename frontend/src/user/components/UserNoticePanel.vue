<template>
  <div class="user-notice-panel">
    <h4 class="panel-title">
      <el-icon><Bell /></el-icon>
      <span>我的通知</span>
    </h4>
    <div class="notice-list">
      <div class="notice-item" v-for="(notice, index) in notices" :key="index">
        <el-icon><Bell /></el-icon>
        <span>{{ notice }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Bell } from '@element-plus/icons-vue'

const notices = ref([
  '您的订单已发货，请注意查收',
  '您有一张优惠券即将过期',
  '会员积分即将到期，请及时使用',
  '您关注的商品降价了'
])
</script>

<style scoped>
.user-notice-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: 0;
  background: rgba(26, 31, 58, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 15px;
  overflow-y: auto;
}

.panel-title {
  font-size: 16px;
  font-weight: bold;
  color: #00d4ff;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  position: relative;
  padding-left: 12px;
}

.panel-title::before {
  content: '';
  position: absolute;
  left: 0;
  width: 4px;
  height: 16px;
  background: linear-gradient(180deg, #00d4ff, #00ff88);
  border-radius: 2px;
}

.panel-title .el-icon {
  color: #00d4ff;
  font-size: 18px;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  height: 100%;
  overflow-y: auto;
}

.notice-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #ccc;
  padding: 6px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-item .el-icon {
  color: #00d4ff;
  font-size: 16px;
}
</style>

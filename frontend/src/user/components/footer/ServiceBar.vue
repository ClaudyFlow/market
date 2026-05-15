<template>
  <div class="service-bar">
    <div 
      v-for="(item, index) in services" 
      :key="index" 
      class="service-item"
    >
      <el-icon :size="item.size || 28">
        <component :is="item.icon" />
      </el-icon>
      <span>{{ item.text }}</span>
    </div>
  </div>
</template>

<script setup>
import { CircleCheck, Van, RefreshLeft, Headset } from '@element-plus/icons-vue'

const services = [
  { icon: CircleCheck, text: '正品保障', size: 28 },
  { icon: Van, text: '极速配送', size: 28 },
  { icon: RefreshLeft, text: '7 天无理由退换', size: 28 },
  { icon: Headset, text: '24 小时客服', size: 28 }
]
</script>

<style scoped>
.service-bar {
  display: flex;
  justify-content: center;
  gap: 60px;
  padding-bottom: 30px;
  border-bottom: 1px solid var(--mall-border-light);
}

.service-item {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--mall-text-secondary);
  font-size: 14px;
}

.service-item .el-icon {
  color: var(--mall-primary);
}
</style>

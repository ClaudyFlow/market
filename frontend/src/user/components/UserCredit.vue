<template>
  <div class="user-credit">
    <el-icon><Trophy /></el-icon>
    <span>{{ credit }} 积分</span>
  </div>
</template>

<script setup lang="ts">
import { Trophy } from '@element-plus/icons-vue'

defineProps<{
  credit?: number | string
}>()
</script>

<style scoped>
.user-credit {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 5px;
  width: 100%;
  background: linear-gradient(90deg, rgba(0, 212, 255, 0.2), rgba(0, 255, 136, 0.15));
  padding: 6px 12px;
  border-radius: 9999px;
  border: 1px solid rgba(0, 212, 255, 0.3);
  white-space: nowrap;
}

.user-credit span {
  font-size: 14px;
  font-weight: bold;
  color: var(--mall-primary);
}

.user-credit .el-icon {
  font-size: 18px;
  color: var(--mall-primary);
}
</style>

<template>
  <figure class="user-avatar-figure">
    <el-avatar :size="size" :src="src">
      <el-icon :size="iconSize"><User /></el-icon>
    </el-avatar>
  </figure>
</template>

<script setup lang="ts">
import { User } from '@element-plus/icons-vue'

// 默认值
withDefaults(defineProps<{
  src?: string
  size?: number
  iconSize?: number
}>(), {
  src: 'https://via.placeholder.com/80x80/00d4ff/fff?text=VIP',
  size: 80,
  iconSize: 40
})
</script>

<style scoped>
.user-avatar-figure {
  display: flex;
  justify-content: center;
  align-items: center;
}

.user-avatar-figure :deep(.el-avatar) {
  border: 3px solid var(--mall-primary);
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.5);
  border-radius: 50%;
}
</style>

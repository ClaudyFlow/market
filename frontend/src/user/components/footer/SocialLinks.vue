<template>
  <div class="social-icons">
    <el-icon 
      v-for="(icon, index) in icons" 
      :key="index"
      :size="icon.size || 24"
      @click="icon.onClick"
    >
      <component :is="icon.component" />
    </el-icon>
  </div>
</template>

<script setup>
import { ChatDotRound, VideoCamera, Share } from '@element-plus/icons-vue'

const icons = [
  { component: ChatDotRound, size: 24 },
  { component: VideoCamera, size: 24 },
  { component: Share, size: 24 }
]
</script>

<style scoped>
.social-icons {
  display: flex;
  gap: 15px;
  color: var(--mall-primary);
}

.social-icons .el-icon {
  cursor: pointer;
  transition: all 0.3s;
}

.social-icons .el-icon:hover {
  color: var(--mall-secondary);
  transform: scale(1.2);
}
</style>

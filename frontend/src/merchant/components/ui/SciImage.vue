<template>
  <el-image
    class="sci-image"
    :src="src"
    :alt="alt"
    :fit="fit"
    :lazy="lazy"
    v-bind="$attrs"
  >
    <template #placeholder>
      <div class="sci-image__placeholder">
        <el-icon><Picture /></el-icon>
      </div>
    </template>
    <template #error>
      <div class="sci-image__error">
        <el-icon><PictureFilled /></el-icon>
      </div>
    </template>
  </el-image>
</template>

<script setup lang="ts">
import { Picture, PictureFilled } from '@element-plus/icons-vue'

interface Props {
  src: string
  alt?: string
  fit?: 'fill' | 'contain' | 'cover' | 'none' | 'scale-down'
  lazy?: boolean
}

withDefaults(defineProps<Props>(), {
  alt: '',
  fit: 'cover',
  lazy: false
})
</script>

<style scoped>
.sci-image {
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid rgba(0, 200, 255, 0.2);
  transition: all 0.3s;
}

.sci-image:hover {
  border-color: #00ffff;
  box-shadow: 0 0 15px rgba(0, 200, 255, 0.3);
}

.sci-image__placeholder,
.sci-image__error {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: rgba(10, 20, 50, 0.8);
  color: #00d4ff;
  font-size: 24px;
}
</style>

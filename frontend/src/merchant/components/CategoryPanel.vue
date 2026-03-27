<template>
  <aside class="category-panel" aria-label="商家分类">
    <header class="category-header">
      <el-icon><Menu /></el-icon>
      <span>商家管理</span>
    </header>
    <el-menu
      :default-active="当前路径"
      class="category-menu"
      @select="跳转分类"
    >
      <el-menu-item v-for="分类 in 分类列表" :key="分类.名称" :index="分类.路径">
        <el-icon><component :is="分类.图标" /></el-icon>
        <span>{{ 分类.名称 }}</span>
      </el-menu-item>
    </el-menu>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Menu } from '@element-plus/icons-vue'
import { 分类列表 } from '@merchant/data/categories'

const router = useRouter()
const route = useRoute()

const 当前路径 = computed(() => route.path)

const 跳转分类 = (路径:string) => {
  router.push(路径)
}
</script>

<style scoped>
/* 分类面板 */
aside.category-panel {
  background: rgba(26, 31, 58, 0.98);
  border: 2px solid rgba(0, 212, 255, 0.4);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 0 30px rgba(0, 212, 255, 0.4), inset 0 0 20px rgba(0, 212, 255, 0.1);
}

.category-header {
  background: linear-gradient(90deg, rgba(0, 212, 255, 0.3), transparent);
  padding: 15px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  color: var(--mall-primary);
}

.category-menu {
  background: transparent !important;
  border: none !important;
}

.category-menu :deep(.el-menu-item) {
  color: #ccc;
  font-size: 14px;
  line-height: 1.5;
  height: auto !important;
  min-height: 42px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05) !important;
}

.category-menu :deep(.el-menu-item:hover) {
  background: rgba(0, 212, 255, 0.1) !important;
  color: var(--mall-primary) !important;
}

.category-menu :deep(.el-menu-item.is-active) {
  background: rgba(0, 212, 255, 0.15) !important;
  color: var(--mall-primary) !important;
}

.category-menu :deep(.el-icon) {
  
  color: #ccc;
}

.category-menu :deep(.el-menu-item:hover .el-icon),
.category-menu :deep(.el-menu-item.is-active .el-icon) {
  color: var(--mall-primary);
}
</style>

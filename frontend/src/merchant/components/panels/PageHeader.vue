<template>
  <header class="page-header">
    <div class="header-left">
      <h1 class="page-title">
        <el-icon v-if="icon"><component :is="icon" /></el-icon>
        {{ title }}
      </h1>
      <p v-if="subtitle" class="page-subtitle">{{ subtitle }}</p>
    </div>
    <div class="header-right">
      <slot name="actions" />
    </div>
  </header>
</template>

<script setup lang="ts">
interface Props {
  title: string
  subtitle?: string
  icon?: any
}

defineProps<Props>()
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.9), rgba(26, 31, 58, 0.7));
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.1);
  margin-bottom: 20px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 22px;
  font-weight: bold;
  color: #fff;
  margin: 0;
}

.page-title .el-icon {
  color: #00d4ff;
  font-size: 26px;
  filter: drop-shadow(0 0 8px rgba(0, 212, 255, 0.5));
}

.page-subtitle {
  margin: 4px 0 0;
  font-size: 14px;
  color: #88aacc;
}

.header-right {
  display: flex;
  gap: 12px;
}
</style>

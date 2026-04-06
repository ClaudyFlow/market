<template>
  <el-table
    class="sci-table"
    :data="data"
    :v-loading="loading"
    :stripe="stripe"
    :border="border"
    v-bind="$attrs"
  >
    <slot />
  </el-table>
</template>

<script setup lang="ts">
interface Props {
  data: any[]
  loading?: boolean
  stripe?: boolean
  border?: boolean
}

withDefaults(defineProps<Props>(), {
  loading: false,
  stripe: false,
  border: false
})
</script>

<style scoped>
.sci-table {
  border-radius: 4px;
  overflow: hidden;
  background: linear-gradient(180deg,
    rgba(10, 20, 50, 0.8) 0%,
    rgba(5, 15, 40, 0.9) 100%);
  position: relative;
  border: 1px solid rgba(0, 180, 255, 0.2);
  box-shadow:
    inset 0 0 60px rgba(0, 150, 255, 0.08),
    0 0 30px rgba(0, 200, 255, 0.1);
}

.sci-table :deep(.el-table__header th) {
  background: linear-gradient(180deg,
    rgba(0, 100, 180, 0.4) 0%,
    rgba(0, 50, 100, 0.6) 100%);
  color: #00ffff;
  font-size: 13px;
  border-bottom: 2px solid rgba(0, 200, 255, 0.6);
  font-weight: 700;
  padding: 16px 0;
  letter-spacing: 1px;
  text-transform: uppercase;
  position: relative;
  z-index: 2;
  text-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
}

.sci-table :deep(.el-table__body td) {
  color: #88aacc !important;
  border-bottom: 1px solid rgba(0, 150, 200, 0.15);
  font-size: 13px;
  padding: 14px 0;
  transition: all 0.3s ease;
  position: relative;
  z-index: 2;
}

.sci-table :deep(.el-table__row) {
  background: transparent !important;
  transition: all 0.3s ease;
  border-radius: 4px;
}

.sci-table :deep(.el-table__row:hover) {
  background: linear-gradient(90deg,
    rgba(0, 180, 220, 0.3) 0%,
    rgba(0, 220, 255, 0.25) 50%,
    rgba(0, 180, 220, 0.3) 100%) !important;
  box-shadow:
    0 0 10px rgba(0, 200, 255, 0.4),
    0 0 20px rgba(0, 200, 255, 0.2);
}

.sci-table :deep(.el-table__row:hover td) {
  color: #ffffff !important;
}

.sci-table :deep(.el-button--primary) {
  background: linear-gradient(180deg, rgba(0, 150, 200, 0.3) 0%, rgba(0, 100, 150, 0.4) 100%);
  border: 1px solid rgba(0, 200, 255, 0.6);
  color: #00ffff;
}

.sci-table :deep(.el-button--success) {
  background: linear-gradient(180deg, rgba(0, 200, 100, 0.3) 0%, rgba(0, 150, 80, 0.4) 100%);
  border: 1px solid rgba(0, 255, 136, 0.6);
  color: #00ff88;
}

.sci-table :deep(.el-button--warning) {
  background: linear-gradient(180deg, rgba(255, 150, 0, 0.3) 0%, rgba(200, 100, 0, 0.4) 100%);
  border: 1px solid rgba(255, 170, 0, 0.6);
  color: #ffaa00;
}
</style>

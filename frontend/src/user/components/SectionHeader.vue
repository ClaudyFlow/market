<template>
  <header class="section-header">
    <div class="section-title">
      <el-icon v-if="icon">
        <component :is="icon" />
      </el-icon>
      <h2><slot /></h2>
    </div>
    <slot name="extra" />
  </header>
</template>

<script setup lang="ts">
defineProps<{
  icon?: any
}>()
</script>

<style scoped>
.section-header {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  background: linear-gradient(90deg, rgba(0, 212, 255, 0.1), rgba(0, 255, 136, 0.05));
  padding: 15px 30px;
  border-radius: 8px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-title h2 {
  font-size: 24px;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.section-title .el-icon {
  font-size: 28px;
  color: var(--mall-accent);
}
</style>

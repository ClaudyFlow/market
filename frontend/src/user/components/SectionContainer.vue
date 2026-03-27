<template>
  <div class="section-container" :class="customClass">
    <slot />
  </div>
</template>

<script setup lang="ts">
defineProps<{
  customClass?: string
}>()
</script>

<style scoped>
.section-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1%;
  padding: 3% 2%;
}

/* 首页主布局 - 3 列 2:6:2 */
.mall-home-container {
  grid-template-columns: 2fr 6fr 2fr !important;
  grid-template-rows: repeat(6, 1fr);
}
</style>

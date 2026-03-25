<template>
  <div class="user-level">
    <span class="level-badge">LV {{ level }}</span>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  level?: number | string
}>()
</script>

<style scoped>
.user-level {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.level-badge {
  background: linear-gradient(90deg, #ff6600, #ff8800);
  padding: 8px 20px;
  border-radius: 9999px;
  font-size: 14px;
  font-weight: bold;
  color: #fff;
  box-shadow: 0 0 10px rgba(255, 102, 0, 0.5);
  display: inline-block;
  text-align: center;
  white-space: nowrap;
  width: 100%;
}
</style>

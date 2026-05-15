<template>
  <span class="loading-dot" :class="{ loading }" aria-label="加载中">
    <span class="dot"></span>
  </span>
</template>

<script setup lang="ts">
defineProps({
  loading: {
    type: Boolean,
    default: true
  }
})
</script>

<style scoped>
.loading-dot {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  
}

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: currentColor;
  opacity: 0.4;
}

.loading-dot.loading .dot {
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.4;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.2);
  }
}
</style>

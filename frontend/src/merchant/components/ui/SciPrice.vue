<template>
  <span class="sci-price" :class="[`sci-price--${type}`]">
    ¥{{ amount.toFixed(2) }}
  </span>
</template>

<script setup lang="ts">
interface Props {
  amount: number
  type?: 'primary' | 'success' | 'warning' | 'default'
}

withDefaults(defineProps<Props>(), {
  type: 'primary'
})
</script>

<style scoped>
.sci-price {
  font-weight: bold;
  font-size: 16px;
}

.sci-price--primary {
  color: #00ffff;
  text-shadow: 0 0 10px rgba(0, 255, 255, 0.8);
}

.sci-price--success {
  color: #00ff88;
  text-shadow: 0 0 10px rgba(0, 255, 136, 0.8);
}

.sci-price--warning {
  color: #ffaa00;
  text-shadow: 0 0 10px rgba(255, 170, 0, 0.8);
}

.sci-price--default {
  color: #88aacc;
}
</style>

<template>
  <div class="sci-card" :class="[`sci-card--${variant}`]" v-bind="$attrs">
    <div v-if="$slots.header" class="sci-card__header">
      <slot name="header" />
    </div>
    <div class="sci-card__body">
      <slot />
    </div>
    <div v-if="$slots.footer" class="sci-card__footer">
      <slot name="footer" />
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  variant?: 'default' | 'gradient' | 'glass'
}

withDefaults(defineProps<Props>(), {
  variant: 'default'
})
</script>

<style scoped>
.sci-card {
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s;
}

.sci-card--default {
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.8), rgba(26, 31, 58, 0.6));
  border: 1px solid rgba(0, 212, 255, 0.15);
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.08);
}

.sci-card--gradient {
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.1), rgba(0, 255, 136, 0.05));
  border: 1px solid rgba(0, 212, 255, 0.2);
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.1);
}

.sci-card--glass {
  background: rgba(26, 31, 58, 0.6);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(0, 212, 255, 0.15);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.sci-card__header {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.1);
}

.sci-card__body {
  margin-bottom: 16px;
}

.sci-card__footer {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid rgba(0, 212, 255, 0.1);
}
</style>

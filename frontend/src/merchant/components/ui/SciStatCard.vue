<template>
  <div
    class="sci-stat-card"
    :class="[`sci-stat-card--${type}`]"
    @click="$emit('click')"
  >
    <div class="sci-stat-card__icon">
      <slot name="icon" />
    </div>
    <div class="sci-stat-card__content">
      <div class="sci-stat-card__value">{{ value }}</div>
      <div class="sci-stat-card__label">{{ label }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  value: string | number
  label: string
  type?: 'primary' | 'success' | 'warning' | 'info'
}

withDefaults(defineProps<Props>(), {
  type: 'primary'
})

defineEmits<{
  'click': []
}>()
</script>

<style scoped>
.sci-stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.8), rgba(26, 31, 58, 0.6));
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  transition: all 0.3s;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.sci-stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--glow-color), transparent);
}

.sci-stat-card:hover {
  transform: translateY(-3px);
  border-color: var(--glow-color);
  box-shadow: 0 8px 30px rgba(0, 212, 255, 0.15);
}

.sci-stat-card--primary { --glow-color: #00d4ff; }
.sci-stat-card--success { --glow-color: #00ff88; }
.sci-stat-card--warning { --glow-color: #ffaa00; }
.sci-stat-card--info { --glow-color: #00ccff; }

.sci-stat-card__icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  flex-shrink: 0;
}

.sci-stat-card--primary .sci-stat-card__icon {
  background: linear-gradient(135deg, #00d4ff, #00a8cc);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
}

.sci-stat-card--success .sci-stat-card__icon {
  background: linear-gradient(135deg, #00ff88, #00cc6a);
  box-shadow: 0 0 15px rgba(0, 255, 136, 0.4);
}

.sci-stat-card--warning .sci-stat-card__icon {
  background: linear-gradient(135deg, #ffaa00, #ff8800);
  box-shadow: 0 0 15px rgba(255, 170, 0, 0.4);
}

.sci-stat-card--info .sci-stat-card__icon {
  background: linear-gradient(135deg, #00ccff, #0088cc);
  box-shadow: 0 0 15px rgba(0, 204, 255, 0.4);
}

.sci-stat-card__content {
  flex: 1;
}

.sci-stat-card__value {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
}

.sci-stat-card__label {
  font-size: 13px;
  color: #888;
}
</style>

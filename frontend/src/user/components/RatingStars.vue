<template>
  <div class="rating-stars">
    <span
      v-for="star in 5"
      :key="star"
      class="star"
      :class="{ active: star <= modelValue }"
      @click="handleClick(star)"
      @mouseenter="handleHover(star)"
      @mouseleave="handleHoverLeave"
    >
      ★
    </span>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

interface Props {
  modelValue: number
  readonly?: boolean
  size?: 'small' | 'medium' | 'large'
}

const props = withDefaults(defineProps<Props>(), {
  readonly: false,
  size: 'medium'
})

const emit = defineEmits<{
  'update:modelValue': [value: number]
  change: [value: number]
}>()

const hoverValue = ref<number | null>(null)

const handleClick = (star: number) => {
  if (!props.readonly) {
    emit('update:modelValue', star)
    emit('change', star)
  }
}

const handleHover = (star: number) => {
  if (!props.readonly) {
    hoverValue.value = star
  }
}

const handleHoverLeave = () => {
  hoverValue.value = null
}

const displayValue = () => {
  return hoverValue.value !== null && !props.readonly ? hoverValue.value : props.modelValue
}
</script>

<style scoped>
.rating-stars {
  display: inline-flex;
  gap: 4px;
}

.star {
  cursor: pointer;
  color: #666;
  font-size: var(--star-size);
  transition: color 0.2s, transform 0.1s;
}

.star:hover {
  transform: scale(1.1);
}

.star.active {
  color: var(--mall-accent);
}

.star:not(.active):hover {
  color: rgba(255, 193, 7, 0.5);
}

:root {
  --star-size: 20px;
}

.rating-stars:has(.star[style*='font-size: 14px']) {
  --star-size: 14px;
}

.rating-stars:has(.star[style*='font-size: 24px']) {
  --star-size: 24px;
}

.rating-stars:has(.star[style*='font-size: 32px']) {
  --star-size: 32px;
}
</style>

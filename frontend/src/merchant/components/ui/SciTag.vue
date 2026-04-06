<template>
  <el-tag
    class="sci-tag"
    :class="[`sci-tag--${type}`]"
    :type="type"
    :size="size"
    :closable="closable"
    :disable-transitions="false"
    @close="$emit('close', $event)"
  >
    <slot />
  </el-tag>
</template>

<script setup lang="ts">
interface Props {
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info'
  size?: 'large' | 'default' | 'small'
  closable?: boolean
}

withDefaults(defineProps<Props>(), {
  type: 'info',
  size: 'default',
  closable: false
})

defineEmits<{
  'close': [event: Event]
}>()
</script>

<style scoped>
.sci-tag {
  border-radius: 4px;
  padding: 4px 12px;
  font-weight: 600;
  transition: all 0.3s ease;
  border: 1px solid;
  text-shadow: 0 0 5px currentColor;
}

.sci-tag--primary {
  background: linear-gradient(180deg, rgba(255, 200, 0, 0.2) 0%, rgba(255, 180, 0, 0.3) 100%);
  color: #ffcc00;
  border-color: rgba(255, 200, 0, 0.6);
}

.sci-tag--success {
  background: linear-gradient(180deg, rgba(0, 200, 100, 0.2) 0%, rgba(0, 150, 80, 0.3) 100%);
  color: #00ff88;
  border-color: rgba(0, 255, 136, 0.6);
}

.sci-tag--warning {
  background: linear-gradient(180deg, rgba(255, 170, 0, 0.2) 0%, rgba(255, 150, 0, 0.3) 100%);
  color: #ffaa00;
  border-color: rgba(255, 170, 0, 0.6);
}

.sci-tag--danger {
  background: linear-gradient(180deg, rgba(255, 80, 80, 0.2) 0%, rgba(200, 50, 50, 0.3) 100%);
  color: #ff6666;
  border-color: rgba(255, 100, 100, 0.6);
}

.sci-tag--info {
  background: linear-gradient(180deg, rgba(0, 150, 200, 0.2) 0%, rgba(0, 100, 150, 0.3) 100%);
  color: #00ccff;
  border-color: rgba(0, 200, 255, 0.6);
}
</style>

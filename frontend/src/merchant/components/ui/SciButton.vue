<template>
  <el-button
    class="sci-button"
    :class="[`sci-button--${type}`, { 'sci-button--block': block, 'sci-button--glow': glow }]"
    :type="elType"
    :size="size"
    :disabled="disabled"
    :loading="loading"
    v-bind="$attrs"
  >
    <slot />
  </el-button>
</template>

<script setup lang="ts">
interface Props {
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default'
  size?: 'large' | 'default' | 'small'
  disabled?: boolean
  loading?: boolean
  block?: boolean
  glow?: boolean
}

withDefaults(defineProps<Props>(), {
  type: 'default',
  size: 'default',
  disabled: false,
  loading: false,
  block: false,
  glow: false
})

const elType = {
  primary: 'primary',
  success: 'success',
  warning: 'warning',
  danger: 'danger',
  info: 'info',
  default: 'info'
} as const
</script>

<style scoped>
.sci-button {
  border-radius: 4px;
  font-weight: 600;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.sci-button--primary {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  color: #000;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
}

.sci-button--primary:hover {
  box-shadow: 0 0 25px rgba(0, 212, 255, 0.6);
  transform: translateY(-2px);
}

.sci-button--success {
  background: linear-gradient(135deg, #00ff88, #00cc6a);
  border: none;
  color: #000;
  box-shadow: 0 0 15px rgba(0, 255, 136, 0.4);
}

.sci-button--success:hover {
  box-shadow: 0 0 25px rgba(0, 255, 136, 0.6);
  transform: translateY(-2px);
}

.sci-button--warning {
  background: linear-gradient(135deg, #ffaa00, #ff8800);
  border: none;
  color: #000;
  box-shadow: 0 0 15px rgba(255, 170, 0, 0.4);
}

.sci-button--warning:hover {
  box-shadow: 0 0 25px rgba(255, 170, 0, 0.6);
  transform: translateY(-2px);
}

.sci-button--danger {
  background: linear-gradient(135deg, #ff6666, #ff4444);
  border: none;
  color: #fff;
  box-shadow: 0 0 15px rgba(255, 102, 102, 0.4);
}

.sci-button--danger:hover {
  box-shadow: 0 0 25px rgba(255, 102, 102, 0.6);
  transform: translateY(-2px);
}

.sci-button--block {
  width: 100%;
}

.sci-button--glow {
  animation: sci-glow 2s ease-in-out infinite;
}

@keyframes sci-glow {
  0%, 100% { box-shadow: 0 0 15px currentColor; }
  50% { box-shadow: 0 0 30px currentColor, 0 0 50px currentColor; }
}
</style>

<template>
  <el-input
    class="sci-input"
    :model-value="modelValue"
    :placeholder="placeholder"
    :disabled="disabled"
    :clearable="clearable"
    :type="type"
    @update:model-value="$emit('update:modelValue', $event)"
    @input="$emit('input', $event)"
    @change="$emit('change', $event)"
  >
    <template v-if="$slots.prepend" #prepend>
      <slot name="prepend" />
    </template>
    <template v-if="$slots.append" #append>
      <slot name="append" />
    </template>
  </el-input>
</template>

<script setup lang="ts">
interface Props {
  modelValue?: string | number
  placeholder?: string
  disabled?: boolean
  clearable?: boolean
  type?: 'text' | 'password' | 'textarea'
}

withDefaults(defineProps<Props>(), {
  modelValue: '',
  placeholder: '请输入',
  disabled: false,
  clearable: true,
  type: 'text'
})

defineEmits<{
  'update:modelValue': [value: string | number]
  'input': [value: string | number]
  'change': [value: string | number]
}>()
</script>

<style scoped>
.sci-input :deep(.el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 8px;
  padding: 8px 12px;
  transition: all 0.3s;
}

.sci-input :deep(.el-input__wrapper:hover) {
  border-color: rgba(0, 212, 255, 0.3);
}

.sci-input :deep(.el-input__wrapper.is-focus) {
  border-color: #00d4ff;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.2);
}

.sci-input :deep(.el-input__inner) {
  color: #fff;
}
</style>

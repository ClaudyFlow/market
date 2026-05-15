<template>
  <el-select
    class="sci-select"
    :model-value="modelValue"
    :placeholder="placeholder"
    :disabled="disabled"
    :clearable="clearable"
    :multiple="multiple"
    @update:model-value="$emit('update:modelValue', $event)"
    @change="$emit('change', $event)"
  >
    <slot />
  </el-select>
</template>

<script setup lang="ts">
interface Props {
  modelValue?: any
  placeholder?: string
  disabled?: boolean
  clearable?: boolean
  multiple?: boolean
}

withDefaults(defineProps<Props>(), {
  modelValue: undefined,
  placeholder: '请选择',
  disabled: false,
  clearable: true,
  multiple: false
})

defineEmits<{
  'update:modelValue': [value: any]
  'change': [value: any]
}>()
</script>

<style scoped>
.sci-select :deep(.el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 8px;
  padding: 8px 12px;
  transition: all 0.3s;
}

.sci-select :deep(.el-input__wrapper:hover) {
  border-color: rgba(0, 212, 255, 0.3);
}

.sci-select :deep(.el-input__wrapper.is-focus) {
  border-color: #00d4ff;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.2);
}

.sci-select :deep(.el-select__selected-item) {
  color: #fff;
}
</style>

<template>
  <SciCard variant="gradient" class="search-panel">
    <el-form :inline="true" :model="modelValue">
      <el-form-item v-for="field in fields" :key="field.prop" :label="field.label">
        <SciInput
          v-if="field.type === 'input'"
          :model-value="(modelValue as any)[field.prop]"
          :placeholder="field.placeholder"
          :clearable="field.clearable !== false"
          @update:model-value="updateField(field.prop, $event)"
        />
        <SciSelect
          v-else-if="field.type === 'select'"
          :model-value="(modelValue as any)[field.prop]"
          :placeholder="field.placeholder"
          :clearable="field.clearable !== false"
          @update:model-value="updateField(field.prop, $event)"
        >
          <el-option
            v-for="opt in field.options"
            :key="opt.value"
            :label="opt.label"
            :value="opt.value"
          />
        </SciSelect>
        <el-date-picker
          v-else-if="field.type === 'date'"
          :model-value="(modelValue as any)[field.prop]"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @update:model-value="updateField(field.prop, $event)"
        />
      </el-form-item>
      <el-form-item>
        <SciButton type="primary" @click="$emit('search')">搜索</SciButton>
        <SciButton @click="$emit('reset')">重置</SciButton>
      </el-form-item>
    </el-form>
  </SciCard>
</template>

<script setup lang="ts">
import { SciCard, SciInput, SciSelect, SciButton } from '../ui'

interface FieldConfig {
  prop: string
  label: string
  type: 'input' | 'select' | 'date'
  placeholder?: string
  clearable?: boolean
  options?: { label: string; value: any }[]
}

interface Props {
  modelValue: Record<string, any>
  fields: FieldConfig[]
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:modelValue': [value: Record<string, any>]
  'search': []
  'reset': []
}>()

const updateField = (prop: string, value: any) => {
  emit('update:modelValue', { ...props.modelValue, [prop]: value })
}
</script>

<style scoped>
.search-panel {
  margin-bottom: 20px;
}

.search-panel :deep(.el-form-item__label) {
  color: #ccc;
  font-weight: 500;
}
</style>

<template>
  <section class="data-panel">
    <SciTable :data="data" :loading="loading">
      <slot />
    </SciTable>
    <SciPagination
      v-if="showPagination"
      v-model:current="currentPageModel"
      v-model:size="pageSizeModel"
      :total="total"
      :page-sizes="pageSizes"
      @size-change="$emit('size-change', $event)"
      @current-change="$emit('current-change', $event)"
    />
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { SciTable, SciPagination } from '../ui'

interface Props {
  data: any[]
  loading?: boolean
  showPagination?: boolean
  currentPage: number
  pageSize: number
  total: number
  pageSizes?: number[]
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  showPagination: true,
  pageSizes: () => [10, 20, 50, 100]
})

const emit = defineEmits<{
  'update:currentPage': [value: number]
  'update:pageSize': [value: number]
  'size-change': [size: number]
  'current-change': [page: number]
}>()

const currentPageModel = computed({
  get: () => props.currentPage,
  set: (val: number) => emit('update:currentPage', val)
})

const pageSizeModel = computed({
  get: () => props.pageSize,
  set: (val: number) => emit('update:pageSize', val)
})
</script>

<style scoped>
.data-panel {
  background: linear-gradient(180deg,
    rgba(8, 12, 28, 0.98) 0%,
    rgba(12, 18, 40, 0.95) 50%,
    rgba(8, 12, 28, 0.98) 100%);
  border: 1px solid rgba(0, 200, 255, 0.3);
  border-radius: 4px;
  padding: 24px;
  position: relative;
  overflow: hidden;
  box-shadow:
    0 0 30px rgba(0, 150, 255, 0.15),
    0 0 60px rgba(0, 100, 255, 0.1),
    inset 0 0 100px rgba(0, 150, 255, 0.05);
}
</style>

<template>
  <div class="sci-pagination">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="pageSizes"
      :layout="layout"
      @size-change="$emit('size-change', $event)"
      @current-change="$emit('current-change', $event)"
    />
  </div>
</template>

<script setup lang="ts">
const currentPage = defineModel<number>('current', { default: 1 })
const pageSize = defineModel<number>('size', { default: 10 })

interface Props {
  total: number
  pageSizes?: number[]
  layout?: string
}

withDefaults(defineProps<Props>(), {
  pageSizes: () => [10, 20, 50, 100],
  layout: 'total, sizes, prev, pager, next, jumper'
})

defineEmits<{
  'size-change': [size: number]
  'current-change': [page: number]
}>()
</script>

<style scoped>
.sci-pagination {
  display: flex;
  justify-content: flex-end;
  padding-top: 20px;
  border-top: 1px solid rgba(0, 200, 255, 0.15);
}

.sci-pagination :deep(.el-pagination) {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sci-pagination :deep(.el-pagination__total) {
  color: #6688aa;
  font-size: 13px;
}

.sci-pagination :deep(.el-pager li) {
  background: linear-gradient(180deg, rgba(0, 100, 150, 0.2) 0%, rgba(0, 50, 100, 0.3) 100%);
  border: 1px solid rgba(0, 180, 220, 0.3);
  border-radius: 4px;
  color: #6688aa;
  min-width: 36px;
  height: 36px;
  transition: all 0.3s ease;
}

.sci-pagination :deep(.el-pager li:hover) {
  background: linear-gradient(180deg, rgba(0, 180, 220, 0.4) 0%, rgba(0, 150, 200, 0.5) 100%);
  border-color: rgba(0, 200, 255, 0.6);
  color: #00ffff;
}

.sci-pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(180deg, rgba(0, 200, 255, 0.5) 0%, rgba(0, 150, 200, 0.6) 100%);
  border-color: #00ffff;
  color: #ffffff;
  font-weight: bold;
}
</style>

<template>
  <div class="product-specs-tab">
    <table class="specs-table">
      <tbody>
        <tr v-for="(value, key) in specifications" :key="key">
          <td class="spec-name">{{ formatSpecLabel(key) }}</td>
          <td class="spec-value">{{ value }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
interface Props {
  specifications: Record<string, string>
  formatSpecLabel: (key: string) => string
}

defineProps<Props>()
</script>

<style scoped>
.product-specs-tab {
  padding: 20px 0;
}

.specs-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.specs-table tr {
  border-bottom: 1px solid #f0f0f0;
}

.specs-table tr:last-child {
  border-bottom: none;
}

.specs-table td {
  padding: 12px 16px;
}

.spec-name {
  width: 120px;
  color: #666;
  background: #f8f9fa;
  font-weight: 500;
}

.spec-value {
  color: #333;
}
</style>

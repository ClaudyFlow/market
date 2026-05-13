<template>
  <section class="search-bar">
    <el-form :inline="true" :model="filterForm">
      <el-form-item label="订单编号">
        <el-input v-model="filterForm.orderNo" placeholder="请输入订单编号" clearable style="width: 180px" />
      </el-form-item>
      <el-form-item label="商品名称">
        <el-input v-model="filterForm.productName" placeholder="请输入商品名称" clearable style="width: 180px" />
      </el-form-item>
      <el-form-item label="订单状态">
        <el-select v-model="filterForm.status" placeholder="请选择状态" clearable style="width: 120px">
          <el-option label="待付款" value="pending" />
          <el-option label="待发货" value="paid" />
          <el-option label="已发货" value="shipped" />
          <el-option label="已完成" value="completed" />
          <el-option label="已取消" value="cancelled" />
          <el-option label="退款中" value="refunding" />
        </el-select>
      </el-form-item>
      <el-form-item label="下单时间">
        <el-date-picker
          v-model="filterForm.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="$emit('search')">搜索</el-button>
        <el-button @click="$emit('reset')">重置</el-button>
      </el-form-item>
    </el-form>
  </section>
</template>

<script setup lang="ts">
import type { OrderFilterForm } from '../types/order'

defineProps<{
  filterForm: OrderFilterForm
}>()

defineEmits<{
  search: []
  reset: []
}>()
</script>

<style scoped>
.search-bar {
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.8), rgba(26, 31, 58, 0.6));
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.08);
}

.search-bar :deep(.el-form-item__label) {
  color: #ccc;
  font-weight: 500;
}

.search-bar :deep(.el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 8px;
  padding: 8px 12px;
  transition: all 0.3s;
}

.search-bar :deep(.el-input__wrapper:hover) {
  border-color: rgba(0, 212, 255, 0.3);
}

.search-bar :deep(.el-input__wrapper.is-focus) {
  border-color: var(--mall-primary);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.2);
}

.search-bar :deep(.el-input__inner) {
  color: #fff;
}

.search-bar :deep(.el-select .el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
}

.search-bar :deep(.el-select__wrapper) {
  color: #fff;
}

.search-bar :deep(.el-date-editor .el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
}

.search-bar :deep(.el-button--primary) {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
  color: #000;
  font-weight: bold;
  padding: 10px 20px;
}

.search-bar :deep(.el-button--primary:hover) {
  box-shadow: 0 0 25px rgba(0, 212, 255, 0.6);
  transform: translateY(-2px);
}

.search-bar :deep(.el-button) {
  border-radius: 8px;
}
</style>

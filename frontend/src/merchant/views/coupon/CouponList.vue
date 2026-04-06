<template>
  <div class="page-container">
    <PageHeader title="优惠券管理" :icon="Ticket">
      <template #actions>
        <SciButton type="primary" @click="openCreateDialog">
          <el-icon><Plus /></el-icon>
          创建优惠券
        </SciButton>
      </template>
    </PageHeader>

    <StatCards :cards="statCards" />

    <SearchPanel
      v-model="filterForm"
      :fields="searchFields"
      @search="loadCouponList"
      @reset="resetFilter"
    />

    <DataPanel
      :data="couponList"
      :loading="loading"
      v-model:current-page="pagination.currentPage"
      v-model:page-size="pagination.pageSize"
      :total="pagination.total"
      @size-change="loadCouponList"
      @current-change="loadCouponList"
    >
      <el-table-column prop="name" label="优惠券名称" min-width="150" />
      <el-table-column label="面额" width="100">
        <template #default="{ row }">
          <SciPrice :amount="row.amount" type="success" />
        </template>
      </el-table-column>
      <el-table-column prop="condition" label="使用条件" width="120" />
      <el-table-column prop="totalCount" label="发行数量" width="100" />
      <el-table-column prop="usedCount" label="已使用" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <SciTag :type="getStatusType(row.status)">{{ row.status }}</SciTag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <SciButton type="primary" size="small" text @click="viewCoupon(row)">查看</SciButton>
          <SciButton type="warning" size="small" text @click="editCoupon(row)">编辑</SciButton>
          <SciButton type="danger" size="small" text @click="deleteCoupon(row)">删除</SciButton>
        </template>
      </el-table-column>
    </DataPanel>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Ticket, Plus } from '@element-plus/icons-vue'
import { PageHeader, DataPanel, SearchPanel, StatCards } from '@merchant/components'
import { SciButton, SciTag, SciPrice } from '@merchant/components/ui'

const loading = ref(false)
const couponList = ref<any[]>([])
const filterForm = reactive({ status: '', sortBy: 'createdAt' })

const searchFields = [
  {
    prop: 'status',
    label: '优惠券状态',
    type: 'select' as const,
    placeholder: '请选择状态',
    options: [
      { label: 'ACTIVE', value: 'ACTIVE' },
      { label: 'INACTIVE', value: 'INACTIVE' },
      { label: 'EXPIRED', value: 'EXPIRED' },
      { label: 'USED_UP', value: 'USED_UP' }
    ]
  },
  {
    prop: 'sortBy',
    label: '排序方式',
    type: 'select' as const,
    placeholder: '选择排序',
    options: [
      { label: '创建时间', value: 'createdAt' },
      { label: '使用量', value: 'usedCount' },
      { label: '剩余量', value: 'remainCount' }
    ]
  }
]

const couponStats = reactive({ total: 25, active: 12, usedUp: 8, expired: 5 })

const statCards = computed(() => [
  { value: couponStats.total, label: '优惠券总数', type: 'primary' as const, icon: Ticket },
  { value: couponStats.active, label: '发放中', type: 'success' as const, icon: Ticket },
  { value: couponStats.usedUp, label: '已领完', type: 'warning' as const, icon: Ticket },
  { value: couponStats.expired, label: '已过期', type: 'info' as const, icon: Ticket }
])

const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })

const mockCoupons = [
  { id: 1, name: '新人专享券', amount: 50, condition: '满100可用', totalCount: 1000, usedCount: 520, status: 'ACTIVE' },
  { id: 2, name: '店铺通用券', amount: 20, condition: '满200可用', totalCount: 500, usedCount: 500, status: 'USED_UP' },
  { id: 3, name: '限时优惠', amount: 100, condition: '满500可用', totalCount: 200, usedCount: 180, status: 'ACTIVE' },
  { id: 4, name: '过期活动券', amount: 30, condition: '无门槛', totalCount: 300, usedCount: 150, status: 'EXPIRED' }
]

const loadCouponList = () => {
  loading.value = true
  setTimeout(() => {
    couponList.value = mockCoupons
    pagination.total = mockCoupons.length
    loading.value = false
  }, 500)
}

const resetFilter = () => {
  filterForm.status = ''
  filterForm.sortBy = 'createdAt'
}

const getStatusType = (status: string) => {
  const map: Record<string, any> = { ACTIVE: 'success', INACTIVE: 'info', EXPIRED: 'danger', USED_UP: 'warning' }
  return map[status] || 'info'
}

const viewCoupon = (row: any) => ElMessage.info(`查看: ${row.name}`)
const editCoupon = (row: any) => ElMessage.info(`编辑: ${row.name}`)
const deleteCoupon = async (row: any) => {
  await ElMessageBox.confirm(`确定删除"${row.name}"吗？`, '提示', { type: 'warning' })
  couponList.value = couponList.value.filter(c => c.id !== row.id)
  ElMessage.success('删除成功')
}

const openCreateDialog = () => ElMessage.info('创建优惠券')

onMounted(() => loadCouponList())
</script>

<style scoped>
.page-container {
  padding: 20px;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.05) 0%, transparent 100%);
  min-height: 100vh;
}
</style>

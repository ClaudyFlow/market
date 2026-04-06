<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <PageHeader title="订单管理" :icon="List">
      <template #actions>
        <SciButton type="success" @click="exportOrders">
          <el-icon><Download /></el-icon>
          导出订单
        </SciButton>
      </template>
    </PageHeader>

    <!-- 统计卡片 -->
    <StatCards :cards="statCards" @click="handleStatClick" />

    <!-- 搜索面板 -->
    <SearchPanel
      v-model="filterForm"
      :fields="searchFields"
      @search="searchOrders"
      @reset="resetFilter"
    />

    <!-- 数据面板 -->
    <DataPanel
      :data="orders"
      :loading="loading"
      v-model:current-page="pagination.currentPage"
      v-model:page-size="pagination.pageSize"
      :total="pagination.total"
      @size-change="loadOrders"
      @current-change="loadOrders"
    >
      <!-- 表格列定义 -->
      <el-table-column prop="orderNo" label="订单编号" width="160" />
      <el-table-column prop="productImage" label="商品图片" width="80">
        <template #default="{ row }">
          <SciImage
            :src="row.productImage || 'https://via.placeholder.com/50x50/1a2a4a/00d4ff?text=商品'"
            style="width: 50px; height: 50px"
          />
        </template>
      </el-table-column>
      <el-table-column prop="productName" label="商品名称" min-width="150" />
      <el-table-column prop="customerName" label="客户" width="100" />
      <el-table-column label="订单金额" width="120">
        <template #default="{ row }">
          <SciPrice :amount="row.amount" />
        </template>
      </el-table-column>
      <el-table-column prop="orderTime" label="下单时间" width="160" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <SciTag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </SciTag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <SciButton type="primary" size="small" text @click="viewOrderDetail(row)">详情</SciButton>
          <SciButton
            v-if="row.status === 'paid'"
            type="success"
            size="small"
            text
            @click="shipOrder(row)"
          >
            发货
          </SciButton>
          <SciButton
            v-if="row.status === 'shipped'"
            type="info"
            size="small"
            text
            @click="viewLogistics(row)"
          >
            物流
          </SciButton>
          <SciButton
            v-if="row.status === 'refunding'"
            type="warning"
            size="small"
            text
            @click="handleRefund(row)"
          >
            退款
          </SciButton>
        </template>
      </el-table-column>
    </DataPanel>

    <!-- 发货对话框 -->
    <el-dialog v-model="shippingDialog.visible" title="发货操作" width="500px">
      <el-form :model="shippingForm" label-width="80px">
        <el-form-item label="物流公司">
          <SciSelect v-model="shippingForm.company" placeholder="请选择物流公司">
            <el-option
              v-for="item in logisticsCompanies"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </SciSelect>
        </el-form-item>
        <el-form-item label="物流单号">
          <SciInput v-model="shippingForm.trackingNo" placeholder="请输入物流单号" />
        </el-form-item>
        <el-form-item label="备注">
          <SciInput v-model="shippingForm.remark" type="textarea" :rows="3" placeholder="选填备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <SciButton @click="shippingDialog.visible = false">取消</SciButton>
        <SciButton type="primary" @click="confirmShipping">确认发货</SciButton>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { List, Download, Clock, ShoppingCart, Van } from '@element-plus/icons-vue'
import { PageHeader, DataPanel, SearchPanel, StatCards } from '@merchant/components'
import { SciButton, SciInput, SciSelect, SciTag, SciImage, SciPrice } from '@merchant/components/ui'
import { useOrderList, LOGISTICS_COMPANIES } from '@merchant/composables/useOrderList'

const {
  loading,
  orders,
  filterForm,
  stats,
  pagination,
  shippingDialog,
  shippingForm,
  loadOrders,
  filterByStatus,
  searchOrders,
  resetFilter,
  getStatusType,
  getStatusText,
  viewOrderDetail,
  shipOrder,
  confirmShipping,
  viewLogistics,
  handleRefund,
  exportOrders
} = useOrderList()

// 统计卡片配置
const statCards = computed(() => [
  { value: stats.value.all, label: '全部订单', type: 'primary' as const, icon: List },
  { value: stats.value.pending, label: '待付款', type: 'warning' as const, icon: Clock },
  { value: stats.value.paid, label: '待发货', type: 'info' as const, icon: ShoppingCart },
  { value: stats.value.shipped, label: '已发货', type: 'success' as const, icon: Van }
])

// 搜索字段配置
const searchFields = [
  { prop: 'orderNo', label: '订单编号', type: 'input' as const, placeholder: '请输入订单编号' },
  { prop: 'productName', label: '商品名称', type: 'input' as const, placeholder: '请输入商品名称' },
  {
    prop: 'status',
    label: '订单状态',
    type: 'select' as const,
    placeholder: '请选择状态',
    options: [
      { label: '待付款', value: 'pending' },
      { label: '待发货', value: 'paid' },
      { label: '已发货', value: 'shipped' },
      { label: '已完成', value: 'completed' },
      { label: '已取消', value: 'cancelled' },
      { label: '退款中', value: 'refunding' }
    ]
  },
  { prop: 'dateRange', label: '下单时间', type: 'date' as const }
]

const logisticsCompanies = LOGISTICS_COMPANIES

// 处理统计卡片点击
const handleStatClick = (card: any) => {
  const statusMap: Record<string, string> = {
    '全部订单': '',
    '待付款': 'pending',
    '待发货': 'paid',
    '已发货': 'shipped'
  }
  filterByStatus(statusMap[card.label] || '')
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.05) 0%, transparent 100%);
  min-height: 100vh;
}
</style>

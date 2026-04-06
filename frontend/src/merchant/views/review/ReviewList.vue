<template>
  <div class="page-container">
    <PageHeader title="评价管理" :icon="ChatDotRound">
      <template #actions>
        <SciButton type="primary" @click="loadReviewList">
          <el-icon><Refresh /></el-icon>
          刷新
        </SciButton>
      </template>
    </PageHeader>

    <StatCards :cards="statCards" />

    <SearchPanel v-model="filterForm" :fields="searchFields" @search="loadReviewList" @reset="resetFilter" />

    <DataPanel
      :data="reviewList"
      :loading="loading"
      v-model:current-page="pagination.currentPage"
      v-model:page-size="pagination.pageSize"
      :total="pagination.total"
      @size-change="loadReviewList"
      @current-change="loadReviewList"
    >
      <el-table-column prop="productName" label="商品名称" min-width="150" />
      <el-table-column prop="userName" label="用户" width="100" />
      <el-table-column label="评分" width="100">
        <template #default="{ row }">
          <el-rate v-model="row.rating" disabled />
        </template>
      </el-table-column>
      <el-table-column prop="content" label="评价内容" min-width="200" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <SciTag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</SciTag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="评价时间" width="160" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <SciButton type="primary" size="small" text @click="viewReview(row)">查看</SciButton>
          <SciButton type="success" size="small" text @click="replyReview(row)">回复</SciButton>
          <SciButton type="danger" size="small" text @click="deleteReview(row)">删除</SciButton>
        </template>
      </el-table-column>
    </DataPanel>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ChatDotRound, Refresh } from '@element-plus/icons-vue'
import { PageHeader, DataPanel, SearchPanel, StatCards } from '@merchant/components'
import { SciButton, SciTag } from '@merchant/components/ui'

const loading = ref(false)
const reviewList = ref<any[]>([])
const filterForm = reactive({ status: '', productName: '' })

const searchFields = [
  { prop: 'productName', label: '商品名称', type: 'input' as const, placeholder: '请输入商品名称' },
  {
    prop: 'status',
    label: '评价状态',
    type: 'select' as const,
    placeholder: '请选择状态',
    options: [
      { label: '待审核', value: 'pending' },
      { label: '已通过', value: 'approved' },
      { label: '已拒绝', value: 'rejected' }
    ]
  }
]

const reviewStats = reactive({ total: 1580, pending: 36, approved: 1520, rejected: 24 })

const statCards = computed(() => [
  { value: reviewStats.total, label: '总评价数', type: 'primary' as const, icon: ChatDotRound },
  { value: reviewStats.pending, label: '待审核', type: 'warning' as const, icon: ChatDotRound },
  { value: reviewStats.approved, label: '已通过', type: 'success' as const, icon: ChatDotRound },
  { value: reviewStats.rejected, label: '已拒绝', type: 'info' as const, icon: ChatDotRound }
])

const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })

const mockReviews = [
  { id: 1, productName: '无线蓝牙耳机', userName: '张先生', rating: 5, content: '音质很好，物流很快', status: 'approved', createdAt: '2026-03-18 10:30' },
  { id: 2, productName: '智能手环', userName: '李女士', rating: 4, content: '功能齐全，续航一般', status: 'pending', createdAt: '2026-03-17 15:20' },
  { id: 3, productName: '机械键盘', userName: '王先生', rating: 3, content: '手感还行，但有杂音', status: 'rejected', createdAt: '2026-03-16 09:45' }
]

const loadReviewList = () => {
  loading.value = true
  setTimeout(() => {
    reviewList.value = mockReviews
    pagination.total = mockReviews.length
    loading.value = false
  }, 500)
}

const resetFilter = () => { filterForm.status = ''; filterForm.productName = '' }
const getStatusType = (s: string) => ({ pending: 'warning', approved: 'success', rejected: 'danger' }[s] || 'info')
const getStatusText = (s: string) => ({ pending: '待审核', approved: '已通过', rejected: '已拒绝' }[s] || s)

const viewReview = (r: any) => ElMessage.info(`查看评价: ${r.userName}`)
const replyReview = (r: any) => ElMessage.info(`回复评价: ${r.userName}`)
const deleteReview = async (r: any) => {
  await ElMessageBox.confirm('确定删除此评价吗？', '提示', { type: 'warning' })
  reviewList.value = reviewList.value.filter(item => item.id !== r.id)
  ElMessage.success('删除成功')
}

onMounted(() => loadReviewList())
</script>

<style scoped>
.page-container {
  padding: 20px;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.05) 0%, transparent 100%);
  min-height: 100vh;
}
</style>

<template>
  <div class="page-container">
    <PageHeader title="活动管理" :icon="Ticket">
      <template #actions>
        <SciButton type="primary" @click="openCreateDialog">
          <el-icon><Plus /></el-icon>
          创建活动
        </SciButton>
      </template>
    </PageHeader>

    <SearchPanel
      v-model="filterForm"
      :fields="searchFields"
      @search="loadActivityList"
      @reset="resetFilter"
    />

    <DataPanel
      :data="activityList"
      :loading="loading"
      v-model:current-page="pagination.currentPage"
      v-model:page-size="pagination.pageSize"
      :total="pagination.total"
      @size-change="loadActivityList"
      @current-change="loadActivityList"
    >
      <el-table-column prop="name" label="活动名称" min-width="150" />
      <el-table-column prop="type" label="活动类型" width="120">
        <template #default="{ row }">
          <SciTag :type="getTypeTagType(row.type)">{{ row.type }}</SciTag>
        </template>
      </el-table-column>
      <el-table-column label="活动时间" min-width="180">
        <template #default="{ row }">
          {{ row.startTime }} ~ {{ row.endTime }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <SciTag :type="getStatusType(row.status)">{{ row.status }}</SciTag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <SciButton type="primary" size="small" text @click="editActivity(row)">编辑</SciButton>
          <SciButton type="danger" size="small" text @click="deleteActivity(row)">删除</SciButton>
        </template>
      </el-table-column>
    </DataPanel>

    <!-- 创建/编辑活动弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑活动' : '创建活动'" width="600px" destroy-on-close>
      <el-form :model="form" label-width="100px">
        <el-form-item label="活动名称" required>
          <SciInput v-model="form.name" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动类型" required>
          <SciSelect v-model="form.type" :options="typeOptions" placeholder="请选择类型" />
        </el-form-item>
        <el-form-item label="开始时间" required>
          <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间" required>
          <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="活动描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入活动描述" />
        </el-form-item>
        <el-form-item label="活动链接">
          <SciInput v-model="form.link" placeholder="请输入活动链接" />
        </el-form-item>
        <el-form-item label="活动图片">
          <SciInput v-model="form.image" placeholder="请输入图片URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <SciButton type="primary" @click="submitForm" :loading="submitting">确定</SciButton>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Ticket, Plus } from '@element-plus/icons-vue'
import { PageHeader, SciButton, SciInput, SciSelect, SciTag, SearchPanel, DataPanel } from '@merchant/components'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const activityId = ref<number>()

const activityList = ref<any[]>([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

const filterForm = reactive({
  keyword: '',
  type: '',
  status: ''
})

const form = reactive({
  name: '',
  type: '',
  startTime: '',
  endTime: '',
  description: '',
  link: '',
  image: ''
})

const searchFields = [
  { key: 'keyword', label: '活动名称', type: 'input', placeholder: '请输入活动名称' },
  { key: 'type', label: '活动类型', type: 'select', options: typeOptions },
  { key: 'status', label: '状态', type: 'select', options: statusOptions }
]

const typeOptions = [
  { label: '秒杀', value: 'FLASH_SALE' },
  { label: '折扣', value: 'DISCOUNT' },
  { label: '满减', value: 'FULL_REDUCE' },
  { label: '拼团', value: 'GROUP_BUY' },
  { label: '抽奖', value: 'LOTTERY' },
  { label: '会员日', value: 'VIP_DAY' }
]

const statusOptions = [
  { label: '待开始', value: 'PENDING' },
  { label: '进行中', value: 'ACTIVE' },
  { label: '已结束', value: 'ENDED' }
]

const getTypeTagType = (type: string) => {
  const map: Record<string, string> = {
    FLASH_SALE: 'danger', DISCOUNT: 'warning', FULL_REDUCE: 'success',
    GROUP_BUY: 'primary', LOTTERY: 'info', VIP_DAY: 'success'
  }
  return map[type] || 'info'
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = { PENDING: 'info', ACTIVE: 'success', ENDED: 'warning' }
  return map[status] || 'info'
}

const loadActivityList = async () => {
  loading.value = true
  try {
    const res = await fetch('/api/merchant/activity', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    const result = await res.json()
    activityList.value = result.data?.list || []
    pagination.total = result.data?.total || 0
  } catch (error) {
    activityList.value = []
  } finally {
    loading.value = false
  }
}

const resetFilter = () => {
  filterForm.keyword = ''
  filterForm.type = ''
  filterForm.status = ''
  loadActivityList()
}

const openCreateDialog = () => {
  isEdit.value = false
  Object.assign(form, { name: '', type: '', startTime: '', endTime: '', description: '', link: '', image: '' })
  dialogVisible.value = true
}

const editActivity = (row: any) => {
  isEdit.value = true
  activityId.value = row.id
  Object.assign(form, row)
  dialogVisible.value = true
}

const deleteActivity = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该活动吗？', '提示', { type: 'warning' })
    ElMessage.success('删除成功')
    loadActivityList()
  } catch { /* cancel */ }
}

const submitForm = async () => {
  if (!form.name || !form.type || !form.startTime || !form.endTime) {
    ElMessage.warning('请填写必要信息')
    return
  }
  submitting.value = true
  try {
    const method = isEdit.value ? 'PUT' : 'POST'
    const url = isEdit.value ? `/api/merchant/activity/${activityId.value}` : '/api/merchant/activity'
    await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${localStorage.getItem('token')}` },
      body: JSON.stringify(form)
    })
    ElMessage.success(isEdit.value ? '保存成功' : '创建成功')
    dialogVisible.value = false
    loadActivityList()
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadActivityList()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.05) 0%, transparent 100%);
  min-height: 100vh;
}
</style>
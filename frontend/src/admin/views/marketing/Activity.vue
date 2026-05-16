<template>
  <div class="page-container">
    <header class="page-header">
      <h1 class="page-title">
        <el-icon><Ticket /></el-icon>
        平台活动管理
      </h1>
      <el-button type="primary" @click="showCreateDialog">
        <el-icon><Plus /></el-icon>
        创建活动
      </el-button>
    </header>

    <section class="stats-cards">
      <el-row :gutter="15">
        <el-col :span="6">
          <div class="stat-card primary">
            <div class="stat-icon"><el-icon><Tickets /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.total }}</div>
              <div class="stat-label">活动总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card success">
            <div class="stat-icon"><el-icon><CircleCheck /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.active }}</div>
              <div class="stat-label">进行中</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card warning">
            <div class="stat-icon"><el-icon><Clock /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.draft }}</div>
              <div class="stat-label">草稿</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card danger">
            <div class="stat-icon"><el-icon><CloseBold /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.ended }}</div>
              <div class="stat-label">已结束</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </section>

    <section class="search-bar">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="活动状态">
          <el-select v-model="filterForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="草稿" value="DRAFT" />
            <el-option label="进行中" value="ACTIVE" />
            <el-option label="暂停" value="PAUSED" />
            <el-option label="已结束" value="ENDED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadActivities">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="table-section">
      <el-table :data="activityList" class="sci-table" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="活动名称" min-width="150">
          <template #default="{ row }">
            <div class="activity-name">{{ row.name }}</div>
            <div class="activity-desc text-muted">{{ row.description }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="discountRate" label="折扣" width="100">
          <template #default="{ row }">
            <span class="discount-tag">{{ (row.discountRate * 10).toFixed(1) }}折</span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            {{ row.type === 'DISCOUNT' ? '折扣' : '返现' }}
          </template>
        </el-table-column>
        <el-table-column label="时间" min-width="200">
          <template #default="{ row }">
            <div>{{ formatDate(row.startTime) }} ~ {{ formatDate(row.endTime) }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="participatingMerchantCount" label="参与商家" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 'DRAFT'">
              <el-button type="success" size="small" @click="publishActivity(row)">发布</el-button>
            </template>
            <template v-if="row.status === 'ACTIVE'">
              <el-button type="warning" size="small" @click="pauseActivity(row)">暂停</el-button>
            </template>
            <template v-if="row.status === 'PAUSED'">
              <el-button type="success" size="small" @click="publishActivity(row)">恢复</el-button>
            </template>
            <template v-if="row.status === 'ACTIVE' || row.status === 'PAUSED'">
              <el-button type="danger" size="small" @click="endActivity(row)">结束</el-button>
            </template>
            <el-button type="primary" size="small" @click="editActivity(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteActivity(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <!-- Create/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px">
      <el-form :model="formData" label-width="110px">
        <el-form-item label="活动名称" required>
          <el-input v-model="formData.name" placeholder="如：618大促、双十一" />
        </el-form-item>
        <el-form-item label="活动描述">
          <el-input v-model="formData.description" type="textarea" :rows="2" placeholder="活动说明" />
        </el-form-item>
        <el-form-item label="折扣率" required>
          <el-input-number v-model="formData.discountRate" :min="0.01" :max="0.99" :precision="2" :step="0.01" />
          <span style="margin-left: 8px; color: #888">（如输入0.85表示85折，管理端只能设置折扣）</span>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="formData.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="formData.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="每人限制次数">
          <el-input-number v-model="formData.maxPerUser" :min="0" placeholder="0表示不限" />
        </el-form-item>
        <el-form-item label="活动总名额">
          <el-input-number v-model="formData.totalQuota" :min="0" placeholder="0表示不限" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Ticket, Plus, Tickets, CircleCheck, Clock, CloseBold } from '@element-plus/icons-vue'
import { platformActivityApi } from '@admin/api/activity'

const loading = ref(false)
const activityList = ref<any[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('创建活动')
const isEdit = ref(false)

const filterForm = ref({ status: '' })

const stats = computed(() => ({
  total: activityList.value.length,
  active: activityList.value.filter(a => a.status === 'ACTIVE').length,
  draft: activityList.value.filter(a => a.status === 'DRAFT').length,
  ended: activityList.value.filter(a => a.status === 'ENDED').length,
}))

const formData = ref({
  id: null as number | null,
  name: '',
  description: '',
  discountRate: 0.85,
  startTime: null as Date | null,
  endTime: null as Date | null,
  maxPerUser: 0,
  totalQuota: 0,
  sortOrder: 0,
  status: 'DRAFT'
})

const formatDate = (d: any) => d ? new Date(d).toLocaleString() : '-'

const statusType = (s: string) => ({
  'DRAFT': 'info', 'ACTIVE': 'success', 'PAUSED': 'warning', 'ENDED': 'danger'
}[s] || 'info')

const statusText = (s: string) => ({
  'DRAFT': '草稿', 'ACTIVE': '进行中', 'PAUSED': '已暂停', 'ENDED': '已结束'
}[s] || s)

const loadActivities = async () => {
  loading.value = true
  try {
    const res: any = await platformActivityApi.getAll()
    let list = res.data || []
    if (filterForm.value.status) {
      list = list.filter((a: any) => a.status === filterForm.value.status)
    }
    activityList.value = list
  } catch (e) {
    ElMessage.error('加载活动失败')
  } finally {
    loading.value = false
  }
}

const resetFilter = () => {
  filterForm.value.status = ''
  loadActivities()
}

const showCreateDialog = () => {
  isEdit.value = false
  dialogTitle.value = '创建活动'
  formData.value = {
    id: null, name: '', description: '', discountRate: 0.85,
    startTime: null, endTime: null, maxPerUser: 0, totalQuota: 0, sortOrder: 0, status: 'DRAFT'
  }
  dialogVisible.value = true
}

const editActivity = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑活动'
  formData.value = {
    id: row.id,
    name: row.name,
    description: row.description || '',
    discountRate: row.discountRate || 0.85,
    startTime: row.startTime ? new Date(row.startTime) : null,
    endTime: row.endTime ? new Date(row.endTime) : null,
    maxPerUser: row.maxPerUser || 0,
    totalQuota: row.totalQuota || 0,
    sortOrder: row.sortOrder || 0,
    status: row.status
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formData.value.name) {
    ElMessage.warning('请填写活动名称')
    return
  }
  try {
    const payload = {
      ...formData.value,
      type: 'DISCOUNT',
    }
    if (isEdit.value) {
      await platformActivityApi.update(formData.value.id!, payload)
      ElMessage.success('更新成功')
    } else {
      await platformActivityApi.create(payload)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadActivities()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const publishActivity = async (row: any) => {
  try {
    await platformActivityApi.publish(row.id)
    ElMessage.success('活动已发布')
    loadActivities()
  } catch (e) { ElMessage.error('操作失败') }
}

const pauseActivity = async (row: any) => {
  try {
    await platformActivityApi.pause(row.id)
    ElMessage.success('活动已暂停')
    loadActivities()
  } catch (e) { ElMessage.error('操作失败') }
}

const endActivity = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定结束该活动吗？', '提示', { type: 'warning' })
    await platformActivityApi.end(row.id)
    ElMessage.success('活动已结束')
    loadActivities()
  } catch (e: any) { if (e !== 'cancel') ElMessage.error('操作失败') }
}

const deleteActivity = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定删除该活动吗？', '提示', { type: 'warning' })
    await platformActivityApi.delete(row.id)
    ElMessage.success('删除成功')
    loadActivities()
  } catch (e: any) { if (e !== 'cancel') ElMessage.error('删除失败') }
}

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.page-container { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-title { font-size: 20px; display: flex; align-items: center; gap: 8px; margin: 0; }
.activity-name { font-weight: 500; }
.activity-desc { font-size: 12px; margin-top: 2px; }
.discount-tag { color: #f56c6c; font-weight: bold; }
.text-muted { color: #999; }
</style>

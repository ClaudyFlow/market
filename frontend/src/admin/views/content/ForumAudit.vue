<template>
  <div class="page-container">
    <header class="page-header">
      <h1 class="page-title">
        <el-icon><ChatDotSquare /></el-icon>
        论坛帖子审核
      </h1>
    </header>

    <!-- 统计卡片 -->
    <section class="stats-cards">
      <el-row :gutter="15">
        <el-col :span="6">
          <div class="stat-card primary">
            <div class="stat-icon"><el-icon><Document /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.total }}</div>
              <div class="stat-label">帖子总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card warning">
            <div class="stat-icon"><el-icon><Clock /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pending }}</div>
              <div class="stat-label">待审核</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card success">
            <div class="stat-icon"><el-icon><CircleCheck /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.approved }}</div>
              <div class="stat-label">已通过</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card danger">
            <div class="stat-icon"><el-icon><CircleClose /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.rejected }}</div>
              <div class="stat-label">已拒绝</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </section>

    <!-- 搜索筛选 -->
    <section class="search-bar">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="审核状态">
          <el-select v-model="filterForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="待审核" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
            <el-option label="已过滤" value="FILTERED" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="filterForm.keyword" placeholder="标题/内容" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadPendingPosts">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <!-- 帖子列表 -->
    <section class="table-section">
      <el-table :data="postList" class="sci-table" v-loading="loading" row-key="id">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <div class="post-title-cell">
              <span class="post-title" @click="viewPostDetail(row)">{{ row.title }}</span>
              <el-tag v-if="row.isPinned" type="warning" size="small" class="ml-2">置顶</el-tag>
              <el-tag v-if="row.isFeatured" type="success" size="small" class="ml-2">精</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="作者" width="120" />
        <el-table-column prop="tags" label="标签" width="150">
          <template #default="{ row }">
            <span v-if="row.tags" class="tags-cell">{{ row.tags }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="likeCount" label="点赞" width="80" />
        <el-table-column prop="commentCount" label="评论" width="80" />
        <el-table-column prop="viewCount" label="浏览" width="80" />
        <el-table-column prop="auditStatus" label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="auditTagType(row.auditStatus)" size="small">
              {{ auditTagText(row.auditStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="帖子状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ statusTagText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <template v-if="row.auditStatus === 'PENDING'">
              <el-button type="success" size="small" @click="auditPost(row, 'APPROVED')">通过</el-button>
              <el-button type="danger" size="small" @click="openRejectDialog(row)">拒绝</el-button>
            </template>
            <el-button type="primary" size="small" @click="viewPostDetail(row)">查看</el-button>
            <el-button v-if="row.status === 'ACTIVE'" type="danger" size="small" @click="hidePost(row)">屏蔽</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="postList.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无帖子" />
      </div>

      <!-- 分页 -->
      <div class="pagination-bar" v-if="pagination.total > 0">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          layout="total, prev, pager, next"
          @current-change="loadPendingPosts"
        />
      </div>
    </section>

    <!-- 拒绝原因弹窗 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝原因" width="450px">
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="帖子标题">
          <span class="fw-500">{{ currentPost?.title }}</span>
        </el-form-item>
        <el-form-item label="拒绝原因" required>
          <el-input v-model="rejectForm.reason" type="textarea" :rows="3" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认拒绝</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="帖子详情" width="700px">
      <div v-if="currentPost" class="post-detail">
        <div class="detail-header">
          <h2>{{ currentPost.title }}</h2>
          <div class="detail-meta">
            <span>作者：{{ currentPost.userName }}</span>
            <span>发布时间：{{ formatDate(currentPost.createdAt) }}</span>
            <el-tag :type="auditTagType(currentPost.auditStatus)" size="small">
              {{ auditTagText(currentPost.auditStatus) }}
            </el-tag>
          </div>
        </div>
        <div class="detail-tags" v-if="currentPost.tags">
          <el-tag v-for="tag in currentPost.tags.split(',')" :key="tag" size="small" class="mr-2">
            #{{ tag.trim() }}
          </el-tag>
        </div>
        <div class="detail-content">
          {{ currentPost.content }}
        </div>
        <div v-if="currentPost.auditReason" class="detail-reject-reason">
          <el-alert type="warning" :closable="false">
            拒绝原因：{{ currentPost.auditReason }}
          </el-alert>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <template v-if="currentPost?.auditStatus === 'PENDING'">
          <el-button type="success" @click="auditPost(currentPost, 'APPROVED')">通过</el-button>
          <el-button type="danger" @click="openRejectDialog(currentPost); detailDialogVisible = false">拒绝</el-button>
        </template>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ChatDotSquare, Document, Clock, CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { forumApi } from '@admin/api/forum'

const loading = ref(false)
const postList = ref<any[]>([])
const stats = ref({ total: 0, pending: 0, approved: 0, rejected: 0 })
const rejectDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentPost = ref<any>(null)

const filterForm = ref({ status: 'PENDING', keyword: '' })

const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

const rejectForm = ref({ reason: '' })

const auditTagType = (status: string) => ({
  'PENDING': 'warning', 'APPROVED': 'success', 'REJECTED': 'danger', 'FILTERED': 'info'
}[status] || 'info')

const auditTagText = (status: string) => ({
  'PENDING': '待审核', 'APPROVED': '已通过', 'REJECTED': '已拒绝', 'FILTERED': '已过滤'
}[status] || status)

const statusTagType = (status: string) => ({
  'ACTIVE': 'success', 'HIDDEN': 'danger', 'DELETED': 'info'
}[status] || 'info')

const statusTagText = (status: string) => ({
  'ACTIVE': '正常', 'HIDDEN': '已屏蔽', 'DELETED': '已删除'
}[status] || status)

const formatDate = (d: any) => d ? new Date(d).toLocaleString('zh-CN') : '-'

const loadStats = async () => {
  try {
    const res: any = await forumApi.getAuditStats()
    stats.value = res.data || { total: 0, pending: 0, approved: 0, rejected: 0 }
  } catch (e) {
    console.error('加载统计失败', e)
  }
}

const loadPendingPosts = async () => {
  loading.value = true
  try {
    const page = pagination.currentPage - 1
    const res: any = await forumApi.getPendingPosts(page, pagination.pageSize)
    postList.value = res.data?.content || []
    pagination.total = res.data?.total || 0
  } catch (e) {
    ElMessage.error('加载帖子失败')
  } finally {
    loading.value = false
  }
}

const resetFilter = () => {
  filterForm.value = { status: 'PENDING', keyword: '' }
  loadPendingPosts()
}

const auditPost = async (post: any, status: string, reason?: string) => {
  try {
    await forumApi.auditPost(post.id, status, reason || '')
    ElMessage.success(status === 'APPROVED' ? '已通过审核' : '已拒绝')
    loadPendingPosts()
    loadStats()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const openRejectDialog = (post: any) => {
  currentPost.value = post
  rejectForm.value.reason = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  if (!rejectForm.value.reason.trim()) {
    ElMessage.warning('请填写拒绝原因')
    return
  }
  await auditPost(currentPost.value, 'REJECTED', rejectForm.value.reason)
  rejectDialogVisible.value = false
}

const hidePost = async (post: any) => {
  try {
    await ElMessageBox.confirm('确定屏蔽该帖子吗？', '提示', { type: 'warning' })
    // Set status to HIDDEN via auditPost with FILTERED status or a separate hide API
    await forumApi.auditPost(post.id, 'REJECTED', '违反社区规定')
    ElMessage.success('已屏蔽')
    loadPendingPosts()
    loadStats()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

const viewPostDetail = (post: any) => {
  currentPost.value = post
  detailDialogVisible.value = true
}

onMounted(() => {
  loadStats()
  loadPendingPosts()
})
</script>

<style scoped>
.page-container { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-title { font-size: 20px; display: flex; align-items: center; gap: 8px; margin: 0; }
.post-title-cell { display: flex; align-items: center; }
.post-title { color: #409eff; cursor: pointer; }
.post-title:hover { text-decoration: underline; }
.tags-cell { font-size: 12px; color: #666; }
.text-muted { color: #999; }
.fw-500 { font-weight: 500; }
.empty-state { padding: 40px 0; text-align: center; }
.post-detail .detail-header { margin-bottom: 16px; }
.post-detail .detail-header h2 { margin: 0 0 8px; font-size: 18px; }
.post-detail .detail-meta { display: flex; gap: 16px; color: #888; font-size: 13px; }
.post-detail .detail-tags { margin-bottom: 16px; }
.post-detail .detail-content { padding: 16px; background: #f5f7fa; border-radius: 8px; line-height: 1.8; white-space: pre-wrap; }
.post-detail .detail-reject-reason { margin-top: 16px; }
.pagination-bar { margin-top: 16px; display: flex; justify-content: center; }
</style>

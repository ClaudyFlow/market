<template>
  <div class="page-container">
    <header class="page-header">
      <h1 class="page-title">
        <el-icon><ChatDotRound /></el-icon>
        评价管理
      </h1>
    </header>

    <!-- 统计卡片 -->
    <section class="stats-cards">
      <el-row :gutter="15">
        <el-col :span="6">
          <div class="stat-card primary">
            <div class="stat-icon"><el-icon><Comment /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ reviewStats.total }}</div>
              <div class="stat-label">评价总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card success">
            <div class="stat-icon"><el-icon><Star /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ reviewStats.averageScore }}</div>
              <div class="stat-label">平均评分</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card warning">
            <div class="stat-icon"><el-icon><Clock /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ reviewStats.pendingReply }}</div>
              <div class="stat-label">待回复</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card danger">
            <div class="stat-icon"><el-icon><CircleClose /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ reviewStats.lowScore }}</div>
              <div class="stat-label">差评数</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </section>

    <!-- 搜索筛选 -->
    <section class="search-bar">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="商品名称">
          <el-input v-model="filterForm.productName" placeholder="请输入商品名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="用户昵称">
          <el-input v-model="filterForm.userName" placeholder="请输入用户昵称" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="评分">
          <el-select v-model="filterForm.score" placeholder="请选择评分" clearable style="width: 120px">
            <el-option label="1 星" :value="1" />
            <el-option label="2 星" :value="2" />
            <el-option label="3 星" :value="3" />
            <el-option label="4 星" :value="4" />
            <el-option label="5 星" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="评价时间">
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
          <el-button type="primary" @click="searchReviews">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <!-- 评价列表 -->
    <section class="table-section">
      <el-table :data="reviewList" class="sci-table" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userAvatar" label="用户" width="100">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="36" :src="row.userAvatar || `https://via.placeholder.com/36x36/00d4ff/fff?text=U`" />
              <span class="user-name">{{ row.userName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="商品名称" min-width="180" />
        <el-table-column prop="score" label="评分" width="100">
          <template #default="{ row }">
            <div class="star-rating">
              <el-icon v-for="i in 5" :key="i" :class="{ active: i <= row.score }">
                <Star />
              </el-icon>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" min-width="250" show-overflow-tooltip />
        <el-table-column prop="reply" label="回复" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="reply-text">{{ row.reply || '未回复' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="评价时间" width="160" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="replyReview(row)">回复</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadReviewList"
          @current-change="loadReviewList"
        />
      </div>
    </section>

    <!-- 回复对话框 -->
    <el-dialog v-model="replyDialog.visible" title="回复评价" width="600px">
      <div class="review-detail">
        <div class="review-header">
          <el-avatar :size="40" :src="currentReview?.userAvatar" />
          <div class="review-meta">
            <span class="user-name">{{ currentReview?.userName }}</span>
            <div class="star-rating">
              <el-icon v-for="i in 5" :key="i" :class="{ active: i <= (currentReview?.score || 0) }">
                <Star />
              </el-icon>
            </div>
          </div>
        </div>
        <p class="review-content">{{ currentReview?.content }}</p>
      </div>

      <el-form :model="replyForm" label-width="60px">
        <el-form-item label="回复">
          <el-input
            v-model="replyForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入回复内容"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="replyDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">提交回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatDotRound, Comment, Star, Clock, CircleClose } from '@element-plus/icons-vue'

interface ReviewItem {
  id: number
  userName: string
  userAvatar?: string
  productName: string
  score: number
  content: string
  reply?: string
  createTime: string
}

interface FilterForm {
  productName: string
  userName: string
  score: number | null
  dateRange: [Date, Date] | null
}

interface ReviewStats {
  total: number
  averageScore: number
  pendingReply: number
  lowScore: number
}

interface Pagination {
  currentPage: number
  pageSize: number
  total: number
}

interface ReplyDialog {
  visible: boolean
}

interface ReplyForm {
  content: string
}

const loading = ref(false)
const reviewList = ref<ReviewItem[]>([])
const filterForm = reactive<FilterForm>({
  productName: '',
  userName: '',
  score: null,
  dateRange: null
})

const reviewStats = ref<ReviewStats>({
  total: 2580,
  averageScore: 4.8,
  pendingReply: 12,
  lowScore: 35
})

const pagination = reactive<Pagination>({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const replyDialog = reactive<ReplyDialog>({
  visible: false
})

const replyForm = reactive<ReplyForm>({
  content: ''
})

const currentReview = ref<ReviewItem | null>(null)

const mockReviewData: ReviewItem[] = [
  { id: 1, userName: '张***3', userAvatar: '', productName: '无线蓝牙耳机', score: 5, content: '音质很好，物流也快，非常满意！', reply: '感谢亲的好评，祝您生活愉快！', createTime: '2026-03-18 10:30' },
  { id: 2, userName: '李***8', userAvatar: '', productName: '智能手环', score: 4, content: '手环不错，功能齐全，就是表带有点硬。', reply: '', createTime: '2026-03-18 09:15' },
  { id: 3, userName: '王***5', userAvatar: '', productName: '机械键盘', score: 5, content: '手感很好，RGB 灯效炫酷！', reply: '谢谢支持，欢迎下次光临！', createTime: '2026-03-17 16:45' },
  { id: 4, userName: '赵***2', userAvatar: '', productName: '空气净化器', score: 3, content: '净化效果还可以，但是噪音有点大。', reply: '', createTime: '2026-03-17 14:20' },
  { id: 5, userName: '钱***7', userAvatar: '', productName: '运动跑鞋', score: 2, content: '鞋子偏码，而且穿着不太舒服。', reply: '', createTime: '2026-03-17 11:00' },
  { id: 6, userName: '孙***1', userAvatar: '', productName: '护肤套装', score: 5, content: '正品，用着很好，会继续购买。', reply: '感谢老客户的信任！', createTime: '2026-03-16 20:30' },
  { id: 7, userName: '周***9', userAvatar: '', productName: '智能手表', score: 4, content: '手表功能强大，续航也不错。', reply: '', createTime: '2026-03-16 15:10' },
  { id: 8, userName: '吴***4', userAvatar: '', productName: '办公椅', score: 5, content: '椅子很舒服，久坐不累，值得购买！', reply: '谢谢亲的推荐！', createTime: '2026-03-16 10:00' }
]

const loadReviewList = () => {
  loading.value = true
  setTimeout(() => {
    reviewList.value = mockReviewData
    pagination.total = mockReviewData.length
    loading.value = false
  }, 500)
}

const searchReviews = () => {
  ElMessage.success('搜索功能演示')
  loadReviewList()
}

const resetFilter = () => {
  filterForm.productName = ''
  filterForm.userName = ''
  filterForm.score = null
  filterForm.dateRange = null
}

const replyReview = (review: ReviewItem) => {
  currentReview.value = review
  replyDialog.visible = true
  replyForm.content = review.reply || ''
}

const submitReply = () => {
  if (!replyForm.content) {
    ElMessage.warning('请输入回复内容')
    return
  }
  if (currentReview.value) {
    currentReview.value.reply = replyForm.content
  }
  ElMessage.success('回复成功')
  replyDialog.visible = false
}

onMounted(() => {
  loadReviewList()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
}

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: bold;
  color: #fff;
}

.page-title .el-icon {
  color: var(--mall-primary);
  font-size: 24px;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--glow-color), transparent);
}

.stat-card:hover {
  transform: translateY(-3px);
  border-color: var(--glow-color);
  box-shadow: 0 8px 30px rgba(0, 212, 255, 0.15);
}

.stat-card.primary { --glow-color: #00d4ff; }
.stat-card.success { --glow-color: #00ff88; }
.stat-card.warning { --glow-color: #ffaa00; }
.stat-card.danger { --glow-color: #ff6666; }

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  flex-shrink: 0;
}

.stat-card.primary .stat-icon {
  background: linear-gradient(135deg, #00d4ff, #00a8cc);
}

.stat-card.success .stat-icon {
  background: linear-gradient(135deg, #00ff88, #00cc6a);
}

.stat-card.warning .stat-icon {
  background: linear-gradient(135deg, #ffaa00, #ff8800);
}

.stat-card.danger .stat-icon {
  background: linear-gradient(135deg, #ff6666, #ff4444);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
}

.stat-label {
  font-size: 13px;
  color: #888;
  margin-top: 4px;
}

.search-bar {
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 15px 20px;
  margin-bottom: 20px;
}

.search-bar :deep(.el-form-item) {
  margin-bottom: 0;
}

.search-bar :deep(.el-form-item__label) {
  color: #aaa;
}

.table-section {
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-name {
  color: #fff;
  font-size: 14px;
}

.star-rating {
  display: flex;
  gap: 2px;
}

.star-rating .el-icon {
  color: rgba(255, 255, 255, 0.2);
  font-size: 16px;
}

.star-rating .el-icon.active {
  color: #ffd700;
}

.reply-text {
  color: #888;
  font-size: 13px;
}

.pagination-bar {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.sci-table :deep(.el-table__header th) {
  background: rgba(0, 212, 255, 0.08);
  color: var(--mall-primary);
  font-size: 13px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.15);
}

.sci-table :deep(.el-table__body td) {
  background: transparent;
  color: #aaa;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  font-size: 13px;
}

.sci-table :deep(.el-table__row:hover) {
  background: rgba(0, 212, 255, 0.05);
}

.review-detail {
  background: rgba(0, 212, 255, 0.05);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.review-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.review-meta .user-name {
  font-weight: bold;
}

.review-content {
  color: #aaa;
  line-height: 1.6;
  margin: 0;
}
</style>

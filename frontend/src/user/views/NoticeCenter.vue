<template>
  <div class="notice-center">
    <!-- 顶部导航 -->
    <Header />

    <!-- 主容器 -->
    <div class="notice-container">
      <!-- 左侧边栏 -->
      <aside class="sidebar">
        <div class="user-card">
          <el-avatar :size="60" :src="userStore.userInfo?.avatar || defaultAvatar" />
          <div class="user-info">
            <h3>{{ userStore.userInfo?.username || '用户' }}</h3>
            <p>通知中心</p>
          </div>
        </div>

        <!-- 统计信息 -->
        <div class="stats-card">
          <div class="stat-item">
            <span class="stat-value">{{ stats.unread }}</span>
            <span class="stat-label">未读</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ stats.total }}</span>
            <span class="stat-label">总计</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ stats.system }}</span>
            <span class="stat-label">系统</span>
          </div>
        </div>

        <!-- 快捷操作 -->
        <div class="quick-actions">
          <el-button type="primary" @click="markAllAsRead" :disabled="stats.unread === 0">
            <el-icon><Check /></el-icon>
            全部已读
          </el-button>
          <el-button type="danger" @click="clearAllNotices" :disabled="stats.total === 0">
            <el-icon><Delete /></el-icon>
            清空通知
          </el-button>
        </div>
      </aside>

      <!-- 右侧主内容 -->
      <main class="main-content">
        <!-- 筛选栏 -->
        <div class="filter-bar">
          <el-radio-group v-model="activeTab" @change="handleTabChange">
            <el-radio-button value="all">
              全部 ({{ stats.total }})
            </el-radio-button>
            <el-radio-button value="unread">
              未读 ({{ stats.unread }})
            </el-radio-button>
            <el-radio-button value="system">
              系统通知
            </el-radio-button>
            <el-radio-button value="activity">
              活动通知
            </el-radio-button>
            <el-radio-button value="order">
              订单通知
            </el-radio-button>
          </el-radio-group>

          <div class="filter-actions">
            <el-select v-model="sortBy" placeholder="排序方式" style="width: 150px">
              <el-option label="最新优先" value="newest" />
              <el-option label="最早优先" value="oldest" />
            </el-select>
          </div>
        </div>

        <!-- 公告列表 -->
        <div class="notice-list">
          <div
            v-for="notice in filteredNotices"
            :key="notice.id"
            class="notice-item"
            :class="{ 
              'unread': !notice.isRead,
              'important': notice.priority >= 4 
            }"
            @click="viewNotice(notice)"
          >
            <!-- 通知类型图标 -->
            <div class="notice-icon">
              <el-icon v-if="notice.type === 'SYSTEM'" :size="24"><Bell /></el-icon>
              <el-icon v-else-if="notice.type === 'ACTIVITY'" :size="24"><Present /></el-icon>
              <el-icon v-else-if="notice.type === 'ORDER'" :size="24"><ShoppingBag /></el-icon>
              <el-icon v-else :size="24"><Message /></el-icon>
            </div>

            <!-- 通知内容 -->
            <div class="notice-content">
              <div class="notice-header">
                <h3 class="notice-title">{{ notice.title }}</h3>
                <div class="notice-meta">
                  <el-tag v-if="notice.priority >= 4" type="warning" size="small">重要</el-tag>
                  <el-tag v-if="notice.type === 'SYSTEM'" type="info" size="small">系统</el-tag>
                  <el-tag v-else-if="notice.type === 'ACTIVITY'" type="success" size="small">活动</el-tag>
                  <el-tag v-else-if="notice.type === 'ORDER'" type="primary" size="small">订单</el-tag>
                  <span class="notice-time">{{ formatDate(notice.sendTime) }}</span>
                </div>
              </div>
              <p class="notice-summary">{{ notice.content }}</p>
            </div>

            <!-- 操作按钮 -->
            <div class="notice-actions" @click.stop>
              <el-button 
                v-if="!notice.isRead" 
                type="primary" 
                link 
                @click="markAsRead(notice)"
              >
                标记已读
              </el-button>
              <el-button 
                type="danger" 
                link 
                @click="deleteNotice(notice)"
              >
                删除
              </el-button>
            </div>
          </div>

          <!-- 空状态 -->
          <el-empty 
            v-if="filteredNotices.length === 0" 
            description="暂无通知"
            :image-size="200"
          >
            <el-button type="primary" @click="activeTab = 'all'">查看全部通知</el-button>
          </el-empty>
        </div>

        <!-- 分页 -->
        <div class="pagination-bar" v-if="filteredNotices.length > 0">
          <el-pagination
            v-model:current-page="pagination.currentPage"
            v-model:page-size="pagination.pageSize"
            :total="pagination.total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="loadNotices"
            @size-change="handleSizeChange"
          />
        </div>
      </main>
    </div>

    <!-- 通知详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      :title="currentNotice.title"
      width="700px"
      class="notice-dialog"
    >
      <div class="notice-detail">
        <div class="detail-header">
          <div class="detail-meta">
            <el-tag v-if="currentNotice.type === 'SYSTEM'" type="info">系统通知</el-tag>
            <el-tag v-else-if="currentNotice.type === 'ACTIVITY'" type="success">活动通知</el-tag>
            <el-tag v-else-if="currentNotice.type === 'ORDER'" type="primary">订单通知</el-tag>
            <el-tag v-if="currentNotice.priority >= 4" type="warning">重要</el-tag>
            <span class="detail-time">
              <el-icon><Clock /></el-icon>
              {{ formatDate(currentNotice.sendTime) }}
            </span>
          </div>
          <div class="detail-status">
            <el-tag v-if="currentNotice.isRead" type="success" size="small">
              <el-icon><Check /></el-icon>
              已读
            </el-tag>
            <el-tag v-else type="warning" size="small">未读</el-tag>
          </div>
        </div>
        
        <div class="detail-content">
          {{ currentNotice.content }}
        </div>

        <div v-if="currentNotice.jumpUrl" class="detail-action">
          <el-button type="primary" @click="jumpTo(currentNotice.jumpUrl)">
            <el-icon><Link /></el-icon>
            了解详情
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Bell, Present, ShoppingBag, Message, Check, Delete, 
  Clock, Link 
} from '@element-plus/icons-vue'
import Header from '@user/components/Header.vue'
import { useUserStore } from '@user/stores/user'
import { useNotificationStore } from '@user/stores/notification'
import { useNotificationWebSocket } from '@user/composables/useNotificationWebSocket'
import { 
  getNotifications, 
  markAsRead as markReadAPI, 
  markAllAsRead as markAllAsReadAPI,
  deleteNotification,
  clearAllNotifications,
  getUnreadCount,
  getNotificationStats
} from '@user/api/notification'
import type { Notification } from '@user/types/notification'

const router = useRouter()
const userStore = useUserStore()
const notificationStore = useNotificationStore()

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 状态
const notices = ref<Notification[]>([])
const activeTab = ref('all')
const sortBy = ref('newest')
const detailVisible = ref(false)
const currentNotice = ref<Notification>({} as Notification)
const loading = ref(false)

// WebSocket 通知推送
const { 
  isConnected, 
  notifications: wsNotifications,
  unreadCount: wsUnreadCount,
  connect: wsConnect,
  disconnect: wsDisconnect
} = useNotificationWebSocket({
  showDesktopNotification: true,
  onNotification: (notification) => {
    // 收到新通知时刷新列表
    loadNotices()
  }
})

// 统计信息
const stats = reactive({
  unread: 0,
  total: 0,
  system: 0
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 计算属性：筛选后的通知
const filteredNotices = computed(() => {
  let filtered = [...notices.value]
  
  // 按类型筛选
  if (activeTab.value === 'unread') {
    filtered = filtered.filter(n => !n.isRead)
  } else if (activeTab.value === 'system') {
    filtered = filtered.filter(n => n.type === 'SYSTEM')
  } else if (activeTab.value === 'activity') {
    filtered = filtered.filter(n => n.type === 'ACTIVITY')
  } else if (activeTab.value === 'order') {
    filtered = filtered.filter(n => n.type === 'ORDER')
  }
  
  // 排序
  if (sortBy.value === 'newest') {
    filtered.sort((a, b) => new Date(b.sendTime).getTime() - new Date(a.sendTime).getTime())
  } else {
    filtered.sort((a, b) => new Date(a.sendTime).getTime() - new Date(b.sendTime).getTime())
  }
  
  return filtered
})

// 加载通知列表
const loadNotices = async () => {
  loading.value = true
  try {
    const res = await getNotifications({
      page: pagination.currentPage,
      size: pagination.pageSize,
      type: activeTab.value === 'all' || activeTab.value === 'unread' ? undefined : activeTab.value.toUpperCase()
    })
    
    notices.value = res.data?.list || res.list || []
    pagination.total = res.data?.total || res.total || 0
    
    // 更新统计
    await loadStats()
  } catch (error: any) {
    ElMessage.error('加载通知失败: ' + (error.message || '未知错误'))
    console.error('加载通知失败', error)
  } finally {
    loading.value = false
  }
}

// 加载统计信息
const loadStats = async () => {
  try {
    const [unreadRes, statsRes] = await Promise.all([
      getUnreadCount(),
      getNotificationStats()
    ])
    
    stats.unread = unreadRes.data?.count ?? unreadRes.count ?? 0
    stats.total = pagination.total
    stats.system = statsRes.data?.system ?? statsRes.system ?? 0
  } catch (error) {
    console.error('加载统计信息失败', error)
  }
}

// 切换标签
const handleTabChange = () => {
  pagination.currentPage = 1
  loadNotices()
}

// 改变每页数量
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  loadNotices()
}

// 查看通知详情
const viewNotice = (notice: Notification) => {
  currentNotice.value = notice
  detailVisible.value = true

  // 标记为已读
  if (!notice.isRead) {
    markAsRead(notice)
  }
}

// 标记已读
const markAsRead = async (notice: Notification) => {
  try {
    await markReadAPI(notice.id)
    notice.isRead = true
    stats.unread = Math.max(0, stats.unread - 1)
    ElMessage.success('已标记为已读')
  } catch (error: any) {
    ElMessage.error('标记失败: ' + (error.message || '未知错误'))
  }
}

// 全部标记已读
const markAllAsRead = async () => {
  try {
    await ElMessageBox.confirm('确定要将所有通知标记为已读吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    await markAllAsReadAPI()
    await loadNotices()
    ElMessage.success('已全部标记为已读')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败: ' + (error.message || '未知错误'))
    }
  }
}

// 删除通知
const deleteNotice = async (notice: Notification) => {
  try {
    await ElMessageBox.confirm('确定要删除此通知吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteNotification(notice.id)
    notices.value = notices.value.filter(n => n.id !== notice.id)
    stats.total = Math.max(0, stats.total - 1)
    ElMessage.success('删除成功')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + (error.message || '未知错误'))
    }
  }
}

// 清空所有通知
const clearAllNotices = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有通知吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await clearAllNotifications()
    notices.value = []
    stats.unread = 0
    stats.total = 0
    stats.system = 0
    ElMessage.success('已清空所有通知')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('清空失败: ' + (error.message || '未知错误'))
    }
  }
}

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  // 小于1分钟
  if (diff < 60000) {
    return '刚刚'
  }
  // 小于1小时
  if (diff < 3600000) {
    return `${Math.floor(diff / 60000)}分钟前`
  }
  // 小于24小时
  if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`
  }
  // 小于7天
  if (diff < 604800000) {
    return `${Math.floor(diff / 86400000)}天前`
  }
  
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 跳转链接
const jumpTo = (url: string) => {
  if (url.startsWith('http')) {
    window.open(url, '_blank')
  } else {
    router.push(url)
  }
  detailVisible.value = false
}

// 初始化
onMounted(() => {
  loadNotices()
  
  // 连接 WebSocket（如果有 token）
  const token = userStore.token
  if (token) {
    wsConnect(token).catch(err => {
      console.warn('[NoticeCenter] WebSocket 连接失败，将使用轮询模式:', err)
    })
  }
})

onUnmounted(() => {
  wsDisconnect()
})
</script>

<style scoped>
.notice-center {
  min-height: 100vh;
  background: linear-gradient(180deg, #f5f7fa 0%, #e8eef5 100%);
}

/* 主容器 */
.notice-container {
  max-width: 1400px;
  margin: 20px auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 24px;
}

/* 左侧边栏 */
.sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.user-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.user-info h3 {
  margin: 0 0 4px;
  font-size: 18px;
  color: #303133;
}

.user-info p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

/* 统计卡片 */
.stats-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 20px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  color: #fff;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  opacity: 0.9;
}

/* 快捷操作 */
.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.quick-actions .el-button {
  width: 100%;
}

/* 主内容区 */
.main-content {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
  flex-wrap: wrap;
  gap: 16px;
}

.filter-actions {
  display: flex;
  gap: 12px;
}

/* 通知列表 */
.notice-list {
  min-height: 400px;
}

/* 通知项 */
.notice-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  margin-bottom: 16px;
  background: #fafafa;
  border-radius: 8px;
  border-left: 4px solid transparent;
  cursor: pointer;
  transition: all 0.3s ease;
}

.notice-item:hover {
  background: #f0f2f5;
  transform: translateX(4px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.notice-item.unread {
  background: #ecf5ff;
  border-left-color: #409eff;
}

.notice-item.important {
  background: #fdf6ec;
  border-left-color: #e6a23c;
}

/* 通知图标 */
.notice-icon {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  color: #409eff;
}

/* 通知内容 */
.notice-content {
  flex: 1;
  min-width: 0;
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
  gap: 12px;
  flex-wrap: wrap;
}

.notice-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
}

.notice-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.notice-time {
  font-size: 13px;
  color: #909399;
  white-space: nowrap;
}

.notice-summary {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 通知操作 */
.notice-actions {
  flex-shrink: 0;
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s;
}

.notice-item:hover .notice-actions {
  opacity: 1;
}

/* 分页 */
.pagination-bar {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

/* 通知详情对话框 */
.notice-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 16px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
  flex-wrap: wrap;
  gap: 12px;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.detail-time {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 14px;
}

.detail-content {
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
  white-space: pre-wrap;
  min-height: 150px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.detail-action {
  margin-top: 20px;
  text-align: center;
}

/* 响应式 */
@media (max-width: 1024px) {
  .notice-container {
    grid-template-columns: 1fr;
  }
  
  .sidebar {
    flex-direction: row;
    flex-wrap: wrap;
  }
  
  .user-card,
  .stats-card,
  .quick-actions {
    flex: 1;
    min-width: 280px;
  }
}

@media (max-width: 768px) {
  .notice-container {
    padding: 0 12px;
    gap: 16px;
  }
  
  .main-content {
    padding: 16px;
  }
  
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .notice-item {
    padding: 16px;
  }
  
  .notice-header {
    flex-direction: column;
  }
  
  .notice-actions {
    opacity: 1;
  }
}
</style>

<template>
  <div class="notice-center">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="nav">
        <div class="logo">购物商城系统</div>
        <div class="nav-menu">
          <router-link to="/">首页</router-link>
          <router-link to="/item">商品</router-link>
          <router-link to="/notice" class="active">公告</router-link>
          <router-link to="/user/center">我的</router-link>
        </div>
      </div>
    </header>

    <!-- 公告标题区域 -->
    <div class="notice-title">
      <h2>
        <i class="fas fa-bell"></i>
        系统公告
      </h2>
    </div>

    <!-- 公告列表主体 -->
    <div class="notice-list">
      <!-- 重要公告 -->
      <div
        v-for="notice in notices"
        :key="notice.id"
        class="notice-item"
        :class="{ 'important': notice.priority >= 4 }"
        @click="viewNotice(notice)"
      >
        <div class="notice-head">
          <div class="notice-title-text">
            <el-tag v-if="notice.priority >= 4" type="warning" size="small">重要</el-tag>
            <el-tag v-else-if="notice.type === 'ACTIVITY'" type="success" size="small">活动</el-tag>
            <el-tag v-else-if="notice.type === 'ORDER'" type="primary" size="small">订单</el-tag>
            {{ notice.title }}
          </div>
          <div class="notice-time">{{ formatDate(notice.sendTime) }}</div>
        </div>
        <div class="notice-content">
          {{ notice.content }}
        </div>
      </div>

      <el-empty v-if="notices.length === 0" description="暂无公告" />
    </div>

    <!-- 分页 -->
    <div class="pagination-bar" v-if="notices.length > 0">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @current-change="loadNotices"
        @size-change="loadNotices"
      />
    </div>

    <!-- 公告详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      :title="currentNotice.title"
      width="600px"
    >
      <div class="notice-detail">
        <div class="detail-meta">
          <span class="detail-time">发布时间：{{ formatDate(currentNotice.sendTime) }}</span>
          <el-tag v-if="currentNotice.priority >= 4" type="warning">重要公告</el-tag>
        </div>
        <div class="detail-content">
          {{ currentNotice.content }}
        </div>
        <div v-if="currentNotice.jumpUrl" class="detail-action">
          <el-button type="primary" @click="jumpTo(currentNotice.jumpUrl)">
            了解详情
          </el-button>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// Font Awesome 图标直接使用类名，无需导入
import { ref, reactive, onMounted } from 'vue'
import request from '@/common/api/request'

const notices = ref([])
const detailVisible = ref(false)
const currentNotice = ref({})

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const loadNotices = async () => {
  try {
    const res = await request.get('/message/list', {
      params: {
        page: pagination.currentPage,
        size: pagination.pageSize
      }
    })
    const data = res.data || res
    notices.value = data.list || []
    pagination.total = data.total || 0
  } catch (error) {
    console.error('加载公告失败', error)
  }
}

const viewNotice = (notice) => {
  currentNotice.value = notice
  detailVisible.value = true

  // 标记为已读
  if (!notice.isRead) {
    request.post('/message/read', [notice.id]).catch(() => {})
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

const jumpTo = (url) => {
  if (url.startsWith('http')) {
    window.open(url, '_blank')
  } else {
    window.location.href = url
  }
}

onMounted(() => {
  loadNotices()
})
</script>

<style scoped>
@import '@user/assets/mall-style.css';

.notice-center {
  min-height: 100vh;
  background: linear-gradient(180deg, rgba(0,212,255,0.1) 0%, rgba(0,8,16,0.95) 100%);
}

/* 顶部导航 */
.header {
  background: linear-gradient(90deg, rgba(0,16,32,0.95) 0%, rgba(0,32,64,0.9) 100%);
  color: #fff;
  padding: 15px 0;
  border-bottom: 1px solid rgba(0,212,255,0.3);
  box-shadow: 0 0 20px rgba(0,212,255,0.2);
}

.nav {
  width: 90%;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 22px;
  font-weight: bold;
  color: var(--mall-primary);
  text-shadow: 0 0 10px var(--mall-glow);
}

.nav-menu {
  display: flex;
  gap: 20px;
}

.nav-menu a {
  color: var(--mall-text-secondary);
  text-decoration: none;
  font-size: 16px;
  transition: all 0.3s;
  padding: 6px 12px;
  border-radius: 4px;
}

.nav-menu a:hover,
.nav-menu a.active {
  color: var(--mall-primary);
  background: rgba(0,212,255,0.1);
  box-shadow: 0 0 10px rgba(0,212,255,0.2);
}

/* 公告标题区域 */
.notice-title {
  text-align: center;
  margin: 30px 0;
}

.notice-title h2 {
  font-size: 28px;
  color: var(--mall-primary);
  position: relative;
  display: inline-block;
  display: flex;
  align-items: center;
  gap: 10px;
  text-shadow: 0 0 10px var(--mall-glow);
}

.notice-title h2::after {
  content: "";
  width: 60%;
  height: 3px;
  background: linear-gradient(90deg, transparent, var(--mall-primary), transparent);
  position: absolute;
  bottom: -8px;
  left: 20%;
  box-shadow: 0 0 10px var(--mall-primary);
}

/* 公告列表主体 */
.notice-list {
  background: rgba(0,16,32,0.8);
  border: 1px solid rgba(0,212,255,0.2);
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 0 30px rgba(0,212,255,0.1);
  margin-bottom: 40px;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

/* 单条公告样式 */
.notice-item {
  padding: 18px 0;
  border-bottom: 1px solid rgba(255,255,255,0.05);
  cursor: pointer;
  transition: all 0.3s;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-item:hover {
  background: rgba(0,212,255,0.05);
  padding-left: 10px;
}

/* 重要公告高亮 */
.notice-item.important {
  background: rgba(255,170,0,0.1);
  border-left: 5px solid #ffd700;
  padding-left: 15px;
  border-radius: 5px;
  box-shadow: 0 0 15px rgba(255,215,0,0.2);
}

.notice-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  gap: 15px;
}

.notice-title-text {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 8px;
}

.notice-time {
  font-size: 14px;
  color: #888;
  white-space: nowrap;
}

.notice-content {
  font-size: 15px;
  color: #aaa;
  line-height: 1.8;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* 分页 */
.pagination-bar {
  display: flex;
  justify-content: center;
  margin: 20px 0 40px;
}

/* 公告详情 */
.notice-detail {
  padding: 10px;
}

.detail-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  margin-bottom: 20px;
}

.detail-time {
  color: #888;
  font-size: 14px;
}

.detail-content {
  font-size: 16px;
  line-height: 2;
  color: #ccc;
  white-space: pre-wrap;
}

.detail-action {
  margin-top: 20px;
  text-align: center;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .nav {
    flex-direction: column;
    gap: 10px;
  }

  .notice-title h2 {
    font-size: 24px;
  }

  .notice-head {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
}
</style>

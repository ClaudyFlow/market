<template>
  <div class="forum-page">
    <header class="page-header">
      <el-icon><Comment /></el-icon>
      <h1>用户论坛</h1>
    </header>

    <div class="forum-body">
      <aside class="forum-sidebar">
        <div class="sidebar-section">
          <h3>论坛统计</h3>
          <div class="stat-item">
            <span class="label">主题总数</span>
            <span class="value">{{ totalTopics }}</span>
          </div>
          <div class="stat-item">
            <span class="label">今日发帖</span>
            <span class="value">{{ todayPosts }}</span>
          </div>
          <div class="stat-item">
            <span class="label">在线用户</span>
            <span class="value">{{ onlineUsers }}</span>
          </div>
        </div>

        <div class="sidebar-section">
          <h3>热门话题</h3>
          <div class="hot-topics">
            <div v-for="(topic, index) in hotTopics" :key="topic.id" class="hot-item" @click="viewTopic(topic.id)">
              <span class="hot-rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</span>
              <span class="hot-title">{{ topic.title }}</span>
            </div>
          </div>
        </div>

        <div class="sidebar-section">
          <h3>论坛分类</h3>
          <div class="category-list">
            <div v-for="cat in categories" :key="cat.id" class="category-item" :class="{ active: currentCategory === cat.id }" @click="selectCategory(cat.id)">
              <el-icon :size="18"><component :is="cat.icon" /></el-icon>
              <span>{{ cat.name }}</span>
            </div>
          </div>
        </div>
      </aside>

      <main class="forum-main">
        <div class="toolbar">
          <div class="search-box">
            <el-input v-model="searchKeyword" placeholder="搜索话题..." prefix-icon="Search" clearable @input="handleSearch" />
          </div>
          <div class="actions">
            <el-select v-model="sortBy" size="default" @change="handleSort">
              <el-option label="最新发布" value="latest" />
              <el-option label="最多回复" value="replies" />
              <el-option label="最多浏览" value="views" />
            </el-select>
            <el-button type="primary" @click="showCreateDialog = true">
              <el-icon><Plus /></el-icon>
              发布主题
            </el-button>
          </div>
        </div>

        <div class="topic-list">
          <div v-for="topic in filteredTopics" :key="topic.id" class="topic-item" @click="viewTopic(topic.id)">
            <div class="topic-avatar">
              <el-avatar :size="44" :src="topic.avatar" />
            </div>
            <div class="topic-content">
              <div class="topic-title-row">
                <el-tag v-if="topic.isTop" size="small" type="warning" effect="dark">置顶</el-tag>
                <el-tag v-if="topic.isHot" size="small" type="danger" effect="dark">热门</el-tag>
                <span class="topic-title">{{ topic.title }}</span>
              </div>
              <div class="topic-excerpt">{{ topic.excerpt }}</div>
              <div class="topic-meta">
                <span class="author">{{ topic.author }}</span>
                <span class="time">{{ topic.time }}</span>
                <span class="category">{{ topic.category }}</span>
                <div class="stats">
                  <el-icon><ChatLineSquare /></el-icon>
                  <span>{{ topic.replies }}</span>
                  <el-icon><View /></el-icon>
                  <span>{{ topic.views }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="pagination-wrapper">
          <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="totalTopics" layout="total, prev, pager, next, jumper" />
        </div>
      </main>
    </div>

    <el-dialog v-model="showCreateDialog" title="发布新主题" width="600px" :close-on-click-modal="false">
      <el-form :model="newTopic" label-width="80px">
        <el-form-item label="标题" required>
          <el-input v-model="newTopic.title" placeholder="请输入主题标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="newTopic.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input v-model="newTopic.content" type="textarea" :rows="8" placeholder="请输入主题内容" maxlength="5000" show-word-limit />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="newTopic.tags" placeholder="请输入标签，多个标签用逗号分隔" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="submitTopic">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Comment, ChatLineSquare, View, Plus, Search, Document, ChatDotSquare, Star, Warning } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()

const searchKeyword = ref('')
const sortBy = ref('latest')
const currentPage = ref(1)
const pageSize = ref(10)
const currentCategory = ref<number | null>(null)
const showCreateDialog = ref(false)

const totalTopics = 128
const todayPosts = 12
const onlineUsers = 56

const hotTopics = [
  { id: 1, title: '新手购物指南，教你如何优惠券叠加使用' },
  { id: 2, title: '晒单分享：刚收到的机械键盘，手感很棒！' },
  { id: 3, title: 'VIP 会员值得开通吗？使用体验分享' },
  { id: 4, title: '618 大促活动攻略，必看！' },
  { id: 5, title: '数码产品选购建议' }
]

const categories = [
  { id: 1, name: '购物分享', icon: 'Document' },
  { id: 2, name: '产品讨论', icon: 'ChatDotSquare' },
  { id: 3, name: '优惠活动', icon: 'Star' },
  { id: 4, name: '建议反馈', icon: 'Warning' }
]

const topics = ref([
  { id: 1, title: '新手购物指南，教你如何优惠券叠加使用', excerpt: '很多新手朋友不知道如何使用优惠券，今天我来详细讲解一下...', author: '购物达人', time: '2 小时前', replies: 23, views: 156, category: '购物分享', avatar: '', isTop: true, isHot: false },
  { id: 2, title: '晒单分享：刚收到的机械键盘，手感很棒！', excerpt: '等了三天终于收到了，包装很好，键盘手感一流，RGB 灯效也很炫酷', author: '键盘侠', time: '3 小时前', replies: 45, views: 289, category: '购物分享', avatar: '', isTop: false, isHot: true },
  { id: 3, title: '建议增加更多支付方式', excerpt: '希望能支持微信和支付宝，现在只有银行卡支付不太方便', author: '用户 123', time: '5 小时前', replies: 12, views: 78, category: '建议反馈', avatar: '', isTop: false, isHot: false },
  { id: 4, title: 'VIP 会员值得开通吗？使用体验分享', excerpt: '开通了一年 VIP，来分享一下实际使用感受，看看是否值得', author: '省钱小能手', time: '1 天前', replies: 67, views: 423, category: '购物分享', avatar: '', isTop: false, isHot: true },
  { id: 5, title: '数码产品选购建议', excerpt: '想买个笔记本电脑，预算 5000-6000，有什么推荐吗？', author: '数码小白', time: '1 天前', replies: 34, views: 201, category: '产品讨论', avatar: '', isTop: false, isHot: false },
  { id: 6, title: '618 大促活动攻略，必看！', excerpt: '整理了今年 618 的所有优惠活动，帮大家省钱', author: '优惠线报', time: '2 天前', replies: 89, views: 567, category: '优惠活动', avatar: '', isTop: true, isHot: true },
  { id: 7, title: '物流太慢了，什么时候能到？', excerpt: '下单已经 5 天了，物流一直卡在转运中心，有人知道怎么回事吗', author: '着急用户', time: '2 天前', replies: 8, views: 45, category: '建议反馈', avatar: '', isTop: false, isHot: false },
  { id: 8, title: '这款手机怎么样？值得入手吗？', excerpt: '看中了这款新发布的手机，配置不错，价格也可以，有用过的朋友吗', author: '手机控', time: '3 天前', replies: 21, views: 134, category: '产品讨论', avatar: '', isTop: false, isHot: false }
])

const newTopic = ref({
  title: '',
  categoryId: null,
  content: '',
  tags: ''
})

const filteredTopics = computed(() => {
  let result = [...topics.value]
  
  if (currentCategory.value) {
    result = result.filter(t => {
      const cat = categories.find(c => c.id === currentCategory.value)
      return t.category === cat?.name
    })
  }
  
  if (searchKeyword.value) {
    result = result.filter(t => t.title.toLowerCase().includes(searchKeyword.value.toLowerCase()))
  }
  
  if (sortBy.value === 'replies') {
    result.sort((a, b) => b.replies - a.replies)
  } else if (sortBy.value === 'views') {
    result.sort((a, b) => b.views - a.views)
  } else {
    result.sort((a, b) => new Date(b.time).getTime() - new Date(a.time).getTime())
  }
  
  return result
})

const selectCategory = (id: number) => {
  currentCategory.value = currentCategory.value === id ? null : id
  currentPage.value = 1
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleSort = () => {
  currentPage.value = 1
}

const viewTopic = (id: number) => {
  router.push(`/forum/${id}`)
}

const submitTopic = () => {
  if (!newTopic.value.title || !newTopic.value.categoryId || !newTopic.value.content) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  ElMessage.success('主题发布成功')
  showCreateDialog.value = false
  newTopic.value = { title: '', categoryId: null, content: '', tags: '' }
}
</script>

<style scoped>
.forum-page {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: rgba(10, 15, 30, 0.95);
  border-radius: 12px;
  overflow: hidden;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
  background: rgba(0, 212, 255, 0.05);
}

.page-header .el-icon {
  font-size: 28px;
  color: var(--mall-primary);
}

.page-header h1 {
  font-size: 22px;
  
  color: #fff;
}

.forum-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.forum-sidebar {
  width: 260px;
  padding: 16px;
  border-right: 1px solid rgba(0, 212, 255, 0.1);
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.sidebar-section h3 {
  font-size: 14px;
  color: var(--mall-primary);
  
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
}

.stat-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 13px;
}

.stat-item .label {
  color: #888;
}

.stat-item .value {
  color: #fff;
  font-weight: 600;
}

.hot-topics {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.hot-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  background: rgba(0, 212, 255, 0.05);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.hot-item:hover {
  background: rgba(0, 212, 255, 0.15);
}

.hot-rank {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  color: #fff;
  background: #666;
}

.hot-rank.rank-1 {
  background: linear-gradient(135deg, #ffd700, #ffa500);
}

.hot-rank.rank-2 {
  background: linear-gradient(135deg, #c0c0c0, #808080);
}

.hot-rank.rank-3 {
  background: linear-gradient(135deg, #cd7f32, #8b4513);
}

.hot-title {
  font-size: 12px;
  color: #ccc;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: rgba(0, 212, 255, 0.05);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  color: #ccc;
  font-size: 13px;
}

.category-item:hover {
  background: rgba(0, 212, 255, 0.15);
  color: #fff;
}

.category-item.active {
  background: rgba(0, 212, 255, 0.2);
  border: 1px solid var(--mall-primary);
  color: #fff;
}

.forum-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.1);
  gap: 16px;
}

.search-box {
  flex: 1;
  max-width: 400px;
}

.actions {
  display: flex;
  gap: 12px;
}

.topic-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.topic-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: rgba(0, 212, 255, 0.05);
  border: 1px solid rgba(0, 212, 255, 0.1);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.topic-item:hover {
  background: rgba(0, 212, 255, 0.1);
  border-color: rgba(0, 212, 255, 0.3);
  transform: translateX(4px);
}

.topic-avatar {
  flex-shrink: 0;
}

.topic-content {
  flex: 1;
  min-width: 0;
}

.topic-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  
}

.topic-title {
  font-size: 16px;
  color: #fff;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
}

.topic-excerpt {
  font-size: 13px;
  color: #888;
  
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.topic-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #666;
}

.topic-meta .author {
  color: var(--mall-primary);
}

.topic-meta .category {
  background: rgba(0, 212, 255, 0.1);
  padding: 2px 8px;
  border-radius: 4px;
  color: #aaa;
}

.topic-meta .stats {
  display: flex;
  align-items: center;
  gap: 8px;
  
}

.topic-meta .stats .el-icon {
  font-size: 14px;
}

.pagination-wrapper {
  padding: 16px 24px;
  border-top: 1px solid rgba(0, 212, 255, 0.1);
  display: flex;
  justify-content: center;
}
</style>

<template>
  <div class="forum-page">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="nav">
        <a href="#" class="logo">万象汇</a>
        <div class="search-box">
          <input
            type="text"
            class="search-input"
            placeholder="搜索帖子、用户、话题..."
            v-model="searchKeyword"
            @keyup.enter="search"
          />
          <button class="search-btn" @click="search">搜索</button>
        </div>
        <div class="nav-menu">
          <a href="#" @click.prevent="activeTab = 'home'">首页</a>
          <a href="#" @click.prevent="activeTab = 'follow'">关注</a>
          <a href="#" @click.prevent="activeTab = 'hot'">热门</a>
          <a href="#" @click.prevent="activeTab = 'my'">我的</a>
        </div>
      </div>
    </header>

    <!-- 主体内容 -->
    <div class="container">
      <!-- 左侧帖子列表 -->
      <div class="post-list">
        <div class="list-title">
          <span v-if="activeTab === 'home'">首页推荐</span>
          <span v-else-if="activeTab === 'follow'">我的关注</span>
          <span v-else-if="activeTab === 'hot'">热门帖子</span>
          <span v-else-if="activeTab === 'my'">我的帖子</span>
        </div>

        <div class="posts">
          <div
            v-for="post in postList"
            :key="post.id"
            class="post-item"
            @click="viewPost(post.id)"
          >
            <img
              :src="post.userAvatar || 'https://via.placeholder.com/50x50?text=用户'"
              alt="avatar"
              class="post-avatar"
            />
            <div class="post-content">
              <a href="#" class="post-title" @click.prevent="viewPost(post.id)">
                {{ post.title }}
              </a>
              <p class="post-excerpt">{{ post.content }}</p>
              <div class="post-info">
                <span class="post-author">{{ post.userName }}</span>
                <span class="post-time">{{ formatTime(post.createdAt) }}</span>
                <span class="post-likes">
                  👍 {{ post.likeCount || 0 }}
                </span>
                <span class="post-comments">
                  💬 {{ post.commentCount || 0 }}
                </span>
              </div>
            </div>
          </div>

          <el-empty v-if="postList.length === 0" description="暂无帖子" />
        </div>

        <div class="pagination-bar" v-if="postList.length > 0">
          <el-pagination
            v-model:current-page="pagination.currentPage"
            v-model:page-size="pagination.pageSize"
            :total="pagination.total"
            layout="total, prev, pager, next"
            @current-change="loadPosts"
          />
        </div>
      </div>

      <!-- 右侧侧边栏 -->
      <div class="sidebar">
        <!-- 快速发帖 -->
        <div class="side-box">
          <h3 class="side-title">快速发帖</h3>
          <el-input
            v-model="newPostTitle"
            placeholder="输入标题..."
            :rows="2"
            type="textarea"
          />
          <el-input
            v-model="newPostContent"
            placeholder="分享你的想法..."
            :rows="3"
            type="textarea"
            class="mt-2"
          />
          <button class="publish-btn" @click="publishPost">
            <el-icon><Plus /></el-icon> 发布帖子
          </button>
        </div>

        <!-- 热门榜单 -->
        <div class="side-box">
          <h3 class="side-title">热门榜单</h3>
          <ul class="hot-list">
            <li v-for="(item, idx) in hotPosts" :key="idx">
              <span class="hot-rank" :class="'rank-' + (idx + 1)">{{ idx + 1 }}</span>
              <a href="#" @click.prevent="viewPost(item.id)">{{ item.title }}</a>
            </li>
          </ul>
        </div>

        <!-- 热门标签 -->
        <div class="side-box">
          <h3 class="side-title">热门标签</h3>
          <div class="tag-cloud">
            <el-tag
              v-for="tag in tags"
              :key="tag.id"
              class="tag-item"
              @click="filterByTag(tag.name)"
            >
              #{{ tag.name }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- 发帖对话框 -->
    <el-dialog
      v-model="publishDialogVisible"
      title="发布新帖子"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="postForm" label-width="80px">
        <el-form-item label="标题" required>
          <el-input v-model="postForm.title" placeholder="请输入帖子标题" maxlength="100" />
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input
            v-model="postForm.content"
            type="textarea"
            :rows="6"
            placeholder="请输入帖子内容"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="postForm.tags" multiple placeholder="选择标签">
            <el-option
              v-for="tag in tags"
              :key="tag.id"
              :label="tag.name"
              :value="tag.name"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="publishDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPost">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/common/api/request'

const router = useRouter()

const activeTab = ref('home')
const searchKeyword = ref('')
const postList = ref([])
const hotPosts = ref([])
const publishDialogVisible = ref(false)
const newPostTitle = ref('')
const newPostContent = ref('')

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const postForm = reactive({
  title: '',
  content: '',
  tags: []
})

const tags = ref([
  { id: 1, name: '购物分享' },
  { id: 2, name: '好物推荐' },
  { id: 3, name: '吐槽大会' },
  { id: 4, name: '新手求助' },
  { id: 5, name: '活动资讯' },
  { id: 6, name: '数码科技' }
])

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'

  return date.toLocaleDateString('zh-CN')
}

const loadPosts = async () => {
  try {
    let url = '/forum/posts'
    const params = {
      page: pagination.currentPage - 1,
      size: pagination.pageSize
    }

    if (activeTab.value === 'hot') {
      url = '/forum/posts/hot'
    } else if (activeTab.value === 'my') {
      url = '/forum/posts/my'
    }

    const res = await request.get(url, { params })
    postList.value = res.data?.content || res.content || []
    pagination.total = res.data?.total || res.total || 0
  } catch (error) {
    console.error('加载帖子列表失败', error)
  }
}

const loadHotPosts = async () => {
  try {
    const res = await request.get('/forum/posts/hot', {
      params: { limit: 5 }
    })
    hotPosts.value = res.data || res || []
  } catch (error) {
    console.error('加载热门帖子失败', error)
  }
}

const search = () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  router.push({
    path: '/forum/search',
    query: { keyword: searchKeyword.value }
  })
}

const viewPost = (postId) => {
  router.push(`/forum/post/${postId}`)
}

const publishPost = () => {
  if (!newPostTitle.value.trim() || !newPostContent.value.trim()) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  postForm.title = newPostTitle.value
  postForm.content = newPostContent.value
  publishDialogVisible.value = true
}

const submitPost = async () => {
  if (!postForm.title.trim() || !postForm.content.trim()) {
    ElMessage.warning('请填写标题和内容')
    return
  }

  try {
    await request.post('/forum/posts', postForm)
    ElMessage.success('发布成功')
    publishDialogVisible.value = false
    newPostTitle.value = ''
    newPostContent.value = ''
    postForm.title = ''
    postForm.content = ''
    postForm.tags = []
    loadPosts()
  } catch (error) {
    console.error('发布帖子失败', error)
    ElMessage.error('发布失败')
  }
}

const filterByTag = (tagName) => {
  router.push({
    path: '/forum/tag',
    query: { name: tagName }
  })
}

onMounted(() => {
  loadPosts()
  loadHotPosts()
})
</script>

<style scoped>
.forum-page {
  min-height: 100vh;
  background-color: #f9f9f9;
}

/* 顶部导航栏 */
.header {
  background-color: #ff7a45;
  padding: 12px 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.nav {
  width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  color: white;
  font-size: 24px;
  font-weight: bold;
  text-decoration: none;
}

.search-box {
  display: flex;
  width: 500px;
}

.search-input {
  flex: 1;
  height: 36px;
  padding: 0 12px;
  border: none;
  border-radius: 4px 0 0 4px;
  outline: none;
  font-size: 14px;
}

.search-btn {
  width: 80px;
  height: 36px;
  background-color: #ff8a5c;
  color: white;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
  transition: background 0.3s;
}

.search-btn:hover {
  background-color: #ff6a2f;
}

.nav-menu {
  display: flex;
  gap: 20px;
}

.nav-menu a {
  color: white;
  text-decoration: none;
  font-size: 15px;
  cursor: pointer;
}

.nav-menu a:hover {
  text-decoration: underline;
}

/* 主体容器 */
.container {
  width: 1200px;
  margin: 20px auto;
  display: flex;
  gap: 20px;
}

/* 左侧帖子列表 */
.post-list {
  flex: 3;
  background: white;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.list-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #ff7a45;
  color: #ff7a45;
}

.posts {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.post-item {
  display: flex;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: all 0.3s;
}

.post-item:hover {
  background-color: #fafafa;
}

.post-item:last-child {
  border-bottom: none;
}

.post-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  margin-right: 15px;
  object-fit: cover;
  flex-shrink: 0;
}

.post-content {
  flex: 1;
  overflow: hidden;
}

.post-title {
  font-size: 16px;
  color: #ff7a45;
  text-decoration: none;
  font-weight: 500;
  display: block;
  margin-bottom: 8px;
}

.post-title:hover {
  text-decoration: underline;
}

.post-excerpt {
  font-size: 14px;
  color: #666;
  margin: 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.post-info {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
  display: flex;
  gap: 15px;
  align-items: center;
}

.post-likes,
.post-comments {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-bar {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 右侧侧边栏 */
.sidebar {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.side-box {
  background: white;
  border-radius: 4px;
  padding: 15px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.side-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
  color: #ff7a45;
}

.publish-btn {
  width: 100%;
  height: 40px;
  background-color: #ff7a45;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 15px;
  cursor: pointer;
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: background 0.3s;
}

.publish-btn:hover {
  background-color: #ff6a2f;
}

/* 热门榜单 */
.hot-list {
  list-style: none;
  padding: 0;
}

.hot-list li {
  padding: 8px 0;
  border-bottom: 1px dashed #eee;
  display: flex;
  align-items: center;
  gap: 8px;
}

.hot-list li:last-child {
  border-bottom: none;
}

.hot-list a {
  color: #333;
  text-decoration: none;
  font-size: 14px;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hot-list a:hover {
  color: #ff7a45;
}

.hot-rank {
  width: 20px;
  height: 20px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  color: white;
  flex-shrink: 0;
}

.hot-rank.rank-1 { background: linear-gradient(135deg, #ffd700, #ffaa00); }
.hot-rank.rank-2 { background: linear-gradient(135deg, #c0c0c0, #a0a0a0); }
.hot-rank.rank-3 { background: linear-gradient(135deg, #cd7f32, #a0522d); }
.hot-rank.rank-4,
.hot-rank.rank-5 { background: #999; }

/* 标签云 */
.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  cursor: pointer;
  transition: all 0.3s;
}

.tag-item:hover {
  transform: scale(1.05);
}

.mt-2 {
  margin-top: 12px;
}
</style>

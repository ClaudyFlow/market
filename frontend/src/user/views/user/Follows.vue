<template>
  <div class="follows-page">
    <div class="container">
      <h1 class="page-title">
        <el-icon><UserFilled /></el-icon>
        我的关注
      </h1>

      <!-- 关注列表 -->
      <div v-loading="loading" class="follows-content">
        <div v-if="follows.length > 0" class="follows-grid">
          <div
            v-for="item in follows"
            :key="item.id"
            class="follow-card"
          >
            <div class="follow-image">
              <img :src="item.shopAvatar || '/images/shop.jpg'" :alt="item.shopName" />
              <el-button
                class="remove-btn"
                type="danger"
                size="small"
                circle
                @click="removeFollow(item.shopId)"
              >
                <el-icon><Close /></el-icon>
              </el-button>
            </div>
            <div class="follow-info">
              <h3 class="shop-name">{{ item.shopName }}</h3>
              <div class="follow-meta">
                <span class="follow-time">
                  <el-icon><Clock /></el-icon>
                  {{ 格式化时间 (item.createdAt) }}
                </span>
              </div>
              <div class="follow-actions">
                <el-button
                  type="primary"
                  size="small"
                  @click="viewShop(item.shopId)"
                >
                  进入店铺
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <el-empty v-else description="暂无关注的店铺" class="empty-state">
          <el-button type="primary" @click="goToProducts">去逛逛</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { UserFilled, Close, Clock } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFollows, removeFollow } from '@user/api/follow'

const router = useRouter()

const loading = ref(false)
const follows = ref([])

// 格式化时间
const 格式化时间 = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date

  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  const week = 7 * day
  const month = 30 * day

  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return Math.floor(diff / minute) + '分钟前'
  } else if (diff < day) {
    return Math.floor(diff / hour) + '小时前'
  } else if (diff < week) {
    return Math.floor(diff / day) + '天前'
  } else if (diff < month) {
    return Math.floor(diff / week) + '周前'
  } else {
    return date.toLocaleDateString('zh-CN')
  }
}

// 加载关注列表
const loadFollows = async () => {
  loading.value = true
  try {
    const res = await getFollows()
    follows.value = res.data || []
  } catch (error) {
    console.error('加载关注列表失败:', error)
    ElMessage.error('加载关注列表失败')
  } finally {
    loading.value = false
  }
}

// 取消关注
const removeFollow = async (shopId) => {
  try {
    await ElMessageBox.confirm('确定要取消关注该店铺吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await removeFollow(shopId)
    ElMessage.success('已取消关注')
    await loadFollows()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消关注失败:', error)
      ElMessage.error('取消关注失败')
    }
  }
}

// 查看店铺
const viewShop = (shopId) => {
  router.push(`/shop/${shopId}`)
}

// 去逛逛
const goToProducts = () => {
  router.push('/items')
}

onMounted(() => {
  loadFollows()
})
</script>

<style scoped>
.follows-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  padding: 40px 20px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  color: #fff;
  font-size: 28px;
  margin-bottom: 30px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.follows-content {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 30px;
  min-height: 400px;
}

.follows-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.follow-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.follow-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.follow-image {
  position: relative;
  height: 160px;
  overflow: hidden;
}

.follow-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  opacity: 0;
  transition: opacity 0.3s;
}

.follow-card:hover .remove-btn {
  opacity: 1;
}

.follow-info {
  padding: 16px;
}

.shop-name {
  font-size: 16px;
  color: #333;
  margin-bottom: 12px;
  font-weight: 600;
}

.follow-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.follow-time {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 4px;
}

.follow-actions {
  display: flex;
  gap: 8px;
}

.follow-actions .el-button {
  flex: 1;
}

.empty-state {
  padding: 60px 20px;
}

:deep(.el-empty__description) {
  color: #666;
}
</style>

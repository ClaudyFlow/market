<template>
  <div class="favorites-page">
    <div class="container">
      <h1 class="page-title">
        <el-icon><StarFilled /></el-icon>
        我的收藏
      </h1>

      <!-- 收藏列表 -->
      <div v-loading="loading" class="favorites-content">
        <div v-if="favorites.length > 0" class="favorites-grid">
          <div
            v-for="item in favorites"
            :key="item.id"
            class="favorite-card"
          >
            <div class="favorite-image">
              <img :src="item.productImage || '/images/product.jpg'" :alt="item.productName" />
              <el-button
                class="remove-btn"
                type="danger"
                size="small"
                circle
                @click="removeFavorite(item.productId)"
              >
                <el-icon><Close /></el-icon>
              </el-button>
            </div>
            <div class="favorite-info">
              <h3 class="product-name">{{ item.productName }}</h3>
              <div class="product-price">
                <span class="price">¥{{ item.productPrice }}</span>
              </div>
              <div class="favorite-meta">
                <span class="favorite-time">
                  <el-icon><Clock /></el-icon>
                  {{ 格式化时间 (item.createdAt) }}
                </span>
              </div>
              <div class="favorite-actions">
                <el-button
                  type="primary"
                  size="small"
                  @click="viewProduct(item.productId)"
                >
                  查看详情
                </el-button>
                <el-button
                  size="small"
                  @click="addToCart(item.productId)"
                >
                  加入购物车
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <el-empty v-else description="暂无收藏商品" class="empty-state">
          <el-button type="primary" @click="goToProducts">去逛逛</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { StarFilled, Close, Clock } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFavorites, removeFavorite } from '@user/api/favorite'
import { addToCart as apiAddToCart } from '@user/api/cart'

const router = useRouter()

const loading = ref(false)
const favorites = ref([])

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

// 加载收藏列表
const loadFavorites = async () => {
  loading.value = true
  try {
    const res = await getFavorites()
    favorites.value = res.data
  } catch (error) {
    console.error('加载收藏失败:', error)
    ElMessage.error('加载收藏列表失败')
  } finally {
    loading.value = false
  }
}

// 取消收藏
const removeFavoriteItem = async (productId) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await removeFavorite(productId)
    favorites.value = favorites.value.filter(f => f.productId !== productId)
    ElMessage.success('已取消收藏')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error('取消收藏失败')
    }
  }
}

// 查看详情
const viewProduct = (productId) => {
  router.push(`/product/${productId}`)
}

// 加入购物车
const addToCart = async (productId) => {
  try {
    await apiAddToCart(productId, 1)
    ElMessage.success('已添加到购物车')
  } catch (error) {
    console.error('添加到购物车失败:', error)
    ElMessage.error(error.response?.data?.message || '添加失败')
  }
}

// 去逛逛
const goToProducts = () => {
  router.push('/products')
}

onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.favorites-page {
  min-height: calc(100vh - 120px);
  padding: 40px 20px;
  background: rgba(10, 25, 41, 0.5);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  font-size: 28px;
  color: #fff;
  margin-bottom: 30px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title .el-icon {
  color: var(--mall-warning);
  font-size: 32px;
}

.favorites-content {
  background: rgba(26, 31, 58, 0.6);
  border-radius: 12px;
  padding: 30px;
  border: 1px solid rgba(0, 212, 255, 0.1);
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.favorite-card {
  background: rgba(26, 31, 58, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
}

.favorite-card:hover {
  transform: translateY(-4px);
  border-color: var(--mall-primary);
  box-shadow: 0 8px 32px rgba(0, 212, 255, 0.15);
}

.favorite-image {
  position: relative;
  padding: 20px;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
}

.favorite-image img {
  width: 100%;
  max-height: 200px;
  object-fit: contain;
}

.remove-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  opacity: 0;
  transition: opacity 0.3s;
  background: rgba(0, 0, 0, 0.6);
  border: none;
}

.favorite-card:hover .remove-btn {
  opacity: 1;
}

.remove-btn:hover {
  background: rgba(245, 108, 108, 0.8);
}

.favorite-info {
  padding: 16px;
}

.product-name {
  font-size: 14px;
  color: #fff;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.5;
  min-height: 42px;
}

.product-price {
  margin-bottom: 12px;
}

.product-price .price {
  color: var(--mall-accent);
  font-size: 20px;
  font-weight: bold;
}

.favorite-meta {
  margin-bottom: 16px;
}

.favorite-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #888;
}

.favorite-time .el-icon {
  font-size: 14px;
}

.favorite-actions {
  display: flex;
  gap: 8px;
}

.favorite-actions .el-button {
  flex: 1;
}

.empty-state {
  padding: 60px 20px;
}

.empty-state :deep(.el-empty__description) {
  color: var(--mall-text-secondary);
}
</style>

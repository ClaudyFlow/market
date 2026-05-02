<template>
  <div class="shop-products">
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-group">
        <label>分类：</label>
        <el-select v-model="selectedCategory" size="default" @change="loadProducts">
          <el-option label="全部分类" value="" />
          <el-option 
            v-for="cat in categories" 
            :key="cat.id" 
            :label="cat.name" 
            :value="cat.id" 
          />
        </el-select>
      </div>
      <div class="filter-group">
        <label>价格：</label>
        <el-select v-model="priceRange" size="default" @change="loadProducts">
          <el-option label="全部价格" value="" />
          <el-option label="0-100 元" value="0-100" />
          <el-option label="100-500 元" value="100-500" />
          <el-option label="500-1000 元" value="500-1000" />
          <el-option label="1000 元以上" value="1000-" />
        </el-select>
      </div>
      <div class="filter-group">
        <label>排序：</label>
        <el-select v-model="sortBy" size="default" @change="loadProducts">
          <el-option label="综合排序" value="default" />
          <el-option label="销量优先" value="sales_desc" />
          <el-option label="价格从低到高" value="price_asc" />
          <el-option label="价格从高到低" value="price_desc" />
          <el-option label="新品优先" value="new" />
        </el-select>
      </div>
      <div class="search-box">
        <el-input 
          v-model="keyword"
          placeholder="搜索店内商品"
          size="default"
          clearable
          @keyup.enter="loadProducts"
        >
          <template #prefix>
            <i class="fas fa-search"></i>
          </template>
        </el-input>
        <el-button type="primary" @click="loadProducts">搜索</el-button>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="product-list" v-loading="loading">
      <div v-if="products.length === 0" class="empty-state">
        <i class="fas fa-box-open"></i>
        <p>暂无商品</p>
      </div>
      
      <div 
        v-for="product in products" 
        :key="product.id" 
        class="product-item"
        @click="goToProduct(product.id)"
      >
        <div class="product-image">
          <img :src="product.image" alt="商品图片" />
          <div class="product-tags" v-if="product.tags">
            <span v-for="tag in product.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
        </div>
        <div class="product-detail">
          <div class="product-name">{{ product.name }}</div>
          <div class="product-desc">{{ product.description }}</div>
          <div class="product-specs" v-if="product.specs">
            <span v-for="spec in product.specs" :key="spec" class="spec">{{ spec }}</span>
          </div>
          <div class="product-meta">
            <span class="sales">
              <i class="fas fa-fire"></i>
              已售{{ product.sales }}
            </span>
            <span class="stock" v-if="product.stock > 0">
              <i class="fas fa-box"></i>
              库存{{ product.stock }}
            </span>
            <span class="stock-sold-out" v-else>
              <i class="fas fa-times-circle"></i>
              售罄
            </span>
          </div>
        </div>
        <div class="product-action">
          <div class="price-box">
            <span class="current-price">¥{{ product.price }}</span>
            <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
            <span class="discount-tag" v-if="product.discount">
              {{ product.discount }}折
            </span>
          </div>
          <el-button 
            type="primary" 
            size="small"
            :disabled="product.stock === 0"
            @click.stop="addToCart(product)"
          >
            <i class="fas fa-cart-plus"></i>
            {{ product.stock === 0 ? '缺货' : '加入购物车' }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadProducts"
        @current-change="loadProducts"
      />
    </div>

    <!-- 通知动画 -->
    <notification-toast ref="toastRef" :duration="3000" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import NotificationToast from '@user/components/NotificationToast.vue'

const router = useRouter()
const route = useRoute()
const toastRef = ref()

const loading = ref(false)
const products = ref<any[]>([])
const categories = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

const selectedCategory = ref('')
const priceRange = ref('')
const sortBy = ref('default')
const keyword = ref('')

// 加载商品
const loadProducts = async () => {
  loading.value = true
  try {
    // TODO: 调用 API 获取商品列表
    // const params = {
    //   page: currentPage.value,
    //   size: pageSize.value,
    //   categoryId: selectedCategory.value,
    //   priceRange: priceRange.value,
    //   sortBy: sortBy.value,
    //   keyword: keyword.value
    // }
    
    // 模拟数据
    setTimeout(() => {
      products.value = [
        {
          id: 1,
          name: '智能手机 Pro Max 256GB 5G 全网通',
          description: '旗舰芯片，超清摄像，长续航',
          price: 5999,
          originalPrice: 6999,
          discount: 8.6,
          sales: 2580,
          stock: 100,
          image: '/images/product-1.jpg',
          tags: ['热销', '新品'],
          specs: ['256GB', '黑色', '全网通']
        },
        {
          id: 2,
          name: '轻薄笔记本电脑 14 英寸 办公学习',
          description: '高性能处理器，长续航，便携轻薄',
          price: 4599,
          originalPrice: 5299,
          discount: 8.7,
          sales: 1860,
          stock: 50,
          image: '/images/product-2.jpg',
          tags: ['爆款'],
          specs: ['16GB+512GB', '银色']
        },
        {
          id: 3,
          name: '智能手表运动版 心率监测 GPS',
          description: '运动追踪，心率监测，GPS 定位',
          price: 1299,
          originalPrice: 1599,
          discount: 8.1,
          sales: 3200,
          stock: 200,
          image: '/images/product-3.jpg',
          tags: ['推荐'],
          specs: ['黑色', '硅胶表带']
        },
        {
          id: 4,
          name: '无线蓝牙耳机 主动降噪 长续航',
          description: '主动降噪，高清通话，长续航',
          price: 599,
          originalPrice: 799,
          discount: 7.5,
          sales: 5600,
          stock: 500,
          image: '/images/product-4.jpg',
          tags: ['爆款', '推荐'],
          specs: ['白色', '蓝牙 5.3']
        }
      ]
      total.value = 368
      categories.value = [
        { id: '1', name: '手机数码' },
        { id: '2', name: '电脑办公' },
        { id: '3', name: '智能穿戴' },
        { id: '4', name: '配件耗材' }
      ]
      loading.value = false
    }, 500)
  } catch (error) {
    toastRef.value?.show({
      type: 'error',
      message: '加载商品失败',
      icon: 'fas fa-exclamation-circle'
    })
    loading.value = false
  }
}

// 加入购物车
const addToCart = async (product: any) => {
  if (product.stock === 0) return
  
  // TODO: 调用 API 加入购物车
  toastRef.value?.show({
    type: 'success',
    message: `${product.name} 已加入购物车`,
    icon: 'fas fa-cart-plus',
    animation: 'slide-right'
  })
}

// 跳转商品详情
const goToProduct = (productId: number) => {
  router.push(`/item/${productId}`)
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.shop-products {
  min-height: 100vh;
  background: var(--mall-bg-dark);
  padding: 20px;
}

/* ==================== 筛选栏 ==================== */
.filter-bar {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: center;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-group label {
  font-size: 14px;
  color: var(--mall-text-secondary);
  white-space: nowrap;
}

.filter-group :deep(.el-select) {
  width: 150px;
}

.search-box {
  display: flex;
  gap: 10px;
  flex: 1;
  min-width: 300px;
}

.search-box :deep(.el-input) {
  flex: 1;
}

.search-box :deep(.el-input__prefix) {
  color: var(--mall-text-muted);
}

/* ==================== 商品列表 ==================== */
.product-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: var(--mall-text-muted);
}

.empty-state i {
  font-size: 64px;
  margin-bottom: 20px;
  display: block;
}

.empty-state p {
  font-size: 16px;
}

.product-item {
  display: flex;
  gap: 20px;
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.product-item:hover {
  border-color: var(--mall-primary);
  transform: translateX(5px);
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.15);
}

.product-image {
  position: relative;
  width: 200px;
  height: 200px;
  flex-shrink: 0;
  border-radius: 8px;
  overflow: hidden;
  background: #0a1a2a;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-item:hover .product-image img {
  transform: scale(1.1);
}

.product-tags {
  position: absolute;
  top: 10px;
  left: 10px;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.product-tags .tag {
  padding: 3px 8px;
  font-size: 11px;
  border-radius: 4px;
  font-weight: bold;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.product-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
}

.product-name {
  font-size: 16px;
  font-weight: bold;
  color: #fff;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-desc {
  font-size: 13px;
  color: var(--mall-text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-top: 8px;
}

.product-specs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.product-specs .spec {
  padding: 3px 8px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  font-size: 12px;
  color: var(--mall-text-secondary);
}

.product-meta {
  display: flex;
  gap: 15px;
  margin-top: auto;
  padding-top: 12px;
}

.product-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: var(--mall-text-muted);
}

.product-meta i {
  font-size: 11px;
}

.product-meta .sales i {
  color: #ff6600;
}

.product-meta .stock i {
  color: var(--mall-secondary);
}

.product-meta .stock-sold-out {
  color: #ff4444;
}

.product-action {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: flex-end;
  min-width: 150px;
}

.price-box {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.current-price {
  font-size: 24px;
  font-weight: bold;
  color: var(--mall-primary);
}

.original-price {
  font-size: 14px;
  color: var(--mall-text-muted);
  text-decoration: line-through;
}

.discount-tag {
  padding: 2px 6px;
  background: linear-gradient(135deg, #ff3366, #ff5588);
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  color: #fff;
}

.product-action .el-button {
  width: 130px;
}

/* ==================== 分页 ==================== */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid var(--mall-border);
}

.pagination-wrapper :deep(.el-pagination) {
  --el-pagination-text-color: var(--mall-text-secondary);
  --el-pagination-button-color: var(--mall-text-secondary);
  --el-pagination-hover-color: var(--mall-primary);
  --el-pagination-button-bg-color: var(--mall-bg-card);
  --el-pagination-button-disabled-bg-color: var(--mall-bg-dark);
}

/* ==================== 响应式 ==================== */
@media (max-width: 768px) {
  .shop-products {
    padding: 10px;
  }

  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-group {
    width: 100%;
  }

  .filter-group :deep(.el-select) {
    flex: 1;
  }

  .search-box {
    width: 100%;
    min-width: auto;
  }

  .product-item {
    flex-direction: column;
  }

  .product-image {
    width: 100%;
    height: 200px;
  }

  .product-action {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    min-width: auto;
  }

  .product-action .el-button {
    width: auto;
  }
}
</style>

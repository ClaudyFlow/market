<template>
  <div class="shop-detail-page">
    <!-- 店铺头部 -->
    <div class="shop-header-section">
      <div class="shop-banner">
        <img :src="shop.banner || '/images/shop-banner-default.jpg'" alt="店铺 banner" />
        <div class="banner-overlay"></div>
      </div>
      
      <div class="shop-info-bar">
        <div class="shop-basic">
          <div class="shop-logo">
            <img :src="shop.logo || '/images/shop-logo-default.jpg'" alt="店铺 logo" />
          </div>
          <div class="shop-meta">
            <h1 class="shop-name">{{ shop.name }}</h1>
            <div class="shop-tags">
              <span v-for="tag in shop.tags" :key="tag" class="tag">{{ tag }}</span>
            </div>
            <div class="shop-rating">
              <el-rate v-model="shop.rating" disabled :colors="['#ff9900', '#ff9900', '#ff9900']" />
              <span class="rating-score">{{ shop.rating }}分</span>
              <span class="divider">|</span>
              <span class="followers">{{ formatNumber(shop.followers) }}人关注</span>
            </div>
          </div>
        </div>
        
        <div class="shop-actions">
          <el-button 
            :type="isFollowing ? 'success' : 'primary'" 
            size="large"
            @click="toggleFollow"
          >
            <el-icon><Star v-if="!isFollowing" /><StarFilled v-else /></el-icon>
            {{ isFollowing ? '已关注' : '关注店铺' }}
          </el-button>
          <el-button size="large" @click="contactMerchant">
            <el-icon><ChatDotRound /></el-icon>
            联系客服
          </el-button>
        </div>
      </div>
    </div>

    <!-- 店铺导航标签页 -->
    <div class="shop-tabs-section">
      <el-tabs v-model="activeTab" class="shop-tabs" type="border-card">
        <!-- 店铺首页标签 -->
        <el-tab-pane label="店铺首页" name="home">
          <div class="tab-content">
            <!-- 店铺公告 -->
            <div class="announcement-card" v-if="shop.announcement">
              <div class="announcement-header">
                <el-icon><Bell /></el-icon>
                <span>店铺公告</span>
              </div>
              <div class="announcement-body">{{ shop.announcement }}</div>
            </div>

            <!-- 店铺统计 -->
            <div class="stats-card">
              <div class="stat-item">
                <div class="stat-value">{{ shop.productCount }}</div>
                <div class="stat-label">商品数量</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ shop.positiveRate }}%</div>
                <div class="stat-label">好评率</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ shop.openYears }}年</div>
                <div class="stat-label">开店时长</div>
              </div>
              <div class="stat-item">
                <div class="stat-value certified">{{ shop.certified ? '已认证' : '未认证' }}</div>
                <div class="stat-label">认证状态</div>
              </div>
            </div>

            <!-- 优惠券 -->
            <div class="coupon-section" v-if="coupons.length > 0">
              <h3 class="section-title">
                <el-icon><Ticket /></el-icon>
                店铺优惠券
              </h3>
              <div class="coupon-list">
                <div 
                  v-for="coupon in coupons" 
                  :key="coupon.id" 
                  class="coupon-item"
                  :class="{ 'received': coupon.received }"
                  @click="receiveCoupon(coupon)"
                >
                  <div class="coupon-amount">
                    <span class="amount">¥{{ coupon.amount }}</span>
                    <span class="condition">满{{ coupon.condition }}可用</span>
                  </div>
                  <div class="coupon-info">
                    <div class="coupon-name">{{ coupon.name }}</div>
                    <div class="coupon-valid">有效期至 {{ coupon.validUntil }}</div>
                  </div>
                  <el-button 
                    :type="coupon.received ? 'success' : 'primary'"
                    size="small"
                    :disabled="coupon.received"
                  >
                    {{ coupon.received ? '已领取' : '领取' }}
                  </el-button>
                </div>
              </div>
            </div>

            <!-- 推荐商品 -->
            <div class="products-section">
              <h3 class="section-title">
                <el-icon><Goods /></el-icon>
                热销商品
              </h3>
              <div class="product-grid">
                <div 
                  v-for="product in hotProducts" 
                  :key="product.id" 
                  class="product-card"
                  @click="goToProduct(product.id)"
                >
                  <div class="product-image">
                    <img :src="product.image" :alt="product.name" />
                    <div class="product-tags" v-if="product.tags">
                      <span v-for="tag in product.tags" :key="tag" class="tag">{{ tag }}</span>
                    </div>
                  </div>
                  <div class="product-info">
                    <div class="product-name">{{ product.name }}</div>
                    <div class="product-price">
                      <span class="current-price">¥{{ product.price }}</span>
                      <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
                    </div>
                    <div class="product-sales">已售 {{ product.sales }}</div>
                  </div>
                  <el-button 
                    type="primary" 
                    class="add-cart-btn"
                    @click.stop="addToCart(product)"
                  >
                    <el-icon><ShoppingCart /></el-icon>
                    加入购物车
                  </el-button>
                </div>
              </div>
              <div class="view-more">
                <el-button type="primary" text @click="activeTab = 'products'">
                  查看全部商品 <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- 全部商品标签 -->
        <el-tab-pane label="全部商品" name="products">
          <div class="tab-content">
            <!-- 筛选栏 -->
            <div class="filter-bar">
              <div class="filter-group">
                <label>分类：</label>
                <el-select v-model="filter.category" placeholder="全部分类" clearable @change="loadProducts">
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
                <el-select v-model="filter.priceRange" placeholder="全部价格" clearable @change="loadProducts">
                  <el-option label="0-100 元" value="0-100" />
                  <el-option label="100-500 元" value="100-500" />
                  <el-option label="500-1000 元" value="500-1000" />
                  <el-option label="1000 元以上" value="1000+" />
                </el-select>
              </div>
              <div class="filter-group">
                <label>排序：</label>
                <el-radio-group v-model="filter.sort" @change="loadProducts">
                  <el-radio-button label="default">综合</el-radio-button>
                  <el-radio-button label="sales">销量</el-radio-button>
                  <el-radio-button label="price_asc">价格↑</el-radio-button>
                  <el-radio-button label="price_desc">价格↓</el-radio-button>
                  <el-radio-button label="new">新品</el-radio-button>
                </el-radio-group>
              </div>
              <div class="search-box">
                <el-input 
                  v-model="filter.keyword" 
                  placeholder="搜索店内商品"
                  clearable
                  @keyup.enter="loadProducts"
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>
                <el-button type="primary" @click="loadProducts">搜索</el-button>
              </div>
            </div>

            <!-- 商品列表 -->
            <div class="products-list" v-loading="loading">
              <div v-if="products.length === 0" class="empty-state">
                <el-empty description="暂无商品" />
              </div>
              
              <div 
                v-for="product in products" 
                :key="product.id" 
                class="product-list-item"
                @click="goToProduct(product.id)"
              >
                <div class="item-image">
                  <img :src="product.image" :alt="product.name" />
                </div>
                <div class="item-detail">
                  <div class="item-name">{{ product.name }}</div>
                  <div class="item-desc">{{ product.description }}</div>
                  <div class="item-specs" v-if="product.specs">
                    <el-tag v-for="spec in product.specs" :key="spec" size="small">{{ spec }}</el-tag>
                  </div>
                  <div class="item-meta">
                    <span class="sales">
                      <el-icon><TrendCharts /></el-icon>
                      已售 {{ product.sales }}
                    </span>
                    <span class="stock" :class="{ 'low': product.stock < 10 }">
                      <el-icon><Box /></el-icon>
                      库存 {{ product.stock }}
                    </span>
                  </div>
                </div>
                <div class="item-action">
                  <div class="price-box">
                    <span class="current-price">¥{{ product.price }}</span>
                    <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
                  </div>
                  <el-button 
                    type="primary" 
                    :disabled="product.stock === 0"
                    @click.stop="addToCart(product)"
                  >
                    <el-icon><ShoppingCart /></el-icon>
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
                :page-sizes="[10, 20, 50]"
                :total="total"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="loadProducts"
                @current-change="loadProducts"
              />
            </div>
          </div>
        </el-tab-pane>

        <!-- 店铺分类标签 -->
        <el-tab-pane label="店铺分类" name="categories">
          <div class="tab-content">
            <div class="categories-grid">
              <div 
                v-for="category in categories" 
                :key="category.id" 
                class="category-card"
                @click="selectCategory(category)"
              >
                <div class="category-icon">
                  <el-icon><FolderOpened /></el-icon>
                </div>
                <div class="category-name">{{ category.name }}</div>
                <div class="category-count">{{ category.count }} 件商品</div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 购物车悬浮按钮 -->
    <div class="cart-float-btn" @click="goToCart" v-if="cartCount > 0">
      <el-badge :value="cartCount" class="cart-badge">
        <el-icon :size="24"><ShoppingCart /></el-icon>
      </el-badge>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Star, StarFilled, ChatDotRound, Bell, Ticket, Goods,
  ShoppingCart, ArrowRight, Search, TrendCharts, Box, FolderOpened
} from '@element-plus/icons-vue'
import { useCartStore } from '@user/stores/cart'
import { useShopStore } from '@user/stores/shop'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()
const shopStore = useShopStore()

// 标签页状态
const activeTab = ref('home')

// 店铺信息
const shop = ref({
  id: 1,
  name: '科技数码旗舰店',
  logo: '/images/shop-logo.jpg',
  banner: '/images/shop-banner.jpg',
  rating: 4.8,
  followers: 12580,
  productCount: 368,
  positiveRate: 98.5,
  openYears: 3,
  certified: true,
  tags: ['品牌旗舰', '正品保障', '极速发货'],
  announcement: '本店所有商品均为正品，支持 7 天无理由退换货。新品上架，欢迎选购！'
})

// 关注状态
const isFollowing = ref(false)

// 优惠券
const coupons = ref([
  { id: 1, name: '新人专享券', amount: 50, condition: 500, validUntil: '2026-12-31', received: false },
  { id: 2, name: '满减优惠券', amount: 100, condition: 1000, validUntil: '2026-06-30', received: false }
])

// 分类
const categories = ref([
  { id: 1, name: '手机数码', count: 120 },
  { id: 2, name: '电脑办公', count: 85 },
  { id: 3, name: '智能穿戴', count: 63 },
  { id: 4, name: '配件耗材', count: 100 }
])

// 热销商品
const hotProducts = ref([
  { id: 1, name: '智能手机 Pro Max', price: 5999, originalPrice: 6999, sales: 2580, image: '/images/product-1.jpg', tags: ['热销'] },
  { id: 2, name: '轻薄笔记本电脑', price: 4599, originalPrice: 5299, sales: 1860, image: '/images/product-2.jpg', tags: ['爆款'] },
  { id: 3, name: '智能手表运动版', price: 1299, originalPrice: 1599, sales: 3200, image: '/images/product-3.jpg', tags: ['推荐'] },
  { id: 4, name: '无线蓝牙耳机', price: 599, originalPrice: 799, sales: 5600, image: '/images/product-4.jpg', tags: ['爆款', '推荐'] }
])

// 商品列表
const loading = ref(false)
const products = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

// 筛选条件
const filter = ref({
  category: '',
  priceRange: '',
  sort: 'default',
  keyword: ''
})

// 购物车数量
const cartCount = computed(() => cartStore.itemCount)

// 格式化数字
const formatNumber = (num: number): string => {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num.toString()
}

// 关注/取消关注
const toggleFollow = async () => {
  try {
    if (isFollowing.value) {
      await shopStore.unfollowShop(shop.value.id)
      isFollowing.value = false
      ElMessage.success('已取消关注')
    } else {
      await shopStore.followShop(shop.value.id)
      isFollowing.value = true
      ElMessage.success('关注成功')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 联系客服
const contactMerchant = () => {
  router.push('/chat')
}

// 领取优惠券
const receiveCoupon = (coupon: any) => {
  if (coupon.received) return
  coupon.received = true
  ElMessage.success(`优惠券领取成功！满${coupon.condition}减${coupon.amount}`)
}

// 加载商品列表
const loadProducts = async () => {
  loading.value = true
  try {
    // TODO: 调用API获取商品列表
    // 模拟数据
    setTimeout(() => {
      products.value = [
        {
          id: 1,
          name: '智能手机 Pro Max 256GB 5G 全网通',
          description: '旗舰芯片，超清摄像，长续航',
          price: 5999,
          originalPrice: 6999,
          sales: 2580,
          stock: 100,
          image: '/images/product-1.jpg',
          specs: ['256GB', '黑色', '全网通']
        },
        {
          id: 2,
          name: '轻薄笔记本电脑 14 英寸 办公学习',
          description: '高性能处理器，长续航，便携轻薄',
          price: 4599,
          originalPrice: 5299,
          sales: 1860,
          stock: 50,
          image: '/images/product-2.jpg',
          specs: ['16GB+512GB', '银色']
        },
        {
          id: 3,
          name: '智能手表运动版 心率监测 GPS',
          description: '运动追踪，心率监测，GPS 定位',
          price: 1299,
          originalPrice: 1599,
          sales: 3200,
          stock: 200,
          image: '/images/product-3.jpg',
          specs: ['黑色', '硅胶表带']
        },
        {
          id: 4,
          name: '无线蓝牙耳机 主动降噪 长续航',
          description: '主动降噪，高清通话，长续航',
          price: 599,
          originalPrice: 799,
          sales: 5600,
          stock: 500,
          image: '/images/product-4.jpg',
          specs: ['白色', '蓝牙 5.3']
        }
      ]
      total.value = 368
      loading.value = false
    }, 500)
  } catch (error) {
    ElMessage.error('加载商品失败')
    loading.value = false
  }
}

// 选择分类
const selectCategory = (category: any) => {
  filter.value.category = category.id
  activeTab.value = 'products'
  loadProducts()
}

// 加入购物车
const addToCart = async (product: any) => {
  try {
    await cartStore.addToCart(product.id, undefined, 1)
    ElMessage.success(`${product.name} 已加入购物车`)
  } catch (error) {
    // 如果API失败，使用本地存储
    const cart = JSON.parse(localStorage.getItem('cart') || '[]')
    const existingItem = cart.find((item: any) => item.id === product.id)
    if (existingItem) {
      existingItem.quantity += 1
    } else {
      cart.push({
        id: product.id,
        name: product.name,
        price: product.price,
        image: product.image,
        quantity: 1,
        selected: true
      })
    }
    localStorage.setItem('cart', JSON.stringify(cart))
    ElMessage.success(`${product.name} 已加入购物车`)
  }
}

// 跳转到商品详情
const goToProduct = (productId: number) => {
  router.push(`/item/${productId}`)
}

// 跳转到购物车
const goToCart = () => {
  router.push('/cart')
}

onMounted(() => {
  const shopId = route.params.id || route.query.id
  if (shopId) {
    // TODO: 根据ID加载店铺信息
  }
  loadProducts()
})
</script>

<style scoped>
.shop-detail-page {
  min-height: 100vh;
  background: var(--mall-bg-dark);
  padding-bottom: 40px;
}

/* ==================== 店铺头部 ==================== */
.shop-header-section {
  position: relative;
}

.shop-banner {
  position: relative;
  height: 300px;
  overflow: hidden;
}

.shop-banner img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0,0,0,0.3) 0%, rgba(0,0,0,0.7) 100%);
}

.shop-info-bar {
  max-width: 1200px;
  margin: -60px auto 0;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  position: relative;
  z-index: 10;
}

.shop-basic {
  display: flex;
  gap: 20px;
  align-items: flex-end;
}

.shop-logo {
  width: 120px;
  height: 120px;
  border-radius: 12px;
  overflow: hidden;
  border: 4px solid var(--mall-bg-dark);
  box-shadow: 0 4px 20px rgba(0,0,0,0.3);
}

.shop-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.shop-meta {
  padding-bottom: 10px;
}

.shop-name {
  font-size: 28px;
  font-weight: bold;
  color: #fff;
  margin: 0 0 10px 0;
  text-shadow: 0 2px 10px rgba(0,0,0,0.5);
}

.shop-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
}

.shop-tags .tag {
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.shop-rating {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #fff;
}

.rating-score {
  color: #ffcc00;
  font-weight: bold;
}

.divider {
  opacity: 0.5;
}

.followers {
  opacity: 0.8;
}

.shop-actions {
  display: flex;
  gap: 12px;
  padding-bottom: 20px;
}

/* ==================== 标签页区域 ==================== */
.shop-tabs-section {
  max-width: 1200px;
  margin: 30px auto 0;
  padding: 0 20px;
}

.shop-tabs {
  --el-tabs-header-bg-color: var(--mall-bg-card);
  --el-tabs-border-color: var(--mall-border);
}

.tab-content {
  padding: 20px 0;
}

/* ==================== 公告卡片 ==================== */
.announcement-card {
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.1), rgba(0, 255, 136, 0.05));
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
}

.announcement-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
  color: var(--mall-primary);
  margin-bottom: 10px;
}

.announcement-body {
  color: var(--mall-text-secondary);
  line-height: 1.6;
}

/* ==================== 统计卡片 ==================== */
.stats-card {
  display: flex;
  justify-content: space-around;
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: var(--mall-primary);
  margin-bottom: 8px;
}

.stat-value.certified {
  color: var(--mall-secondary);
}

.stat-label {
  font-size: 14px;
  color: var(--mall-text-muted);
}

/* ==================== 优惠券区域 ==================== */
.coupon-section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 15px;
}

.coupon-list {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.coupon-item {
  display: flex;
  align-items: center;
  gap: 15px;
  background: var(--mall-bg-card);
  border: 1px dashed var(--mall-border);
  border-radius: 8px;
  padding: 15px 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.coupon-item:hover {
  border-color: var(--mall-primary);
  transform: translateY(-2px);
}

.coupon-item.received {
  opacity: 0.6;
  border-style: solid;
}

.coupon-amount {
  text-align: center;
}

.coupon-amount .amount {
  font-size: 32px;
  font-weight: bold;
  color: var(--mall-primary);
  display: block;
}

.coupon-amount .condition {
  font-size: 12px;
  color: var(--mall-text-muted);
}

.coupon-info {
  flex: 1;
}

.coupon-name {
  font-size: 14px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 4px;
}

.coupon-valid {
  font-size: 12px;
  color: var(--mall-text-muted);
}

/* ==================== 商品区域 ==================== */
.products-section {
  margin-bottom: 30px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.product-card {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
  border-color: var(--mall-primary);
  box-shadow: 0 8px 25px rgba(0, 212, 255, 0.2);
}

.product-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
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
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: bold;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.product-info {
  padding: 15px;
}

.product-name {
  font-size: 14px;
  color: #fff;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 8px;
}

.current-price {
  font-size: 20px;
  font-weight: bold;
  color: var(--mall-primary);
}

.original-price {
  font-size: 14px;
  color: var(--mall-text-muted);
  text-decoration: line-through;
}

.product-sales {
  font-size: 12px;
  color: var(--mall-text-muted);
}

.add-cart-btn {
  width: 100%;
  border-radius: 0;
}

.view-more {
  text-align: center;
}

/* ==================== 筛选栏 ==================== */
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: center;
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
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

.search-box {
  display: flex;
  gap: 10px;
  flex: 1;
  min-width: 300px;
}

/* ==================== 商品列表 ==================== */
.products-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.product-list-item {
  display: flex;
  gap: 20px;
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.product-list-item:hover {
  border-color: var(--mall-primary);
  transform: translateX(5px);
}

.item-image {
  width: 150px;
  height: 150px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.item-name {
  font-size: 16px;
  font-weight: bold;
  color: #fff;
}

.item-desc {
  font-size: 13px;
  color: var(--mall-text-muted);
}

.item-specs {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.item-meta {
  display: flex;
  gap: 15px;
  margin-top: auto;
  font-size: 13px;
  color: var(--mall-text-muted);
}

.item-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.item-meta .low {
  color: #ff4444;
}

.item-action {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: flex-end;
  min-width: 150px;
}

.price-box {
  text-align: right;
}

.price-box .current-price {
  font-size: 24px;
  display: block;
}

/* ==================== 分类网格 ==================== */
.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.category-card {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  padding: 30px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.category-card:hover {
  border-color: var(--mall-primary);
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 212, 255, 0.2);
}

.category-icon {
  font-size: 48px;
  color: var(--mall-primary);
  margin-bottom: 15px;
}

.category-name {
  font-size: 16px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 8px;
}

.category-count {
  font-size: 13px;
  color: var(--mall-text-muted);
}

/* ==================== 分页 ==================== */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

/* ==================== 购物车悬浮按钮 ==================== */
.cart-float-btn {
  position: fixed;
  right: 30px;
  bottom: 100px;
  width: 56px;
  height: 56px;
  background: var(--mall-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.4);
  transition: all 0.3s;
  z-index: 100;
}

.cart-float-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 30px rgba(0, 212, 255, 0.6);
}

.cart-float-btn .el-icon {
  color: #000;
}

/* ==================== 响应式 ==================== */
@media (max-width: 768px) {
  .shop-banner {
    height: 200px;
  }
  
  .shop-info-bar {
    flex-direction: column;
    align-items: center;
    text-align: center;
    margin-top: -40px;
  }
  
  .shop-basic {
    flex-direction: column;
    align-items: center;
  }
  
  .shop-logo {
    width: 80px;
    height: 80px;
  }
  
  .shop-name {
    font-size: 20px;
  }
  
  .shop-actions {
    width: 100%;
    justify-content: center;
    margin-top: 15px;
  }
  
  .stats-card {
    flex-wrap: wrap;
    gap: 20px;
  }
  
  .stat-item {
    width: 50%;
  }
  
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-box {
    min-width: auto;
  }
  
  .product-list-item {
    flex-direction: column;
  }
  
  .item-image {
    width: 100%;
    height: 200px;
  }
  
  .item-action {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    width: 100%;
  }
}
</style>
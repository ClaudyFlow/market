<template>
  <div class="shop-home">
    <!-- 店铺头图 -->
    <div class="shop-banner">
      <div class="banner-overlay"></div>
      <img :src="shop.banner || '/images/shop-banner-default.jpg'" alt="店铺 banner" class="banner-image" />
      <div class="banner-content">
        <div class="shop-logo">
          <img :src="shop.logo || '/images/shop-logo-default.jpg'" alt="店铺 logo" />
        </div>
        <div class="shop-info">
          <h1 class="shop-name">{{ shop.name }}</h1>
          <div class="shop-tags">
            <span v-for="tag in shop.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
          <div class="shop-rating">
            <el-rate v-model="shop.rating" disabled :colors="['#ff9900', '#ff9900', '#ff9900']" />
            <span class="rating-text">{{ shop.rating }}分</span>
            <span class="followers">| {{ shop.followers }}人关注</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 店铺信息卡片 -->
    <div class="shop-info-card">
      <div class="info-row">
        <div class="info-item">
          <i class="fas fa-box"></i>
          <span class="label">商品数量</span>
          <span class="value">{{ shop.productCount }}</span>
        </div>
        <div class="info-item">
          <i class="fas fa-star"></i>
          <span class="label">好评率</span>
          <span class="value">{{ shop.positiveRate }}%</span>
        </div>
        <div class="info-item">
          <i class="fas fa-clock"></i>
          <span class="label">开店时长</span>
          <span class="value">{{ shop.openYears }}年</span>
        </div>
        <div class="info-item">
          <i class="fas fa-certificate"></i>
          <span class="label">认证状态</span>
          <span class="value certified">{{ shop.certified ? '已认证' : '未认证' }}</span>
        </div>
      </div>
      <div class="action-buttons">
        <el-button 
          :type="isFollowing ? 'primary' : 'default'" 
          size="large"
          @click="toggleFollow"
          class="follow-btn"
        >
          <i :class="isFollowing ? 'fas fa-check' : 'fas fa-plus'"></i>
          {{ isFollowing ? '已关注' : '关注店铺' }}
        </el-button>
        <el-button size="large" @click="contactMerchant">
          <i class="fas fa-comments"></i>
          联系商家
        </el-button>
        <el-button size="large" @click="shareShop">
          <i class="fas fa-share-alt"></i>
          分享店铺
        </el-button>
      </div>
    </div>

    <!-- 店铺公告 -->
    <div class="shop-announcement" v-if="shop.announcement">
      <div class="announcement-title">
        <i class="fas fa-bullhorn"></i>
        <span>店铺公告</span>
      </div>
      <div class="announcement-content slide-in-text">
        {{ shop.announcement }}
      </div>
    </div>

    <!-- 优惠券区域 -->
    <div class="coupon-section" v-if="shop.coupons && shop.coupons.length > 0">
      <div class="section-header">
        <h3><i class="fas fa-ticket-alt"></i> 店铺优惠券</h3>
      </div>
      <div class="coupon-list">
        <div 
          v-for="coupon in shop.coupons" 
          :key="coupon.id" 
          class="coupon-card"
          :class="{ 'received': coupon.received }"
          @click="receiveCoupon(coupon)"
        >
          <div class="coupon-left">
            <div class="amount">¥{{ coupon.amount }}</div>
            <div class="condition">满{{ coupon.condition }}可用</div>
          </div>
          <div class="coupon-right">
            <div class="coupon-name">{{ coupon.name }}</div>
            <div class="coupon-desc">{{ coupon.description }}</div>
            <div class="coupon-validity">有效期至 {{ coupon.validUntil }}</div>
            <el-button 
              size="small" 
              :type="coupon.received ? 'success' : 'primary'"
              class="receive-btn"
            >
              {{ coupon.received ? '已领取' : '立即领取' }}
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品分类 -->
    <div class="category-section">
      <div class="section-header">
        <h3><i class="fas fa-th-large"></i> 商品分类</h3>
      </div>
      <div class="category-list">
        <div 
          v-for="category in categories" 
          :key="category.id" 
          class="category-item"
          :class="{ active: selectedCategory === category.id }"
          @click="selectCategory(category.id)"
        >
          <i :class="category.icon"></i>
          <span>{{ category.name }}</span>
          <span class="count">{{ category.count }}</span>
        </div>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="product-section">
      <div class="section-header">
        <h3><i class="fas fa-shopping-bag"></i> 全部商品</h3>
        <div class="sort-options">
          <span 
            v-for="sort in sortOptions" 
            :key="sort.value"
            :class="{ active: selectedSort === sort.value }"
            @click="selectedSort = sort.value"
          >
            {{ sort.label }}
          </span>
        </div>
      </div>
      <div class="product-grid">
        <div 
          v-for="product in products" 
          :key="product.id" 
          class="product-card"
          @click="goToProduct(product.id)"
        >
          <div class="product-image">
            <img :src="product.image" alt="商品图片" />
            <div class="product-overlay">
              <el-button size="small" type="primary" @click.stop="addToCart(product)">
                <i class="fas fa-cart-plus"></i> 加入购物车
              </el-button>
            </div>
          </div>
          <div class="product-info">
            <div class="product-name">{{ product.name }}</div>
            <div class="product-desc">{{ product.description }}</div>
            <div class="product-meta">
              <span class="sales">已售{{ product.sales }}</span>
              <span class="stock">库存{{ product.stock }}</span>
            </div>
            <div class="product-price">
              <span class="current-price">¥{{ product.price }}</span>
              <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 通知动画组件 -->
    <notification-toast 
      ref="toastRef"
      :duration="3000"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import NotificationToast from '@user/components/NotificationToast.vue'

const router = useRouter()
const toastRef = ref()

// 店铺信息
const shop = ref({
  id: 1,
  name: '科技数码旗舰店',
  banner: '/images/shop-banner.jpg',
  logo: '/images/shop-logo.jpg',
  rating: 4.8,
  followers: 12580,
  productCount: 368,
  positiveRate: 98.5,
  openYears: 3,
  certified: true,
  tags: ['品牌旗舰', '正品保障', '极速发货'],
  announcement: '本店所有商品均为正品，支持 7 天无理由退换货。新品上架，欢迎选购！',
  coupons: [
    {
      id: 1,
      name: '新人专享券',
      amount: 50,
      condition: 500,
      description: '新人专享优惠券',
      validUntil: '2026-12-31',
      received: false
    },
    {
      id: 2,
      name: '满减优惠券',
      amount: 100,
      condition: 1000,
      description: '全店通用',
      validUntil: '2026-06-30',
      received: false
    }
  ]
})

// 是否已关注
const isFollowing = ref(false)

// 商品分类
const categories = ref([
  { id: 0, name: '全部', icon: 'fas fa-th', count: 368 },
  { id: 1, name: '手机数码', icon: 'fas fa-mobile-alt', count: 120 },
  { id: 2, name: '电脑办公', icon: 'fas fa-laptop', count: 85 },
  { id: 3, name: '智能穿戴', icon: 'fas fa-watch', count: 63 },
  { id: 4, name: '配件耗材', icon: 'fas fa-usb', count: 100 }
])

const selectedCategory = ref(0)

// 排序选项
const sortOptions = ref([
  { label: '综合', value: 'default' },
  { label: '销量', value: 'sales' },
  { label: '价格', value: 'price' },
  { label: '新品', value: 'new' }
])

const selectedSort = ref('default')

// 商品列表
const products = ref([
  {
    id: 1,
    name: '智能手机 Pro Max 256GB',
    description: '旗舰芯片，超清摄像',
    price: 5999,
    originalPrice: 6999,
    sales: 2580,
    stock: 100,
    image: '/images/product-1.jpg'
  },
  {
    id: 2,
    name: '轻薄笔记本电脑 14 英寸',
    description: '高性能处理器，长续航',
    price: 4599,
    originalPrice: 5299,
    sales: 1860,
    stock: 50,
    image: '/images/product-2.jpg'
  },
  {
    id: 3,
    name: '智能手表运动版',
    description: '心率监测，GPS 定位',
    price: 1299,
    originalPrice: 1599,
    sales: 3200,
    stock: 200,
    image: '/images/product-3.jpg'
  },
  {
    id: 4,
    name: '无线蓝牙耳机',
    description: '主动降噪，长续航',
    price: 599,
    originalPrice: 799,
    sales: 5600,
    stock: 500,
    image: '/images/product-4.jpg'
  }
])

// 关注店铺
const toggleFollow = async () => {
  isFollowing.value = !isFollowing.value
  if (isFollowing.value) {
    toastRef.value?.show({
      type: 'success',
      message: '关注店铺成功',
      icon: 'fas fa-heart'
    })
  } else {
    toastRef.value?.show({
      type: 'info',
      message: '已取消关注',
      icon: 'fas fa-heart-broken'
    })
  }
}

// 联系商家
const contactMerchant = () => {
  toastRef.value?.show({
    type: 'info',
    message: '正在连接商家客服...',
    icon: 'fas fa-comments'
  })
  setTimeout(() => {
    router.push('/service')
  }, 1000)
}

// 分享店铺
const shareShop = () => {
  const shareUrl = window.location.href
  navigator.clipboard?.writeText(shareUrl)
  toastRef.value?.show({
    type: 'success',
    message: '店铺链接已复制到剪贴板',
    icon: 'fas fa-share-alt'
  })
}

// 选择分类
const selectCategory = (categoryId: number) => {
  selectedCategory.value = categoryId
  toastRef.value?.show({
    type: 'info',
    message: `已切换到${categories.value.find(c => c.id === categoryId)?.name}`,
    icon: 'fas fa-filter'
  })
}

// 领取优惠券
const receiveCoupon = (coupon: any) => {
  if (coupon.received) return
  coupon.received = true
  toastRef.value?.show({
    type: 'success',
    message: `优惠券领取成功！满${coupon.condition}减${coupon.amount}`,
    icon: 'fas fa-ticket-alt',
    animation: 'bounce'
  })
}

// 加入购物车
const addToCart = (product: any) => {
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
</script>

<style scoped>
.shop-home {
  min-height: 100vh;
  background: var(--mall-bg-dark);
  padding-bottom: 60px;
}

/* ==================== 店铺头图 ==================== */
.shop-banner {
  position: relative;
  height: 280px;
  overflow: hidden;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    to bottom,
    rgba(0, 8, 16, 0.3) 0%,
    rgba(0, 8, 16, 0.8) 100%
  );
  z-index: 1;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-content {
  position: absolute;
  bottom: 30px;
  left: 30px;
  display: flex;
  align-items: center;
  gap: 20px;
  z-index: 2;
}

.shop-logo {
  width: 100px;
  height: 100px;
  border-radius: 12px;
  overflow: hidden;
  border: 3px solid var(--mall-primary);
  box-shadow: 0 4px 20px var(--mall-glow);
}

.shop-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.shop-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.shop-name {
  font-size: 28px;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
}

.shop-tags {
  display: flex;
  gap: 8px;
}

.shop-tags .tag {
  padding: 4px 10px;
  background: rgba(0, 0, 0, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  font-size: 12px;
  color: #fff;
}

.shop-rating {
  display: flex;
  align-items: center;
  gap: 10px;
}

.shop-rating :deep(.el-rate) {
  font-size: 16px;
}

.rating-text {
  color: #ffcc00;
  font-size: 14px;
}

.followers {
  color: var(--mall-text-secondary);
  font-size: 14px;
}

/* ==================== 店铺信息卡片 ==================== */
.shop-info-card {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  padding: 20px;
  margin: -60px 20px 20px;
  position: relative;
  z-index: 3;
  backdrop-filter: blur(10px);
}

.info-row {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.info-item i {
  font-size: 24px;
  color: var(--mall-primary);
}

.info-item .label {
  font-size: 12px;
  color: var(--mall-text-muted);
}

.info-item .value {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
}

.info-item .value.certified {
  color: var(--mall-secondary);
}

.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.follow-btn {
  --el-button-hover-border-color: var(--mall-primary);
}

/* ==================== 店铺公告 ==================== */
.shop-announcement {
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.1), rgba(0, 255, 136, 0.05));
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  padding: 15px 20px;
  margin: 0 20px 20px;
}

.announcement-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
  color: var(--mall-primary);
  margin-bottom: 10px;
}

.announcement-content {
  font-size: 14px;
  color: var(--mall-text-secondary);
  line-height: 1.6;
}

/* 滑入动画 */
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.slide-in-text {
  animation: slideIn 0.5s ease-out;
}

/* ==================== 优惠券区域 ==================== */
.coupon-section {
  margin: 0 20px 20px;
}

.section-header {
  margin-bottom: 15px;
}

.section-header h3 {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-header h3 i {
  color: var(--mall-primary);
}

.coupon-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.coupon-card {
  display: flex;
  background: var(--mall-bg-card);
  border: 1px dashed var(--mall-border);
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.coupon-card:hover {
  border-color: var(--mall-primary);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.2);
}

.coupon-card.received {
  border-style: solid;
  opacity: 0.7;
}

.coupon-left {
  width: 120px;
  background: linear-gradient(135deg, var(--mall-primary), var(--mall-primary-dark));
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 15px;
}

.coupon-left .amount {
  font-size: 28px;
  font-weight: bold;
  color: #fff;
}

.coupon-left .condition {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 4px;
}

.coupon-right {
  flex: 1;
  padding: 15px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.coupon-name {
  font-size: 16px;
  font-weight: bold;
  color: #fff;
}

.coupon-desc {
  font-size: 13px;
  color: var(--mall-text-muted);
  margin-top: 4px;
}

.coupon-validity {
  font-size: 12px;
  color: var(--mall-text-muted);
}

.receive-btn {
  align-self: flex-start;
}

/* ==================== 商品分类 ==================== */
.category-section {
  margin: 0 20px 20px;
}

.category-list {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  color: var(--mall-text-secondary);
}

.category-item i {
  font-size: 16px;
  color: var(--mall-primary);
}

.category-item .count {
  font-size: 12px;
  color: var(--mall-text-muted);
  margin-left: 4px;
}

.category-item:hover,
.category-item.active {
  border-color: var(--mall-primary);
  background: rgba(0, 212, 255, 0.1);
  color: #fff;
}

/* ==================== 商品列表 ==================== */
.product-section {
  margin: 0 20px 20px;
}

.sort-options {
  display: flex;
  gap: 15px;
}

.sort-options span {
  font-size: 14px;
  color: var(--mall-text-secondary);
  cursor: pointer;
  transition: color 0.3s ease;
}

.sort-options span:hover,
.sort-options span.active {
  color: var(--mall-primary);
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
  margin-top: 15px;
}

.product-card {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.product-card:hover {
  transform: translateY(-5px);
  border-color: var(--mall-primary);
  box-shadow: 0 8px 25px rgba(0, 212, 255, 0.2);
}

.product-image {
  position: relative;
  width: 100%;
  height: 220px;
  overflow: hidden;
  background: #0a1a2a;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.1);
}

.product-overlay {
  position: absolute;
  bottom: -50px;
  left: 0;
  right: 0;
  padding: 10px;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  transition: bottom 0.3s ease;
}

.product-card:hover .product-overlay {
  bottom: 0;
}

.product-info {
  padding: 15px;
}

.product-name {
  font-size: 14px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-desc {
  font-size: 12px;
  color: var(--mall-text-muted);
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-meta {
  display: flex;
  gap: 10px;
  font-size: 12px;
  color: var(--mall-text-muted);
  margin-bottom: 8px;
}

.product-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
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

/* ==================== 响应式 ==================== */
@media (max-width: 768px) {
  .shop-banner {
    height: 200px;
  }

  .banner-content {
    flex-direction: column;
    bottom: 20px;
    left: 20px;
  }

  .shop-logo {
    width: 80px;
    height: 80px;
  }

  .shop-name {
    font-size: 20px;
  }

  .shop-info-card {
    margin: -40px 15px 15px;
    padding: 15px;
  }

  .info-row {
    gap: 15px;
  }

  .action-buttons {
    flex-wrap: wrap;
  }

  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }

  .product-image {
    height: 150px;
  }
}
</style>

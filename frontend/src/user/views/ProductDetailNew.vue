<template>
  <div class="product-detail-page">
    <div class="container">
      <!-- 商品主体区域 - 上下布局 -->
      <div class="product-main-section">
        <!-- 上方：图片轮播 -->
        <div class="product-gallery">
          <!-- 主图 -->
          <div class="main-image-wrapper">
            <div class="main-image">
              <img :src="currentImage" :alt="product.name" />
            </div>
            <!-- 放大效果 -->
            <div class="image-zoom" ref="zoomArea" @mousemove="handleZoom" @mouseleave="hideZoom">
              <img :src="currentImage" :alt="product.name" ref="zoomImg" />
            </div>
          </div>

          <!-- 缩略图轮播 -->
          <div class="thumbnail-carousel">
            <el-carousel :interval="4000" type="card" height="100px" v-if="productImages.length > 4">
              <el-carousel-item v-for="(img, idx) in productImages" :key="idx">
                <div 
                  class="thumbnail-item" 
                  :class="{ active: currentImage === img }"
                  @click="currentImage = img"
                >
                  <img :src="img" :alt="product.name" />
                </div>
              </el-carousel-item>
            </el-carousel>
            <div class="thumbnail-list" v-else>
              <div 
                v-for="(img, idx) in productImages" 
                :key="idx"
                class="thumbnail-item"
                :class="{ active: currentImage === img }"
                @click="currentImage = img"
              >
                <img :src="img" :alt="product.name" />
              </div>
            </div>
          </div>
        </div>

        <!-- 下方：商品信息 -->
        <div class="product-info-panel">
          <h1 class="product-title">{{ product.name }}</h1>
          <p class="product-subtitle">{{ product.description }}</p>

          <!-- 价格 -->
          <div class="price-section">
            <div class="current-price">
              <span class="currency">¥</span>
              <span class="price-value">{{ formatPrice(product.price) }}</span>
            </div>
            <div class="original-price">¥{{ formatPrice(product.originalPrice || product.price * 1.2) }}</div>
            <el-tag v-if="product.discount" type="danger" size="small">省{{ formatPrice(product.originalPrice - product.price) }}</el-tag>
          </div>

          <!-- 促销信息 -->
          <div class="promotion-section" v-if="product.promotions?.length">
            <div class="promotion-label">促销</div>
            <div class="promotion-list">
              <div v-for="(promo, idx) in product.promotions" :key="idx" class="promotion-item">
                <i class="fas fa-ticket-alt"></i>
                <span>{{ promo }}</span>
              </div>
            </div>
          </div>

          <!-- 规格选择 -->
          <div class="specs-section">
            <!-- 颜色 -->
            <div class="spec-row" v-if="product.colors?.length">
              <span class="spec-label">颜色</span>
              <div class="spec-options">
                <div 
                  v-for="color in product.colors" 
                  :key="color"
                  class="spec-option"
                  :class="{ active: selectedSpecs.color === color }"
                  @click="selectedSpecs.color = color"
                >
                  {{ color }}
                </div>
              </div>
            </div>

            <!-- 版本/型号 -->
            <div class="spec-row" v-if="product.versions?.length">
              <span class="spec-label">版本</span>
              <div class="spec-options">
                <div 
                  v-for="version in product.versions" 
                  :key="version"
                  class="spec-option"
                  :class="{ active: selectedSpecs.version === version }"
                  @click="selectedSpecs.version = version"
                >
                  {{ version }}
                </div>
              </div>
            </div>

            <!-- 数量 -->
            <div class="spec-row">
              <span class="spec-label">数量</span>
              <div class="spec-options">
                <el-input-number 
                  v-model="quantity" 
                  :min="1" 
                  :max="product.stock || 999" 
                  size="small"
                  :disabled="product.stock === 0"
                />
                <span class="stock-status" v-if="product.stock">
                  <template v-if="product.stock > 100">现货</template>
                  <template v-else-if="product.stock > 0">仅剩{{ product.stock }}件</template>
                  <template v-else>缺货</template>
                </span>
              </div>
            </div>
          </div>

          <!-- 已选规格 -->
          <div class="selected-specs" v-if="hasSelectedSpecs">
            <span>已选：</span>
            <el-tag v-if="selectedSpecs.color" size="small" closable @close="selectedSpecs.color = ''">{{ selectedSpecs.color }}</el-tag>
            <el-tag v-if="selectedSpecs.version" size="small" closable @close="selectedSpecs.version = ''">{{ selectedSpecs.version }}</el-tag>
          </div>

          <!-- 服务承诺 -->
          <div class="service-promise">
            <div class="service-item">
              <i class="fas fa-check-circle"></i>
              <span>正品保障</span>
            </div>
            <div class="service-item">
              <i class="fas fa-truck"></i>
              <span>极速配送</span>
            </div>
            <div class="service-item">
              <i class="fas fa-undo"></i>
              <span>7 天无理由</span>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="action-buttons">
            <el-button
              type="warning"
              size="large"
              class="buy-now-btn"
              @click="buyNow"
              :disabled="product.stock === 0"
            >
              <i class="fas fa-credit-card"></i> 立即购买
            </el-button>
            <el-button
              type="danger"
              size="large"
              class="add-cart-btn"
              @click="addToCart"
              :disabled="product.stock === 0"
            >
              <i class="fas fa-shopping-cart"></i> 加入购物车
            </el-button>
          </div>

          <!-- 收藏和分享 -->
          <div class="social-actions">
            <el-button text @click="toggleFavorite">
              <i :class="isFavorited ? 'fas fa-star' : 'far fa-star'" :style="{ color: isFavorited ? '#ff4757' : '#999' }"></i>
              {{ isFavorited ? '已收藏' : '收藏' }}
            </el-button>
            <el-button text @click="shareProduct">
              <i class="fas fa-share-alt"></i> 分享
            </el-button>
          </div>
        </div>
      </div>

      <!-- 商品详情 Tabs -->
      <div class="product-detail-tabs">
        <el-tabs v-model="activeTab">
          <el-tab-pane name="detail">
            <template #label>
              <span><i class="fas fa-file-alt"></i> 商品详情</span>
            </template>
            <div class="tab-content detail-content">
              <div class="detail-images">
                <img v-for="(img, idx) in product.detailImages" :key="idx" :src="img" :alt="'详情图' + idx" />
              </div>
              <div class="detail-text" v-if="product.detailText" v-html="product.detailText"></div>
            </div>
          </el-tab-pane>

          <el-tab-pane name="specs">
            <template #label>
              <span><i class="fas fa-cog"></i> 规格参数</span>
            </template>
            <div class="tab-content specs-content">
              <table class="specs-table">
                <tr v-for="(value, key) in product.specifications" :key="key">
                  <td class="specs-label">{{ formatSpecLabel(key) }}</td>
                  <td>{{ value }}</td>
                </tr>
              </table>
            </div>
          </el-tab-pane>

          <el-tab-pane name="reviews">
            <template #label>
              <span><i class="fas fa-comments"></i> 商品评价 ({{ reviewCount }})</span>
            </template>
            <div class="tab-content reviews-content">
              <!-- 评价统计 -->
              <div class="review-summary">
                <div class="rating-overview">
                  <div class="rating-score">{{ averageRating }}</div>
                  <div class="rating-stars">
                    <el-rate v-model="averageRating" disabled />
                  </div>
                  <div class="rating-count">{{ reviewCount }}条评价</div>
                </div>
                <div class="rating-tags">
                  <el-tag v-for="tag in reviewTags" :key="tag.label" size="small">{{ tag.label }}({{ tag.count }})</el-tag>
                </div>
              </div>

              <!-- 评价列表 -->
              <div class="review-list">
                <div v-for="review in reviews" :key="review.id" class="review-item">
                  <div class="reviewer-info">
                    <el-avatar :src="review.user.avatar" :size="40" />
                    <div class="reviewer-name">{{ review.user.name }}</div>
                    <el-rate v-model="review.rating" disabled size="small" />
                    <div class="review-date">{{ formatDate(review.createdAt) }}</div>
                  </div>
                  <div class="review-content">
                    <p class="review-text">{{ review.content }}</p>
                    <div class="review-images" v-if="review.images?.length">
                      <el-image
                        v-for="(img, idx) in review.images"
                        :key="idx"
                        :src="img"
                        :preview-src-list="review.images"
                        :initial-index="idx"
                        fit="cover"
                        class="review-image"
                      />
                    </div>
                  </div>
                  <div class="selected-specs" v-if="review.specs">
                    <el-tag v-for="(val, key) in review.specs" :key="key" size="small">{{ val }}</el-tag>
                  </div>
                </div>
              </div>

              <!-- 发表评价按钮 -->
              <div class="write-review-btn" v-if="isPurchased">
                <el-button type="primary" @click="showReviewDialog = true">
                  <i class="fas fa-pen-to-square"></i> 发表评价
                </el-button>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 常用标签区域 -->
      <div class="common-tags-section">
        <div class="tag-title">
          <i class="fas fa-tags"></i>
          <span>常用标签</span>
        </div>
        <div class="tag-list">
          <div class="tag-item">
            <i class="fab fa-github"></i>
            <span>GitHub</span>
          </div>
          <div class="tag-item">
            <i class="fas fa-code-branch"></i>
            <span>Gitee</span>
          </div>
          <div class="tag-item">
            <i class="fab fa-vuejs"></i>
            <span>Vue3</span>
          </div>
          <div class="tag-item">
            <i class="fab fa-node"></i>
            <span>Node.js</span>
          </div>
          <div class="tag-item">
            <i class="fas fa-database"></i>
            <span>PostgreSQL</span>
          </div>
          <div class="tag-item">
            <i class="fab fa-docker"></i>
            <span>Docker</span>
          </div>
          <div class="tag-item">
            <i class="fas fa-shield-alt"></i>
            <span>Spring Security</span>
          </div>
          <div class="tag-item">
            <i class="fas fa-rocket"></i>
            <span>性能优化</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右下角悬浮购物车按钮 -->
    <div class="floating-cart-bar">
      <div class="floating-actions">
        <div class="floating-item" @click="toggleFavorite">
          <el-badge :value="favoriteCount" :hidden="favoriteCount === 0">
            <i :class="isFavorited ? 'fas fa-star' : 'far fa-star'" style="font-size: 24px;"></i>
          </el-badge>
          <span>收藏</span>
        </div>
        <div class="floating-item" @click="scrollToTop">
          <i class="fas fa-arrow-up" style="font-size: 24px;"></i>
          <span>顶部</span>
        </div>
        <div class="floating-item cart" @click="addToCart">
          <el-badge :value="cartCount" :hidden="cartCount === 0">
            <i class="fas fa-shopping-cart" style="font-size: 24px;"></i>
          </el-badge>
          <span>购物车</span>
        </div>
        <div class="floating-item buy" @click="buyNow">
          <i class="fas fa-credit-card" style="font-size: 24px;"></i>
          <span>立即购买</span>
        </div>
      </div>
    </div>

    <!-- 发表评价弹窗 -->
    <el-dialog v-model="showReviewDialog" title="发表评价" width="600px">
      <ReviewFormWithImages 
        :productId="product.id"
        @success="handleReviewSuccess"
        @close="showReviewDialog = false"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useCartStore } from '@user/stores/cart'
import ReviewFormWithImages from '@user/components/ReviewFormWithImages.vue'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

// 定义商品接口
interface Product {
  id: string | number
  name: string
  description: string
  price: number
  originalPrice?: number
  discount?: boolean
  stock: number
  category: string
  colors?: string[]
  versions?: string[]
  promotions?: string[]
  specifications: Record<string, string>
  images: string[]
  detailImages: string[]
  detailText?: string
}

// 定义评价用户接口
interface ReviewUser {
  name: string
  avatar: string
}

// 定义评价接口
interface Review {
  id: number
  user: ReviewUser
  rating: number
  content: string
  images?: string[]
  specs?: Record<string, string>
  createdAt: string
}

// 定义评价标签接口
interface ReviewTag {
  label: string
  count: number
}

// 定义选中规格接口
interface SelectedSpecs {
  color: string
  version: string
}

const productId = route.params.id as string

// 商品数据（模拟，实际从 API 获取）
const product = ref<Product>({
  id: productId,
  name: 'Apple iPhone 15 Pro Max 5G 手机',
  description: 'A17 Pro 芯片，钛金属设计，4800 万像素主摄，USB-C 接口',
  price: 9999,
  originalPrice: 10999,
  discount: true,
  stock: 168,
  category: '手机数码',
  colors: ['原色钛金属', '蓝色钛金属', '白色钛金属', '黑色钛金属'],
  versions: ['256GB', '512GB', '1TB'],
  promotions: ['限时立减 1000 元', '晒单送无线充电器', '12 期免息'],
  specifications: {
    brand: 'Apple',
    model: 'iPhone 15 Pro Max',
    screen: '6.7 英寸 OLED',
    processor: 'A17 Pro',
    camera: '4800 万主摄',
    battery: '4422mAh',
    os: 'iOS 17',
    network: '5G'
  },
  images: [
    'https://via.placeholder.com/800x800/1a2a4a/00d4ff?text=iPhone1',
    'https://via.placeholder.com/800x800/1a2a4a/00d4ff?text=iPhone2',
    'https://via.placeholder.com/800x800/1a2a4a/00d4ff?text=iPhone3',
    'https://via.placeholder.com/800x800/1a2a4a/00d4ff?text=iPhone4',
    'https://via.placeholder.com/800x800/1a2a4a/00d4ff?text=iPhone5'
  ],
  detailImages: [
    'https://via.placeholder.com/800x600/1a2a4a/00d4ff?text=Detail1',
    'https://via.placeholder.com/800x600/1a2a4a/00d4ff?text=Detail2',
    'https://via.placeholder.com/800x600/1a2a4a/00d4ff?text=Detail3'
  ],
  detailText: '<p>这里是商品详细介绍...</p>'
})

// 当前图片
const currentImage = ref<string>(product.value.images[0])
const productImages = computed<string[]>(() => product.value.images)

// 选中的规格
const selectedSpecs = reactive<SelectedSpecs>({
  color: '',
  version: ''
})

const hasSelectedSpecs = computed<boolean>(() => selectedSpecs.color || selectedSpecs.version)

// 数量
const quantity = ref<number>(1)

// 收藏状态
const isFavorited = ref<boolean>(false)
const favoriteCount = ref<number>(0)

// 购物车数量
const cartCount = ref<number>(0)

// 是否购买过
const isPurchased = ref<boolean>(false)

// Tab
const activeTab = ref<string>('detail')

// 评价相关
const reviewCount = ref<number>(128)
const averageRating = ref<number>(4.8)
const reviewTags = ref<ReviewTag[]>([
  { label: '物流快', count: 86 },
  { label: '质量好', count: 72 },
  { label: '性价比高', count: 45 },
  { label: '包装好', count: 38 }
])

const reviews = ref<Review[]>([
  {
    id: 1,
    user: { name: '张***3', avatar: '' },
    rating: 5,
    content: '手机很好用，运行流畅，拍照效果也很棒！物流很快，包装完好。',
    images: [
      'https://via.placeholder.com/100x100/1a2a4a/00d4ff?text=review1',
      'https://via.placeholder.com/100x100/1a2a4a/00d4ff?text=review2'
    ],
    specs: { color: '原色钛金属', version: '256GB' },
    createdAt: '2024-01-15'
  },
  {
    id: 2,
    user: { name: '李***8', avatar: '' },
    rating: 5,
    content: '第一次用苹果手机，系统确实很流畅，做工精细，值得购买。',
    images: [],
    specs: { color: '蓝色钛金属', version: '512GB' },
    createdAt: '2024-01-12'
  }
])

// 评价弹窗
const showReviewDialog = ref<boolean>(false)

// 格式化价格
const formatPrice = (price: number): string => {
  return price.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

// 格式化规格标签
const formatSpecLabel = (key: string): string => {
  const labels: Record<string, string> = {
    brand: '品牌',
    model: '型号',
    screen: '屏幕',
    processor: '处理器',
    camera: '摄像头',
    battery: '电池',
    os: '系统',
    network: '网络'
  }
  return labels[key] || key
}

// 格式化日期
const formatDate = (date: string): string => {
  return date
}

// 放大效果
const zoomArea = ref<HTMLElement | null>(null)
const zoomImg = ref<HTMLImageElement | null>(null)

const handleZoom = (e: MouseEvent): void => {
  if (!zoomArea.value || !zoomImg.value) return
  
  const rect = zoomArea.value.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top
  
  const percentX = x / rect.width
  const percentY = y / rect.height
  
  zoomImg.value.style.transformOrigin = `${percentX * 100}% ${percentY * 100}%`
  zoomImg.value.style.transform = 'scale(2)'
}

const hideZoom = (): void => {
  if (zoomImg.value) {
    zoomImg.value.style.transform = 'scale(1)'
    zoomImg.value.style.transformOrigin = 'center center'
  }
}

// 加入购物车
const addToCart = (): void => {
  if (!selectedSpecs.color && product.value.colors?.length) {
    ElMessage.warning('请选择颜色')
    return
  }
  
  // 添加到购物车 store
  cartStore.addToCart({
    id: Number(product.value.id),
    name: product.value.name,
    price: product.value.price,
    quantity: quantity.value,
    image: product.value.images[0],
    selected: true,
    specs: { ...selectedSpecs }
  })
  
  ElMessage.success('已加入购物车')
  cartCount.value++
}

// 立即购买
const buyNow = (): void => {
  if (!selectedSpecs.color && product.value.colors?.length) {
    ElMessage.warning('请选择颜色')
    return
  }
  // 跳转到支付页面
  router.push({
    path: '/payment',
    query: {
      productName: product.value.name,
      quantity: quantity.value,
      amount: product.value.price * quantity.value
    }
  })
}

// 收藏
const toggleFavorite = (): void => {
  isFavorited.value = !isFavorited.value
  favoriteCount.value += isFavorited.value ? 1 : -1
  ElMessage.success(isFavorited.value ? '已收藏' : '已取消收藏')
}

// 分享
const shareProduct = (): void => {
  ElMessage.success('分享链接已复制')
}

// 滚动到顶部
const scrollToTop = (): void => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 评价成功
const handleReviewSuccess = (): void => {
  ElMessage.success('评价提交成功')
  showReviewDialog.value = false
}

// 加载商品数据
onMounted(async (): Promise<void> => {
  // TODO: 从 API 加载商品详情
  // const res = await fetch(`/api/product/${productId}`)
  // product.value = await res.json()
})
</script>

<style scoped>
.product-detail-page {
  min-height: 100vh;
  background: linear-gradient(180deg, rgba(0,212,255,0.15) 0%, rgba(10,14,26,0.8) 100%);
  padding-bottom: 80px;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 面包屑导航 */
.breadcrumb-bar {
  background: rgba(26,31,58,0.8);
  padding: 12px 0;
  margin-bottom: 20px;
  border-bottom: 1px solid rgba(0,212,255,0.2);
}

/* 商品主体区域 */
.product-main-section {
  display: grid;
  grid-template-columns: 500px 1fr;
  gap: 30px;
  background: rgba(26,31,58,0.8);
  padding: 30px;
  border-radius: 12px;
  margin-bottom: 20px;
  border: 1px solid rgba(0,212,255,0.2);
}

/* 主体区域 - 左右布局：左侧图片，右侧信息 */
.product-main-section {
  display: grid;
  grid-template-columns: 500px 1fr;
  gap: 30px;
  background: rgba(26,31,58,0.8);
  padding: 30px;
  border-radius: 12px;
  margin-bottom: 20px;
  border: 1px solid rgba(0,212,255,0.2);
}

/* 图片区 - 左侧：大图在上，缩略图在下 */
.product-gallery {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
}

.main-image-wrapper {
  position: relative;
  width: 100%;
  height: 500px;
  overflow: hidden;
  border-radius: 8px;
  background: rgba(0,0,0,0.2);
  border: 1px solid rgba(0,212,255,0.1);
}

.main-image {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.main-image img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.image-zoom {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  cursor: crosshair;
  opacity: 0;
  transition: opacity 0.3s;
  pointer-events: none;
}

.main-image-wrapper:hover .image-zoom {
  opacity: 1;
  pointer-events: auto;
}

.image-zoom img {
  width: 200%;
  height: 200%;
  transition: transform 0.1s;
}

/* 缩略图 */
.thumbnail-carousel {
  margin-top: 16px;
}

.thumbnail-list {
  display: flex;
  gap: 12px;
}

.thumbnail-item {
  width: 80px;
  height: 80px;
  border: 2px solid rgba(255,255,255,0.1);
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.thumbnail-item:hover,
.thumbnail-item.active {
  border-color: #00d4ff;
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 商品信息区 */
.product-info-panel {
  width: 100%;
}

.product-title {
  font-size: 22px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 8px;
  line-height: 1.4;
}

.product-subtitle {
  font-size: 14px;
  color: #aaa;
  margin-bottom: 20px;
  line-height: 1.6;
}

/* 价格 */
.price-section {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 20px;
  padding: 16px 0;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.current-price {
  display: flex;
  align-items: baseline;
}

.current-price .currency {
  font-size: 18px;
  color: #00ff88;
  font-weight: 600;
}

.current-price .price-value {
  font-size: 32px;
  color: #00ff88;
  font-weight: 700;
}

.original-price {
  font-size: 14px;
  color: #666;
  text-decoration: line-through;
}

/* 促销信息 */
.promotion-section {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 20px;
}

.promotion-label {
  font-size: 14px;
  color: #aaa;
  min-width: 50px;
}

.promotion-list {
  flex: 1;
}

.promotion-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #00d4ff;
  margin-bottom: 6px;
}

.promotion-item .el-icon {
  font-size: 16px;
}

/* 规格选择 */
.specs-section {
  margin-bottom: 20px;
}

.spec-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.spec-label {
  font-size: 14px;
  color: #aaa;
  min-width: 50px;
}

.spec-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.spec-option {
  padding: 8px 16px;
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 4px;
  font-size: 13px;
  color: #ccc;
  cursor: pointer;
  transition: all 0.3s;
  background: rgba(0,0,0,0.2);
}

.spec-option:hover {
  border-color: #00d4ff;
  color: #00d4ff;
}

.spec-option.active {
  border-color: #00d4ff;
  color: #00d4ff;
  background: rgba(0,212,255,0.1);
}

.stock-status {
  font-size: 13px;
  color: #00ff88;
}

/* 已选规格 */
.selected-specs {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: rgba(0,0,0,0.2);
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 13px;
  color: #aaa;
}

/* 服务承诺 */
.service-promise {
  display: flex;
  gap: 20px;
  padding: 16px 0;
  margin-bottom: 20px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.service-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #aaa;
}

.service-item .el-icon {
  color: #00d4ff;
  font-size: 18px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.action-buttons .el-button {
  flex: 1;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
}

.buy-now-btn {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  color: #000;
}

.add-cart-btn {
  background: linear-gradient(135deg, #00d4ff, #0099cc);
  border: none;
  color: #000;
}

/* 社交操作 */
.social-actions {
  display: flex;
  gap: 16px;
}

/* 详情 Tabs */
.product-detail-tabs {
  background: rgba(26,31,58,0.8);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid rgba(0,212,255,0.2);
}

.product-detail-tabs :deep(.el-tabs__header) {
  display: flex !important;
  flex-direction: row !important;
  margin-bottom: 20px !important;
  border-bottom: 1px solid rgba(0,212,255,0.2) !important;
  padding-bottom: 15px !important;
}

.product-detail-tabs :deep(.el-tabs__nav-wrap) {
  display: flex !important;
}

.product-detail-tabs :deep(.el-tabs__item) {
  font-size: 16px;
  padding: 15px 25px !important;
  color: #aaa;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(0,212,255,0.1);
  border-radius: 8px;
  margin-right: 10px;
  display: inline-flex !important;
  align-items: center;
  height: auto !important;
  line-height: 1.4 !important;
}

.product-detail-tabs :deep(.el-tabs__item.is-active) {
  color: #fff;
  font-weight: 600;
  background: linear-gradient(135deg, rgba(0,212,255,0.3), rgba(0,255,136,0.2));
  border-color: #00d4ff;
}

.product-detail-tabs :deep(.el-tabs__active-bar) {
  background: linear-gradient(90deg, #00d4ff, #00ff88);
  height: 3px;
}

.product-detail-tabs :deep(.el-tabs__content) {
  display: block !important;
  margin-top: 10px;
}

.tab-content {
  padding: 30px 20px;
  min-height: 400px;
}

/* 常用标签区域 */
.common-tags-section {
  background: rgba(26,31,58,0.8);
  border-radius: 12px;
  padding: 20px;
  margin-top: 20px;
  margin-bottom: 20px;
  border: 1px solid rgba(0,212,255,0.2);
}

.tag-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(0,212,255,0.2);
}

.tag-title i {
  color: #00d4ff;
  font-size: 20px;
}

.tag-title span {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.tag-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: rgba(0,212,255,0.1);
  border: 1px solid rgba(0,212,255,0.3);
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.tag-item:hover {
  background: rgba(0,212,255,0.2);
  border-color: #00d4ff;
  box-shadow: 0 0 15px rgba(0,212,255,0.3);
  transform: translateY(-2px);
}

.tag-item i {
  color: #00d4ff;
  font-size: 18px;
}

.tag-item span {
  font-size: 14px;
  color: #fff;
  font-weight: 500;
}

/* 商品详情 */
.detail-images {
  max-width: 800px;
  margin: 0 auto;
}

.detail-images img {
  width: 100%;
  display: block;
  margin-bottom: 20px;
}

/* 规格参数表 */
.specs-table {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  border-collapse: collapse;
}

.specs-table td {
  padding: 12px 16px;
  border-bottom: 1px solid rgba(255,255,255,0.05);
  font-size: 14px;
  color: #ccc;
}

.specs-table .specs-label {
  width: 150px;
  color: #aaa;
  background: rgba(0,0,0,0.2);
}

/* 评价统计 */
.review-summary {
  display: flex;
  align-items: center;
  gap: 30px;
  padding: 30px;
  background: rgba(0,0,0,0.2);
  border-radius: 8px;
  margin-bottom: 30px;
  border: 1px solid rgba(0,212,255,0.1);
}

.rating-overview {
  text-align: center;
}

.rating-score {
  font-size: 48px;
  font-weight: 700;
  color: #00ff88;
}

.rating-count {
  font-size: 13px;
  color: #aaa;
  margin-top: 8px;
}

.rating-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.review-list {
  max-width: 800px;
  margin: 0 auto;
}

.review-item {
  padding: 20px 0;
  border-bottom: 1px solid rgba(255,255,255,0.05);
}

.reviewer-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.reviewer-name {
  font-size: 14px;
  color: #ccc;
  font-weight: 600;
}

.review-content {
  margin-bottom: 12px;
}

.review-text {
  font-size: 14px;
  color: #ccc;
  line-height: 1.6;
  margin-bottom: 12px;
}

.review-images {
  display: flex;
  gap: 8px;
}

.review-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  cursor: pointer;
}

.write-review-btn {
  text-align: center;
  padding: 30px;
}

/* 悬浮购物车条 */
.floating-cart-bar {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background: rgba(26,31,58,0.9);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,212,255,0.3);
  border: 1px solid rgba(0,212,255,0.2);
  z-index: 1000;
}

.floating-actions {
  display: flex;
}

.floating-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
  transition: all 0.3s;
  color: #aaa;
  font-size: 12px;
  gap: 6px;
  border-right: 1px solid rgba(255,255,255,0.1);
}

.floating-item:last-child {
  border-right: none;
}

.floating-item:hover {
  background: rgba(0,212,255,0.1);
}

.floating-item.cart,
.floating-item.buy {
  color: #00d4ff;
}

.floating-item.buy {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  color: #000;
}

.floating-item.buy:hover {
  background: linear-gradient(135deg, #00ff88, #00d4ff);
}
</style>

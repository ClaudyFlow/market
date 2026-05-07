<template>
  <div class="product-detail-page">
    <div class="container">
      <!-- 主体布局：图片 + 信息 -->
      <div class="product-main-section">
        <ProductGallery 
          :images="productImages" 
          :product-name="product.name" 
          v-model:current-image="currentImage" 
        />
        
        <ProductInfoPanel 
          :product="product" 
          v-model:selected-specs="selectedSpecs" 
          v-model:quantity="quantity"
          :format-price="formatPrice"
          :selected-specs-text="selectedSpecsText"
          :is-favorited="isFavorited"
          :favorite-count="favoriteCount"
          @add-to-cart="addToCart"
          @buy-now="buyNow"
          @toggle-favorite="toggleFavorite"
          @share="shareProduct"
        />
      </div>

      <!-- Tabs 区域 -->
      <el-tabs v-model="activeTab" class="product-detail-tabs">
        <el-tab-pane label="商品详情" name="detail">
          <ProductDetailTab 
            :detail-images="product.detailImages" 
            :detail-text="product.detailText" 
          />
        </el-tab-pane>
        
        <el-tab-pane label="规格参数" name="specs">
          <ProductSpecsTab 
            :specifications="product.specifications" 
            :format-spec-label="formatSpecLabel" 
          />
        </el-tab-pane>
        
        <el-tab-pane name="reviews">
          <template #label>
            <span>商品评价</span>
            <el-badge :value="reviewCount" class="review-badge" />
          </template>
          
          <ProductReviewsTab 
            :review-count="reviewCount"
            :average-rating="averageRating"
            :review-tags="reviewTags"
            :reviews="reviews"
            @open-review="showReviewDialog = true"
          />
        </el-tab-pane>
      </el-tabs>

      <!-- 常用标签区域 -->
      <CommonTagsSection />

      <!-- 悬浮操作栏 -->
      <FloatingActionBar 
        :is-favorited="isFavorited"
        :favorite-count="favoriteCount"
        :cart-count="cartCount"
        @toggle-favorite="toggleFavorite"
        @scroll-to-top="scrollToTop"
        @add-to-cart="addToCart"
        @buy-now="buyNow"
      />
    </div>

    <!-- 评价弹窗 -->
    <el-dialog 
      v-model="showReviewDialog" 
      title="发表评价" 
      width="600px"
      :close-on-click-modal="false"
    >
      <ReviewFormWithImages 
        :product-id="product.id" 
        @success="handleReviewSuccess" 
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ReviewFormWithImages } from '@user/components'
import {
  useProductDetail,
  useProductSpecs,
  useProductReviews,
  useProductActions
} from '@user/composables'
import {
  ProductGallery,
  ProductInfoPanel,
  ProductDetailTab,
  ProductSpecsTab,
  ProductReviewsTab,
  CommonTagsSection,
  FloatingActionBar
} from '@user/components/product'

// 获取路由参数
const route = useRoute()
const productId = route.params.id as string

// 使用 composables
const { 
  product, 
  currentImage, 
  productImages, 
  formatPrice, 
  formatSpecLabel, 
  loadProduct 
} = useProductDetail(productId)

const { 
  selectedSpecs, 
  quantity, 
  selectedSpecsText 
} = useProductSpecs(product)

const { 
  reviewCount, 
  averageRating, 
  reviewTags, 
  reviews, 
  showReviewDialog, 
  handleReviewSuccess 
} = useProductReviews(productId)

const { 
  isFavorited, 
  favoriteCount, 
  cartCount, 
  addToCart, 
  buyNow, 
  toggleFavorite, 
  shareProduct, 
  scrollToTop 
} = useProductActions(product, selectedSpecs, quantity)

// 当前激活 Tab
const activeTab = ref('detail')

// 加载商品数据
onMounted(() => {
  loadProduct()
})
</script>

<style scoped>
.product-detail-page {
  min-height: 100vh;
  padding: 40px 0;
  background: #f5f7fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.product-main-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  margin-bottom: 40px;
  padding: 30px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.product-detail-tabs {
  margin-bottom: 40px;
  padding: 30px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.review-badge {
  margin-left: 8px;
}

@media (max-width: 980px) {
  .product-main-section {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .product-detail-page {
    padding: 20px 0;
  }

  .container {
    padding: 0 16px;
  }

  .product-main-section,
  .product-detail-tabs {
    padding: 20px;
  }
}
</style>

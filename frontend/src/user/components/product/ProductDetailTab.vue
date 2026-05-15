<template>
  <div class="product-detail-tab">
    <!-- 详情图片 -->
    <div class="detail-images" v-if="detailImages?.length">
      <img 
        v-for="(img, index) in detailImages" 
        :key="index" 
        :src="img" 
        :alt="`商品详情 ${index + 1}`"
        loading="lazy"
      />
    </div>
    
    <!-- 详情文字（HTML 内容） -->
    <div 
      class="detail-text" 
      v-if="detailText"
      v-html="detailText"
    ></div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  detailImages?: string[]
  detailText?: string
}

defineProps<Props>()
</script>

<style scoped>
.product-detail-tab {
  padding: 20px 0;
}

.detail-images {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-images img {
  width: 100%;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.detail-text {
  font-size: 15px;
  line-height: 1.8;
  color: #333;
}

.detail-text :deep(h3) {
  margin: 24px 0 12px;
  font-size: 18px;
  color: #1a1a1a;
}

.detail-text :deep(p) {
  margin: 12px 0;
}
</style>

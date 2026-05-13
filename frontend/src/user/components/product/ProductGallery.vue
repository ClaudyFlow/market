<template>
  <div class="product-gallery">
    <!-- 主图显示区 -->
    <div 
      class="main-image-wrapper" 
      @mousemove="handleZoom" 
      @mouseleave="hideZoom"
      ref="zoomArea"
    >
      <img :src="currentImage" :alt="productName" class="main-image" />
      <!-- 缩放图层 -->
      <div class="image-zoom" v-show="zoomImg">
        <img :src="currentImage" ref="zoomImg" class="zoom-image" />
      </div>
      <!-- 图片计数器 -->
      <div class="image-counter">
        {{ currentIndex + 1 }} / {{ images.length }}
      </div>
    </div>

    <!-- 缩略图列表 -->
    <div class="thumbnail-list">
      <div
        v-for="(img, index) in images"
        :key="index"
        class="thumbnail-item"
        :class="{ active: index === currentIndex }"
        @click="selectImage(index)"
      >
        <img :src="img" :alt="`${productName} - 图片${index + 1}`" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useImageZoom } from '@user/composables'

interface Props {
  images: string[]
  productName: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:currentImage': [image: string]
}>()

const currentIndex = ref(0)
const { zoomArea, zoomImg, handleZoom, hideZoom } = useImageZoom()

const currentImage = ref(props.images[0])

// 监听图片列表变化
watch(() => props.images, (newImages) => {
  if (newImages.length > 0) {
    currentImage.value = newImages[0]
    currentIndex.value = 0
  }
})

// 选择图片
const selectImage = (index: number) => {
  currentIndex.value = index
  currentImage.value = props.images[index]
  emit('update:currentImage', props.images[index])
}

defineExpose({
  currentImage,
  currentIndex
})
</script>

<style scoped>
.product-gallery {
  position: relative;
}

.main-image-wrapper {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  border-radius: 16px;
  overflow: hidden;
  background: #f5f5f5;
  cursor: crosshair;
}

.main-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.image-zoom {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  pointer-events: none;
}

.zoom-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.2s ease;
  transform-origin: center center;
}

.image-counter {
  position: absolute;
  bottom: 12px;
  right: 12px;
  padding: 6px 12px;
  border-radius: 20px;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  font-size: 12px;
}

.thumbnail-list {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  overflow-x: auto;
  padding-bottom: 4px;
}

.thumbnail-item {
  flex-shrink: 0;
  width: 64px;
  height: 64px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.2s;
}

.thumbnail-item:hover {
  border-color: var(--mall-primary, #00d4ff);
}

.thumbnail-item.active {
  border-color: var(--mall-primary, #00d4ff);
  box-shadow: 0 0 0 2px rgba(0, 212, 255, 0.2);
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>

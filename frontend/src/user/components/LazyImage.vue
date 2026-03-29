<template>
  <div class="lazy-image" :style="containerStyle">
    <!-- 占位图 -->
    <div class="lazy-placeholder" v-if="!loaded && !error">
      <el-icon :size="30" class="loading-icon"><Picture /></el-icon>
      <span class="loading-text">加载中...</span>
    </div>

    <!-- 加载失败 -->
    <div class="lazy-error" v-else-if="error">
      <el-icon :size="30" class="error-icon"><PictureFilled /></el-icon>
      <span class="error-text">加载失败</span>
    </div>

    <!-- 实际图片 -->
    <img
      v-show="loaded"
      ref="imgRef"
      :src="displaySrc"
      :alt="alt"
      :class="['lazy-img', { 'blur-up': useBlurUp }]"
      @load="handleLoad"
      @error="handleError"
    />

    <!-- 加载进度 -->
    <div class="lazy-progress" v-if="showProgress && !loaded && !error">
      <el-progress :percentage="progress" :stroke-width="2" :show-text="false" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { Picture, PictureFilled } from '@element-plus/icons-vue'

const props = defineProps({
  src: {
    type: String,
    required: true
  },
  thumbnail: {
    type: String,
    default: ''
  },
  alt: {
    type: String,
    default: ''
  },
  width: {
    type: [String, Number],
    default: '100%'
  },
  height: {
    type: [String, Number],
    default: 'auto'
  },
  fit: {
    type: String,
    default: 'cover',
    validator: (val: string) => ['cover', 'contain', 'fill', 'none', 'scale-down'].includes(val)
  },
  useBlurUp: {
    type: Boolean,
    default: true // 使用模糊渐变效果
  },
  showProgress: {
    type: Boolean,
    default: false
  },
  threshold: {
    type: Number,
    default: 50 // 提前 50px 开始加载
  }
})

const imgRef = ref<HTMLImageElement | null>(null)
const loaded = ref(false)
const error = ref(false)
const progress = ref(0)
const useThumbnail = ref(false)

// 容器样式
const containerStyle = computed(() => ({
  width: typeof props.width === 'number' ? props.width + 'px' : props.width,
  height: typeof props.height === 'number' ? props.height + 'px' : props.height,
  position: 'relative' as const
}))

// 显示的图片源（先显示缩略图）
const displaySrc = computed(() => {
  if (useThumbnail.value && props.thumbnail) {
    return props.thumbnail
  }
  return props.src
})

// 处理图片加载完成
const handleLoad = () => {
  progress.value = 100
  
  if (useThumbnail.value && props.thumbnail) {
    // 缩略图加载完成后，加载原图
    loadOriginalImage()
  } else {
    loaded.value = true
  }
}

// 加载原图
const loadOriginalImage = () => {
  const img = new Image()
  img.onload = () => {
    loaded.value = true
  }
  img.src = props.src
}

// 处理加载失败
const handleError = () => {
  error.value = true
  progress.value = 0
}

// 检查元素是否在视口内
const checkInView = () => {
  if (!imgRef.value) return false
  
  const rect = imgRef.value.getBoundingClientRect()
  const viewHeight = window.innerHeight
  
  return rect.top < viewHeight + props.threshold && rect.bottom > -props.threshold
}

// 监听滚动
const setupObserver = () => {
  if ('IntersectionObserver' in window) {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          startLoad()
          observer.disconnect()
        }
      })
    }, {
      rootMargin: `${props.threshold}px`
    })
    
    if (imgRef.value) {
      observer.observe(imgRef.value)
    }
    
    return observer
  } else {
    // 降级处理
    window.addEventListener('scroll', onScroll)
    window.addEventListener('resize', onScroll)
    onScroll()
    return null
  }
}

const startLoad = () => {
  if (props.thumbnail) {
    useThumbnail.value = true
  }
}

const onScroll = () => {
  if (checkInView() && !loaded.value && !error.value) {
    startLoad()
  }
}

onMounted(() => {
  setupObserver()
})

// 监听 src 变化
watch(() => props.src, () => {
  loaded.value = false
  error.value = false
  progress.value = 0
  useThumbnail.value = false
})
</script>

<style scoped>
.lazy-image {
  display: inline-block;
  overflow: hidden;
  background: rgba(26, 31, 58, 0.5);
}

.lazy-placeholder,
.lazy-error {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.lazy-placeholder {
  background: rgba(0, 212, 255, 0.05);
}

.loading-icon {
  color: var(--mall-primary);
  animation: pulse 1.5s ease-in-out infinite;
}

.loading-text {
  font-size: 12px;
  color: var(--mall-text-secondary);
}

.lazy-error {
  background: rgba(255, 0, 0, 0.05);
}

.error-icon {
  color: var(--mall-text-muted);
}

.error-text {
  font-size: 12px;
  color: var(--mall-text-muted);
}

.lazy-img {
  width: 100%;
  height: 100%;
  object-fit: var(--image-fit, cover);
  transition: filter 0.3s ease;
}

.lazy-img.blur-up {
  filter: blur(10px);
}

.lazy-img.blur-up.loaded {
  filter: blur(0);
}

.lazy-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 4px 8px;
  background: rgba(0, 0, 0, 0.5);
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.5;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.1);
  }
}
</style>

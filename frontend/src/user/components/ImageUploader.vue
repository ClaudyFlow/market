<template>
  <div class="image-uploader">
    <!-- 预览区域 -->
    <div class="preview-area" v-if="imageUrl">
      <img :src="imageUrl" alt="预览" class="preview-image" />
      <div class="image-info" v-if="showInfo">
        <span class="size">{{ fileSize }}</span>
        <span class="dimension" v-if="imageDimension">{{ imageDimension.width }}×{{ imageDimension.height }}</span>
      </div>
      <button class="remove-btn" @click="removeImage" type="button">
        <el-icon><Close /></el-icon>
      </button>
    </div>

    <!-- 上传区域 -->
    <div class="upload-area" v-else @click="triggerUpload" @dragover.prevent @drop.prevent="handleDrop">
      <input 
        ref="fileInput" 
        type="file" 
        accept="image/*" 
        @change="handleFileChange"
        class="file-input"
      />
      <div class="upload-placeholder">
        <el-icon :size="40"><Plus /></el-icon>
        <p>{{ placeholderText }}</p>
        <span class="tip">支持 jpg/png/webp，自动压缩</span>
      </div>
    </div>

    <!-- 上传进度 -->
    <div class="upload-progress" v-if="uploading">
      <el-progress 
        :percentage="progress" 
        :stroke-width="3"
        :status="progress >= 100 ? 'success' : undefined"
      >
        <template v-if="compressing">
          <span>压缩中... {{ compressionRate }}%</span>
        </template>
      </el-progress>
      <div class="compress-info" v-if="compressing && originalSize > 0">
        <span>{{ formatSize(originalSize) }} → {{ formatSize(compressedSize) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { Plus, Close } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { compressImage, getBase64Size, formatFileSize } from '@user/utils/imageCompress'
import { base64ToFile } from '@user/utils/imageDecode'
import { uploadImage } from '@user/api/upload'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholderText: {
    type: String,
    default: '点击上传图片'
  },
  maxSize: {
    type: Number,
    default: 2 // MB
  },
  compressQuality: {
    type: Number,
    default: 0.8 // 压缩质量 0.1-1.0
  },
  maxWidth: {
    type: Number,
    default: 1920 // 最大宽度
  },
  maxHeight: {
    type: Number,
    default: 1920 // 最大高度
  },
  showInfo: {
    type: Boolean,
    default: true // 显示图片信息
  },
  autoUpload: {
    type: Boolean,
    default: true // 自动上传到后端
  }
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'upload:start', file: File): void
  (e: 'upload:success', base64: string): void
  (e: 'upload:error', error: string): void
}>()

const fileInput = ref<HTMLInputElement | null>(null)
const imageUrl = ref(props.modelValue)
const uploading = ref(false)
const compressing = ref(false)
const progress = ref(0)
const compressionRate = ref(0)
const originalSize = ref(0)
const compressedSize = ref(0)
const imageDimension = ref<{ width: number, height: number } | null>(null)

// 格式化文件大小
const formatSize = (bytes: number) => formatFileSize(bytes)

// 计算当前图片大小
const fileSize = computed(() => {
  if (!imageUrl.value) return '0 B'
  return formatSize(getBase64Size(imageUrl.value))
})

// 触发文件选择
const triggerUpload = () => {
  fileInput.value?.click()
}

// 处理文件选择
const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    processFile(file)
  }
  target.value = ''
}

// 处理拖拽上传
const handleDrop = (event: DragEvent) => {
  const file = event.dataTransfer?.files[0]
  if (file && file.type.startsWith('image/')) {
    processFile(file)
  }
}

// 处理文件（压缩 + 上传）
const processFile = async (file: File) => {
  // 验证类型
  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    return
  }

  uploading.value = true
  compressing.value = true
  progress.value = 0
  originalSize.value = file.size
  emit('upload:start', file)

  try {
    // 压缩图片
    const compressedBase64 = await compressImage(file, {
      quality: props.compressQuality,
      maxWidth: props.maxWidth,
      maxHeight: props.maxHeight,
      format: file.type === 'image/png' ? 'image/png' : 'image/jpeg'
    })

    compressing.value = false
    compressedSize.value = getBase64Size(compressedBase64)
    
    // 计算压缩率
    compressionRate.value = Math.round((1 - compressedSize.value / originalSize.value) * 100)
    progress.value = 100

    // 获取图片尺寸
    getImageDimension(compressedBase64)

    // 保存结果
    imageUrl.value = compressedBase64
    emit('update:modelValue', compressedBase64)
    emit('upload:success', compressedBase64)

    ElMessage.success(`压缩成功！${compressionRate.value}% (${formatSize(originalSize.value)} → ${formatSize(compressedSize.value)})`)

    // 自动上传到后端
    if (props.autoUpload) {
      await uploadToBackend(compressedBase64)
    }

  } catch (error: any) {
    ElMessage.error('处理失败：' + error.message)
    emit('upload:error', error.message)
  } finally {
    uploading.value = false
    compressing.value = false
    progress.value = 0
  }
}

// 获取图片尺寸
const getImageDimension = (base64: string) => {
  const img = new Image()
  img.onload = () => {
    imageDimension.value = { width: img.width, height: img.height }
  }
  img.src = base64
}

// 上传到后端（只存储，不处理）
const uploadToBackend = async (base64: string) => {
  try {
    const filename = `image_${Date.now()}.jpg`
    const file = base64ToFile(base64, filename)
    await uploadImage(file)
  } catch (error: any) {
    // 上传失败不影响使用，只是不能存储到服务器
    console.warn('上传到后端失败，但图片仍可使用:', error.message)
  }
}

// 移除图片
const removeImage = () => {
  imageUrl.value = ''
  imageDimension.value = null
  emit('update:modelValue', '')
}

// 监听外部值变化
watch(() => props.modelValue, (val) => {
  imageUrl.value = val
  if (val) {
    getImageDimension(val)
  }
})
</script>

<style scoped>
.image-uploader {
  width: 100%;
}

/* 预览区域 */
.preview-area {
  position: relative;
  width: 200px;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid var(--mall-border-light);
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.7);
  padding: 4px 8px;
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: #fff;
}

.remove-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.remove-btn:hover {
  background: rgba(255, 0, 0, 0.8);
  transform: scale(1.1);
}

/* 上传区域 */
.upload-area {
  width: 200px;
  height: 200px;
  border: 2px dashed var(--mall-border-light);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  background: rgba(26, 31, 58, 0.5);
}

.upload-area:hover {
  border-color: var(--mall-primary);
  background: rgba(0, 212, 255, 0.1);
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--mall-text-secondary);
}

.upload-placeholder .el-icon {
  color: var(--mall-primary);
  margin-bottom: 12px;
}

.upload-placeholder p {
  margin: 0;
  font-size: 14px;
}

.upload-placeholder .tip {
  font-size: 12px;
  color: var(--mall-text-muted);
  margin-top: 8px;
}

.file-input {
  display: none;
}

/* 上传进度 */
.upload-progress {
  margin-top: 12px;
  width: 200px;
}

.compress-info {
  margin-top: 8px;
  font-size: 12px;
  color: var(--mall-text-secondary);
  text-align: center;
}
</style>

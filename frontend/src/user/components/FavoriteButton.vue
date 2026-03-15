<template>
  <el-button
    :type="isFavorited ? 'warning' : 'default'"
    :size="size"
    :circle="circle"
    class="favorite-btn"
    @click.stop="handleToggleFavorite"
  >
    <el-icon>
      <Star v-if="!isFavorited" />
      <StarFilled v-else />
    </el-icon>
    <span v-if="!circle && showText">{{ isFavorited ? '已收藏' : '收藏' }}</span>
  </el-button>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Star, StarFilled } from '@element-plus/icons-vue'
import { toggleFavorite, checkFavorite } from '@user/api/favorite'
import { ElMessage } from 'element-plus'

const props = defineProps({
  productId: {
    type: Number,
    required: true
  },
  size: {
    type: String,
    default: 'small'
  },
  circle: {
    type: Boolean,
    default: false
  },
  showText: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['change'])

const isFavorited = ref(false)
const loading = ref(false)

// 检查是否已收藏
const checkIsFavorite = async () => {
  try {
    const res = await checkFavorite(props.productId)
    isFavorited.value = res.data.isFavorite
  } catch (error) {
    console.error('检查收藏状态失败:', error)
  }
}

// 切换收藏状态
const handleToggleFavorite = async () => {
  if (loading.value) return
  
  loading.value = true
  try {
    const res = await toggleFavorite(props.productId)
    isFavorited.value = res.data.isFavorite
    ElMessage.success(res.data.message)
    emit('change', { productId: props.productId, isFavorited: isFavorited.value })
  } catch (error) {
    console.error('切换收藏失败:', error)
    ElMessage.error(error.response?.data?.message || '操作失败')
  } finally {
    loading.value = false
  }
}

// 监听 productId 变化
watch(() => props.productId, (newId) => {
  if (newId) {
    checkIsFavorite()
  }
})

onMounted(() => {
  if (props.productId) {
    checkIsFavorite()
  }
})
</script>

<style scoped>
.favorite-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border-color: rgba(255, 255, 255, 0.2);
  background: rgba(0, 0, 0, 0.2);
  color: #fff;
  transition: all 0.3s;
}

.favorite-btn:hover {
  border-color: var(--mall-primary);
  background: rgba(0, 212, 255, 0.1);
  color: var(--mall-primary);
}

.favorite-btn.is-loading {
  opacity: 0.6;
}

/* 已收藏状态 */
.favorite-btn[type="warning"] {
  border-color: var(--mall-warning);
  background: rgba(230, 162, 60, 0.1);
  color: var(--mall-warning);
}

.favorite-btn[type="warning"]:hover {
  background: rgba(230, 162, 60, 0.2);
}
</style>

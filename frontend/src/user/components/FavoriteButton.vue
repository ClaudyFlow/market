<template>
  <el-button
    :type="isFavorited ? 'warning' : 'default'"
    :size="size"
    :circle="circle"
    class="favorite-btn"
    @click.stop="handleToggleFavorite"
  >
    <i :class="isFavorited ? 'fas fa-star' : 'far fa-star'"></i>
    <span v-if="!circle && showText">{{ isFavorited ? '已收藏' : '收藏' }}</span>
  </el-button>
</template>

<script setup>
// Font Awesome 图标直接使用类名，无需导入
import { ref, onMounted, watch } from 'vue'
import { toggleFavorite, checkFavorite } from '@user/api/favorite'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@user/stores/user'

const userStore = useUserStore()

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
  if (!userStore.isLoggedIn) {
    return
  }
  try {
    const res = await checkFavorite('product', props.productId)
    isFavorited.value = res.data.favorite
  } catch (error) {
  }
}

// 切换收藏状态
const handleToggleFavorite = async () => {
  if (loading.value) return
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }

  loading.value = true
  try {
    const res = await toggleFavorite('product', props.productId)
    isFavorited.value = res.data.favorited
    ElMessage.success(res.data.favorited ? '收藏成功' : '已取消收藏')
    emit('change', { productId: props.productId, isFavorited: isFavorited.value })
  } catch (error) {
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

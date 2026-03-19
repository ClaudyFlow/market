<template>
  <el-button
    :type="isFollowed ? 'primary' : 'default'"
    :size="size"
    :circle="circle"
    class="follow-btn"
    :loading="loading"
    @click.stop="handleToggleFollow"
  >
    <el-icon>
      <User v-if="!isFollowed" />
      <UserFilled v-else />
    </el-icon>
    <span v-if="!circle && showText">{{ isFollowed ? '已关注' : '关注' }}</span>
  </el-button>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { User, UserFilled } from '@element-plus/icons-vue'
import { toggleFollow, checkFollow } from '@user/api/follow'
import { ElMessage } from 'element-plus'

const props = defineProps({
  shopId: {
    type: Number,
    required: true
  },
  shopName: {
    type: String,
    required: true
  },
  shopAvatar: {
    type: String,
    default: ''
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

const isFollowed = ref(false)
const loading = ref(false)

// 检查是否已关注
const checkIsFollow = async () => {
  try {
    const res = await checkFollow(props.shopId)
    isFollowed.value = res.data.isFavorite
  } catch (error) {
    console.error('检查关注状态失败:', error)
  }
}

// 切换关注状态
const handleToggleFollow = async () => {
  if (loading.value) return

  loading.value = true
  try {
    const res = await toggleFollow(props.shopId, props.shopName, props.shopAvatar)
    isFollowed.value = res.data.isFavorite
    ElMessage.success(res.data.message)
    emit('change', { shopId: props.shopId, isFollowed: isFollowed.value })
  } catch (error) {
    console.error('切换关注失败:', error)
    ElMessage.error(error.response?.data?.message || '操作失败')
  } finally {
    loading.value = false
  }
}

// 监听 shopId 变化
watch(() => props.shopId, (newId) => {
  if (newId) {
    checkIsFollow()
  }
})

onMounted(() => {
  if (props.shopId) {
    checkIsFollow()
  }
})
</script>

<style scoped>
.follow-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border-color: rgba(255, 255, 255, 0.2);
  background: rgba(0, 0, 0, 0.2);
  color: #fff;
  transition: all 0.3s;
}

.follow-btn:hover {
  border-color: var(--el-color-primary);
  background: rgba(64, 158, 255, 0.1);
  color: var(--el-color-primary);
}

.follow-btn.is-loading {
  opacity: 0.6;
}

/* 已关注状态 */
.follow-btn[type="primary"] {
  border-color: var(--el-color-primary);
  background: rgba(64, 158, 255, 0.2);
  color: var(--el-color-primary);
}

.follow-btn[type="primary"]:hover {
  background: rgba(64, 158, 255, 0.3);
}
</style>

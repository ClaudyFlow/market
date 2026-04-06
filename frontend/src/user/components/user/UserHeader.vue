<template>
  <div class="user-header">
    <div class="avatar-section">
      <el-avatar :size="100" :src="userInfo.avatar || '/images/avatar-default.png'" />
      <el-button class="edit-avatar-btn" size="small" @click="$emit('change-avatar')">
        更换头像
      </el-button>
      <input ref="avatarInput" type="file" accept="image/*" style="display: none" @change="$emit('avatar-upload', $event)" />
    </div>
    <div class="user-info-section">
      <h2 class="username">{{ userInfo.nickname || userInfo.username }}</h2>
      <div class="user-level">
        <el-tag :type="getLevelType(userInfo.level)" size="large">
          {{ getLevelName(userInfo.level) }}
        </el-tag>
      </div>
      <div class="user-stats">
        <div class="stat-item" v-for="stat in stats" :key="stat.label">
          <span class="label">{{ stat.label }}</span>
          <span class="value">{{ stat.value }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface UserInfo {
  username: string
  nickname?: string
  avatar?: string
  level?: number
  points?: number
  followingCount?: number
  followerCount?: number
  favoriteCount?: number
}

const props = defineProps<{
  userInfo: UserInfo
}>()

defineEmits<{
  'change-avatar': []
  'avatar-upload': [event: Event]
}>()

const getLevelType = (level: number = 1) => {
  const types = ['info', 'success', 'warning', 'danger']
  return types[Math.min(level - 1, types.length - 1)]
}

const getLevelName = (level: number = 1) => {
  const names = ['普通会员', '白银会员', '黄金会员', '钻石会员']
  return names[Math.min(level - 1, names.length - 1)]
}

const stats = computed(() => [
  { label: '积分', value: props.userInfo.points || 0 },
  { label: '关注', value: props.userInfo.followingCount || 0 },
  { label: '粉丝', value: props.userInfo.followerCount || 0 },
  { label: '收藏', value: props.userInfo.favoriteCount || 0 }
])
</script>

<style scoped>
.user-header {
  display: flex;
  gap: 30px;
  padding: 30px;
  background: #fff;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.edit-avatar-btn { margin-top: 8px; }

.user-info-section { flex: 1; }

.username {
  margin: 0 0 12px;
  font-size: 24px;
  color: #1a1a1a;
}

.user-level { margin-bottom: 16px; }

.user-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-item .label { font-size: 13px; color: #666; }
.stat-item .value { font-size: 20px; font-weight: 600; color: #1a1a1a; }
</style>

<template>
  <div class="common-tags-section">
    <div class="tag-title">常用标签</div>
    <div class="tag-list">
      <el-tag
        v-for="tag in commonTags"
        :key="tag"
        class="tag-item"
        size="large"
        effect="plain"
      >
        {{ tag }}
      </el-tag>
    </div>
  </div>
</template>

<script setup lang="ts">
const commonTags = [
  '蓝牙5.4',
  '主动降噪',
  '48小时续航',
  '空间音频',
  '快充技术',
  '可折叠',
  '触控操作',
  '多设备连接'
]
</script>

<style scoped>
.common-tags-section {
  margin: 24px 0;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
}

.tag-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 12px;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-item {
  cursor: pointer;
  transition: all 0.2s;
}

.tag-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
</style>

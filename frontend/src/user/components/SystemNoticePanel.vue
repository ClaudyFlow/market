<template>
  <div class="system-notice" aria-label="系统公告">
    <header class="notice-title">
      <i class="fas fa-bell"></i>
      <span>系统公告</span>
    </header>
    <el-carousel
      :interval="3000"
      direction="vertical"
      height="120px"
      aria-label="公告列表"
    >
      <el-carousel-item v-for="(note, index) in announcements" :key="index">
        <div class="notice-item">
          <div class="notice-content">
            <div class="notice-title-row">
              <span class="notice-type">{{ note.type }}</span>
              <span class="notice-title">{{ note.title }}</span>
            </div>
            <p class="notice-text">{{ note.content }}</p>
          </div>
          <div class="notice-header">
            <span class="notice-date">{{ note.date }}</span>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup>
// Font Awesome 图标直接使用类名，无需导入
import { announcements } from '@user/data/announcements'
</script>

<style scoped>
.system-notice {
  width: 100%;
  height: 100%;
  background: rgba(26, 31, 58, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 15px;
  box-sizing: border-box;
  overflow: hidden;
}

.notice-title {
  display: flex;
  align-items: center;
  gap: 8px;
  
  font-size: 16px;
  font-weight: bold;
  color: #00d4ff;
}

.notice-title i {
  color: #00d4ff;
  font-size: 18px;
}

.notice-title span {
  color: #00d4ff;
  font-weight: bold;
  font-size: 16px;
}

.system-notice :deep(.el-carousel__container) {
  height: 120px;
}

.system-notice :deep(.el-carousel__arrow) {
  font-size: 24px;
  color: #00d4ff;
}

.system-notice :deep(.el-carousel__arrow:hover) {
  color: #00ff88;
}

.system-notice :deep(.el-carousel-item) {
  display: flex;
  align-items: center;
  height: 100%;
}

.notice-item {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100%;
  padding: 8px 50px 8px 10px;
  width: 100%;
}

.notice-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.notice-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.notice-type {
  font-size: 12px;
  font-weight: bold;
  padding: 2px 6px;
  border-radius: 4px;
  color: #fff;
  flex-shrink: 0;
}

.notice-type.type-活动 {
  background: linear-gradient(135deg, #ff6600, #ff8800);
}

.notice-type.type-公告 {
  background: linear-gradient(135deg, #00d4ff, #00a8cc);
}

.notice-type.type-优惠 {
  background: linear-gradient(135deg, #ff3366, #ff5588);
}

.notice-type.type-新品 {
  background: linear-gradient(135deg, #00ff88, #00cc6a);
}

.notice-title-row .notice-title {
  font-size: 15px;
  font-weight: bold;
  color: #00d4ff;
  
  line-height: 1.5;
}

.notice-text {
  font-size: 13px;
  color: #aaa;
  line-height: 1.5;
  
  text-align: justify;
  text-indent: 2em;
}

.notice-header {
  display: flex;
  justify-content: flex-end;
  align-items: flex-end;
  
}

.notice-date {
  font-size: 10px;
  color: #666;
}
</style>

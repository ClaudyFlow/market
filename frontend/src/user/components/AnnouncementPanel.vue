<template>
  <div class="announcement" aria-label="网站公告">
    <header class="announcement-title">
      <el-icon><Bell/></el-icon>
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
              <span class="notice-type" :class="'type-' + note.type">{{ note.type }}</span>
              <h4 class="notice-title">{{ note.title }}</h4>
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
import { Bell } from '@element-plus/icons-vue'
import { announcements } from '@user/data/announcements'
</script>

<style scoped>
.announcement {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 0;
  background: rgba(26, 31, 58, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 15px;
  overflow-y: auto;
}

.announcement-title {
  display: flex;
  align-items: center;
  gap: 6px;
  
  padding-bottom: 6px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.15);
  justify-content: flex-start;
  position: relative;
  padding-left: 12px;
}

.announcement-title::before {
  content: '';
  position: absolute;
  left: 0;
  width: 4px;
  height: 16px;
  background: linear-gradient(180deg, #00d4ff, #00ff88);
  border-radius: 2px;
}

.announcement-title .el-icon {
  color: #00d4ff;
  font-size: 14px;
}

.announcement-title span {
  color: #00d4ff;
  font-weight: bold;
  font-size: 16px;
}

.announcement :deep(.el-carousel__container) {
  height: 160px;
}

.announcement :deep(.el-carousel__arrow) {
  font-size: 24px;
  color: #00d4ff;
}

.announcement :deep(.el-carousel__arrow:hover) {
  color: #00ff88;
}

.announcement :deep(.el-carousel-item) {
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
  padding: 2px 8px;
  border-radius: 4px;
  color: #fff;
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

.notice-title {
  font-size: 15px;
  font-weight: bold;
  color: #00d4ff;
  
  line-height: 2;
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

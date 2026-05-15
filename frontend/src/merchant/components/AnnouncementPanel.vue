<template>
  <div class="announcement" aria-label="商家公告">
    <header class="announcement-title">
      <el-icon><Bell /></el-icon>
      <span>商家公告</span>
    </header>
    <el-carousel
      :interval="3000"
      direction="vertical"
      height="120px"
      aria-label="公告列表"
    >
      <el-carousel-item v-for="(公告,index) in 公告列表" :key="index">
        <div class="notice-item">
          <div class="notice-content">
            <div class="notice-title-row">
              <span class="notice-type" :class="'type-' + 公告.类型">{{ 公告.类型 }}</span>
              <h4 class="notice-title">{{ 公告.标题 }}</h4>
            </div>
            <p class="notice-text">{{ 公告.内容 }}</p>
          </div>
          <div class="notice-header">
            <span class="notice-date">{{ 公告.日期 }}</span>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup lang="ts">
import { Bell } from '@element-plus/icons-vue'

interface 公告类型 {
  类型:string
  标题:string
  内容:string
  日期:string
}

const 公告列表:公告类型 [] = [
  {
    类型:'活动',
    标题:'双 11 大促报名开启',
    内容:'年度最大促销活动,全场商品 5 折起,流量扶持,快来报名!',
    日期:'2026-03-15'
  },
  {
    类型:'公告',
    标题:'商家后台系统升级通知',
    内容:'系统将于本周末进行升级维护,届时部分功能可能无法使用.',
    日期:'2026-03-14'
  },
  {
    类型:'规则',
    标题:'商品发布规范更新',
    内容:'新增商品图片质量要求,请商家朋友们注意遵守最新规范.',
    日期:'2026-03-10'
  },
  {
    类型:'培训',
    标题:'商家运营培训课程',
    内容:'免费线上培训课程,帮助商家提升运营能力,欢迎报名参加.',
    日期:'2026-03-08'
  }
]
</script>

<style scoped>
.announcement {
  background: rgba(26, 31, 58, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 10px;
  flex: 1;
  min-height: 150px;
  max-height: 250px;
  overflow: hidden;
}

.announcement-title {
  display: flex;
  align-items: center;
  gap: 6px;
  
  padding-bottom: 6px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.15);
  justify-content: flex-start;
}

.announcement-title .el-icon {
  color: #00d4ff;
  font-size: 14px;
}

.announcement-title span {
  color: #00d4ff;
  font-weight: bold;
  font-size: 13px;
}

.announcement :deep(.el-carousel__container) {
  height: 140px;
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
  font-size: 11px;
  font-weight: bold;
  padding: 1px 6px;
  border-radius: 3px;
  color: #fff;
  white-space: nowrap;
}

.notice-type.type-活动 {
  background: linear-gradient(135deg, #ff6600, #ff8800);
}

.notice-type.type-公告 {
  background: linear-gradient(135deg, #00d4ff, #00a8cc);
}

.notice-type.type-规则 {
  background: linear-gradient(135deg, #ff3366, #ff5588);
}

.notice-type.type-培训 {
  background: linear-gradient(135deg, #00ff88, #00cc6a);
}

.notice-title {
  font-size: 13px;
  font-weight: bold;
  color: #00d4ff;
  
  line-height: 2;
}

.notice-text {
  font-size: 11px;
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

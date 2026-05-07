<template>
  <el-carousel
    height="100%"
    :interval="4000"
    :autoplay="true"
    indicator-position=""
    aria-label="促销轮播"
  >
    <el-carousel-item v-for="(banner, index) in banners" :key="index">
      <article class="banner-item" :style="{ background: banner.gradient }">
        <div class="banner-info">
          <h2 class="banner-title">{{ banner.title }}</h2>
          <p class="banner-subtitle">{{ banner.subtitle }}</p>
          <el-button type="primary" size="large" class="glow-btn">
            立即查看
          </el-button>
        </div>
      </article>
    </el-carousel-item>
  </el-carousel>
</template>

<script setup lang="ts">
import { banners } from "@user/data/categories";
</script>

<style scoped>
/* 轮播图容器 */
.banner-item {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px 40px;
  box-sizing: border-box;
}

.banner-info {
  text-align: center;
  color: #fff;
}

.banner-title {
  font-size: 42px;
  
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.banner-subtitle {
  font-size: 20px;
  
  color: #aaa;
}

.glow-btn {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.5);
  color: #000;
  font-weight: bold;
}

.glow-btn:hover {
  box-shadow: 0 0 30px rgba(0, 212, 255, 0.8);
}

/* 自定义轮播图指示器样式 - 悬浮在内容内部 */
:deep(.el-carousel__indicators) {
  display: flex;
  justify-content: center;
  gap: 8px;
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
}

:deep(.el-carousel__container) {
  height: 100%;
}

:deep(.el-carousel__indicator) {
  background: transparent;
  border: none;
}

:deep(.el-carousel__button) {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  border: 2px solid rgba(0, 212, 255, 0.5);
  transition: all 0.3s;
}

:deep(.el-carousel__button:hover),
:deep(.el-carousel__indicator.is-active .el-carousel__button) {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border-color: #00d4ff;
  box-shadow: 0 0 10px rgba(0, 212, 255, 0.5);
  transform: scale(1.2);
}
</style>

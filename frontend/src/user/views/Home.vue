<template>
  <main class="home-main">
    
    <!-- 警报通知 -->
    <div class="alarm-container" v-if="showAlarm">
      <div class="alarm-content">
        <div class="alarm-icon-wrapper">
          <span class="alarm-icon"></span>
          <span class="alarm-pulse"></span>
        </div>
        <span class="alarm-text">系统提醒：限时特惠商品即将售罄！</span>
        <button class="alarm-close" @click="showAlarm = false">
          <i class="fas fa-times"></i>
        </button>
      </div>
    </div>

    <section class="mall-home-section">
      <MallHomeSection />
    </section>

    <section class="flash-sale-section">
      <FlashSaleSection />
    </section>

    <section class="recommended-section">
      <RecommendedSection />
    </section>

    <section class="brand-section">
      <BrandSection />
    </section>
  </main>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import BrandSection from "@user/views/home/BrandSection.vue";
import RecommendedSection from "@user/views/home/RecommendedSection.vue";
import FlashSaleSection from "@user/views/home/FlashSaleSection.vue";
import MallHomeSection from "@user/views/home/MallHomeSection.vue";

const showAlarm = ref(true);

onMounted(() => {
  setTimeout(() => {
    showAlarm.value = false;
  }, 4000);
});
</script>

<style scoped>
@import "@user/assets/mall-style.css";

.mall-home-section,
.flash-sale-section,
.recommended-section,
.brand-section {
  background: linear-gradient(180deg,
      rgba(0, 212, 255, 0.15) 0%,
      rgba(10, 14, 26, 0.8) 100%);
}

/* ========== 警报容器 - 赛博朋克风格 ========== */
.alarm-container {
  position: fixed;
  top: 80px;
  right: 20px;
  z-index: 9999;
  animation: slideInAlarm 0.5s ease-out forwards;
}

.alarm-content {
  /* 深色半透明背景 */
  background: linear-gradient(135deg, 
      rgba(0, 16, 32, 0.95) 0%, 
      rgba(0, 32, 64, 0.9) 100%);
  
  /* 青色边框发光效果 */
  border: 1px solid rgba(0, 212, 255, 0.5);
  border-radius: 12px;
  
  /* 多层阴影营造科技感 */
  box-shadow: 
    0 0 20px rgba(0, 212, 255, 0.3),
    0 0 40px rgba(0, 212, 255, 0.1),
    inset 0 0 20px rgba(0, 212, 255, 0.05);
  
  padding: 15px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  font-weight: 500;
  color: #fff;
  white-space: nowrap;
  backdrop-filter: blur(10px);
  
  /* 消失动画 */
  animation: fadeOutAlarm 0.5s ease-in 3.5s forwards;
}

/* 图标区域 */
.alarm-icon-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
}

.alarm-icon {
  font-size: 18px;
  color: #00d4ff;
  display: inline-block;
  animation: pulseIcon 1.5s ease-in-out infinite;
}

.alarm-icon::before {
  content: '⚠';
  filter: drop-shadow(0 0 5px rgba(0, 212, 255, 0.8));
}

/* 脉冲光晕效果 */
.alarm-pulse {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: rgba(0, 212, 255, 0.3);
  animation: pulseRing 1.5s ease-out infinite;
}

/* 警报文字 */
.alarm-text {
  color: #fff;
  text-shadow: 0 0 10px rgba(0, 212, 255, 0.5);
  letter-spacing: 0.5px;
}

/* 关闭按钮 */
.alarm-close {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #888;
  transition: all 0.3s ease;
  padding: 0;
  margin-left: 10px;
}

.alarm-close:hover {
  background: rgba(255, 77, 79, 0.2);
  border-color: rgba(255, 77, 79, 0.5);
  color: #ff4d4f;
  box-shadow: 0 0 10px rgba(255, 77, 79, 0.3);
}

.alarm-close i {
  font-size: 12px;
}

/* ========== 动画效果 ========== */

/* 滑入动画 */
@keyframes slideInAlarm {
  0% {
    opacity: 0;
    transform: translateX(100px) scale(0.9);
  }
  100% {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}

/* 淡出动画 */
@keyframes fadeOutAlarm {
  0% {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
  100% {
    opacity: 0;
    transform: translateX(100px) scale(0.9);
  }
}

/* 图标脉冲 */
@keyframes pulseIcon {
  0%, 100% {
    transform: scale(1);
    filter: drop-shadow(0 0 5px rgba(0, 212, 255, 0.8));
  }
  50% {
    transform: scale(1.1);
    filter: drop-shadow(0 0 15px rgba(0, 212, 255, 1));
  }
}

/* 脉冲光晕环 */
@keyframes pulseRing {
  0% {
    transform: scale(1);
    opacity: 0.8;
  }
  100% {
    transform: scale(2.5);
    opacity: 0;
  }
}

/* ========== 响应式适配 ========== */
@media (max-width: 768px) {
  .alarm-container {
    top: 70px;
    right: 10px;
    left: 10px;
  }
  
  .alarm-content {
    padding: 12px 15px;
    font-size: 13px;
  }
  
  .alarm-text {
    white-space: normal;
    word-break: break-word;
  }
}
</style>

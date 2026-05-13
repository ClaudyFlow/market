<template>
  <SectionContainer custom-class="mall-home-container">
    <!-- 左侧区域：用户面板 + 下方区域 -->
    <div class="left-section">
      <div class="user-panel">
        <UserPanel />
      </div>
      <button class="left-bottom" type="button" @click="goToCustomerService">
        <CustomerService />
      </button>
    </div>
    <!-- 中间：轮播图 + 快捷操作 -->
    <div class="banner-section">
      <BannerCarousel />
      <!-- 快捷操作 -->
      <QuickActionsBar />
    </div>

    <!-- 右侧：系统通知 + 用户通知 + 用户论坛 -->
    <div class="right-section">
      <!-- 系统通知 -->
      <SystemNoticePanel />
      <!-- 用户通知 -->
      <UserNoticePanel />
      <!-- 用户论坛 -->
      <button class="forum-btn" type="button" @click="goToForum">
        <UserForum />
      </button>
    </div>
  </SectionContainer>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import SystemNoticePanel from "@user/components/SystemNoticePanel.vue";
import UserNoticePanel from "@user/components/UserNoticePanel.vue";
import UserPanel from "@user/components/UserPanel.vue";
import QuickActionsBar from "@user/components/QuickActionsBar.vue";
import BannerCarousel from "@user/components/BannerCarousel.vue";
import CustomerService from "@user/components/CustomerService.vue";
import UserForum from "@user/components/UserForum.vue";
import SectionContainer from "@user/components/SectionContainer.vue";

const router = useRouter();

const goToCustomerService = () => {
  router.push("/service");
};

const goToForum = () => {
  router.push("/forum");
};
</script>

<style scoped>
/* 左侧区域容器 - 占 3 行，内部 2:1 比例 */
.left-section {
  grid-column: 1;
  grid-row: 1 / 7;
  display: grid;
  grid-template-rows: 2fr 1fr;
  gap: 15px;
}

/* 用户面板 - 全宽全高 */
.user-panel {
  width: 100%;
  height: 100%;
}

/* 左侧下方区域 - 全宽全高 */
.left-bottom {
  width: 100%;
  height: 100%;
  border: none;
  background: none;
  padding: 0;
  cursor: pointer;
}

/* 论坛按钮 - 全宽全高 */
.forum-btn {
  width: 100%;
  height: 100%;
  border: none;
  background: none;
  padding: 0;
  cursor: pointer;
  text-align: left;
}

/* 中间区域 - 轮播图占 2 行，快捷入口占 1 行，内部 2:1 比例 */
.banner-section {
  grid-column: 2;
  grid-row: 1 / 7;
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-rows: 2fr 1fr;
  gap: 15px;
}

/* 轮播图 - 全宽全高 */
.banner-section .el-carousel {
  grid-row: 1;
  width: 100%;
  height: 100%;
  padding: 0;
}

/* 快捷操作栏 - 全宽全高 */
.banner-section .quick-actions-bar {
  grid-row: 2;
  width: 100%;
  height: 100%;
}

/* 右侧区域容器 - 3 行，高度 1:1:1 */
.right-section {
  grid-column: 3;
  grid-row: 1 / 7;
  display: grid;
  grid-template-rows: 1fr 1fr 1fr;
  gap: 15px;
}

/* 用户卡片 */
article.user-card {
  background: rgba(26, 31, 58, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
}

.user-name {
  font-size: 16px;
}

.user-level {
  display: flex;
  justify-content: center;
  gap: 8px;
  align-items: center;
}

.level-badge {
  background: linear-gradient(90deg, #ff6600, #ff8800);
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
}

.user-vip {
}

.vip-tag {
  background: linear-gradient(90deg, #ffd700, #ffaa00);
  padding: 2px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
  color: #000;
}

.user-credit {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 5px;
}

.user-credit .credit-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  padding: 2px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
  color: #000;
}

.check-in-btn {
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.2), rgba(0, 255, 136, 0.2));
  border: 1px solid rgba(0, 212, 255, 0.4);
  border-radius: 8px;
  padding: 10px 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s;
  color: var(--mall-primary);
  font-size: 14px;
  font-weight: bold;
  width: 100%;
}

.check-in-btn:hover {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  color: #000;
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.5);
  transform: translateY(-2px);
}

.check-in-btn.checked-in {
  background: #cccccc;
  border-color: #cccccc;
  color: #666666;
  cursor: default;
  pointer-events: none;
}

.check-in-btn.checked-in:hover {
  background: #cccccc;
  color: #666666;
  box-shadow: none;
  transform: none;
}

.check-in-btn .el-icon {
  font-size: 18px;
}

.check-in-reward {
  background: linear-gradient(90deg, #ff6600, #ff8800);
  color: #fff;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: bold;
}

/* 抽奖按钮 */
.lottery-btn {
  background: linear-gradient(135deg, rgba(255, 102, 0, 0.2), rgba(255, 136, 0, 0.2));
  border: 1px solid rgba(255, 102, 0, 0.4);
  border-radius: 8px;
  padding: 10px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  cursor: pointer;
  transition: all 0.3s;
  color: #ff8800;
  font-size: 14px;
  font-weight: bold;
  width: 100%;
}

.lottery-btn:hover {
  background: linear-gradient(135deg, #ff6600, #ff8800);
  color: #ffffff;
  box-shadow: 0 0 20px rgba(255, 102, 0, 0.5);
  transform: translateY(-2px);
}

.lottery-btn .el-icon {
  font-size: 20px;
}

.lottery-cost {
  font-size: 11px;
  font-weight: normal;
  opacity: 0.8;
}
</style>

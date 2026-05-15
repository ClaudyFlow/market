<template>
  <article class="user-card">
    <UserAvatar />
    <h3 class="user-name">尊敬的会员</h3>
    <div class="user-info">
      <UserLevel level="8" />
      <VipLevelBadge level-name="普通会员" text-color="#fff9c4" />
      <UserCredit :credit="userStore.userCredit" />
    </div>
    <button
      class="check-in-btn"
      :class="{ 'checked-in': userStore.hasCheckedIn }"
      @click="handleCheckIn"
      :aria-label="userStore.hasCheckedIn ? '已签到' : '立即签到'"
    >
      <i class="far fa-calendar-alt"></i>
      <span>{{ userStore.hasCheckedIn ? "已签到" : "立即签到" }}</span>
      <span class="check-in-reward" v-if="userStore.hasCheckedIn">+10 积分</span>
    </button>
    <button
      class="lottery-btn"
      @click="goToLottery"
      aria-label="点击抽奖"
    >
      <span><i class="fas fa-coins"></i>点击抽奖</span>
    </button>
  </article>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '@user/stores/user'
// Font Awesome 图标直接使用类名，无需导入
import VipLevelBadge from './VipLevelBadge.vue'
import UserLevel from './UserLevel.vue'
import UserCredit from './UserCredit.vue'
import UserAvatar from './UserAvatar.vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

// 签到处理
const handleCheckIn = async () => {
  await userStore.doCheckIn()
  ElMessage.success('签到成功')
}

// 跳转到抽奖页面
const goToLottery = () => {
  router.push('/lottery')
}
</script>

<style scoped>
/* 用户卡片 */
.user-card {
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: auto auto auto auto;
  gap: 15px;
  background: rgba(26, 31, 58, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 20px;
  box-sizing: border-box;
}

/* 头像单独占一行，居中 */
.user-card > :first-child {
  grid-column: 1 / 3;
  display: flex;
  justify-content: center;
}

/* 用户名称 */
.user-name {
  grid-row: 2;
  grid-column: 1 / 3;
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  
  text-align: center;
}

/* 用户信息 - 等级、VIP、积分 */
.user-info {
  grid-row: 3;
  grid-column: 1 / 3;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 10px;
  width: 100%;
}

.user-info > * {
  width: 60%;
  border-radius: 9999px;
}

/* 签到按钮 */
.check-in-btn {
  grid-row: 4;
  grid-column: 1;
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

.check-in-btn i {
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
  grid-row: 4;
  grid-column: 2;
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
}

.lottery-btn:hover {
  background: linear-gradient(135deg, #ff6600, #ff8800);
  color: #fff;
  box-shadow: 0 0 20px rgba(255, 102, 0, 0.5);
  transform: translateY(-2px);
}

.lottery-btn i {
  font-size: 20px;
}
</style>

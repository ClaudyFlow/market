<template>
  <div class="lottery-page">
    <div class="lottery-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">
          <span class="title-icon">🎰</span>
          幸运抽奖
        </h1>
        <p class="page-subtitle">消耗积分抽取丰厚奖品</p>
      </div>

      <!-- 用户信息卡片 -->
      <div class="user-info-cards">
        <div class="info-card credit-card">
          <div class="card-icon">
            <el-icon :size="32"><Coin /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-label">当前积分</div>
            <div class="card-value">{{ userCredit }}</div>
          </div>
        </div>
        <div class="info-card count-card">
          <div class="card-icon">
            <el-icon :size="32"><Ticket /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-label">今日抽奖</div>
            <div class="card-value">{{ dailyDrawCount }} / {{ maxDailyDraws }}</div>
          </div>
        </div>
        <div class="info-card times-card">
          <div class="card-icon">
            <el-icon :size="32"><Star /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-label">总抽奖次数</div>
            <div class="card-value">{{ drawCount }}</div>
          </div>
        </div>
      </div>

      <!-- 老虎机转盘 -->
      <div class="slot-machine-wrapper">
        <div class="slot-machine">
          <!-- 上指针 -->
          <div class="win-pointer win-pointer-top">
            <div class="pointer-arrow">▼</div>
          </div>
          
          <div class="slot-window">
            <!-- 7 个固定标签 -->
            <div class="slot-grid">
              <div class="slot-item" v-for="(item, i) in displayItems" :key="i">
                <div class="item-content">
                  <span class="item-icon">{{ item.icon }}</span>
                  <span class="item-name">{{ item.name }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 下指针 -->
          <div class="win-pointer win-pointer-bottom">
            <div class="pointer-arrow">▲</div>
          </div>
        </div>
      </div>

      <!-- 抽奖按钮 -->
      <div class="draw-section">
        <el-button
          type="primary"
          size="large"
          class="draw-button"
          :loading="isDrawing"
          :disabled="userCredit < 100 || isDrawing"
          @click="handleDraw"
        >
          <el-icon><Star /></el-icon>
          {{ isDrawing ? '抽奖中...' : '消耗 100 积分抽奖' }}
        </el-button>
        <p class="draw-tip" v-if="userCredit < 100">积分不足，快去赚取积分吧！</p>
      </div>

      <!-- 中奖结果弹窗 -->
      <el-dialog
        v-model="resultDialogVisible"
        title="🎉 恭喜中奖"
        width="420"
        class="result-dialog"
        center
      >
        <div class="result-content">
          <div class="result-icon">🎁</div>
          <div class="result-name">{{ lastPrize }}</div>
          <div class="result-message">{{ resultMessage }}</div>
        </div>
        <template #footer>
          <el-button type="primary" size="large" @click="resultDialogVisible = false" class="accept-btn">
            开心收下
          </el-button>
        </template>
      </el-dialog>

      <!-- 奖品列表 -->
      <div class="prizes-section">
        <div class="section-header">
          <h2 class="section-title">
            <span class="section-icon">🎁</span>
            奖品列表
          </h2>
        </div>
        <div class="prizes-grid">
          <div class="prize-item" v-for="prize in prizes" :key="prize.id">
            <div class="prize-icon">{{ getPrizeIcon(prize.name) }}</div>
            <div class="prize-name">{{ prize.name }}</div>
            <div class="prize-type" :class="prize.type === 1 ? 'credit' : 'physical'">
              {{ prize.type === 1 ? '积分' : '实物' }}
            </div>
          </div>
        </div>
      </div>

      <!-- 抽奖记录 -->
      <div class="records-section">
        <div class="section-header">
          <h2 class="section-title">
            <span class="section-icon">📜</span>
            抽奖记录
          </h2>
        </div>
        <el-table :data="records" style="width: 100%" max-height="300" class="records-table">
          <el-table-column prop="prizeName" label="奖品" min-width="150" />
          <el-table-column prop="prizeType" label="类型" width="100">
            <template #default="{ row }">
              <el-tag :type="row.prizeType === 1 ? 'success' : 'warning'" size="small" round>
                {{ row.prizeType === 1 ? '💰 积分' : '🎁 实物' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="cost" label="消耗" width="100">
            <template #default="{ row }">
              <span class="cost-text">-{{ row.cost }} 积分</span>
            </template>
          </el-table-column>
          <el-table-column label="时间" min-width="160">
            <template #default="{ row }">
              <span class="time-text">{{ formatTime(row.createdAt) }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 签到测试 -->
      <div class="checkin-section">
        <div class="section-header">
          <h2 class="section-title">
            <span class="section-icon">📅</span>
            签到测试
          </h2>
          <div class="checkin-summary">
            <span>连续签到：{{ currentCheckinDay }} / 7 天</span>
            <el-button v-if="allCompleted" type="primary" size="small" @click="resetCheckin" class="reset-btn">
              <el-icon><Refresh /></el-icon> 重新开始
            </el-button>
          </div>
        </div>
        <div class="checkin-carousel">
          <div class="checkin-window">
            <div class="checkin-days">
              <div
                class="checkin-day"
                v-for="(day, index) in checkinDays"
                :key="index"
                :class="{
                  'completed': day.completed,
                  'available': day.available,
                  'current': day.current
                }"
                @click="handleCheckin(index)"
              >
                <div class="day-number">第{{ index + 1 }}天</div>
                <div class="day-credit">+{{ day.credit }}</div>
                <div class="day-status">
                  <el-icon v-if="day.completed" class="status-icon"><CircleCheckFilled /></el-icon>
                  <span v-else-if="day.available" class="status-text">可签到</span>
                  <span v-else class="status-text">🔒</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Coin, Ticket, Star, CircleCheckFilled, Refresh } from '@element-plus/icons-vue'
import { drawLottery, getLotteryRecords, getUserCredit } from '@user/api/lottery'
import { checkIn as apiCheckIn } from '@user/api/user'
import { useUserStore } from '@user/stores/user'

const userStore = useUserStore()
const userCredit = ref(0)

// 同步积分到 store
watch(userCredit, (newVal) => {
  userStore.userCredit = newVal
})

// 从 store 同步积分
watch(() => userStore.userCredit, (newVal) => {
  if (newVal !== userCredit.value) {
    userCredit.value = newVal
  }
}, { immediate: true })

const isDrawing = ref(false)
const drawCount = ref(0)
const dailyDrawCount = ref(0) // 每日抽奖次数
const maxDailyDraws = 10 // 每日最多抽奖次数

const reelItems = ref([])
const itemWidth = 130 // 每个奖品卡片的宽度
const visibleCount = 7 // 一行显示 7 个
let spinningAnimation = null // 转动动画 ID

// 显示的 7 个奖品
const displayItems = ref([
  { icon: '💰', name: '10 积分' },
  { icon: '💰', name: '50 积分' },
  { icon: '💰', name: '100 积分' },
  { icon: '💰', name: '200 积分' },
  { icon: '💰', name: '500 积分' },
  { icon: '🎫', name: '优惠券' },
  { icon: '🎧', name: '耳机' }
])

// 奖品配置（带概率）
const prizeConfig = [
  { id: 1, name: '10 积分', type: 1, icon: '💰', probability: 0.35 },
  { id: 2, name: '50 积分', type: 1, icon: '💰', probability: 0.25 },
  { id: 3, name: '100 积分', type: 1, icon: '💰', probability: 0.15 },
  { id: 4, name: '200 积分', type: 1, icon: '💰', probability: 0.10 },
  { id: 5, name: '500 积分', type: 1, icon: '💰', probability: 0.05 },
  { id: 6, name: '优惠券', type: 1, icon: '🎫', probability: 0.05 },
  { id: 7, name: '蓝牙耳机', type: 2, icon: '🎧', probability: 0.03 },
  { id: 8, name: '机械键盘', type: 2, icon: '⌨️', probability: 0.02 }
]

const prizes = ref<any[]>([
  { id: 1, name: '10 积分', type: 1 },
  { id: 2, name: '50 积分', type: 1 },
  { id: 3, name: '100 积分', type: 1 },
  { id: 4, name: '200 积分', type: 1 },
  { id: 5, name: '500 积分', type: 1 },
  { id: 6, name: '优惠券', type: 1 },
  { id: 7, name: '蓝牙耳机', type: 2 },
  { id: 8, name: '机械键盘', type: 2 }
])

const records = ref<any[]>([])
const resultDialogVisible = ref(false)
const lastPrize = ref('')
const resultMessage = ref('')

const checkinDays = reactive([
  { credit: 10, completed: false, available: true, current: true },
  { credit: 10, completed: false, available: false, current: false },
  { credit: 15, completed: false, available: false, current: false },
  { credit: 15, completed: false, available: false, current: false },
  { credit: 20, completed: false, available: false, current: false },
  { credit: 20, completed: false, available: false, current: false },
  { credit: 30, completed: false, available: false, current: false }
])

const allCompleted = computed(() => checkinDays.every(d => d.completed))

// 当前连续签到天数（已完成的数量）
const currentCheckinDay = computed(() => {
  return checkinDays.filter(d => d.completed).length
})

const resetCheckin = () => {
  checkinDays.forEach((day, i) => {
    day.completed = false
    day.available = i === 0
    day.current = i === 0
  })
  saveCheckinState()
  ElMessage.success('签到已重置')
}

// 保存签到状态到 localStorage
const saveCheckinState = () => {
  const state = {
    checkinDays: checkinDays,
    lastCheckinDate: new Date().toDateString()
  }
  localStorage.setItem('lotteryCheckin', JSON.stringify(state))
}

// 加载签到状态
const loadCheckinState = () => {
  const saved = localStorage.getItem('lotteryCheckin')
  if (saved) {
    try {
      const state = JSON.parse(saved)
      const today = new Date().toDateString()
      
      // 如果是新的一天，重置签到
      if (state.lastCheckinDate !== today) {
        resetCheckin()
      } else {
        // 恢复之前的签到状态
        state.checkinDays.forEach((day: any, i: number) => {
          if (i < checkinDays.length) {
            checkinDays[i].completed = day.completed
            checkinDays[i].available = day.available
            checkinDays[i].current = day.current
          }
        })
      }
    } catch (e) {
      console.error('加载签到状态失败', e)
    }
  }
}

// 保存每日抽奖次数
const saveDailyDrawCount = () => {
  const today = new Date().toDateString()
  localStorage.setItem('dailyDrawCount', JSON.stringify({
    count: dailyDrawCount.value,
    date: today
  }))
}

// 加载每日抽奖次数
const loadDailyDrawCount = () => {
  const saved = localStorage.getItem('dailyDrawCount')
  if (saved) {
    try {
      const data = JSON.parse(saved)
      const today = new Date().toDateString()
      
      if (data.date === today) {
        dailyDrawCount.value = data.count
      } else {
        dailyDrawCount.value = 0 // 新的一天，重置次数
      }
    } catch (e) {
      console.error('加载抽奖次数失败', e)
      dailyDrawCount.value = 0
    }
  }
}

const handleCheckin = async (index: number) => {
  const day = checkinDays[index]
  if (!day.available || day.completed) return

  try {
    // 测试环境，不调用实际 API
    // await apiCheckIn()
    
    // 模拟 API 延迟
    await new Promise(resolve => setTimeout(resolve, 300))
    
    day.completed = true
    const earnedCredits = day.credit
    userCredit.value += earnedCredits
    
    // 保存签到状态
    saveCheckinState()
    
    ElMessage.success({
      message: `签到成功！获得 ${earnedCredits} 积分`,
      icon: '🎉'
    })

    if (index < checkinDays.length - 1) {
      checkinDays[index + 1].available = true
      checkinDays[index + 1].current = true
    } else {
      ElMessage.success('恭喜完成本周签到！')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '签到失败')
  }
}

const getPrizeIcon = (name: string) => {
  const icons: Record<string, string> = {
    '积分': '💰',
    '优惠券': '🎫',
    '耳机': '🎧',
    '手机': '📱',
    '手表': '⌚',
    '键盘': '⌨️'
  }
  for (const [key, icon] of Object.entries(icons)) {
    if (name.includes(key)) return icon
  }
  return '🎁'
}

const initReelItems = () => {
  const items = []
  const displayItems = 80 // 生成 80 个奖品，足够转动多圈
  for (let i = 0; i < displayItems; i++) {
    const prize = prizes.value[i % prizes.value.length]
    items.push({
      icon: getPrizeIcon(prize.name),
      name: prize.name
    })
  }
  reelItems.value = items
}

// 根据概率随机选择奖品
const drawPrize = () => {
  const random = Math.random()
  let cumulative = 0
  
  for (const prize of prizeConfig) {
    cumulative += prize.probability
    if (random <= cumulative) {
      return prize
    }
  }
  
  return prizeConfig[0] // 默认返回 10 积分
}

// 开始转动动画（文字快速变化）
const startSpinningAnimation = () => {
  const allIcons = ['💰', '🎫', '🎧', '📱', '⌚', '🎁', '🎰', '⭐']
  const allNames = ['10 积分', '50 积分', '100 积分', '200 积分', '500 积分', '优惠券', '耳机', '键盘']
  
  spinningAnimation = setInterval(() => {
    displayItems.value = displayItems.value.map(() => ({
      icon: allIcons[Math.floor(Math.random() * allIcons.length)],
      name: allNames[Math.floor(Math.random() * allNames.length)]
    }))
  }, 80) // 每 80ms 变化一次
}

// 停止转动动画
const stopSpinningAnimation = () => {
  if (spinningAnimation) {
    clearInterval(spinningAnimation)
    spinningAnimation = null
  }
}

const handleDraw = async () => {
  if (userCredit.value < 100) {
    ElMessage.warning('积分不足，快去赚取积分吧！')
    return
  }
  
  if (dailyDrawCount.value >= maxDailyDraws) {
    ElMessage.warning('今日抽奖次数已用完，明天再来吧！')
    return
  }

  isDrawing.value = true
  
  try {
    // 开始转动动画（文字快速变化）
    startSpinningAnimation()
    
    // 根据概率抽取奖品
    const res = drawPrize()
    
    // 扣除积分
    userCredit.value -= 100
    drawCount.value++
    dailyDrawCount.value++
    
    // 保存每日抽奖次数
    saveDailyDrawCount()

    // 3 秒后停止并显示结果
    setTimeout(() => {
      stopSpinningAnimation()
      
      // 设置最终结果（随机一个位置显示中奖奖品）
      const winPosition = Math.floor(Math.random() * 7)
      displayItems.value = displayItems.value.map((item, i) => {
        if (i === winPosition) {
          return { icon: res.icon, name: res.name }
        } else {
          return {
            icon: prizeTypes[Math.floor(Math.random() * prizeTypes.length)].icon,
            name: prizeTypes[Math.floor(Math.random() * prizeTypes.length)].name
          }
        }
      })
      
      isDrawing.value = false
      lastPrize.value = res.name
      resultMessage.value = res.type === 1 
        ? `获得${res.name}，已自动存入账户` 
        : '🎉 实物奖品请前往用户中心填写收货地址'
      resultDialogVisible.value = true
      
      // 添加记录
      records.value.unshift({
        prizeName: res.name,
        prizeType: res.type,
        cost: 100,
        createdAt: new Date().toISOString()
      })
      
      ElMessage.success(`恭喜获得${res.name}！`)
    }, 3000)
  } catch (error: any) {
    isDrawing.value = false
    ElMessage.error(error.message || '抽奖失败')
  }
}

const formatTime = (time: string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

const loadRecords = async () => {
  try {
    const res = await getLotteryRecords()
    records.value = res
  } catch (error) {
    records.value = []
  }
}

const loadUserCredit = async () => {
  try {
    // 优先从 store 读取
    if (userStore.userCredit > 0) {
      userCredit.value = userStore.userCredit
      return
    }
    
    // 否则从 API 读取
    const res = await getUserCredit()
    userCredit.value = res
  } catch (error) {
    // 测试数据：给 1000 积分方便测试
    userCredit.value = 1000
    ElMessage.info('测试模式：已赠送 1000 积分')
  }
}

onMounted(() => {
  initReelItems()
  loadRecords()
  loadUserCredit()
  loadCheckinState() // 加载签到状态
  loadDailyDrawCount() // 加载每日抽奖次数
})
</script>

<style scoped>
.lottery-page {
  min-height: 100vh;
  background: linear-gradient(180deg, rgba(10, 14, 26, 0.95) 0%, rgba(26, 31, 58, 0.9) 100%);
  padding-bottom: 40px;
}

.lottery-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 页面头部 */
.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 36px;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 8px;
}

.title-icon {
  margin-right: 10px;
}

.page-subtitle {
  color: #888;
  font-size: 16px;
}

/* 用户信息卡片 */
.user-info-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
  max-width: 900px;
  margin-left: auto;
  margin-right: auto;
}

.info-card {
  background: rgba(26, 31, 58, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
}

.card-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.2), rgba(0, 255, 136, 0.2));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--mall-primary);
}

.card-content {
  flex: 1;
}

.card-label {
  font-size: 14px;
  color: #888;
  margin-bottom: 4px;
}

.card-value {
  font-size: 28px;
  font-weight: bold;
  color: var(--mall-primary);
}

/* 老虎机 */
.slot-machine-wrapper {
  margin-bottom: 30px;
}

.slot-machine {
  position: relative;
  background: rgba(26, 31, 58, 0.8);
  border: 2px solid rgba(0, 212, 255, 0.3);
  border-radius: 16px;
  overflow: visible;
  max-width: 910px;
  margin: 0 auto;
}

.slot-window {
  overflow: hidden;
  height: 130px;
  position: relative;
  z-index: 1;
}

/* 滚动内容层 */
.slot-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 1%;
  padding: 1%;
}

.slot-item {
  width: 100%;
  height: 130px;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  box-sizing: border-box;
}

.item-content {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.item-icon {
  font-size: 42px;
  display: block;
  line-height: 1;
}

.item-name {
  font-size: 13px;
  color: #fff;
  white-space: nowrap;
  font-weight: 500;
}

/* 上下指针 */
.win-pointer {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
  pointer-events: none;
}

.win-pointer-top {
  top: -25px;
}

.win-pointer-bottom {
  bottom: -25px;
}

.pointer-arrow {
  font-size: 28px;
  color: var(--mall-accent);
  filter: drop-shadow(0 0 10px var(--mall-primary));
  animation: pointerPulse 1.5s infinite;
}

@keyframes pointerPulse {
  0%, 100% { 
    transform: scale(1);
    filter: drop-shadow(0 0 10px var(--mall-primary));
  }
  50% { 
    transform: scale(1.2);
    filter: drop-shadow(0 0 20px var(--mall-secondary));
  }
}

/* 抽奖按钮 */
.draw-section {
  text-align: center;
  margin-bottom: 40px;
}

.draw-button {
  font-size: 20px;
  padding: 15px 50px;
  background: linear-gradient(135deg, var(--mall-primary), var(--mall-secondary));
  border: none;
  box-shadow: 0 0 30px rgba(0, 212, 255, 0.4);
}

.draw-button:hover:not(:disabled) {
  box-shadow: 0 0 50px rgba(0, 212, 255, 0.6);
}

.draw-tip {
  margin-top: 10px;
  color: #ff6600;
  font-size: 14px;
}

/* 区域标题 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 22px;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-icon {
  font-size: 24px;
}

/* 奖品列表 */
.prizes-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  margin-bottom: 40px;
}

.prize-item {
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  transition: all 0.3s;
}

.prize-item:hover {
  border-color: var(--mall-primary);
  transform: translateY(-4px);
}

.prize-icon {
  font-size: 40px;
  margin-bottom: 10px;
}

.prize-name {
  font-size: 14px;
  color: #fff;
  margin-bottom: 8px;
}

.prize-type {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 12px;
  display: inline-block;
}

.prize-type.credit {
  background: rgba(0, 255, 136, 0.2);
  color: #00ff88;
}

.prize-type.physical {
  background: rgba(255, 102, 0, 0.2);
  color: #ff6600;
}

/* 记录表格 */
.records-section {
  margin-bottom: 40px;
}

.records-table {
  background: rgba(26, 31, 58, 0.5);
  border-radius: 12px;
  overflow: hidden;
}

.cost-text {
  color: #ff6600;
  font-weight: 500;
}

.time-text {
  color: #888;
}

/* 签到区域 */
.checkin-section {
  background: rgba(26, 31, 58, 0.5);
  border-radius: 16px;
  padding: 25px;
}

.checkin-summary {
  display: flex;
  align-items: center;
  gap: 15px;
  color: #888;
}

.reset-btn {
  background: linear-gradient(135deg, var(--mall-primary), var(--mall-secondary));
  border: none;
}

.checkin-window {
  overflow-x: auto;
  padding: 10px 0;
  width: 100%;
}

.checkin-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 15px;
  min-width: 100%;
}

.checkin-day {
  width: 100%;
  height: 120px;
  background: rgba(0, 0, 0, 0.3);
  border: 2px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.checkin-day.available {
  border-color: var(--mall-primary);
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.15), rgba(0, 255, 136, 0.1));
}

.checkin-day.available:hover {
  transform: scale(1.05);
}

.checkin-day.completed {
  border-color: #00ff88;
  background: rgba(0, 255, 136, 0.1);
}

.checkin-day.current {
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.4);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { box-shadow: 0 0 20px rgba(0, 212, 255, 0.4); }
  50% { box-shadow: 0 0 30px rgba(0, 212, 255, 0.6); }
}

.day-number {
  font-size: 12px;
  color: #888;
}

.day-credit {
  font-size: 20px;
  font-weight: bold;
  color: var(--mall-primary);
}

.day-status {
  font-size: 12px;
}

.status-icon {
  font-size: 24px;
  color: #00ff88;
}

.status-text {
  color: #888;
}

/* 结果弹窗 */
.result-dialog :deep(.el-dialog) {
  background: linear-gradient(180deg, rgba(26, 31, 58, 0.98), rgba(10, 14, 26, 0.98));
  border: 1px solid rgba(0, 212, 255, 0.3);
}

.result-dialog :deep(.el-dialog__title) {
  color: #fff;
  text-align: center;
}

.result-content {
  text-align: center;
  padding: 20px;
}

.result-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.result-name {
  font-size: 24px;
  color: var(--mall-primary);
  margin-bottom: 10px;
}

.result-message {
  color: #888;
  font-size: 14px;
}

.accept-btn {
  width: 100%;
  background: linear-gradient(135deg, var(--mall-primary), var(--mall-secondary));
  border: none;
}
</style>

<template>
  <div class="lottery-page">
    <div class="lottery-container">
      <h1 class="page-title">🎰 幸运抽奖 🎰</h1>
      
      <!-- 用户信息 -->
      <div class="user-info">
        <div class="credit-display">
          <el-icon><Coin /></el-icon>
          <span>当前积分:{{ userCredit }}</span>
        </div>
        <div class="draw-count">
          <el-icon><Ticket /></el-icon>
          <span>已抽次数:{{ drawCount }}</span>
        </div>
      </div>

      <!-- 老虎机转盘 -->
      <div class="slot-machine">
        <div class="slot-window">
          <div class="slot-grid" :style="{ transform: `translateX(${reelOffset}px)` }">
            <div class="slot-item" v-for="(item, i) in reelItems" :key="i">
              <div class="item-content">
                <span class="item-icon">{{ item.icon }}</span>
                <span class="item-name">{{ item.name }}</span>
              </div>
            </div>
          </div>
        </div>
        <!-- 中奖指针 -->
        <div class="win-pointer"></div>
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
      </div>

      <!-- 中奖结果 -->
      <el-dialog v-model="resultDialogVisible" title="🎉 中奖啦!" width="400" class="result-dialog">
        <div class="result-content">
          <div class="result-icon">🎁</div>
          <div class="result-name">{{ lastPrize }}</div>
          <div class="result-message">{{ resultMessage }}</div>
        </div>
        <template #footer>
          <el-button type="primary" @click="resultDialogVisible = false">开心收下</el-button>
        </template>
      </el-dialog>

      <!-- 奖品列表 -->
      <div class="prizes-section">
        <h2 class="section-title">🎁 奖品列表</h2>
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
        <h2 class="section-title">📜 抽奖记录</h2>
        <el-table :data="records" style="width: 100%" max-height="300">
          <el-table-column prop="prizeName" label="奖品" />
          <el-table-column prop="prizeType" label="类型" width="80">
            <template #default="{ row }">
              <el-tag :type="row.prizeType === 1 ? 'success' : 'warning'" size="small">
                {{ row.prizeType === 1 ? '积分' : '实物' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="cost" label="消耗" width="80">
            <template #default="{ row }">
              -{{ row.cost }}积分
            </template>
          </el-table-column>
          <el-table-column label="时间" width="180">
            <template #default="{ row }">
              {{ formatTime(row.createdAt) }}
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 签到测试 -->
      <div class="checkin-test-section">
        <h2 class="section-title">📅 签到测试</h2>
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
                <div class="day-credit">+{{ day.credit }}积分</div>
                <div class="day-status">
                  <el-icon v-if="day.completed"><CircleCheckFilled /></el-icon>
                  <span v-else-if="day.available">可签到</span>
                  <span v-else>锁定</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="checkin-info">
          <span>当前连续签到:{{ currentCheckinDay }} / 7 天</span>
          <el-button v-if="allCompleted" type="primary" size="small" @click="resetCheckin" class="reset-btn">
            <el-icon><Refresh /></el-icon> 重新开始
          </el-button>
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

// 用户积分
const userCredit = ref(0)

// 监听积分变化,同步到 store
watch(userCredit, (newVal) => {
  userStore.userCredit = newVal
})

// 抽奖状态
const isDrawing = ref(false)
const drawCount = ref(0)

// 老虎机转盘(单排横向)
const reelItems = ref([])
const reelOffset = ref(0)
const itemWidth = 130 // 每个奖品卡片的宽度(含间距)
const visibleCount = 9 // 一行显示 9 个
let currentRowIndex = 0 // 当前显示的行索引

// 奖品列表
const prizes = ref<any[]>([])

// 抽奖记录
const records = ref<any[]>([])

// 中奖结果
const resultDialogVisible = ref(false)
const lastPrize = ref('')
const resultMessage = ref('')

// 签到测试
const checkinDays = reactive([
  { credit: 10, completed: false, available: true, current: true },
  { credit: 10, completed: false, available: false, current: false },
  { credit: 15, completed: false, available: false, current: false },
  { credit: 15, completed: false, available: false, current: false },
  { credit: 20, completed: false, available: false, current: false },
  { credit: 20, completed: false, available: false, current: false },
  { credit: 30, completed: false, available: false, current: false }
])

// 是否全部完成
const allCompleted = computed(() => {
  return checkinDays.every(d => d.completed)
})

const currentCheckinDay = computed(() => {
  const index = checkinDays.findIndex(d => !d.completed)
  return index === -1 ? 7 : index + 1
})

// 重置签到
const resetCheckin = () => {
  checkinDays.forEach((day, index) => {
    day.completed = false
    day.available = index === 0
    day.current = index === 0
  })
  ElMessage.success('签到已重置,可以重新签到')
}

// 奖品图标
const prizeIcons: Record<string, string> = {
  '50 积分': '💰',
  '75 积分': '💰💰',
  '100 积分': '💰💰💰',
  '抽纸(一袋)': '🧻',
  '洗衣液(一瓶)': '🧴',
  '电饭锅(一个)': '🍚',
  '电脑(一台)': '💻',
  '人类下一颗类地行星命名权(遥遥无期)': '🪐'
}

const getPrizeIcon = (name: string) => {
  return prizeIcons[name] || '🎁'
}

// 初始化老虎机物品
const initSlotItems = () => {
  const allItems = prizes.value.map(p => ({
    id: p.id,
    name: p.name,
    icon: getPrizeIcon(p.name)
  }))
  // 重复 2 组,保证只有一行滚动
  reelItems.value = [...allItems, ...allItems]
  reelOffset.value = 0
  currentRowIndex = 0
}

// 获取用户积分
const loadUserCredit = async () => {
  try {
    const res = await getUserCredit()
    const credit = (res as any).data?.credit || 0
    userCredit.value = credit
  } catch (e) {
    console.error('获取积分失败:', e)
  }
}

// 获取奖品列表
const loadPrizes = async () => {
  try {
    const res = await fetch('/api/lottery/prizes')
    const data = await res.json()
    prizes.value = data
    initSlotItems()
  } catch (e) {
    console.error('获取奖品列表失败:', e)
  }
}

// 获取抽奖记录
const loadRecords = async () => {
  try {
    const res = await getLotteryRecords()
    records.value = (res as any).data || []
    drawCount.value = records.value.length
  } catch (e) {
    console.error('获取抽奖记录失败:', e)
  }
}

// 抽奖
const handleDraw = async () => {
  if (userCredit.value < 100) {
    ElMessage.error('积分不足 100 积分')
    return
  }

  isDrawing.value = true

  try {
    // 开始滚动动画
    const animationPromise = spinReels()

    // 调用 API
    const result = await drawLottery()
    const resultData = result as any
    
    console.log('抽奖返回结果:', resultData)

    // 等待动画完成
    await animationPromise

    if (resultData.success) {
      lastPrize.value = resultData.prizeName
      resultMessage.value = resultData.message
      userCredit.value = resultData.remainingCredit
      drawCount.value++
      resultDialogVisible.value = true
      loadRecords()
    } else {
      ElMessage.error(resultData.message)
    }
  } catch (e: any) {
    console.error('抽奖失败:', e)
    ElMessage.error(e.response?.data?.message || '抽奖失败')
  } finally {
    isDrawing.value = false
  }
}

// 老虎机滚动动画 - 单向滚动
const spinReels = () => {
  return new Promise<void>((resolve) => {
    const randomIndex = Math.floor(Math.random() * 8) // 8 个奖品
    // 目标位置:第一组 + 随机位置
    const targetIndex = 9 + randomIndex // 第二组的随机位置
    
    // 计算偏移量
    const targetOffset = -(targetIndex * itemWidth)
    
    // 动画滚动
    const duration = 3000
    const startTime = Date.now()
    const startOffset = reelOffset.value
    
    const animate = () => {
      const elapsed = Date.now() - startTime
      const progress = Math.min(elapsed / duration, 1)
      // 缓动函数 - 先快后慢
      const easeOut = 1 - Math.pow(1 - progress, 4)
      reelOffset.value = startOffset + (targetOffset - startOffset) * easeOut
      
      if (progress < 1) {
        requestAnimationFrame(animate)
      } else {
        // 重置位置到第一组对应位置(实现循环效果)
        reelOffset.value = -(randomIndex * itemWidth)
        setTimeout(resolve, 500)
      }
    }
    
    requestAnimationFrame(animate)
  })
}

// 签到测试
const handleCheckin = async (index: number) => {
  // 检查是否可以签到
  if (index === 0) {
    // 第一天可以直接签到
  } else if (!checkinDays[index - 1].completed) {
    ElMessage.warning('请先完成前一天的签到')
    return
  }

  if (checkinDays[index].completed) {
    ElMessage.info('今天已经签到过了')
    return
  }

  if (!checkinDays[index].available && index !== 0) {
    ElMessage.warning('请先完成前一天的签到')
    return
  }

  // 测试模式:直接更新 UI,不调用后端 API
  checkinDays[index].completed = true
  if (index < checkinDays.length - 1) {
    checkinDays[index + 1].available = true
    checkinDays[index + 1].current = true
  }
  checkinDays[index].current = false

  const credit = checkinDays[index].credit
  userCredit.value += credit
  ElMessage.success(`测试签到成功!获得 ${credit} 积分`)
}

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserCredit()
  loadPrizes()
  loadRecords()
})
</script>

<style scoped>
.lottery-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1f3a 0%, #0a0e1a 100%);
  padding: 20px;
}

.lottery-container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  text-align: center;
  color: #00d4ff;
  font-size: 32px;
  margin-bottom: 30px;
  text-shadow: 0 0 20px rgba(0, 212, 255, 0.5);
}

.user-info {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-bottom: 30px;
}

.credit-display,
.draw-count {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: rgba(0, 212, 255, 0.1);
  border: 1px solid rgba(0, 212, 255, 0.3);
  border-radius: 8px;
  color: #fff;
  font-size: 16px;
}

.credit-display .el-icon {
  color: #ffd700;
}

.draw-count .el-icon {
  color: #00ff88;
}

/* 老虎机样式 */
.slot-machine {
  position: relative;
  background: linear-gradient(180deg, #2a2f4a 0%, #1a1f3a 100%);
  border: 3px solid #00d4ff;
  border-radius: 16px;
  padding: 10px;
  margin: 0 auto 30px auto;
  box-shadow: 0 0 30px rgba(0, 212, 255, 0.3);
  width: calc(140px * 7 + 10px * 6 + 20px);
}

.slot-window {
  overflow: hidden;
  background: #0a0e1a;
  border-radius: 12px;
  padding: 10px;
}

.slot-grid {
  display: flex;
  flex-wrap: nowrap;
  gap: 10px;
  transition: transform 0.5s ease-out;
  user-select: none;
  pointer-events: none;
}

.slot-item {
  width: 140px;
  height: 140px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.1), rgba(0, 255, 136, 0.1));
  border: 2px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  transition: all 0.3s;
}

.slot-item:hover {
  transform: scale(1.05);
  border-color: rgba(0, 212, 255, 0.5);
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.3);
}

.item-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.item-icon {
  font-size: 48px;
}

.item-name {
  font-size: 13px;
  color: #fff;
  text-align: center;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 中奖指针 */
.win-pointer {
  position: absolute;
  top: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 15px solid transparent;
  border-right: 15px solid transparent;
  border-top: 25px solid #ffd700;
  filter: drop-shadow(0 0 10px rgba(255, 215, 0, 0.8));
  z-index: 10;
}

.draw-section {
  text-align: center;
  margin-bottom: 40px;
}

.draw-button {
  font-size: 20px;
  padding: 15px 50px;
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  color: #000;
  font-weight: bold;
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.5);
}

.draw-button:hover:not(:disabled) {
  box-shadow: 0 0 40px rgba(0, 212, 255, 0.8);
  transform: scale(1.05);
}

.draw-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 奖品列表 */
.prizes-section,
.records-section,
.checkin-test-section {
  background: rgba(26, 31, 58, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 30px;
}

.section-title {
  color: #00d4ff;
  font-size: 20px;
  margin-bottom: 20px;
  text-align: center;
}

.prizes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 15px;
}

.prize-item {
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.1), rgba(0, 255, 136, 0.1));
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 15px;
  text-align: center;
  transition: all 0.3s;
}

.prize-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 212, 255, 0.3);
}

.prize-icon {
  font-size: 40px;
  margin-bottom: 8px;
}

.prize-name {
  color: #fff;
  font-size: 14px;
  margin-bottom: 8px;
}

.prize-type {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  display: inline-block;
}

.prize-type.credit {
  background: rgba(0, 255, 136, 0.2);
  color: #00ff88;
}

.prize-type.physical {
  background: rgba(255, 165, 0, 0.2);
  color: #ffa500;
}

/* 签到测试转盘 */
.checkin-test-section {
  margin-bottom: 30px;
  width: 100%;
}

.checkin-carousel {
  margin-bottom: 15px;
  width: 100%;
  max-width: 100%;
}

.checkin-window {
  overflow-x: auto;
  overflow-y: hidden;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 10px;
  width: 100%;
  max-width: 100%;
}

.checkin-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 10px;
  width: 100%;
}

.checkin-day {
  width: 100%;
  height: 100px;
  background: rgba(255, 255, 255, 0.05);
  border: 2px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 12px 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 6px;
}

.checkin-day.completed {
  background: linear-gradient(135deg, rgba(0, 255, 136, 0.2), rgba(0, 212, 255, 0.2));
  border-color: #00ff88;
}

.checkin-day.available {
  border-color: #00d4ff;
  background: rgba(0, 212, 255, 0.1);
}

.checkin-day.available:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 20px rgba(0, 212, 255, 0.4);
}

.checkin-day.current {
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.5);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { box-shadow: 0 0 15px rgba(0, 212, 255, 0.5); }
  50% { box-shadow: 0 0 25px rgba(0, 212, 255, 0.8); }
}

.checkin-day.completed .day-status {
  color: #00ff88;
}

.checkin-day.available .day-status {
  color: #00d4ff;
}

.day-number {
  color: #00d4ff;
  font-size: 13px;
  font-weight: bold;
}

.day-credit {
  color: #ffd700;
  font-size: 16px;
  font-weight: bold;
}

.day-status {
  font-size: 11px;
  color: #888;
}

.checkin-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  color: #888;
  font-size: 14px;
}

.reset-btn {
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.3), rgba(0, 255, 136, 0.3));
  border: 1px solid rgba(0, 212, 255, 0.5);
  color: #00d4ff;
}

.reset-btn:hover {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  color: #000;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.5);
}

.day-number {
  color: #00d4ff;
  font-size: 14px;
  margin-bottom: 5px;
}

.day-credit {
  color: #ffd700;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
}

.day-status {
  font-size: 12px;
  color: #888;
}

.checkin-day.completed .day-status {
  color: #00ff88;
}

.checkin-day.available .day-status {
  color: #00d4ff;
}

.checkin-info {
  text-align: center;
  color: #888;
  font-size: 14px;
}

/* 中奖结果对话框 */
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
  color: #00d4ff;
  margin-bottom: 10px;
  font-weight: bold;
}

.result-message {
  color: #888;
  font-size: 16px;
}

:deep(.result-dialog .el-dialog) {
  background: linear-gradient(135deg, #1a1f3a, #0a0e1a);
  border: 1px solid rgba(0, 212, 255, 0.3);
}

:deep(.result-dialog .el-dialog__title) {
  color: #fff;
}

:deep(.result-dialog .el-dialog__body) {
  color: #fff;
}
</style>

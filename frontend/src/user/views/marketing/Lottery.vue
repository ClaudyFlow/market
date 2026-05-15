<template>
  <div class="lottery-page">
    <h1 class="page-title">🎁 幸运抽奖 🎁</h1>

    <div class="lottery-container">
      <!-- 抽奖机 -->
      <div class="slot-machine">
        <div class="machine-header">
          <div class="chance-info">
            <el-icon><Ticket /></el-icon>
            <span>每日可抽 <strong>3</strong> 次</span>
          </div>
          <div class="chance-info today-chance">
            <el-icon><Clock /></el-icon>
            <span>今日还可抽 <strong>{{ freeChance }}</strong> 次</span>
          </div>
          <div class="credit-info">
            <el-icon><Coin /></el-icon>
            <span>我的积分：<strong>{{ userStore.credit }}</strong></span>
          </div>
        </div>

        <div class="slot-display">
          <!-- 上指针 -->
          <div class="win-pointer win-pointer-top">
            <div class="pointer-arrow">▼</div>
          </div>
          <div class="slot-row">
            <div
              v-for="(symbol, idx) in displaySymbols"
              :key="idx"
              class="slot-item"
              :class="{ active: idx === activeIndex }"
            >
              <span class="item-icon">{{ getPrizeIcon(symbol) }}</span>
              <span class="item-name">{{ symbol }}</span>
            </div>
          </div>
          <!-- 下指针 -->
          <div class="win-pointer win-pointer-bottom">
            <div class="pointer-arrow">▲</div>
          </div>
        </div>

        <div class="action-area">
          <button 
            class="spin-button" 
            @click="spin" 
            :disabled="spinning"
          >
            <el-icon v-if="!spinning"><MagicStick /></el-icon>
            <el-icon v-else><Loading /></el-icon>
            {{ spinning ? '抽奖中...' : '开始抽奖' }}
          </button>
          <div v-if="showResult" class="result-message" :class="{ win: isWin }">
            {{ resultText }}
          </div>
        </div>
      </div>

      <!-- 奖池列表 -->
      <div class="prize-pool-card">
        <div class="card-header">
          <el-icon><Present /></el-icon>
          <h3>奖池列表</h3>
        </div>
        <div class="prize-grid">
          <div
            v-for="(prize, idx) in prizeList"
            :key="idx"
            class="prize-card"
            :class="{ highlight: prize.name === lastPrize }"
          >
            <div class="prize-emoji">{{ getPrizeIcon(prize.name) }}</div>
            <div class="prize-info">
              <div class="prize-name">{{ prize.name }}</div>
              <div class="prize-prob">
                <el-progress 
                  :percentage="prize.probability" 
                  :stroke-width="4"
                  :show-text="false"
                  :color="getProgressColor(prize.probability)"
                />
                <span>{{ prize.probability }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 抽奖规则 -->
      <div class="rules-card">
        <div class="card-header">
          <el-icon><Document /></el-icon>
          <h3>抽奖规则</h3>
        </div>
        <ul class="rules-list">
          <li>
            <el-icon><Check /></el-icon>
            每日可抽奖 <strong>3</strong> 次，今日还可抽奖 <strong>{{ freeChance }}</strong> 次
          </li>
          <li>
            <el-icon><Coin /></el-icon>
            消耗 <strong>100 积分</strong> 可增加 1 次抽奖机会
          </li>
          <li>
            <el-icon><CircleCheck /></el-icon>
            中奖后奖品将自动发放至您的账户
          </li>
          <li>
            <el-icon><InfoFilled /></el-icon>
            本活动最终解释权归平台所有
          </li>
        </ul>
      </div>

      <!-- 我的奖品 -->
      <div class="my-prizes-card">
        <div class="card-header">
          <el-icon><Trophy /></el-icon>
          <h3>我的奖品</h3>
        </div>
        <el-table :data="myPrizes" style="width: 100%" v-loading="loadingPrizes" :header-cell-style="{ background: 'rgba(0, 212, 255, 0.1)', color: '#fff' }">
          <el-table-column prop="prizeName" label="奖品名称" min-width="180" />
          <el-table-column prop="prizeType" label="类型" width="100">
            <template #default="{ row }">
              <el-tag :type="getPrizeTypeColor(row.prizeType)" size="small">
                {{ getPrizeTypeText(row.prizeType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="drawTime" label="中奖时间" width="180" />
          <el-table-column prop="status" label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 'USED' ? 'success' : 'warning'" size="small">
                {{ row.status === 'USED' ? '已使用' : '未使用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button
                v-if="row.status === 'UNUSED' && row.prizeType === 'COUPON'"
                type="primary"
                size="small"
                @click="usePrize(row)"
              >
                使用
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Ticket, Coin, MagicStick, Loading, Present, Document,
  Trophy, Check, CircleCheck, InfoFilled, Clock
} from '@element-plus/icons-vue'
import request from '@user/api/request'
import { useUserStore } from '@common/stores/user'

interface Prize {
  id: number
  name: string
  type: string
  probability: number
  value?: number
}

interface MyPrize {
  id: number
  prizeName: string
  prizeType: string
  drawTime: string
  status: string
}

const userStore = useUserStore()

const spinning = ref(false)
const showResult = ref(false)
const isWin = ref(false)
const resultText = ref('')
const activeIndex = ref(3)
const displaySymbols = ref<string[]>([])
const lastPrize = ref('')
const freeChance = ref(3)
const myPrizes = ref<MyPrize[]>([])
const loadingPrizes = ref(false)

// 奖池数据
const prizeList = reactive<Prize[]>([
  { name: 'iPhone 15', type: 'PRODUCT', probability: 1 },
  { name: 'MacBook Pro', type: 'PRODUCT', probability: 2 },
  { name: 'AirPods Pro', type: 'PRODUCT', probability: 5 },
  { name: 'iPad Air', type: 'PRODUCT', probability: 5 },
  { name: 'Apple Watch', type: 'PRODUCT', probability: 7 },
  { name: '100 积分', type: 'CREDIT', probability: 20 },
  { name: '50 积分', type: 'CREDIT', probability: 25 },
  { name: '谢谢参与', type: 'NONE', probability: 35 }
])

const visibleCount = 7

// 获取奖品图标
const getPrizeIcon = (name: string): string => {
  const icons: Record<string, string> = {
    'iPhone 15': '📱',
    'MacBook Pro': '💻',
    'AirPods Pro': '🎧',
    'iPad Air': '📱',
    'Apple Watch': '⌚',
    '100 积分': '💰',
    '50 积分': '💰',
    '谢谢参与': '😢'
  }
  return icons[name] || '🎁'
}

// 获取奖品类型文本
const getPrizeTypeText = (type: string): string => {
  const texts: Record<string, string> = {
    'PRODUCT': '实物',
    'CREDIT': '积分',
    'COUPON': '优惠券',
    'NONE': '未中奖'
  }
  return texts[type] || type
}

// 获取奖品类型颜色
const getPrizeTypeColor = (type: string): string => {
  const colors: Record<string, string> = {
    'PRODUCT': 'success',
    'CREDIT': 'warning',
    'COUPON': 'primary',
    'NONE': 'info'
  }
  return colors[type] || 'info'
}

// 获取进度条颜色
const getProgressColor = (probability: number): string => {
  if (probability >= 30) return '#00d4ff'
  if (probability >= 15) return '#00ff88'
  if (probability >= 5) return '#ffd700'
  return '#ff6b6b'
}

// 初始化符号
const initSymbols = (): void => {
  displaySymbols.value = Array(visibleCount).fill('').map(() => {
    return prizeList[Math.floor(Math.random() * prizeList.length)].name
  })
}

// 抽奖
const spin = async (): Promise<void> => {
  if (spinning.value) return

  // 检查抽奖次数
  if (freeChance.value <= 0) {
    try {
      await ElMessageBox.confirm(
        '免费次数已用完，是否消耗 100 积分增加 1 次机会？',
        '提示',
        { 
          confirmButtonText: '确定', 
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      await request.post('/lottery/buy-chance')
      freeChance.value++
      ElMessage.success('购买成功')
    } catch (error) {
      return
    }
  }

  spinning.value = true
  showResult.value = false
  freeChance.value--

  // 从后端获取抽奖结果
  let prizeName = ''
  try {
    const res = await request.post('/lottery/draw')
    prizeName = res.data?.prizeName || (res as any).prizeName
  } catch (error) {
    // 如果接口失败，使用前端随机
    const rand = Math.random() * 100
    let cumulative = 0
    for (const prize of prizeList) {
      cumulative += prize.probability
      if (rand <= cumulative) {
        prizeName = prize.name
        break
      }
    }
  }

  // 开始动画
  let step = 0
  const totalSteps = 50

  const animate = (): void => {
    if (step < totalSteps) {
      // 移动符号
      displaySymbols.value.shift()
      displaySymbols.value.push(
        prizeList[Math.floor(Math.random() * prizeList.length)].name
      )

      // 减速效果
      if (step < 20) {
        setTimeout(animate, 50)
      } else if (step < 35) {
        setTimeout(animate, 100)
      } else {
        setTimeout(animate, 200)
      }

      step++
    } else {
      // 显示结果
      displaySymbols.value[activeIndex.value] = prizeName
      lastPrize.value = prizeName
      spinning.value = false
      showResult.value = true
      isWin.value = prizeName !== '谢谢参与'
      resultText.value = isWin.value
        ? `🎉 恭喜中奖！获得 ${prizeName}！`
        : '😢 很遗憾，未中奖'

      // 加载奖品列表
      loadMyPrizes()
    }
  }

  animate()
}

// 加载我的奖品
const loadMyPrizes = async (): Promise<void> => {
  loadingPrizes.value = true
  try {
    const res = await request.get('/lottery/records')
    myPrizes.value = (res.data as any) || []
  } catch (error) {
    console.error('加载奖品列表失败', error)
  } finally {
    loadingPrizes.value = false
  }
}

// 使用奖品
const usePrize = async (prize: MyPrize): Promise<void> => {
  try {
    await request.post(`/lottery/prizes/${prize.id}/use`)
    ElMessage.success('使用成功')
    loadMyPrizes()
  } catch (error) {
    ElMessage.error('使用失败')
  }
}

// 加载免费次数
const loadFreeChance = async (): Promise<void> => {
  try {
    const res = await request.get('/lottery/chance')
    freeChance.value = (res.data as any)?.chance || 3
  } catch (error) {
    console.error('加载抽奖次数失败', error)
  }
}

onMounted(() => {
  initSymbols()
  loadFreeChance()
  loadMyPrizes()
})
</script>

<style scoped>
.lottery-page {
  min-height: 100vh;
  background: linear-gradient(180deg, 
    rgba(0, 212, 255, 0.15) 0%, 
    rgba(10, 14, 26, 0.9) 100%);
  padding: 40px 20px;
}

.page-title {
  text-align: center;
  color: #fff;
  font-size: 36px;
  margin-bottom: 40px;
  text-shadow: 0 0 20px rgba(0, 212, 255, 0.5);
}

.lottery-container {
  max-width: 1000px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 抽奖机 */
.slot-machine {
  background: rgba(26, 31, 58, 0.8);
  border: 2px solid rgba(0, 212, 255, 0.3);
  border-radius: 20px;
  padding: 30px;
  backdrop-filter: blur(10px);
  box-shadow: 0 0 40px rgba(0, 212, 255, 0.2);
}

.machine-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 12px;
}

.chance-info,
.credit-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  font-size: 16px;
}

.chance-info .el-icon,
.credit-info .el-icon {
  color: #00d4ff;
  font-size: 20px;
}

.chance-info strong,
.credit-info strong {
  color: #00ff88;
  font-size: 18px;
}

.today-chance strong {
  color: #ffd700;
}

/* 抽奖显示区域 */
.slot-display {
  position: relative;
  margin-bottom: 24px;
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
  color: #ff4757;
  filter: drop-shadow(0 0 10px rgba(255, 71, 87, 0.6));
  animation: pointerPulse 1.5s infinite;
}

@keyframes pointerPulse {
  0%, 100% {
    transform: scale(1);
    filter: drop-shadow(0 0 10px rgba(255, 71, 87, 0.6));
  }
  50% {
    transform: scale(1.2);
    filter: drop-shadow(0 0 20px rgba(255, 71, 87, 0.8));
  }
}

.slot-row {
  display: flex;
  justify-content: center;
  gap: 8px;
  padding: 16px;
  background: linear-gradient(180deg, 
    rgba(44, 62, 80, 0.9) 0%, 
    rgba(52, 73, 94, 0.8) 50%, 
    rgba(44, 62, 80, 0.9) 100%);
  border: 2px solid rgba(0, 212, 255, 0.4);
  border-radius: 16px;
  box-shadow: inset 0 0 20px rgba(0, 0, 0, 0.5),
              0 0 20px rgba(0, 212, 255, 0.2);
  position: relative;
}

.slot-item {
  width: 120px;
  height: 90px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, 
    rgba(51, 51, 51, 0.8) 0%, 
    rgba(85, 85, 85, 0.6) 100%);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  opacity: 0.6;
  transition: all 0.2s ease;
}

.slot-item.active {
  opacity: 1;
  background: linear-gradient(180deg, 
    #ffd700 0%, 
    #ffb300 50%, 
    #ff8c00 100%);
  border: 2px solid #ffd700;
  box-shadow: 0 0 30px rgba(255, 215, 0, 0.6),
              inset 0 0 10px rgba(255, 255, 255, 0.3);
  transform: scale(1.1);
  z-index: 5;
}

.item-icon {
  font-size: 28px;
  margin-bottom: 4px;
}

.item-name {
  font-size: 12px;
  color: #fff;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.slot-item.active .item-name {
  color: #1a1f2e;
  font-weight: bold;
}

/* 操作区域 */
.action-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.spin-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 48px;
  font-size: 20px;
  font-weight: bold;
  color: #1a1f2e;
  background: linear-gradient(135deg, #ffd700 0%, #ffb300 100%);
  border: none;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 0 30px rgba(255, 215, 0, 0.4);
}

.spin-button:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 0 50px rgba(255, 215, 0, 0.6);
}

.spin-button:disabled {
  background: linear-gradient(135deg, #666 0%, #888 100%);
  color: #aaa;
  cursor: not-allowed;
  box-shadow: none;
}

.spin-button .el-icon {
  font-size: 24px;
}

.result-message {
  font-size: 18px;
  font-weight: bold;
  padding: 12px 24px;
  border-radius: 50px;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  min-height: 44px;
  display: flex;
  align-items: center;
}

.result-message.win {
  background: linear-gradient(135deg, 
    rgba(255, 215, 0, 0.3) 0%, 
    rgba(255, 179, 0, 0.2) 100%);
  color: #ffd700;
  box-shadow: 0 0 30px rgba(255, 215, 0, 0.4);
  animation: glow 1s ease-in-out infinite alternate;
}

@keyframes glow {
  from { 
    box-shadow: 0 0 10px rgba(255, 215, 0, 0.4); 
  }
  to { 
    box-shadow: 0 0 30px rgba(255, 215, 0, 0.8); 
  }
}

/* 卡片通用样式 */
.prize-pool-card,
.rules-card,
.my-prizes-card {
  background: rgba(26, 31, 58, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 16px;
  padding: 24px;
  backdrop-filter: blur(10px);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
}

.card-header .el-icon {
  color: #00d4ff;
  font-size: 24px;
}

.card-header h3 {
  color: #fff;
  font-size: 20px;
  margin: 0;
}

/* 奖池网格 */
.prize-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.prize-card {
  display: grid;
  grid-template-columns: 50px 1fr;
  grid-template-rows: 1fr 1fr;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.prize-card:hover {
  border-color: #00d4ff;
  transform: translateY(-2px);
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.2);
}

.prize-card.highlight {
  background: linear-gradient(135deg, 
    rgba(255, 215, 0, 0.2) 0%, 
    rgba(255, 179, 0, 0.1) 100%);
  border-color: #ffd700;
  box-shadow: 0 0 30px rgba(255, 215, 0, 0.4);
  animation: pulse 1s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.02); }
}

.prize-emoji {
  grid-row: 1 / 3;
  font-size: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.prize-info {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.prize-name {
  color: #fff;
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.prize-prob {
  display: flex;
  align-items: center;
  gap: 8px;
}

.prize-prob span {
  color: #88aacc;
  font-size: 12px;
  flex-shrink: 0;
}

/* 规则列表 */
.rules-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.rules-list li {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  color: #ccc;
  font-size: 15px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.rules-list li:last-child {
  border-bottom: none;
}

.rules-list li .el-icon {
  color: #00ff88;
  font-size: 18px;
  flex-shrink: 0;
}

.rules-list li strong {
  color: #00d4ff;
}

/* 表格样式覆盖 */
:deep(.el-table) {
  background: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: rgba(0, 212, 255, 0.1);
  --el-table-text-color: #ccc;
  --el-table-header-text-color: #fff;
  --el-table-row-hover-bg-color: rgba(0, 212, 255, 0.1);
  --el-table-border-color: rgba(255, 255, 255, 0.1);
}

:deep(.el-table__inner-wrapper::before) {
  background: transparent;
}

/* 响应式 */
@media (max-width: 768px) {
  .lottery-page {
    padding: 20px 12px;
  }

  .page-title {
    font-size: 28px;
  }

  .slot-machine {
    padding: 20px;
  }

  .machine-header {
    flex-direction: column;
    gap: 12px;
  }

  .slot-row {
    gap: 4px;
    padding: 12px 8px;
  }

  .slot-item {
    width: 80px;
    height: 70px;
  }

  .item-icon {
    font-size: 20px;
  }

  .item-name {
    font-size: 10px;
  }

  .spin-button {
    padding: 12px 32px;
    font-size: 18px;
  }

  .prize-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
}
</style>

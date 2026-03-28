<template>
  <div class="lottery-page">
    <h1 class="page-title">幸运抽奖</h1>

    <div class="slot-machine">
      <div class="slot-header">
        <button class="spin-btn" id="spinBtn" @click="spin" :disabled="spinning">
          {{ spinning ? '抽奖中...' : '开始抽奖' }}
        </button>
        <div class="result" v-if="showResult" :class="{ 'win': isWin }">
          <span>{{ resultText }}</span>
        </div>
      </div>

      <div class="slot-row-wrapper">
        <div class="pointer">▼</div>
        <div class="slot-row" ref="slotRow">
          <div
            v-for="(symbol, idx) in displaySymbols"
            :key="idx"
            class="slot-symbol"
            :class="{ 'active': idx === activeIndex }"
          >
            {{ symbol }}
          </div>
        </div>
      </div>

      <div class="prize-pool">
        <h3>奖池列表</h3>
        <div class="prize-grid">
          <div
            v-for="(prize, idx) in prizes"
            :key="idx"
            class="prize-item"
            :class="{ 'highlight': prize.name === lastPrize }"
          >
            <span class="prize-icon">{{ getPrizeIcon(prize.name) }}</span>
            <span class="prize-name">{{ prize.name }}</span>
            <span class="prize-probability">概率：{{ prize.probability }}%</span>
          </div>
        </div>
      </div>

      <div class="lottery-rules">
        <h3>抽奖规则</h3>
        <ul>
          <li>每日可免费抽奖 {{ freeChance }} 次</li>
          <li>消耗 100 积分可增加 1 次抽奖机会</li>
          <li>中奖后奖品将发放至您的账户</li>
          <li>本活动最终解释权归平台所有</li>
        </ul>
      </div>
    </div>

    <div class="my-prizes">
      <h3>我的奖品</h3>
      <el-table :data="myPrizes" style="width: 100%" v-loading="loadingPrizes">
        <el-table-column prop="prizeName" label="奖品名称" width="200" />
        <el-table-column prop="prizeType" label="奖品类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getPrizeTypeColor(row.prizeType)">
              {{ getPrizeTypeText(row.prizeType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="drawTime" label="中奖时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'USED' ? 'success' : 'primary'">
              {{ row.status === 'USED' ? '已使用' : '未使用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'UNUSED' && row.prizeType === 'COUPON'"
              type="primary"
              size="small"
              @click="usePrize(row)"
            >
              立即使用
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/common/api/request'
import { useUserStore } from '@/common/stores/user'

const userStore = useUserStore()

const spinning = ref(false)
const showResult = ref(false)
const isWin = ref(false)
const resultText = ref('')
const activeIndex = ref(4)
const displaySymbols = ref([])
const lastPrize = ref('')
const freeChance = ref(3)
const myPrizes = ref([])
const loadingPrizes = ref(false)

const prizes = reactive([
  { name: 'iPhone 15', probability: 1 },
  { name: 'MacBook Pro', probability: 2 },
  { name: 'AirPods Pro', probability: 5 },
  { name: 'iPad Air', probability: 5 },
  { name: 'Apple Watch', probability: 7 },
  { name: '100 积分', probability: 20 },
  { name: '50 积分', probability: 25 },
  { name: '谢谢参与', probability: 35 }
])

const visibleCount = 9

const getPrizeIcon = (name) => {
  const icons = {
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

const getPrizeTypeText = (type) => {
  const texts = {
    'PRODUCT': '实物',
    'CREDIT': '积分',
    'COUPON': '优惠券'
  }
  return texts[type] || type
}

const getPrizeTypeColor = (type) => {
  const colors = {
    'PRODUCT': 'success',
    'CREDIT': 'warning',
    'COUPON': 'primary'
  }
  return colors[type] || 'info'
}

const initSymbols = () => {
  displaySymbols.value = Array(visibleCount).fill().map(() => {
    return prizes[Math.floor(Math.random() * prizes.length)].name
  })
}

const spin = async () => {
  if (spinning.value) return

  // 检查抽奖次数
  if (freeChance.value <= 0) {
    // 可以使用积分购买抽奖次数
    try {
      await ElMessageBox.confirm(
        '免费次数已用完，是否消耗 100 积分增加 1 次机会？',
        '提示',
        { confirmButtonText: '确定', cancelButtonText: '取消' }
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
    prizeName = res.data?.prizeName || res.prizeName
  } catch (error) {
    // 如果接口失败，使用前端随机
    const rand = Math.random() * 100
    let cumulative = 0
    for (const prize of prizes) {
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
  const finalIndex = 4

  const animate = () => {
    if (step < totalSteps) {
      // 移动符号
      displaySymbols.value.shift()
      displaySymbols.value.push(
        prizes[Math.floor(Math.random() * prizes.length)].name
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
      displaySymbols.value[finalIndex] = prizeName
      lastPrize.value = prizeName
      spinning.value = false
      showResult.value = true
      isWin.value = prizeName !== '谢谢参与'
      resultText.value = isWin.value
        ? `恭喜中奖！获得 ${prizeName}！`
        : '很遗憾，未中奖'

      // 加载奖品列表
      loadMyPrizes()
    }
  }

  animate()
}

const loadMyPrizes = async () => {
  loadingPrizes.value = true
  try {
    const res = await request.get('/lottery/prizes')
    myPrizes.value = res.data || res || []
  } catch (error) {
    console.error('加载奖品列表失败', error)
  } finally {
    loadingPrizes.value = false
  }
}

const usePrize = async (prize) => {
  try {
    await request.post(`/lottery/prizes/${prize.id}/use`)
    ElMessage.success('使用成功')
    loadMyPrizes()
  } catch (error) {
    ElMessage.error('使用失败')
  }
}

const loadFreeChance = async () => {
  try {
    const res = await request.get('/lottery/chance')
    freeChance.value = res.data?.chance || res.chance || 3
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.page-title {
  text-align: center;
  color: #fff;
  font-size: 36px;
  margin-bottom: 40px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.slot-machine {
  max-width: 600px;
  margin: 0 auto 40px;
  background: linear-gradient(145deg, #ffffff 0%, #f8f9fa 100%);
  border-radius: 25px;
  padding: 40px 30px;
  border: 3px solid #e9ecef;
  position: relative;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

.slot-machine::before {
  content: '';
  position: absolute;
  top: -3px;
  left: -3px;
  right: -3px;
  bottom: -3px;
  background: linear-gradient(45deg, #ffd700, #ffb300, #ff8c00);
  border-radius: 28px;
  z-index: -1;
}

.slot-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.spin-btn {
  font-size: 20px;
  padding: 12px 40px;
  border-radius: 12px;
  background: linear-gradient(90deg, #ffd700 0%, #ffb300 100%);
  color: #8b0000;
  border: none;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(255, 215, 0, 0.4);
}

.spin-btn:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(255, 215, 0, 0.6);
}

.spin-btn:disabled {
  background: #ccc;
  color: #666;
  cursor: not-allowed;
}

.result {
  font-size: 18px;
  font-weight: bold;
  min-height: 24px;
  padding: 8px 16px;
  border-radius: 8px;
}

.result.win {
  color: #d4af37;
  background: rgba(212, 175, 55, 0.2);
  animation: glow 1s ease-in-out infinite alternate;
}

@keyframes glow {
  from { box-shadow: 0 0 10px rgba(212, 175, 55, 0.5); }
  to { box-shadow: 0 0 20px rgba(212, 175, 55, 0.8); }
}

.slot-row-wrapper {
  position: relative;
  height: 100px;
  margin-bottom: 20px;
}

.pointer {
  position: absolute;
  left: 50%;
  top: 0;
  transform: translateX(-50%) translateY(-100%);
  font-size: 32px;
  color: #e74c3c;
  z-index: 2;
}

.slot-row {
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(180deg, #2c3e50 0%, #34495e 50%, #2c3e50 100%);
  border-radius: 15px;
  padding: 10px;
  border: 2px solid #ffd700;
  box-shadow: inset 0 2px 10px rgba(0, 0, 0, 0.3);
}

.slot-symbol {
  width: 100px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: #fff;
  opacity: 0.5;
  margin: 0 4px;
  border-radius: 8px;
  background: linear-gradient(135deg, #333 0%, #555 100%);
  transition: all 0.2s;
  text-align: center;
  overflow: hidden;
  flex-shrink: 0;
}

.slot-symbol.active {
  opacity: 1;
  color: #2c3e50;
  background: linear-gradient(180deg, #ffd700 0%, #ffb300 50%, #ff8c00 100%);
  font-weight: bold;
  box-shadow: 0 0 20px 5px #ffd700;
  border: 3px solid #ffd700;
  transform: scale(1.1);
  z-index: 3;
}

.prize-pool,
.lottery-rules,
.my-prizes {
  max-width: 800px;
  margin: 0 auto 30px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.prize-pool h3,
.lottery-rules h3,
.my-prizes h3 {
  color: #2c3e50;
  margin-bottom: 16px;
  text-align: center;
}

.prize-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
}

.prize-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px;
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.1), rgba(0, 255, 136, 0.05));
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 8px;
  transition: all 0.3s;
}

.prize-item.highlight {
  background: linear-gradient(135deg, rgba(255, 215, 0, 0.2), rgba(255, 170, 0, 0.1));
  border-color: #ffd700;
  animation: pulse 1s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.prize-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.prize-name {
  font-size: 14px;
  color: #2c3e50;
  font-weight: bold;
}

.prize-probability {
  font-size: 12px;
  color: #888;
  margin-top: 4px;
}

.lottery-rules ul {
  list-style: none;
  padding: 0;
}

.lottery-rules li {
  padding: 8px 0;
  padding-left: 24px;
  position: relative;
  color: #555;
}

.lottery-rules li::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: #00d4ff;
  font-weight: bold;
}
</style>

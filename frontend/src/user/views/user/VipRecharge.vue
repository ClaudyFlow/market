<template>
  <div class="vip-recharge">
    <div class="container">
      <div class="page-header">
        <h2>VIP 充值中心</h2>
        <p class="subtitle">充值成长值，升级会员等级，享受更多权益</p>
      </div>

      <!-- VIP Levels Display -->
      <div class="vip-levels-card">
        <h3>我的VIP等级</h3>
        <div class="current-level" v-if="currentVip">
          <div class="level-badge" :style="{background: currentVip.backgroundColor || '#409EFF'}">
            <span class="level-name">{{ currentVip.name || '普通会员' }}</span>
          </div>
          <div class="level-info">
            <div class="growth-value">成长值：{{ currentVip.growthValue || 0 }}</div>
            <div class="progress-bar">
              <div class="progress" :style="{width: (currentVip.progressPercent || 0) + '%'}"></div>
            </div>
            <div class="next-level-tip" v-if="currentVip.nextLevel">
              距离下一级还需 {{ currentVip.remainingGrowth || 0 }} 成长值
            </div>
          </div>
        </div>
      </div>

      <!-- Amount Selection -->
      <div class="recharge-card">
        <h3>选择充值金额</h3>
        <div class="amount-grid">
          <div 
            v-for="amt in amounts" 
            :key="amt"
            class="amount-item"
            :class="{active: selectedAmount === amt}"
            @click="selectedAmount = amt"
          >
            <span class="amount-value">{{ amt }}</span>
            <span class="amount-label">元</span>
            <span class="growth-tip">+{{ amt * 10 }}成长值</span>
          </div>
        </div>
        
        <div class="custom-amount">
          <el-input-number v-model="customAmount" :min="1" :max="10000" placeholder="自定义金额" />
          <span class="growth-label">= {{ customAmount * 10 }} 成长值</span>
        </div>

        <div class="action-row">
          <el-button 
            type="primary" 
            size="large" 
            :loading="processing"
            :disabled="!finalAmount"
            @click="handleRecharge"
          >
            立即充值（{{ finalAmount }}元 = {{ finalAmount * 10 }}成长值）
          </el-button>
        </div>
      </div>

      <!-- Recharge History -->
      <div class="history-card" v-if="history.length">
        <h3>充值记录</h3>
        <el-table :data="history" stripe>
          <el-table-column prop="orderNo" label="订单号" width="200" />
          <el-table-column prop="amount" label="充值金额" width="120">
            <template #default="{row}">¥{{ row.amount }}</template>
          </el-table-column>
          <el-table-column prop="growthValue" label="成长值" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{row}">
              <el-tag :type="row.status === 'PAID' ? 'success' : 'warning'" size="small">
                {{ row.status === 'PAID' ? '已完成' : '待支付' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="时间" />
        </el-table>
      </div>
    </div>

    <!-- Upgrade Celebration Modal -->
    <el-dialog v-model="showUpgradeModal" title="恭喜升级！" width="400px" :close-on-click-modal="false">
      <div class="upgrade-celebration">
        <div class="confetti-container">
          <div v-for="i in 30" :key="i" class="confetti" :style="confettiStyle(i)"></div>
        </div>
        <div class="upgrade-icon">🎉</div>
        <div class="upgrade-message">
          <p>恭喜您升至 <strong>{{ newLevelName }}</strong>！</p>
          <p class="sub-tip">感谢您的支持，继续购物获取更多权益吧~</p>
        </div>
      </div>
      <template #footer>
        <el-button type="primary" @click="showUpgradeModal = false">太好了！</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { get, post } from '@user/api/request'
import { ElMessage } from 'element-plus'

const amounts = [10, 50, 100, 200, 500]
const selectedAmount = ref(null)
const customAmount = ref(null)
const processing = ref(false)
const currentVip = ref(null)
const history = ref([])
const showUpgradeModal = ref(false)
const newLevelName = ref('')

const finalAmount = computed(() => customAmount.value || selectedAmount.value || 0)

const confettiStyle = (i) => {
  const colors = ['#FF6B6B', '#4ECDC4', '#FFE66D', '#95E1D3', '#F38181', '#AA96DA']
  return {
    left: Math.random() * 100 + '%',
    background: colors[i % colors.length],
    animationDelay: Math.random() * 2 + 's',
    animationDuration: (Math.random() * 2 + 2) + 's'
  }
}

const fetchVipInfo = async () => {
  try {
    const res = await get('/user/vip/my')
    currentVip.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const fetchHistory = async () => {
  try {
    const res = await get('/user/vip/recharge/records')
    history.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const handleRecharge = async () => {
  if (!finalAmount.value) return
  processing.value = true
  
  const oldLevel = currentVip.value?.currentLevel?.level || 0
  const oldVipName = currentVip.value?.currentLevel?.name || '普通会员'
  
  try {
    // Step 1: Create order
    const createRes = await post('/user/vip/recharge', { amount: finalAmount.value })
    const orderNo = createRes.data.orderNo
    
    // Step 2: Simulate payment (balance)
    await post('/user/vip/recharge/' + orderNo + '/pay', { paymentMethod: 'balance' })
    
    ElMessage.success('充值成功！')
    
    // Refresh VIP info
    const newRes = await get('/user/vip/my')
    currentVip.value = newRes.data
    
    // Check if upgraded
    const newLevel = newRes.data?.currentLevel?.level || 0
    const levelNames = ['普通会员', '白银会员', '黄金会员', '铂金会员', '钻石会员', '至尊会员']
    
    if (newLevel > oldLevel) {
      newLevelName.value = levelNames[newLevel] || '更高等级'
      showUpgradeModal.value = true
    }
    
    // Refresh history
    await fetchHistory()
  } catch (e) {
    ElMessage.error(e.message || '充值失败')
  } finally {
    processing.value = false
  }
}

onMounted(() => {
  fetchVipInfo()
  fetchHistory()
})
</script>

<style scoped>
.vip-recharge { min-height: 100vh; background: #f5f5f5; padding: 20px; }
.container { max-width: 800px; margin: 0 auto; }
.page-header { text-align: center; margin-bottom: 24px; }
.page-header h2 { font-size: 28px; margin-bottom: 8px; }
.subtitle { color: #888; font-size: 14px; }

.vip-levels-card, .recharge-card, .history-card {
  background: #fff; border-radius: 12px; padding: 24px; margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}
.vip-levels-card h3, .recharge-card h3, .history-card h3 {
  font-size: 18px; margin-bottom: 16px; border-left: 4px solid #409EFF;
  padding-left: 12px;
}

.current-level { display: flex; align-items: center; gap: 20px; }
.level-badge {
  width: 80px; height: 80px; border-radius: 50%; display: flex;
  align-items: center; justify-content: center; color: #fff; font-size: 14px;
}
.level-name { font-size: 16px; font-weight: bold; text-align: center; }
.growth-value { font-size: 16px; color: #333; margin-bottom: 8px; }
.progress-bar {
  width: 200px; height: 8px; background: #eee; border-radius: 4px; overflow: hidden;
}
.progress { height: 100%; background: linear-gradient(90deg, #409EFF, #67C23A); transition: width 0.3s; }
.next-level-tip { font-size: 12px; color: #888; margin-top: 4px; }

.amount-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 12px; margin-bottom: 20px; }
.amount-item {
  border: 2px solid #eee; border-radius: 8px; padding: 16px 8px; text-align: center;
  cursor: pointer; transition: all 0.2s;
}
.amount-item:hover { border-color: #409EFF; }
.amount-item.active { border-color: #409EFF; background: #ecf5ff; }
.amount-value { font-size: 24px; font-weight: bold; color: #333; }
.amount-label { font-size: 12px; color: #888; }
.growth-tip { display: block; font-size: 11px; color: #67C23A; margin-top: 4px; }

.custom-amount { display: flex; align-items: center; gap: 12px; margin-bottom: 20px; }
.growth-label { color: #67C23A; font-size: 14px; }

.action-row { text-align: center; }
.action-row .el-button { width: 300px; height: 48px; font-size: 16px; }

/* Upgrade celebration modal */
.upgrade-celebration { text-align: center; padding: 20px; position: relative; overflow: hidden; }
.upgrade-icon { font-size: 64px; margin-bottom: 16px; animation: bounce 1s infinite; }
.upgrade-message p { font-size: 18px; margin-bottom: 8px; }
.sub-tip { font-size: 14px; color: #888; }
@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.confetti-container { position: absolute; top: 0; left: 0; width: 100%; height: 100%; pointer-events: none; }
.confetti {
  position: absolute; top: -10px; width: 10px; height: 10px; border-radius: 2px;
  animation: fall linear forwards;
}
@keyframes fall {
  to { transform: translateY(400px) rotate(720deg); opacity: 0; }
}
</style>

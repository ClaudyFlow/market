<template>
  <div class="vip-center">
    <!-- VIP 头部展示区 -->
    <header class="vip-header">
      <div class="user-info">
        <div class="avatar-wrapper">
          <el-avatar :size="80" :src="userAvatar" class="user-avatar" />
        </div>
        <div class="user-details">
          <div class="vip-level-badge">
            <img :src="currentLevel.icon" alt="" class="level-icon" v-if="currentLevel.icon" />
            <span class="level-name" :style="{ color: currentLevel.textColor || '#fff9c4' }">{{ currentLevel.name }}</span>
          </div>
          <div class="user-name" style="color: #fff9c4; text-shadow: 0 0 10px rgba(255, 249, 196, 0.6);">{{ userName }}</div>
        </div>
      </div>

      <div class="growth-progress">
          <div class="progress-title">
            <span>成长值</span>
            <span class="growth-value">{{ growthValue }} / {{ nextLevel?.growthValueRequired || 'MAX' }}</span>
          </div>
          <div class="progress-bar-wrapper">
            <el-progress
              :percentage="progressPercent"
              :stroke-width="12"
              :show-text="false"
              color="linear-gradient(90deg, #ffd700, #ffaa00)"
            />
          </div>
          <div class="progress-tip" v-if="nextLevel">
            <el-icon><TrendCharts /></el-icon>
            <span>再获得 {{ remainingGrowth }} 成长值可升级至 {{ nextLevel.name }}</span>
          </div>
        </div>
    </header>
    <section class="vip-benefits">
      <h2 class="section-title">VIP 专属权益</h2>
      <div class="benefits-grid">
        <div class="benefit-item" v-for="benefit in benefitsList" :key="benefit.label">
          <div class="benefit-icon">
            <el-icon :size="28"><component :is="benefit.icon" /></el-icon>
          </div>
          <div class="benefit-label">{{ benefit.label }}</div>
          <div class="benefit-value">{{ benefit.value }}</div>
        </div>
      </div>
    </section>

    <!-- 每日礼包 -->
    <section class="gift-section">
      <div class="section-header">
        <h2 class="section-title">每日礼包</h2>
        <el-tag type="warning" v-if="dailyGifts.length > 0">每日刷新</el-tag>
      </div>
      <div class="gift-grid">
        <div 
          v-for="gift in dailyGifts" 
          :key="gift.id" 
          class="gift-card"
          :class="{ 'can-claim': gift.canClaim, 'claimed': gift.claimed }"
          @click="claimGiftHandler(gift)"
        >
          <div class="gift-image">
            <img :src="gift.image" alt="" />
            <div class="gift-overlay" v-if="!gift.canClaim && !gift.claimed">
              <el-icon><Lock /></el-icon>
              <span>等级不足</span>
            </div>
          </div>
          <div class="gift-info">
            <div class="gift-name">{{ gift.name }}</div>
            <div class="gift-reward">
              <el-icon><Ticket /></el-icon>
              <span>+{{ gift.creditReward }} 积分</span>
            </div>
            <div class="gift-status" v-if="gift.remainingSeconds !== undefined && gift.remainingSeconds > 0">
              <el-icon><Clock /></el-icon>
              <span>{{ formatCountdown(gift.remainingSeconds) }}</span>
            </div>
            <div class="gift-status claimed" v-else-if="gift.claimed">
              <el-icon><CircleCheck /></el-icon>
              <span>已领取</span>
            </div>
            <el-button 
              v-else-if="gift.canClaim" 
              type="warning" 
              size="small" 
              class="claim-btn"
              @click.stop="claimGiftHandler(gift)"
            >
              立即领取
            </el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- 每月礼包 -->
    <section class="gift-section">
      <div class="section-header">
        <h2 class="section-title">每月礼包</h2>
        <el-tag type="success" v-if="monthlyGifts.length > 0">每月刷新</el-tag>
      </div>
      <div class="gift-grid">
        <div 
          v-for="gift in monthlyGifts" 
          :key="gift.id" 
          class="gift-card monthly"
          :class="{ 'can-claim': gift.canClaim, 'claimed': gift.claimed }"
          @click="claimGiftHandler(gift)"
        >
          <div class="gift-image">
            <img :src="gift.image" alt="" />
            <div class="gift-overlay" v-if="!gift.canClaim && !gift.claimed">
              <el-icon><Lock /></el-icon>
              <span>等级不足</span>
            </div>
          </div>
          <div class="gift-info">
            <div class="gift-name">{{ gift.name }}</div>
            <div class="gift-reward">
              <el-icon><Ticket /></el-icon>
              <span>+{{ gift.creditReward }} 积分</span>
            </div>
            <div class="gift-status" v-if="gift.remainingDays !== undefined && gift.remainingDays > 0">
              <el-icon><Clock /></el-icon>
              <span>可领取：{{ gift.nextAvailable ? formatDate(gift.nextAvailable) : '-' }}</span>
            </div>
            <div class="gift-status claimed" v-else-if="gift.claimed">
              <el-icon><CircleCheck /></el-icon>
              <span>已领取</span>
            </div>
            <el-button 
              v-else-if="gift.canClaim" 
              type="success" 
              size="small" 
              class="claim-btn"
              @click.stop="claimGiftHandler(gift)"
            >
              立即领取
            </el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- VIP 充值 -->
    <section class="recharge-section">
      <h2 class="section-title">VIP 充值</h2>
      <div class="recharge-desc">充值可获得成长值，1 元 = 10 成长值</div>
      <div class="recharge-grid">
        <div 
          v-for="option in rechargeOptions" 
          :key="option.amount"
          class="recharge-card"
          :class="{ active: selectedAmount === option.amount }"
          @click="selectedAmount = option.amount"
        >
          <div class="recharge-amount">¥{{ option.amount }}</div>
          <div class="recharge-growth">+{{ option.growth }} 成长值</div>
        </div>
      </div>
      <div class="recharge-action">
        <el-button 
          type="primary" 
          size="large" 
          class="recharge-submit"
          :disabled="!selectedAmount"
          @click="handleRecharge"
        >
          立即充值
        </el-button>
      </div>
    </section>

    <!-- 充值记录 -->
    <section class="records-section">
      <h2 class="section-title">充值记录</h2>
      <el-table :data="rechargeRecords" class="sci-table" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="180">
          <template #default="{ row }">
            <span>{{ row.orderNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }">
            <span class="amount-text">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="growthValue" label="成长值" width="100">
          <template #default="{ row }">
            <span>+{{ row.growthValue }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'PAID' ? 'success' : 'warning'" size="small">
              {{ row.status === 'PAID' ? '已支付' : '待支付' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template #default="{ row }">
            <span>{{ row.createdAt }}</span>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <!-- VIP 等级说明 -->
    <section class="levels-section">
      <h2 class="section-title">VIP 等级说明</h2>
      <el-table :data="vipLevels" class="sci-table level-table" style="width: 100%">
        <el-table-column prop="level" label="等级" width="80">
          <template #default="{ row }">
            <span>{{ row.level }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="等级名称" width="120">
          <template #default="{ row }">
            <span>{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="growthValueRequired" label="所需成长值" width="100">
          <template #default="{ row }">
            <span>{{ row.growthValueRequired }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="discountRate" label="折扣" width="80">
          <template #default="{ row }">
            <span v-if="row.discountRate < 1">{{ (row.discountRate * 10).toFixed(0) }}折</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="dailyCredit" label="每日积分" width="80">
          <template #default="{ row }">
            <span>{{ row.dailyCredit }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="monthlyCredit" label="每月积分" width="80">
          <template #default="{ row }">
            <span>{{ row.monthlyCredit }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="freeShippingCount" label="免邮次数" width="80">
          <template #default="{ row }">
            <span>{{ row.freeShippingCount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="特权" min-width="200">
          <template #default="{ row }">
            <div class="privileges-tags">
              <el-tag v-if="row.refundPriority" size="small">退款优先</el-tag>
              <el-tag v-if="row.exclusiveService" size="small">专属客服</el-tag>
              <el-tag v-if="row.freeShippingCount > 5" size="small">无限免邮</el-tag>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <!-- 充值对话框 -->
    <el-dialog v-model="rechargeDialog.visible" title="确认充值" width="400px">
      <div class="recharge-confirm">
        <div class="confirm-amount">充值金额：¥{{ selectedAmount }}</div>
        <div class="confirm-growth">获得成长值：+{{ selectedAmount * 10 }}</div>
        <el-form label-width="80px">
          <el-form-item label="支付方式">
            <el-radio-group v-model="selectedPaymentMethod">
              <el-radio label="ALIPAY">支付宝</el-radio>
              <el-radio label="WECHAT">微信</el-radio>
              <el-radio label="CARD">银行卡</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="rechargeDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="confirmRecharge">确认支付</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Ticket, Clock, CircleCheck, Lock, Shop, Discount, Truck, 
  CustomerService, Star, DataAnalysis 
} from '@element-plus/icons-vue'
import { 
  getVipLevels, 
  getMyVipInfo, 
  getDailyGifts, 
  getMonthlyGifts, 
  claimGift,
  createRechargeOrder,
  payRechargeOrder,
  getRechargeRecords,
  type VipLevel,
  type VipGift 
} from '@user/api/vip'

const userAvatar = ref('/DouglasMacArthur.jpg')
const userName = ref('用户')

const currentLevel = ref<VipLevel>({
  level: 0,
  name: '普通会员',
  growthValueRequired: 0,
  discountRate: 1,
  dailyCredit: 0,
  monthlyCredit: 0,
  freeShippingCount: 0,
  refundPriority: false,
  exclusiveService: false
})

const growthValue = ref(0)
const progressPercent = ref(0)
const nextLevel = ref<VipLevel | null>(null)
const remainingGrowth = ref(0)

const vipLevels = ref<VipLevel[]>([
  { level: 0, name: '普通会员', growthValueRequired: 0, discountRate: 1, dailyCredit: 0, monthlyCredit: 0, freeShippingCount: 0, refundPriority: false, exclusiveService: false },
  { level: 1, name: '白银会员', growthValueRequired: 500, discountRate: 0.95, dailyCredit: 10, monthlyCredit: 100, freeShippingCount: 3, refundPriority: false, exclusiveService: false },
  { level: 2, name: '黄金会员', growthValueRequired: 2000, discountRate: 0.9, dailyCredit: 30, monthlyCredit: 300, freeShippingCount: 5, refundPriority: false, exclusiveService: false },
  { level: 3, name: '铂金会员', growthValueRequired: 5000, discountRate: 0.85, dailyCredit: 50, monthlyCredit: 500, freeShippingCount: 10, refundPriority: true, exclusiveService: false },
  { level: 4, name: '钻石会员', growthValueRequired: 10000, discountRate: 0.8, dailyCredit: 100, monthlyCredit: 1000, freeShippingCount: 20, refundPriority: true, exclusiveService: true },
  { level: 5, name: '至尊会员', growthValueRequired: 999999, discountRate: 0.7, dailyCredit: 200, monthlyCredit: 2000, freeShippingCount: 99, refundPriority: true, exclusiveService: true }
])
const dailyGifts = ref<VipGift[]>([])
const monthlyGifts = ref<VipGift[]>([])
const rechargeRecords = ref<any[]>([
  { orderNo: 'RCH20260324001', amount: 100, growthValue: 1000, status: 'PAID', createdAt: '2026-03-24 10:30:00' },
  { orderNo: 'RCH20260323002', amount: 50, growthValue: 500, status: 'PAID', createdAt: '2026-03-23 15:20:00' },
  { orderNo: 'RCH20260322003', amount: 30, growthValue: 300, status: 'PAID', createdAt: '2026-03-22 09:15:00' },
  { orderNo: 'RCH20260321004', amount: 200, growthValue: 2000, status: 'PENDING', createdAt: '2026-03-21 18:45:00' }
])

const rechargeOptions = [
  { amount: 10, growth: 100 },
  { amount: 30, growth: 300 },
  { amount: 50, growth: 500 },
  { amount: 100, growth: 1000 },
  { amount: 300, growth: 3000 },
  { amount: 500, growth: 5000 }
]

const selectedAmount = ref<number | null>(null)
const selectedPaymentMethod = ref('ALIPAY')
const rechargeDialog = reactive({ visible: false })

// 权益列表
const benefitsList = computed(() => {
  const list = [
    { icon: 'Discount', label: '购物折扣', value: currentLevel.value.discountRate < 1 ? `${(currentLevel.value.discountRate * 10).toFixed(0)}折` : '无' },
    { icon: 'Ticket', label: '每日积分', value: `+${currentLevel.value.dailyCredit}` },
    { icon: 'Ticket', label: '每月积分', value: `+${currentLevel.value.monthlyCredit}` },
    { icon: 'Truck', label: '免费配送', value: `${currentLevel.value.freeShippingCount}次/月` },
    { icon: 'CustomerService', label: '专属客服', value: currentLevel.value.exclusiveService ? '✓' : '-' },
    { icon: 'Star', label: '退款优先', value: currentLevel.value.refundPriority ? '✓' : '-' }
  ]
  return list
})

// 加载 VIP 信息
const loadVipInfo = async () => {
  try {
    const res = await getMyVipInfo()
    currentLevel.value = res.data.currentLevel
    growthValue.value = res.data.growthValue
    progressPercent.value = res.data.progressPercent
    nextLevel.value = res.data.nextLevel || null
    remainingGrowth.value = res.data.remainingGrowth || 0
    userName.value = res.data.userName || '用户'
  } catch (error: any) {
    console.error('加载 VIP 信息失败', error)
  }
}

// 加载 VIP 等级列表
const loadVipLevels = async () => {
  try {
    const res = await getVipLevels()
    vipLevels.value = res.data
  } catch (error: any) {
    console.error('加载 VIP 等级失败', error)
  }
}

// 加载每日礼包
const loadDailyGifts = async () => {
  try {
    const res = await getDailyGifts()
    dailyGifts.value = res.data
  } catch (error: any) {
    console.error('加载每日礼包失败', error)
  }
}

// 加载每月礼包
const loadMonthlyGifts = async () => {
  try {
    const res = await getMonthlyGifts()
    monthlyGifts.value = res.data
  } catch (error: any) {
    console.error('加载每月礼包失败', error)
  }
}

// 加载充值记录
const loadRechargeRecords = async () => {
  try {
    const res = await getRechargeRecords()
    rechargeRecords.value = res.data
  } catch (error: any) {
    console.error('加载充值记录失败', error)
  }
}

// 领取礼包
const claimGiftHandler = async (gift: VipGift) => {
  if (!gift.canClaim) {
    if (gift.vipLevelRequired > currentLevel.value.level) {
      ElMessage.warning(`需要达到 ${getLevelName(gift.vipLevelRequired)} 才能领取`)
    }
    return
  }

  try {
    await ElMessageBox.confirm(`确定领取${gift.name}吗？`, '提示', { type: 'info' })
    await claimGift(gift.id)
    ElMessage.success('领取成功')
    
    // 重新加载礼包列表
    if (gift.type === 'DAILY') {
      loadDailyGifts()
    } else {
      loadMonthlyGifts()
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '领取失败')
    }
  }
}

// 获取等级名称
const getLevelName = (level: number): string => {
  const levelNames = ['普通会员', '白银会员', '黄金会员', '铂金会员', '钻石会员', '至尊会员']
  return levelNames[level] || `VIP${level}`
}

// 处理充值
const handleRecharge = () => {
  if (!selectedAmount.value) {
    ElMessage.warning('请选择充值金额')
    return
  }
  rechargeDialog.visible = true
}

// 确认充值
const confirmRecharge = async () => {
  if (!selectedAmount.value) return

  try {
    const orderRes = await createRechargeOrder(selectedAmount.value)
    await payRechargeOrder(orderRes.data.orderNo, selectedPaymentMethod.value)
    
    ElMessage.success('充值成功')
    rechargeDialog.visible = false
    selectedAmount.value = null
    
    // 刷新数据
    loadVipInfo()
    loadRechargeRecords()
  } catch (error: any) {
    ElMessage.error(error.message || '充值失败')
  }
}

// 格式化倒计时
const formatCountdown = (seconds: number): string => {
  if (seconds <= 0) return '可领取'
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

// 格式化日期
const formatDate = (dateStr: string): string => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

onMounted(() => {
  loadVipInfo()
  loadVipLevels()
  loadDailyGifts()
  loadMonthlyGifts()
  loadRechargeRecords()
})
</script>

<style scoped>
.vip-center {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  min-height: 100vh;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 50%, #1a1a2e 100%);
}

/* VIP 头部 */
.vip-header {
  position: relative;
  border-radius: 20px;
  padding: 30px;
  margin-bottom: 20px;
  overflow: hidden;
  background: linear-gradient(135deg, #e6c24a 0%, #c9a227 50%, #b8952a 100%);
  border: 2px solid rgba(255, 249, 196, 0.9);
  box-shadow: 0 0 30px rgba(255, 213, 79, 0.6), inset 0 0 50px rgba(255, 249, 196, 0.3);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.vip-header::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -100%;
  width: 50%;
  height: 200%;
  background: linear-gradient(90deg, transparent, rgba(255, 249, 196, 0.8), rgba(255, 223, 0, 0.9), rgba(255, 249, 196, 0.8), transparent);
  transform: skewX(-45deg);
  animation: shine-slide 5s linear infinite;
  pointer-events: none;
  z-index: 0;
}

@keyframes shine-slide {
  0% {
    left: -100%;
  }
  100% {
    left: 600%;
  }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-wrapper {
  position: relative;
  width: 160px;
  height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: url('/olive.png') no-repeat center center;
  background-size: contain;
}

.user-avatar {
  position: relative;
  z-index: 1;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.vip-level-badge {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: bold;
  background: linear-gradient(90deg, #ffdf00, #ffcc00);
  padding: 6px 16px;
  border-radius: 20px;
  border: 1px solid rgba(255, 223, 0, 0.9);
  box-shadow: 0 0 25px rgba(255, 223, 0, 0.8);
  position: relative;
  overflow: hidden;
}

.vip-level-badge::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -100%;
  width: 50%;
  height: 200%;
  background: linear-gradient(90deg, transparent, rgba(255, 249, 196, 0.6), rgba(255, 223, 0, 0.7), rgba(255, 249, 196, 0.6), transparent);
  transform: skewX(-45deg);
  animation: badge-shine 5s linear infinite;
  pointer-events: none;
}

@keyframes badge-shine {
  0% {
    left: -100%;
  }
  100% {
    left: 600%;
  }
}

.level-icon {
  width: 36px;
  height: 36px;
  filter: drop-shadow(0 0 20px rgba(255, 249, 196, 0.9));
}

.user-name {
  font-size: 16px;
  color: #fff9c4;
  background-image: linear-gradient(90deg, #fff9c4, #daa520);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 0 10px rgba(255, 249, 196, 0.6);
}

.growth-progress {
  flex: 1;
  max-width: 450px;
  min-width: 280px;
}

.progress-title {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 18px;
  font-weight: bold;
  color: #fff9c4;
  text-shadow: 0 0 10px rgba(255, 249, 196, 0.6);
}

.growth-value {
  font-weight: bold;
  font-size: 18px;
  color: #fff9c4;
  text-shadow: 0 0 20px rgba(255, 249, 196, 0.8);
}

.progress-bar-wrapper {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  padding: 2px;
  border: 1px solid rgba(230, 194, 74, 0.7);
  box-shadow: inset 0 0 15px rgba(230, 194, 74, 0.4), 0 0 20px rgba(230, 194, 74, 0.3);
}

.progress-tip {
  margin-top: 12px;
  font-size: 12px;
  color: #fff9c4;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.progress-tip .el-icon {
  color: #fff9c4;
  filter: drop-shadow(0 0 8px rgba(255, 249, 196, 0.8));
}

/* ==================== 统一亮金色边框样式 ==================== */
.vip-golden-border {
  border: 1px solid rgba(255, 198, 0, 0.85);
  box-shadow: 0 0 30px rgba(255, 198, 0, 0.6), inset 0 0 50px rgba(255, 198, 0, 0.2);
}

.vip-golden-border-strong {
  border: 1px solid rgba(255, 198, 0, 0.95);
  box-shadow: 0 0 40px rgba(255, 198, 0, 0.7), inset 0 0 60px rgba(255, 198, 0, 0.25);
}

.vip-golden-glow {
  box-shadow: 0 0 40px rgba(255, 198, 0, 0.8);
}

.vip-golden-text {
  color: #fff9c4;
  text-shadow: 0 0 20px rgba(255, 249, 196, 0.9);
}

.vip-golden-bg {
  background: linear-gradient(135deg, rgba(255, 198, 0, 0.55) 0%, rgba(249, 168, 0, 0.5) 100%);
}

/* 通用区块样式 */
section {
  background: linear-gradient(135deg, #e6c24a, #c9a227);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 20px;
  border: 1px solid rgba(230, 194, 74, 0.9);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.3), 0 0 35px rgba(230, 194, 74, 0.5);
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #fff9c4;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  text-shadow: 0 0 15px rgba(255, 249, 196, 0.8);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

/* 权益网格 */
.benefits-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.benefit-item {
  background: linear-gradient(135deg, #ffdf00, #ffcc00);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  border: 1px solid rgba(255, 223, 0, 0.9);
  box-shadow: 0 0 35px rgba(255, 223, 0, 0.6);
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.benefit-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.5), transparent);
  transition: left 0.5s;
}

.benefit-item:hover::before {
  left: 100%;
}

.benefit-item:hover {
  background: linear-gradient(135deg, #ffe066, #ffd600);
  border-color: rgba(255, 223, 0, 1);
  box-shadow: 0 0 50px rgba(255, 223, 0, 0.9);
  transform: translateY(-3px);
}

.benefit-icon {
  width: 56px;
  height: 56px;
  margin: 0 auto 12px;
  background: linear-gradient(135deg, #ffd700, #daa520, #c9a227);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffdf00;
  box-shadow: 0 0 35px rgba(230, 194, 74, 0.9);
}

.benefit-label {
  font-size: 13px;
  color: #fff9c4;
  margin-bottom: 6px;
  font-weight: 600;
  text-shadow: 0 0 10px rgba(255, 249, 196, 0.8);
}

.benefit-value {
  font-size: 18px;
  font-weight: bold;
  color: #fff9c4;
  text-shadow: 0 0 15px rgba(255, 249, 196, 0.9);
}

/* 礼包网格 */
.gift-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.gift-card {
  background: linear-gradient(135deg, #e6c24a, #c9a227);
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid rgba(230, 194, 74, 0.9);
  box-shadow: 0 0 35px rgba(230, 194, 74, 0.5);
  transition: all 0.3s;
  cursor: pointer;
  position: relative;
}

.gift-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at center, rgba(255, 223, 0, 0.3) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.3s;
}

.gift-card:hover::after {
  opacity: 1;
}

.gift-card:hover {
  border-color: rgba(230, 194, 74, 1);
  box-shadow: 0 0 50px rgba(230, 194, 74, 0.9);
  transform: translateY(-3px);
}

.gift-card.can-claim {
  border-color: rgba(255, 223, 0, 0.9);
  box-shadow: 0 0 50px rgba(255, 223, 0, 0.9);
}

.gift-card.can-claim::after {
  opacity: 0.8;
}

.gift-card.claimed {
  opacity: 0.5;
  filter: grayscale(0.6);
}

.gift-card.monthly.can-claim {
  border-color: rgba(255, 223, 0, 0.9);
  box-shadow: 0 0 50px rgba(255, 223, 0, 0.9);
}

.gift-image {
  position: relative;
  height: 140px;
  overflow: hidden;
}

.gift-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.gift-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #888;
}

.gift-info {
  padding: 16px;
}

.gift-name {
  font-size: 14px;
  font-weight: bold;
  color: #fff9c4;
  margin-bottom: 8px;
  text-shadow: 0 0 15px rgba(255, 249, 196, 0.8);
}

.gift-reward {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #fff9c4;
  font-size: 13px;
  margin-bottom: 8px;
  text-shadow: 0 0 10px rgba(255, 249, 196, 0.6);
}

.gift-status {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #fff9c4;
}

.gift-status.claimed {
  color: #00ff88;
}

.claim-btn {
  width: 100%;
  margin-top: 8px;
}

/* 充值区域 */
.recharge-desc {
  color: #fff9c4;
  margin-bottom: 20px;
  font-size: 14px;
  text-align: center;
  text-shadow: 0 0 10px rgba(255, 249, 196, 0.6);
}

.recharge-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.recharge-card {
  background: linear-gradient(135deg, #ffdf00, #ffcc00);
  border: 1px solid rgba(255, 223, 0, 0.9);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
  box-shadow: 0 0 25px rgba(255, 223, 0, 0.6);
}

.recharge-card::before {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(255, 255, 255, 0.5) 0%, transparent 60%);
  opacity: 0;
  transition: opacity 0.3s;
}

.recharge-card:hover::before {
  opacity: 1;
}

.recharge-card:hover {
  background: linear-gradient(135deg, #ffe066, #ffd600);
  border-color: rgba(255, 223, 0, 1);
  box-shadow: 0 0 45px rgba(255, 223, 0, 0.9);
  transform: translateY(-3px);
}

.recharge-card.active {
  background: linear-gradient(135deg, #ffe066, #ffd600);
  border-color: rgba(255, 223, 0, 1);
  box-shadow: 0 0 65px rgba(255, 223, 0, 0.95);
}

.recharge-amount {
  font-size: 24px;
  font-weight: bold;
  color: #fff9c4;
  margin-bottom: 8px;
  text-shadow: 0 0 15px rgba(255, 249, 196, 0.8);
  position: relative;
  z-index: 1;
}

.recharge-growth {
  font-size: 13px;
  color: #fff9c4;
  position: relative;
  z-index: 1;
}

.recharge-action {
  text-align: center;
}

.recharge-submit {
  min-width: 200px;
  background: linear-gradient(135deg, #ffdf00, #ffcc00);
  border: none;
  box-shadow: 0 0 25px rgba(255, 223, 0, 0.7);
  font-weight: bold;
  color: #fff9c4;
  text-shadow: 0 0 10px rgba(255, 249, 196, 0.6);
}

.recharge-submit:hover {
  background: linear-gradient(135deg, #ffe066, #ffd600);
  box-shadow: 0 0 45px rgba(255, 223, 0, 0.9);
  transform: translateY(-3px);
}

/* 表格样式 */
.sci-table {
  background: #ffd54f;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid rgba(255, 213, 79, 0.9);
  box-shadow: 0 0 35px rgba(255, 213, 79, 0.5);
}

.sci-table :deep(.el-table__header th) {
  background: #fff9c4;
  color: #daa520;
  border-bottom: 2px solid rgba(255, 249, 196, 0.9);
  font-weight: 600;
  font-size: 14px;
}

.sci-table :deep(.el-table__body td) {
  color: #fff9c4;
  border-bottom-color: rgba(255, 249, 196, 0.3);
  font-weight: 600;
  font-size: 14px;
  background: #ffd54f !important;
}

.sci-table :deep(.el-table__row:hover) {
  background: linear-gradient(90deg, rgba(255, 249, 196, 0.3), rgba(255, 224, 130, 0.25)) !important;
}

.sci-table :deep(.el-table__row:hover td) {
  color: #fff9c4 !important;
  text-shadow: 0 0 10px rgba(255, 249, 196, 0.6);
}

.amount-text {
  color: #fff9c4;
  text-shadow: 0 0 15px rgba(255, 249, 196, 0.8);
}

.level-table :deep(.el-table__body td) {
  font-weight: 600;
  color: #fff9c4;
  text-shadow: 0 0 10px rgba(255, 249, 196, 0.6);
}

.privileges-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.privileges-tags .el-tag {
  background: transparent;
  border: 1px solid rgba(255, 249, 196, 0.5);
  color: #daa520;
  font-weight: 600;
}

/* 礼包名称 */
.gift-name {
  font-size: 15px;
  font-weight: bold;
  color: #fff9c4;
  margin-bottom: 8px;
  text-shadow: 0 0 15px rgba(255, 249, 196, 0.8);
}

/* 刷新标签 */
.section-header .el-tag {
  background: linear-gradient(135deg, #fff9c4, #ffe082);
  border: 1px solid rgba(255, 249, 196, 0.9);
  color: #fff9c4;
  font-weight: bold;
  text-shadow: 0 0 10px rgba(255, 249, 196, 0.6);
}

/* 表格状态标签 - 统一样式 */
.sci-table :deep(.el-tag),
.sci-table :deep(.el-tag--success),
.sci-table :deep(.el-tag--warning) {
  background: linear-gradient(135deg, #fff9c4, #daa520);
  border: 1px solid rgba(218, 165, 32, 0.9);
  color: #000;
  font-weight: 600;
}

/* 充值确认对话框 */
.recharge-confirm {
  text-align: center;
  padding: 20px 0;
}

.confirm-amount {
  font-size: 24px;
  font-weight: bold;
  color: #fff9c4;
  margin-bottom: 10px;
  text-shadow: 0 0 15px rgba(255, 249, 196, 0.8);
}

.confirm-growth {
  font-size: 16px;
  color: #fff9c4;
  margin-bottom: 20px;
  text-shadow: 0 0 15px rgba(255, 249, 196, 0.8);
}
</style>

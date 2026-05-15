<template>
  <div class="page-container">
    <header class="page-header">
      <h1 class="page-title">
        <el-icon><ChatLineSquare /></el-icon>
        消息中心
      </h1>
      <div class="header-actions">
        <el-button type="primary" @click="markAllAsRead">
          <el-icon><Check /></el-icon>
          全部已读
        </el-button>
      </div>
    </header>

    <section class="message-section">
      <el-row :gutter="15">
        <!-- 消息分类 -->
        <el-col :span="6">
          <div class="category-card">
            <div class="category-list">
              <div
                v-for="cat in categories"
                :key="cat.type"
                class="category-item"
                :class="{ active: currentCategory === cat.type }"
                @click="switchCategory(cat.type)"
              >
                <div class="category-info">
                  <el-icon class="category-icon" :style="{ color: cat.color }">
                    <component :is="cat.icon" />
                  </el-icon>
                  <span class="category-name">{{ cat.name }}</span>
                </div>
                <el-badge :value="cat.unread" :hidden="cat.unread === 0" class="category-badge" />
              </div>
            </div>
          </div>
        </el-col>

        <!-- 消息列表 -->
        <el-col :span="18">
          <div class="message-list-card">
            <div class="list-header">
              <h3 class="list-title">{{ currentCategoryName }}</h3>
              <el-input
                v-model="searchKeyword"
                placeholder="搜索消息"
                clearable
                prefix-icon="Search"
                style="width: 200px"
              />
            </div>

            <div class="message-list" v-loading="loading">
              <div
                v-for="msg in filteredMessages"
                :key="msg.id"
                class="message-item"
                :class="{ unread: !msg.isRead }"
                @click="viewMessage(msg)"
              >
                <div class="message-left">
                  <el-avatar :size="48" class="message-avatar">
                    <el-icon><component :is="msg.icon" /></el-icon>
                  </el-avatar>
                </div>
                <div class="message-content">
                  <div class="message-header">
                    <span class="message-title">{{ msg.title }}</span>
                    <span class="message-time">{{ msg.time }}</span>
                  </div>
                  <p class="message-summary">{{ msg.summary }}</p>
                  <div class="message-footer">
                    <el-tag :type="getTypeByCategory(msg.category)" size="small">{{ msg.categoryName }}</el-tag>
                  </div>
                </div>
                <div class="message-right">
                  <el-icon v-if="!msg.isRead" class="unread-dot"><CircleCheck /></el-icon>
                </div>
              </div>

              <el-empty v-if="filteredMessages.length === 0" description="暂无消息" />
            </div>
          </div>
        </el-col>
      </el-row>
    </section>

    <!-- 消息详情对话框 -->
    <el-dialog v-model="detailDialog.visible" title="消息详情" width="600px">
      <div class="message-detail">
        <div class="detail-header">
          <h4 class="detail-title">{{ currentMessage?.title }}</h4>
          <span class="detail-time">{{ currentMessage?.time }}</span>
        </div>
        <el-divider />
        <div class="detail-content">
          <p>{{ currentMessage?.content }}</p>
        </div>
        <div class="detail-actions" v-if="currentMessage?.action">
          <el-button type="primary" @click="handleAction">{{ currentMessage?.actionText }}</el-button>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailDialog.visible = false">关闭</el-button>
        <el-button type="primary" v-if="currentMessage && !currentMessage.isRead" @click="markAsRead">
          标记为已读
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  ChatLineSquare,
  Check,
  CircleCheck,
  Bell,
  ShoppingCart,
  User,
  Shop,
  DocumentChecked,
  Warning
} from '@element-plus/icons-vue'
import type { Component } from 'vue'

interface MessageItem {
  id: number
  title: string
  summary: string
  content: string
  category: string
  categoryName: string
  icon: Component
  time: string
  isRead: boolean
  action?: () => void
  actionText?: string
}

interface CategoryItem {
  type: string
  name: string
  icon: Component
  color: string
  unread: number
}

const loading = ref(false)
const currentCategory = ref('all')
const searchKeyword = ref('')
const currentMessage = ref<MessageItem | null>(null)

const detailDialog = reactive({
  visible: false
})

const categories = ref<CategoryItem[]>([
  { type: 'all', name: '全部消息', icon: Bell, color: '#00d4ff', unread: 0 },
  { type: 'system', name: '系统通知', icon: Bell, color: '#00d4ff', unread: 3 },
  { type: 'order', name: '订单消息', icon: ShoppingCart, color: '#00ff88', unread: 5 },
  { type: 'user', name: '用户消息', icon: User, color: '#ffaa00', unread: 2 },
  { type: 'merchant', name: '商家消息', icon: Shop, color: '#a335ee', unread: 1 },
  { type: 'audit', name: '审核消息', icon: DocumentChecked, color: '#00d4ff', unread: 0 },
  { type: 'warning', name: '预警消息', icon: Warning, color: '#ff6666', unread: 1 }
])

const messages = ref<MessageItem[]>([
  {
    id: 1,
    title: '平台系统升级通知',
    summary: '平台将于今晚 23:00 进行系统升级维护...',
    content: '尊敬的管理员：平台将于今晚 23:00 至次日 2:00 进行系统升级维护，届时部分功能可能无法正常使用。请提前做好工作安排。',
    category: 'system',
    categoryName: '系统通知',
    icon: Bell,
    time: '2026-03-18 10:30',
    isRead: false
  },
  {
    id: 2,
    title: '新订单提醒',
    summary: '您有新的订单待处理，订单号：DD202603180001',
    content: '您有新的订单待处理，订单号：DD202603180001，金额：¥199.00，请及时处理。',
    category: 'order',
    categoryName: '订单消息',
    icon: ShoppingCart,
    time: '2026-03-18 09:15',
    isRead: false,
    action: () => ElMessage.info('跳转到订单详情'),
    actionText: '查看订单'
  },
  {
    id: 3,
    title: '用户举报处理',
    summary: '收到一起用户举报，需要及时处理',
    content: '用户举报商品 ID:3005 涉嫌售假，请及时核实处理。',
    category: 'audit',
    categoryName: '审核消息',
    icon: DocumentChecked,
    time: '2026-03-17 16:45',
    isRead: false,
    action: () => ElMessage.info('跳转到举报详情'),
    actionText: '查看详情'
  },
  {
    id: 4,
    title: '商家入驻申请',
    summary: '新的商家入驻申请待审核',
    content: '新的商家"时尚精品店"提交入驻申请，请及时审核。',
    category: 'merchant',
    categoryName: '商家消息',
    icon: Shop,
    time: '2026-03-17 14:20',
    isRead: false
  },
  {
    id: 5,
    title: '库存预警',
    summary: '商品"无线蓝牙耳机"库存不足，请及时补货',
    content: '商品"无线蓝牙耳机"当前库存仅剩 5 件，已低于安全库存，请及时补货。',
    category: 'warning',
    categoryName: '预警消息',
    icon: Warning,
    time: '2026-03-17 11:00',
    isRead: false,
    action: () => ElMessage.info('跳转到商品管理'),
    actionText: '去补货'
  },
  {
    id: 6,
    title: '用户咨询消息',
    summary: '用户张先生咨询商品详情',
    content: '用户张先生咨询："无线蓝牙耳机 Pro"的保修期是多久？',
    category: 'user',
    categoryName: '用户消息',
    icon: User,
    time: '2026-03-16 20:30',
    isRead: true
  },
  {
    id: 7,
    title: '订单已完成',
    summary: '订单 DD202603160006 已完成',
    content: '订单 DD202603160006 用户已确认收货，订单已完成。',
    category: 'order',
    categoryName: '订单消息',
    icon: ShoppingCart,
    time: '2026-03-16 15:10',
    isRead: true
  },
  {
    id: 8,
    title: '平台规则更新',
    summary: '平台商家管理规则已更新',
    content: '平台商家管理规则已更新，主要变更：1. 优化审核流程 2. 调整违规处罚标准 3. 新增信用评价体系',
    category: 'system',
    categoryName: '系统通知',
    icon: Bell,
    time: '2026-03-16 10:00',
    isRead: true
  }
])

const currentCategoryName = computed(() => {
  const cat = categories.value.find(c => c.type === currentCategory.value)
  return cat?.name || '全部消息'
})

const filteredMessages = computed(() => {
  let result = messages.value
  if (currentCategory.value !== 'all') {
    result = result.filter(m => m.category === currentCategory.value)
  }
  if (searchKeyword.value) {
    result = result.filter(m =>
      m.title.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      m.summary.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }
  return result
})

const switchCategory = (type: string) => {
  currentCategory.value = type
}

const viewMessage = (msg: MessageItem) => {
  currentMessage.value = msg
  detailDialog.visible = true
}

const markAsRead = () => {
  if (currentMessage.value) {
    currentMessage.value.isRead = true
    updateUnreadCount()
    ElMessage.success('已标记为已读')
    detailDialog.visible = false
  }
}

const markAllAsRead = () => {
  messages.value.forEach(m => m.isRead = true)
  updateUnreadCount()
  ElMessage.success('全部标记为已读')
}

const handleAction = () => {
  if (currentMessage.value?.action) {
    currentMessage.value.action()
  }
}

const getTypeByCategory = (category: string) => {
  const map: Record<string, string> = {
    system: '',
    order: 'success',
    user: 'warning',
    merchant: '',
    audit: '',
    warning: 'danger'
  }
  return map[category] || 'info'
}

const updateUnreadCount = () => {
  categories.value.forEach(cat => {
    if (cat.type === 'all') {
      cat.unread = messages.value.filter(m => !m.isRead).length
    } else {
      cat.unread = messages.value.filter(m => m.category === cat.type && !m.isRead).length
    }
  })
}

onMounted(() => {
  updateUnreadCount()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
}

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: bold;
  color: #fff;
}

.page-title .el-icon {
  color: var(--mall-primary);
  font-size: 24px;
}

.message-section {
  
}

.category-card {
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 15px;
  height: calc(100vh - 200px);
  overflow-y: auto;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.category-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  background: rgba(0, 212, 255, 0.05);
  border: 1px solid rgba(0, 212, 255, 0.1);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.category-item:hover {
  background: rgba(0, 212, 255, 0.1);
  border-color: var(--mall-primary);
}

.category-item.active {
  background: rgba(0, 212, 255, 0.15);
  border-color: var(--mall-primary);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.2);
}

.category-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.category-icon {
  font-size: 20px;
}

.category-name {
  color: #fff;
  font-size: 14px;
}

.category-badge {
  --el-badge-size: 18px;
}

.message-list-card {
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 20px;
  height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.15);
}

.list-title {
  font-size: 16px;
  font-weight: bold;
  color: #fff;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message-item {
  display: flex;
  gap: 15px;
  padding: 15px;
  background: rgba(0, 212, 255, 0.05);
  border: 1px solid rgba(0, 212, 255, 0.1);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.message-item:hover {
  background: rgba(0, 212, 255, 0.1);
  border-color: var(--mall-primary);
}

.message-item.unread {
  background: rgba(0, 212, 255, 0.12);
  border-left: 3px solid var(--mall-primary);
}

.message-left {
  flex-shrink: 0;
}

.message-avatar {
  background: rgba(0, 212, 255, 0.2);
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
}

.message-title {
  font-size: 15px;
  font-weight: bold;
  color: #fff;
}

.message-time {
  font-size: 12px;
  color: #666;
}

.message-summary {
  font-size: 13px;
  color: #888;
  
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-footer {
  display: flex;
  gap: 10px;
}

.message-right {
  flex-shrink: 0;
  display: flex;
  align-items: center;
}

.unread-dot {
  color: var(--mall-primary);
  font-size: 20px;
}

.message-detail {
  padding: 10px;
}

.detail-header {
  
}

.detail-title {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  
}

.detail-time {
  font-size: 13px;
  color: #666;
}

.detail-content {
  color: #aaa;
  line-height: 1.8;
  font-size: 14px;
}

.detail-actions {
  
  text-align: center;
}

:deep(.el-empty) {
  
}

:deep(.el-empty__description) {
  color: #666;
}
</style>

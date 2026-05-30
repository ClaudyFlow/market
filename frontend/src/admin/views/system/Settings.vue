<template>
  <div class="page-container">
    <header class="page-header">
      <h1 class="page-title">
        <el-icon><Setting /></el-icon>
        系统设置
      </h1>
    </header>

    <section class="settings-section">
      <el-tabs v-model="activeTab" class="settings-tabs">
        <el-tab-pane label="基本设置" name="basic">
          <el-form :model="basicSettings" label-width="120px" class="settings-form">
            <el-form-item label="平台名称">
              <el-input v-model="basicSettings.platformName" placeholder="请输入平台名称" />
            </el-form-item>
            <el-form-item label="平台 Logo">
              <el-input v-model="basicSettings.platformLogo" placeholder="请输入 Logo URL" />
            </el-form-item>
            <el-form-item label="联系邮箱">
              <el-input v-model="basicSettings.email" placeholder="请输入联系邮箱" />
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="basicSettings.phone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="平台公告">
              <el-input v-model="basicSettings.announcement" type="textarea" :rows="4" placeholder="请输入平台公告" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveBasicSettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="交易设置" name="trade">
          <el-form :model="tradeSettings" label-width="120px" class="settings-form">
            <el-form-item label="订单超时时间">
              <el-input-number v-model="tradeSettings.orderTimeout" :min="5" :max="120" /> 分钟
            </el-form-item>
            <el-form-item label="退货有效期">
              <el-input-number v-model="tradeSettings.refundDays" :min="1" :max="30" /> 天
            </el-form-item>
            <el-form-item label="最低提现金额">
              <el-input-number v-model="tradeSettings.minWithdraw" :min="1" :precision="2" /> 元
            </el-form-item>
            <el-form-item label="平台抽成比例">
              <el-input-number v-model="tradeSettings.platformFee" :min="0" :max="100" /> %
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveTradeSettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="安全设置" name="security">
          <el-form :model="securitySettings" label-width="120px" class="settings-form">
            <el-form-item label="登录失败锁定">
              <el-input-number v-model="securitySettings.maxLoginAttempts" :min="3" :max="10" /> 次
            </el-form-item>
            <el-form-item label="锁定时长">
              <el-input-number v-model="securitySettings.lockDuration" :min="5" :max="60" /> 分钟
            </el-form-item>
            <el-form-item label="密码强度要求">
              <el-checkbox-group v-model="securitySettings.passwordRequirements">
                <el-checkbox label="uppercase">大写字母</el-checkbox>
                <el-checkbox label="lowercase">小写字母</el-checkbox>
                <el-checkbox label="numbers">数字</el-checkbox>
                <el-checkbox label="special">特殊字符</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item label="是否启用验证码">
              <el-switch v-model="securitySettings.enableCaptcha" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveSecuritySettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="通知设置" name="notification">
          <el-form :model="notificationSettings" label-width="120px" class="settings-form">
            <el-form-item label="订单通知">
              <el-switch v-model="notificationSettings.orderNotification" />
            </el-form-item>
            <el-form-item label="退款通知">
              <el-switch v-model="notificationSettings.refundNotification" />
            </el-form-item>
            <el-form-item label="活动通知">
              <el-switch v-model="notificationSettings.activityNotification" />
            </el-form-item>
            <el-form-item label="系统通知">
              <el-switch v-model="notificationSettings.systemNotification" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveNotificationSettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Setting } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('basic')

const basicSettings = reactive({
  platformName: '市场平台',
  platformLogo: '/images/logo.png',
  email: 'admin@market.com',
  phone: '400-123-4567',
  announcement: '欢迎来到市场平台购物！'
})

const tradeSettings = reactive({
  orderTimeout: 30,
  refundDays: 7,
  minWithdraw: 100,
  platformFee: 5
})

const securitySettings = reactive({
  maxLoginAttempts: 5,
  lockDuration: 30,
  passwordRequirements: ['uppercase', 'lowercase', 'numbers'],
  enableCaptcha: true
})

const notificationSettings = reactive({
  orderNotification: true,
  refundNotification: true,
  activityNotification: true,
  systemNotification: true
})

const saveBasicSettings = () => {
  ElMessage.success('基本设置已保存')
}

const saveTradeSettings = () => {
  ElMessage.success('交易设置已保存')
}

const saveSecuritySettings = () => {
  ElMessage.success('安全设置已保存')
}

const saveNotificationSettings = () => {
  ElMessage.success('通知设置已保存')
}

onMounted(() => {
})
</script>

<style scoped>
.page-container {
  padding: 20px;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.05) 0%, transparent 100%);
  min-height: 100vh;
}

.page-header {
  padding: 20px;
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.9), rgba(26, 31, 58, 0.7));
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.1);
  margin-bottom: 20px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 22px;
  font-weight: bold;
  color: #fff;
  margin: 0;
}

.page-title .el-icon {
  color: var(--mall-primary);
  font-size: 26px;
}

.settings-section {
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.8), rgba(26, 31, 58, 0.6));
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 20px;
}

.settings-tabs :deep(.el-tabs__header) {
  background: transparent;
  margin-bottom: 20px;
}

.settings-tabs :deep(.el-tabs__item) {
  color: #b0d4ff;
  font-size: 14px;
}

.settings-tabs :deep(.el-tabs__item.is-active) {
  color: #00d4ff;
}

.settings-tabs :deep(.el-tabs__nav-wrap::after) {
  background: rgba(0, 212, 255, 0.2);
}

.settings-form :deep(.el-form-item__label) {
  color: #b0d4ff;
}

.settings-form :deep(.el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 8px;
}

.settings-form :deep(.el-input__inner) {
  color: #fff;
}

.settings-form :deep(.el-input-number) {
  margin-right: 8px;
}

.settings-form :deep(.el-checkbox) {
  color: #b0d4ff;
}

.settings-form :deep(.el-button--primary) {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  color: #000;
  font-weight: bold;
}
</style>
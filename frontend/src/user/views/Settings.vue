<template>
  <div class="settings-page">
    <header class="page-header">
      <h1 class="page-title">
        <el-icon><Setting /></el-icon>
        设置
      </h1>
    </header>

    <section class="settings-content">
      <!-- 日志设置卡片 -->
      <div class="settings-card">
        <div class="card-header">
          <el-icon><Document /></el-icon>
          <h2>本地日志设置</h2>
        </div>

        <div class="card-body">
          <div class="setting-item">
            <div class="setting-info">
              <h3>本地文件日志</h3>
              <p class="setting-desc">
                将日志直接写入您选择的本地文件，便于问题排查和日志分析。
                <br>
                <el-tag type="info" size="small">仅支持 Chrome/Edge 桌面版</el-tag>
              </p>
            </div>

            <div class="setting-action">
              <el-button
                v-if="!fileLoggerEnabled"
                type="primary"
                :loading="enabling"
                @click="enableFileLogger"
              >
                <el-icon><FolderAdd /></el-icon>
                启用日志文件
              </el-button>
              <el-button
                v-else
                type="danger"
                :loading="disabling"
                @click="disableFileLogger"
              >
                <el-icon><Close /></el-icon>
                关闭日志文件
              </el-button>
            </div>
          </div>

          <!-- 日志状态 -->
          <div v-if="fileLoggerEnabled" class="log-status">
            <div class="status-item">
              <span class="status-label">状态</span>
              <el-tag type="success" size="small">运行中</el-tag>
            </div>
            <div class="status-item">
              <span class="status-label">缓冲区</span>
              <span>{{ logStatus.bufferSize }} 条</span>
            </div>
            <div class="status-item">
              <span class="status-label">已写入</span>
              <span>{{ formatFileSize(logStatus.totalWritten) }}</span>
            </div>
          </div>

          <!-- API 支持提示 -->
          <el-alert
            v-if="!isFileSystemSupported"
            title="您的浏览器不支持本地文件写入"
            type="warning"
            :closable="false"
          >
            <template #default>
              <p>请使用 Chrome 或 Edge 桌面版浏览器以启用此功能。</p>
            </template>
          </el-alert>
        </div>
      </div>

      <!-- 其他设置 -->
      <div class="settings-card">
        <div class="card-header">
          <el-icon><Bell /></el-icon>
          <h2>通知设置</h2>
        </div>
        <div class="card-body">
          <div class="setting-item">
            <div class="setting-info">
              <h3>订单通知</h3>
              <p class="setting-desc">接收订单状态变更通知</p>
            </div>
            <el-switch v-model="orderNotification" />
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { Setting, Document, FolderAdd, Close, Bell } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { createFileLogger } from '@/utils/localFileLogger'
import createLogger from '@/utils/logger'

// 创建用户模块日志器
const logger = computed(() => createLogger('UserSettings'))

// 本地日志状态
const fileLoggerEnabled = ref(false)
const enabling = ref(false)
const disabling = ref(false)
const fileLogger = ref(null)
const logStatus = ref({ bufferSize: 0, totalWritten: 0 })
let statusTimer = null

// 检查 API 支持
const isFileSystemSupported = computed(() => {
  return 'showSaveFilePicker' in window
})

// 通知设置
const orderNotification = ref(true)

// 启用文件日志
const enableFileLogger = async () => {
  if (!isFileSystemSupported.value) {
    ElMessage.warning('您的浏览器不支持本地文件写入')
    return
  }

  enabling.value = true
  try {
    fileLogger.value = createFileLogger('user', {
      bufferSize: 50,
      flushInterval: 3000,
      fileName: `market-user-${getDateStr()}.log`
    })

    const success = await fileLogger.value.enable()
    if (success) {
      fileLoggerEnabled.value = true
      ElMessage.success('本地日志已启用')
      startStatusUpdate()
    } else {
      ElMessage.error('启用失败，请重试')
    }
  } catch (err) {
    console.error('启用文件日志失败:', err)
    ElMessage.error('启用失败：' + err.message)
  } finally {
    enabling.value = false
  }
}

// 关闭文件日志
const disableFileLogger = async () => {
  disabling.value = true
  try {
    if (fileLogger.value) {
      await fileLogger.value.disable()
      fileLogger.value = null
      fileLoggerEnabled.value = false
      stopStatusUpdate()
      ElMessage.success('本地日志已关闭')
    }
  } catch (err) {
    console.error('关闭文件日志失败:', err)
    ElMessage.error('关闭失败：' + err.message)
  } finally {
    disabling.value = false
  }
}

// 更新日期状态
function getDateStr() {
  const now = new Date()
  return `${now.getFullYear()}${(now.getMonth() + 1).toString().padStart(2, '0')}${now.getDate().toString().padStart(2, '0')}`
}

// 格式化文件大小
function formatFileSize(bytes) {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB'
}

// 开始更新状态
const startStatusUpdate = () => {
  statusTimer = setInterval(() => {
    if (fileLogger.value) {
      logStatus.value = fileLogger.value.getStatus()
    }
  }, 2000)
}

// 停止更新状态
const stopStatusUpdate = () => {
  if (statusTimer) {
    clearInterval(statusTimer)
    statusTimer = null
  }
  logStatus.value = { bufferSize: 0, totalWritten: 0 }
}

// 页面卸载时清理
onUnmounted(() => {
  stopStatusUpdate()
  if (fileLogger.value) {
    fileLogger.value.disable()
  }
})

onMounted(() => {
  // 检查是否有已启用的日志器
  if (fileLogger.value) {
    startStatusUpdate()
  }
})
</script>

<style scoped>
.settings-page {
  min-height: 100vh;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.15) 0%, rgba(10, 14, 26, 0.8) 100%);
  padding: 40px 20px;
}

.page-header {
  max-width: 1200px;
  margin: 0 auto 40px;
}

.page-title {
  color: #fff;
  font-size: 32px;
  text-align: center;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.settings-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.settings-card {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 16px;
  overflow: hidden;
  backdrop-filter: blur(10px);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 24px 32px;
  background: rgba(0, 212, 255, 0.1);
  border-bottom: 1px solid var(--mall-border);
}

.card-header h2 {
  margin: 0;
  font-size: 20px;
  color: #fff;
  font-weight: 600;
}

.card-header .el-icon {
  font-size: 24px;
  color: var(--mall-primary);
}

.card-body {
  padding: 32px;
}

.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #fff;
  font-weight: 500;
}

.setting-desc {
  margin: 0;
  font-size: 13px;
  color: #8899aa;
  line-height: 1.6;
}

.setting-action .el-button {
  min-width: 140px;
}

/* 日志状态 */
.log-status {
  margin-top: 20px;
  padding: 16px 20px;
  background: rgba(0, 255, 136, 0.1);
  border: 1px solid rgba(0, 255, 136, 0.3);
  border-radius: 8px;
  display: flex;
  gap: 24px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.status-label {
  color: #8899aa;
}

.status-item .el-tag {
  background: rgba(0, 255, 136, 0.2);
  border-color: rgba(0, 255, 136, 0.5);
  color: #00ff88;
}

.status-item span:last-child {
  color: #fff;
  font-weight: 500;
}

/* 警告提示 */
.el-alert {
  margin-top: 20px;
  background: rgba(255, 170, 0, 0.1);
  border-color: rgba(255, 170, 0, 0.3);
}

.el-alert :deep(.el-alert__title) {
  color: #ffaa00;
  font-size: 14px;
}

.el-alert :deep(.el-alert__content) {
  color: #cc8800;
}
</style>

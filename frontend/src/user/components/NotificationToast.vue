<template>
  <teleport to="body">
    <transition-group name="notification-list" tag="div" class="notification-container">
      <div 
        v-for="notice in notices" 
        :key="notice.id"
        class="notification-toast"
        :class="[
          `type-${notice.type}`,
          `animation-${notice.animation || 'fade'}`,
          { 'is-clickable': notice.onClick }
        ]"
        @click="handleClick(notice)"
      >
        <div class="toast-icon">
          <i :class="notice.icon || getDefaultIcon(notice.type)"></i>
        </div>
        <div class="toast-content">
          <div class="toast-message">{{ notice.message }}</div>
          <div class="toast-description" v-if="notice.description">{{ notice.description }}</div>
        </div>
        <div class="toast-close" @click.stop="removeNotice(notice.id)">
          <i class="fas fa-times"></i>
        </div>
        <div class="toast-progress" v-if="notice.duration > 0">
          <div 
            class="progress-bar" 
            :style="{ animationDuration: `${notice.duration}ms` }"
          ></div>
        </div>
      </div>
    </transition-group>
  </teleport>
</template>

<script setup lang="ts">
import { ref, getCurrentInstance } from 'vue'

interface NoticeItem {
  id: number
  type: 'success' | 'error' | 'warning' | 'info' | 'loading'
  message: string
  description?: string
  icon?: string
  duration?: number
  animation?: 'fade' | 'slide-top' | 'slide-bottom' | 'slide-left' | 'slide-right' | 'bounce' | 'zoom' | 'flip'
  onClick?: () => void
}

const notices = ref<NoticeItem[]>([])
const noticeId = ref(0)
const instance = getCurrentInstance()

// 获取默认图标
const getDefaultIcon = (type: string): string => {
  const iconMap: Record<string, string> = {
    success: 'fas fa-check-circle',
    error: 'fas fa-times-circle',
    warning: 'fas fa-exclamation-triangle',
    info: 'fas fa-info-circle',
    loading: 'fas fa-spinner fa-spin'
  }
  return iconMap[type] || 'fas fa-bell'
}

// 显示通知
const show = (options: Partial<NoticeItem>) => {
  const id = ++noticeId.value
  const notice: NoticeItem = {
    id,
    type: options.type || 'info',
    message: options.message || '',
    description: options.description,
    icon: options.icon,
    duration: options.duration ?? 3000,
    animation: options.animation,
    onClick: options.onClick
  }
  
  notices.value.push(notice)
  
  // 自动关闭
  if (notice.duration > 0) {
    setTimeout(() => {
      removeNotice(id)
    }, notice.duration)
  }
  
  return id
}

// 移除通知
const removeNotice = (id: number) => {
  const index = notices.value.findIndex(n => n.id === id)
  if (index !== -1) {
    notices.value.splice(index, 1)
  }
}

// 点击处理
const handleClick = (notice: NoticeItem) => {
  if (notice.onClick) {
    notice.onClick()
  }
}

// 清空所有通知
const clear = () => {
  notices.value = []
}

// 暴露方法给父组件
defineExpose({
  show,
  clear,
  removeNotice
})
</script>

<style scoped>
.notification-container {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-width: 480px;
  width: 90%;
  pointer-events: none;
}

/* ==================== 通知卡片基础样式 ==================== */
.notification-toast {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  border-radius: 12px;
  background: var(--mall-bg-card);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
  cursor: default;
  position: relative;
  overflow: hidden;
  min-width: 280px;
  max-width: 480px;
  pointer-events: auto;
}

.notification-toast.is-clickable {
  cursor: pointer;
}

/* 类型颜色 */
.notification-toast.type-success {
  border: 1px solid rgba(0, 255, 136, 0.3);
}

.notification-toast.type-success .toast-icon {
  color: #00ff88;
}

.notification-toast.type-error {
  border: 1px solid rgba(255, 68, 68, 0.3);
}

.notification-toast.type-error .toast-icon {
  color: #ff4444;
}

.notification-toast.type-warning {
  border: 1px solid rgba(255, 187, 0, 0.3);
}

.notification-toast.type-warning .toast-icon {
  color: #ffbb00;
}

.notification-toast.type-info {
  border: 1px solid rgba(0, 212, 255, 0.3);
}

.notification-toast.type-info .toast-icon {
  color: #00d4ff;
}

.notification-toast.type-loading {
  border: 1px solid rgba(157, 78, 221, 0.3);
}

.notification-toast.type-loading .toast-icon {
  color: #9d4edd;
}

/* 图标 */
.toast-icon {
  font-size: 20px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
}

/* 内容 */
.toast-content {
  flex: 1;
  min-width: 0;
}

.toast-message {
  font-size: 14px;
  font-weight: 500;
  color: #fff;
  line-height: 1.5;
}

.toast-description {
  font-size: 12px;
  color: var(--mall-text-muted);
  margin-top: 4px;
  line-height: 1.4;
}

/* 关闭按钮 */
.toast-close {
  font-size: 14px;
  color: var(--mall-text-muted);
  cursor: pointer;
  padding: 2px;
  transition: color 0.2s;
  flex-shrink: 0;
}

.toast-close:hover {
  color: #fff;
}

/* 进度条 */
.toast-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: rgba(255, 255, 255, 0.1);
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  animation: progress-linear ease-in-out;
  animation-fill-mode: forwards;
}

@keyframes progress-linear {
  from {
    width: 100%;
  }
  to {
    width: 0%;
  }
}

/* ==================== 动画效果 ==================== */

/* 淡入淡出 (默认 - 正上方) */
.animation-fade {
  animation: fadeInTop 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes fadeInTop {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 从顶部滑入 */
.animation-slide-top {
  animation: slideInTop 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes slideInTop {
  from {
    opacity: 0;
    transform: translateY(-50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 从底部滑入 */
.animation-slide-bottom {
  animation: slideInBottom 0.3s ease-out;
}

@keyframes slideInBottom {
  from {
    opacity: 0;
    transform: translateY(100%);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 从左侧滑入 */
.animation-slide-left {
  animation: slideInLeft 0.3s ease-out;
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-100%);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 从右侧滑入 */
.animation-slide-right {
  animation: slideInRight 0.3s ease-out;
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(100%);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 弹跳效果 */
.animation-bounce {
  animation: bounceIn 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

@keyframes bounceIn {
  0% {
    opacity: 0;
    transform: scale(0.3) translateY(-20px);
  }
  50% {
    transform: scale(1.05) translateY(-10px);
  }
  70% {
    transform: scale(0.9) translateY(-5px);
  }
  100% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

/* 缩放效果 */
.animation-zoom {
  animation: zoomIn 0.3s ease-out;
}

@keyframes zoomIn {
  from {
    opacity: 0;
    transform: scale(0.5) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

/* 翻转效果 */
.animation-flip {
  animation: flipIn 0.5s ease-out;
}

@keyframes flipIn {
  from {
    opacity: 0;
    transform: perspective(400px) rotateY(90deg) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: perspective(400px) rotateY(0) translateY(0);
  }
}

/* ==================== 列表过渡动画 ==================== */
.notification-list-enter-active {
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.notification-list-leave-active {
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  position: absolute;
  width: 100%;
}

.notification-list-enter-from {
  opacity: 0;
  transform: translateY(-30px);
}

.notification-list-enter-to {
  opacity: 1;
  transform: translateY(0);
}

.notification-list-leave-from {
  opacity: 1;
  transform: translateY(0);
}

.notification-list-leave-to {
  opacity: 0;
  transform: translateY(-30px);
}

/* ==================== 响应式 ==================== */
@media (max-width: 768px) {
  .notification-container {
    top: 10px;
    left: 10px;
    right: 10px;
    max-width: none;
    width: calc(100% - 20px);
  }

  .notification-toast {
    min-width: auto;
    max-width: none;
  }
}
</style>

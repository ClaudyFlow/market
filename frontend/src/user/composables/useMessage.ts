/**
 * 通知/消息 composable
 */

import { ElMessage, ElNotification, type MessageOptions, type NotificationOptions } from 'element-plus'

interface MessageHandle {
  close: () => void
}

export function useMessage() {
  const show = (options: string | MessageOptions) => {
    return ElMessage(typeof options === 'string' ? { message: options } : options)
  }

  const success = (message: string, options?: Partial<MessageOptions>) => {
    return ElMessage.success({ message, ...options })
  }

  const error = (message: string, options?: Partial<MessageOptions>) => {
    return ElMessage.error({ message, ...options })
  }

  const warning = (message: string, options?: Partial<MessageOptions>) => {
    return ElMessage.warning({ message, ...options })
  }

  const info = (message: string, options?: Partial<MessageOptions>) => {
    return ElMessage.info({ message, ...options })
  }

  return {
    show,
    success,
    error,
    warning,
    info
  }
}

export function useNotification() {
  const show = (options: string | NotificationOptions) => {
    return ElNotification(typeof options === 'string' ? { message: options } : options)
  }

  const success = (options: string | Partial<NotificationOptions>) => {
    const opts = typeof options === 'string' ? { message: options } : options
    return ElNotification.success(opts)
  }

  const error = (options: string | Partial<NotificationOptions>) => {
    const opts = typeof options === 'string' ? { message: options } : options
    return ElNotification.error(opts)
  }

  const warning = (options: string | Partial<NotificationOptions>) => {
    const opts = typeof options === 'string' ? { message: options } : options
    return ElNotification.warning(opts)
  }

  const info = (options: string | Partial<NotificationOptions>) => {
    const opts = typeof options === 'string' ? { message: options } : options
    return ElNotification.info(opts)
  }

  return {
    show,
    success,
    error,
    warning,
    info
  }
}

/**
 * 确认对话框
 */
import { ElMessageBox, type MessageBoxOptions } from 'element-plus'

export function useConfirm() {
  const confirm = (message: string, title: string = '提示', options?: Partial<MessageBoxOptions>) => {
    return ElMessageBox.confirm(message, title, {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      ...options
    })
  }

  const alert = (message: string, title: string = '提示', options?: Partial<MessageBoxOptions>) => {
    return ElMessageBox.alert(message, title, {
      confirmButtonText: '确定',
      ...options
    })
  }

  const prompt = (message: string, title: string = '提示', options?: Partial<MessageBoxOptions>) => {
    return ElMessageBox.prompt(message, title, {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      ...options
    })
  }

  return {
    confirm,
    alert,
    prompt
  }
}

/**
 * 加载状态
 */
import { ElLoading } from 'element-plus'

export function useLoading() {
  const showLoading = (options: { target?: string; text?: string; fullscreen?: boolean } = {}) => {
    return ElLoading.service({
      lock: true,
      text: options.text || '加载中...',
      background: 'rgba(0, 0, 0, 0.7)',
      target: options.target,
      fullscreen: options.fullscreen !== false
    })
  }

  return {
    showLoading
  }
}

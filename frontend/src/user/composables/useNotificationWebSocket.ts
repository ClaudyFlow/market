/**
 * 通知 WebSocket 推送
 * 用于接收实时通知（系统通知、订单状态、活动等）
 */
import { ref, onMounted, onUnmounted } from 'vue'
import { Client, IMessage } from '@stomp/stompjs'
import { ElNotification } from 'element-plus'
import type { Notification } from '@user/types/notification'

export interface UseNotificationWebSocketOptions {
  /** WebSocket 端点地址 */
  endpoint?: string
  /** 是否自动连接 */
  autoConnect?: boolean
  /** 是否显示桌面通知 */
  showDesktopNotification?: boolean
  /** 收到通知时的回调 */
  onNotification?: (notification: Notification) => void
}

export function useNotificationWebSocket(options: UseNotificationWebSocketOptions = {}) {
  const {
    endpoint = import.meta.env.VITE_WS_URL || '/ws',
    autoConnect = true,
    showDesktopNotification = true,
    onNotification
  } = options

  const client = ref<Client | null>(null)
  const isConnected = ref(false)
  const notifications = ref<Notification[]>([])
  const unreadCount = ref(0)

  /**
   * 连接 WebSocket
   */
  const connect = async (token?: string): Promise<void> => {
    return new Promise((resolve, reject) => {
      try {
        client.value = new Client({
          brokerURL: endpoint,
          connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
          debug: (str: string) => {
            if (import.meta.env.DEV) {
              console.log('[NotificationWS]', str)
            }
          },
          onConnect: () => {
            console.log('[NotificationWS] 连接成功')
            isConnected.value = true

            // 订阅通知队列
            subscribe('/user/queue/notifications', handleMessage)
            
            resolve()
          },
          onStompError: (frame: any) => {
            console.error('[NotificationWS] STOMP 错误:', frame)
            reject(frame)
          },
          onWebSocketError: (event: any) => {
            console.error('[NotificationWS] WebSocket 错误:', event)
            reject(event)
          },
          onDisconnect: () => {
            console.log('[NotificationWS] 断开连接')
            isConnected.value = false
          }
        })

        client.value.activate()
      } catch (error) {
        console.error('[NotificationWS] 连接失败:', error)
        reject(error)
      }
    })
  }

  /**
   * 订阅目标地址
   */
  const subscribe = (destination: string, callback: (message: any) => void) => {
    if (!client.value || !isConnected.value) {
      console.warn('[NotificationWS] 未连接，无法订阅')
      return
    }

    client.value.subscribe(destination, (message: IMessage) => {
      try {
        const data = JSON.parse(message.body)
        callback(data)
      } catch (e) {
        console.error('[NotificationWS] 解析消息失败:', e)
      }
    })
  }

  /**
   * 处理接收到的通知
   */
  const handleMessage = (notification: Notification) => {
    console.log('[NotificationWS] 收到通知:', notification)
    
    // 添加到列表
    notifications.value.unshift(notification)
    
    // 更新未读数
    if (!notification.isRead) {
      unreadCount.value++
    }

    // 显示桌面通知
    if (showDesktopNotification) {
      showNotification(notification)
    }

    // 触发回调
    if (onNotification) {
      onNotification(notification)
    }
  }

  /**
   * 显示 Element Plus 通知
   */
  const showNotification = (notification: Notification) => {
    const typeMap: Record<string, 'success' | 'warning' | 'info' | 'error'> = {
      'SYSTEM': 'info',
      'ACTIVITY': 'success',
      'ORDER': 'warning'
    }

    ElNotification({
      title: notification.title || '新通知',
      message: notification.content || '',
      type: typeMap[notification.type] || 'info',
      duration: notification.priority >= 4 ? 0 : 4500, // 重要通知不自动关闭
      position: 'bottom-right',
      onClick: () => {
        // 点击通知跳转到通知中心
        window.location.href = '/notice'
      }
    })
  }

  /**
   * 标记通知已读
   */
  const markAsRead = (notificationId: number) => {
    const index = notifications.value.findIndex(n => n.id === notificationId)
    if (index !== -1 && !notifications.value[index].isRead) {
      notifications.value[index].isRead = true
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    }
  }

  /**
   * 清空所有通知
   */
  const clearAll = () => {
    notifications.value = []
    unreadCount.value = 0
  }

  /**
   * 断开连接
   */
  const disconnect = () => {
    if (client.value) {
      client.value.deactivate()
      client.value = null
      isConnected.value = false
      console.log('[NotificationWS] 已断开连接')
    }
  }

  // 生命周期
  onMounted(() => {
    if (autoConnect) {
      connect().catch(console.error)
    }
  })

  onUnmounted(() => {
    disconnect()
  })

  return {
    /** WebSocket 客户端 */
    client,
    /** 是否已连接 */
    isConnected,
    /** 通知列表 */
    notifications,
    /** 未读数量 */
    unreadCount,
    /** 连接 */
    connect,
    /** 断开连接 */
    disconnect,
    /** 标记已读 */
    markAsRead,
    /** 清空通知 */
    clearAll
  }
}

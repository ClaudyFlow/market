/**
 * WebSocket 网络库 - 客服聊天
 * 支持自动重连、心跳检测、消息队列
 */

import { ref, computed, type Ref, type ComputedRef } from 'vue'

// WebSocket 连接状态
export enum WSStatus {
  CONNECTING = 'connecting',
  CONNECTED = 'connected',
  DISCONNECTED = 'disconnected',
  RECONNECTING = 'reconnecting',
  ERROR = 'error'
}

// 消息类型
export interface WSMessage {
  id: string
  type: 'text' | 'image' | 'file' | 'system' | 'typing'
  content: string
  senderId: number
  senderName?: string
  senderAvatar?: string
  receiverId: number
  timestamp: number
  isSelf?: boolean
}

// WebSocket 配置
interface WSConfig {
  url: string
  reconnectInterval?: number
  maxReconnectAttempts?: number
  heartbeatInterval?: number
  heartbeatMsg?: string
}

// 创建 WebSocket 连接
export class WebSocketClient {
  private ws: WebSocket | null = null
  private config: Required<WSConfig>
  private reconnectAttempts = 0
  private reconnectTimer: ReturnType<typeof setTimeout> | null = null
  private heartbeatTimer: ReturnType<typeof setInterval> | null = null
  private messageQueue: string[] = []
  private listeners: Map<string, Set<(data: any) => void>> = new Map()

  public status: Ref<WSStatus>
  public isConnected: ComputedRef<boolean>

  constructor(config: WSConfig) {
    this.config = {
      reconnectInterval: 3000,
      maxReconnectAttempts: 5,
      heartbeatInterval: 30000,
      heartbeatMsg: JSON.stringify({ type: 'ping' }),
      ...config
    }
    this.status = ref<WSStatus>(WSStatus.DISCONNECTED)
    this.isConnected = computed(() => this.status.value === WSStatus.CONNECTED)
  }

  connect(): void {
    if (this.ws?.readyState === WebSocket.OPEN) return

    this.status.value = WSStatus.CONNECTING
    this.emit('status', { status: WSStatus.CONNECTING })

    try {
      this.ws = new WebSocket(this.config.url)

      this.ws.onopen = () => {
        this.status.value = WSStatus.CONNECTED
        this.reconnectAttempts = 0
        this.emit('status', { status: WSStatus.CONNECTED })
        this.startHeartbeat()
        this.flushMessageQueue()
      }

      this.ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data)
          this.emit('message', data)
        } catch {
          this.emit('message', { type: 'raw', content: event.data })
        }
      }

      this.ws.onclose = (event) => {
        this.status.value = WSStatus.DISCONNECTED
        this.stopHeartbeat()
        this.emit('status', { status: WSStatus.DISCONNECTED, code: event.code })
        this.attemptReconnect()
      }

      this.ws.onerror = (error) => {
        this.status.value = WSStatus.ERROR
        this.emit('status', { status: WSStatus.ERROR, error })
        this.emit('error', error)
      }
    } catch (error) {
      this.status.value = WSStatus.ERROR
      this.attemptReconnect()
    }
  }

  disconnect(): void {
    this.stopHeartbeat()
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
    this.status.value = WSStatus.DISCONNECTED
  }

  send(message: string | object): boolean {
    const msg = typeof message === 'string' ? message : JSON.stringify(message)
    if (this.ws?.readyState === WebSocket.OPEN) {
      this.ws.send(msg)
      return true
    }
    this.messageQueue.push(msg)
    return false
  }

  sendChatMessage(content: string, receiverId: number, type = 'text'): void {
    this.send({
      type: 'chat',
      content,
      receiverId,
      messageType: type,
      timestamp: Date.now()
    })
  }

  sendTyping(receiverId: number, isTyping: boolean): void {
    this.send({
      type: 'typing',
      receiverId,
      isTyping,
      timestamp: Date.now()
    })
  }

  private attemptReconnect(): void {
    if (this.reconnectAttempts >= this.config.maxReconnectAttempts) {
      this.emit('status', { status: WSStatus.ERROR, message: '重连失败' })
      return
    }
    this.reconnectAttempts++
    this.status.value = WSStatus.RECONNECTING
    this.emit('status', { status: WSStatus.RECONNECTING, attempt: this.reconnectAttempts })
    this.reconnectTimer = setTimeout(() => this.connect(), this.config.reconnectInterval)
  }

  private startHeartbeat(): void {
    this.heartbeatTimer = setInterval(() => {
      if (this.ws?.readyState === WebSocket.OPEN) {
        this.ws.send(this.config.heartbeatMsg)
      }
    }, this.config.heartbeatInterval)
  }

  private stopHeartbeat(): void {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer)
      this.heartbeatTimer = null
    }
  }

  private flushMessageQueue(): void {
    while (this.messageQueue.length > 0 && this.ws?.readyState === WebSocket.OPEN) {
      const msg = this.messageQueue.shift()
      if (msg) this.ws.send(msg)
    }
  }

  on(event: string, callback: (data: any) => void): () => void {
    if (!this.listeners.has(event)) {
      this.listeners.set(event, new Set())
    }
    this.listeners.get(event)!.add(callback)
    return () => this.listeners.get(event)?.delete(callback)
  }

  private emit(event: string, data: any): void {
    this.listeners.get(event)?.forEach(callback => {
      try {
        callback(data)
      } catch (e) {
        console.error(`[WebSocket] 事件 ${event} 处理错误:`, e)
      }
    })
  }
}

let wsClient: WebSocketClient | null = null

export function createWebSocketClient(url?: string): WebSocketClient {
  const wsUrl = url || `ws://${window.location.host}/ws/chat`
  wsClient = new WebSocketClient({ url: wsUrl })
  return wsClient
}

export function getWebSocketClient(): WebSocketClient | null {
  return wsClient
}

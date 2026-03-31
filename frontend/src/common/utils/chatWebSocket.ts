/**
 * WebSocket 聊天客户端
 * 用于客服聊天功能的实时通信
 */
import { Client, IMessage } from '@stomp/stompjs'

export interface ChatMessageData {
  id?: number
  senderId: number
  receiverId: number
  content: string
  type: 'TEXT' | 'IMAGE' | 'SYSTEM' | 'FILE'
  isRead?: boolean
  createdAt?: string
}

export type MessageCallback = (message: ChatMessageData) => void
export type ConnectCallback = () => void
export type ErrorCallback = (error: any) => void

export class ChatWebSocketClient {
  private client: Client | null = null
  private messageCallbacks: MessageCallback[] = []
  private connectCallbacks: ConnectCallback[] = []
  private errorCallbacks: ErrorCallback[] = []
  private connected = false
  private reconnectAttempts = 0
  private maxReconnectAttempts = 5
  private reconnectDelay = 3000

  constructor(private wsEndpoint: string = '/ws') {}

  /**
   * 连接 WebSocket
   */
  connect(token?: string): Promise<void> {
    return new Promise((resolve, reject) => {
      // 使用 SockJS 作为传输层
      this.client = new Client({
        brokerURL: this.wsEndpoint,
        connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
        debug: (str: string) => {
          console.log('[STOMP]', str)
        },
        onConnect: () => {
          console.log('[ChatWS] 连接成功')
          this.connected = true
          this.reconnectAttempts = 0
          
          // 订阅个人消息队列
          this.subscribe('/user/queue/messages', this.handleMessage.bind(this))
          
          // 触发连接回调
          this.connectCallbacks.forEach(cb => cb())
          resolve()
        },
        onStompError: (frame: any) => {
          console.error('[ChatWS] STOMP 错误:', frame)
          this.errorCallbacks.forEach(cb => cb(frame))
          reject(frame)
        },
        onWebSocketError: (event: any) => {
          console.error('[ChatWS] WebSocket 错误:', event)
          this.errorCallbacks.forEach(cb => cb(event))
          reject(event)
        },
        onDisconnect: () => {
          console.log('[ChatWS] 断开连接')
          this.connected = false
          this.handleReconnect()
        }
      })

      this.client.activate()
    })
  }

  /**
   * 订阅目标地址
   */
  private subscribe(destination: string, callback: (message: any) => void) {
    if (!this.client || !this.connected) return

    this.client.subscribe(destination, (message: IMessage) => {
      try {
        const data = JSON.parse(message.body)
        callback(data)
      } catch (e) {
        console.error('[ChatWS] 解析消息失败:', e)
      }
    })
  }

  /**
   * 处理接收到的消息
   */
  private handleMessage(message: ChatMessageData) {
    console.log('[ChatWS] 收到消息:', message)
    this.messageCallbacks.forEach(cb => cb(message))
  }

  /**
   * 发送消息
   */
  send(destination: string, message: ChatMessageData) {
    if (!this.client || !this.connected) {
      console.warn('[ChatWS] 未连接，使用备用发送')
      return false
    }

    this.client.publish({
      destination,
      body: JSON.stringify(message)
    })
    return true
  }

  /**
   * 发送聊天消息
   */
  sendChatMessage(receiverId: number, content: string, type: string = 'TEXT') {
    return this.send('/app/chat.send', {
      receiverId,
      content,
      type
    })
  }

  /**
   * 加入聊天
   */
  joinChat(roomId?: string) {
    return this.send('/app/chat.join', {
      type: 'SYSTEM',
      content: roomId ? `加入聊天室 ${roomId}` : '加入客服聊天'
    } as any)
  }

  /**
   * 监听消息
   */
  onMessage(callback: MessageCallback) {
    this.messageCallbacks.push(callback)
  }

  /**
   * 监听连接
   */
  onConnect(callback: ConnectCallback) {
    this.connectCallbacks.push(callback)
  }

  /**
   * 监听错误
   */
  onError(callback: ErrorCallback) {
    this.errorCallbacks.push(callback)
  }

  /**
   * 处理重连
   */
  private handleReconnect() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('[ChatWS] 重连次数已达上限')
      return
    }

    this.reconnectAttempts++
    console.log(`[ChatWS] ${this.reconnectDelay / 1000}秒后尝试重连 (${this.reconnectAttempts}/${this.maxReconnectAttempts})`)
    
    setTimeout(() => {
      console.log('[ChatWS] 正在重连...')
      this.connect().catch(console.error)
    }, this.reconnectDelay)
  }

  /**
   * 断开连接
   */
  disconnect() {
    if (this.client) {
      this.client.deactivate()
      this.client = null
      this.connected = false
      console.log('[ChatWS] 已断开连接')
    }
  }

  /**
   * 是否已连接
   */
  isConnected(): boolean {
    return this.connected && this.client?.active
  }
}

// 导出单例
export const chatWS = new ChatWebSocketClient()

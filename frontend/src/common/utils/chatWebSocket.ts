/**
 * 客服聊天 WebSocket 工具
 */
import { createWebSocketClient, type WebSocketClient } from '@common/api/websocket'

class ChatWebSocket {
  private client: WebSocketClient | null = null
  private messageHandlers: ((data: any) => void)[] = []
  private connectHandlers: (() => void)[] = []
  private errorHandlers: ((error: any) => void)[] = []

  connect(token?: string): Promise<void> {
    return new Promise((resolve, reject) => {
      const wsUrl = `ws://${window.location.host}/ws/chat${token ? `?token=${token}` : ''}`
      this.client = createWebSocketClient(wsUrl)

      this.client.on('message', (data) => {
        this.messageHandlers.forEach(handler => handler(data))
      })

      this.client.on('status', (data) => {
        if (data.status === 'connected') {
          this.connectHandlers.forEach(handler => handler())
          resolve()
        }
      })

      this.client.on('error', (error) => {
        this.errorHandlers.forEach(handler => handler(error))
        reject(error)
      })

      this.client.connect()
    })
  }

  disconnect() {
    this.client?.disconnect()
    this.client = null
  }

  joinChat() {
    this.client?.send({ type: 'join', room: 'customer_service' })
  }

  sendChatMessage(receiverId: number, content: string, type: string): boolean {
    if (!this.client?.isConnected) return false
    this.client.sendChatMessage(content, receiverId, type)
    return true
  }

  sendReadReceipt(senderId: number, messageId: number) {
    this.client?.send({
      type: 'read_receipt',
      senderId,
      messageId
    })
  }

  onMessage(handler: (data: any) => void) {
    this.messageHandlers.push(handler)
    return () => {
      const index = this.messageHandlers.indexOf(handler)
      if (index > -1) this.messageHandlers.splice(index, 1)
    }
  }

  onConnect(handler: () => void) {
    this.connectHandlers.push(handler)
    return () => {
      const index = this.connectHandlers.indexOf(handler)
      if (index > -1) this.connectHandlers.splice(index, 1)
    }
  }

  onError(handler: (error: any) => void) {
    this.errorHandlers.push(handler)
    return () => {
      const index = this.errorHandlers.indexOf(handler)
      if (index > -1) this.errorHandlers.splice(index, 1)
    }
  }

  get isConnected() {
    return this.client?.isConnected.value || false
  }
}

export const chatWS = new ChatWebSocket()

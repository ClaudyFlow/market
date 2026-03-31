/**
 * 消息模板工具类
 * 用于快速生成各种场景的客服消息
 */
import type { ChatMessage } from './chat'
import { MessageStatus, OrderMessageStatus, PaymentMessageStatus, LogisticsMessageStatus, AfterSalesMessageStatus } from './messageStatus'

const CS_USER_ID = 999999 // 客服 ID

export interface MessageTemplate {
  status: number
  content: string
  type: 'TEXT' | 'CARD' | 'SYSTEM'
  data?: any
}

export const MessageTemplates = {
  // ========== 订单相关消息模板 ==========
  order: {
    created: (orderNo: string, amount: number): MessageTemplate => ({
      status: OrderMessageStatus.ORDER_CREATED,
      content: `您的订单 ${orderNo} 已创建，订单金额：¥${amount.toFixed(2)}`,
      type: 'CARD',
      data: { orderNo, amount }
    }),
    
    confirmed: (orderNo: string): MessageTemplate => ({
      status: OrderMessageStatus.ORDER_CONFIRMED,
      content: `订单 ${orderNo} 已确认，我们将尽快为您处理`,
      type: 'SYSTEM'
    }),
    
    shipped: (orderNo: string, trackingNo: string): MessageTemplate => ({
      status: OrderMessageStatus.ORDER_SHIPPED,
      content: `订单 ${orderNo} 已发货，快递单号：${trackingNo}`,
      type: 'CARD',
      data: { orderNo, trackingNo }
    }),
    
    delivered: (orderNo: string): MessageTemplate => ({
      status: OrderMessageStatus.ORDER_DELIVERED,
      content: `订单 ${orderNo} 已送达，请确认收货`,
      type: 'SYSTEM'
    }),
    
    abnormal: (orderNo: string, reason: string): MessageTemplate => ({
      status: OrderMessageStatus.ORDER_ABNORMAL,
      content: `订单 ${orderNo} 异常：${reason}，请及时处理`,
      type: 'SYSTEM'
    })
  },

  // ========== 支付相关消息模板 ==========
  payment: {
    pending: (orderNo: string, amount: number, expireTime: string): MessageTemplate => ({
      status: PaymentMessageStatus.PAYMENT_PENDING,
      content: `订单 ${orderNo} 待支付，金额：¥${amount.toFixed(2)}，请在 ${expireTime} 前完成支付`,
      type: 'CARD',
      data: { orderNo, amount, expireTime }
    }),
    
    success: (orderNo: string, amount: number): MessageTemplate => ({
      status: PaymentMessageStatus.PAYMENT_SUCCESS,
      content: `支付成功！订单 ${orderNo}，支付金额：¥${amount.toFixed(2)}`,
      type: 'SYSTEM'
    }),
    
    failed: (orderNo: string, reason: string): MessageTemplate => ({
      status: PaymentMessageStatus.PAYMENT_FAILED,
      content: `支付失败，订单 ${orderNo}，原因：${reason}`,
      type: 'SYSTEM'
    }),
    
    refunded: (orderNo: string, amount: number): MessageTemplate => ({
      status: PaymentMessageStatus.PAYMENT_REFUNDED,
      content: `退款成功！订单 ${orderNo}，退款金额：¥${amount.toFixed(2)}`,
      type: 'CARD',
      data: { orderNo, amount }
    })
  },

  // ========== 物流相关消息模板 ==========
  logistics: {
    pickedUp: (trackingNo: string): MessageTemplate => ({
      status: LogisticsMessageStatus.LOGISTICS_PICKED_UP,
      content: `快递 ${trackingNo} 已被揽收`,
      type: 'SYSTEM'
    }),
    
    inTransit: (trackingNo: string, location: string): MessageTemplate => ({
      status: LogisticsMessageStatus.LOGISTICS_IN_TRANSIT,
      content: `快递 ${trackingNo} 正在运输中，当前位置：${location}`,
      type: 'SYSTEM'
    }),
    
    outForDelivery: (trackingNo: string, courierName: string, courierPhone: string): MessageTemplate => ({
      status: LogisticsMessageStatus.LOGISTICS_OUT_FOR_DELIVERY,
      content: `快递 ${trackingNo} 正在派送，派送员：${courierName}，电话：${courierPhone}`,
      type: 'CARD',
      data: { trackingNo, courierName, courierPhone }
    }),
    
    exception: (trackingNo: string, reason: string): MessageTemplate => ({
      status: LogisticsMessageStatus.LOGISTICS_EXCEPTION,
      content: `物流异常：${trackingNo}，原因：${reason}`,
      type: 'SYSTEM'
    })
  },

  // ========== 售后相关消息模板 ==========
  afterSales: {
    returnApproved: (returnNo: string): MessageTemplate => ({
      status: AfterSalesMessageStatus.RETURN_APPROVED,
      content: `退货申请 ${returnNo} 已批准，请寄回商品`,
      type: 'SYSTEM'
    }),
    
    returnReceived: (returnNo: string): MessageTemplate => ({
      status: AfterSalesMessageStatus.RETURN_RECEIVED,
      content: `退货 ${returnNo} 已收到，正在为您处理退款`,
      type: 'SYSTEM'
    }),
    
    returnCompleted: (returnNo: string, amount: number): MessageTemplate => ({
      status: AfterSalesMessageStatus.RETURN_COMPLETED,
      content: `退货完成！退货单 ${returnNo}，退款金额：¥${amount.toFixed(2)}`,
      type: 'CARD',
      data: { returnNo, amount }
    }),
    
    exchangeCompleted: (exchangeNo: string): MessageTemplate => ({
      status: AfterSalesMessageStatus.EXCHANGE_COMPLETED,
      content: `换货完成！换货单 ${exchangeNo}，新商品已寄出`,
      type: 'SYSTEM'
    }),
    
    complaintResolved: (complaintNo: string, result: string): MessageTemplate => ({
      status: AfterSalesMessageStatus.COMPLAINT_RESOLVED,
      content: `投诉 ${complaintNo} 已解决，处理结果：${result}`,
      type: 'SYSTEM'
    })
  },

  // ========== 通用消息模板 ==========
  common: {
    welcome: (): MessageTemplate => ({
      status: MessageStatus.DELIVERED,
      content: '您好，欢迎使用在线客服，请问有什么可以帮您？',
      type: 'TEXT'
    }),
    
    autoReply: (): MessageTemplate => ({
      status: MessageStatus.DELIVERED,
      content: '您好，客服正在为您解答，请稍候...',
      type: 'SYSTEM'
    }),
    
    workingHours: (): MessageTemplate => ({
      status: MessageStatus.DELIVERED,
      content: '当前为非工作时间，客服工作时间为 9:00-18:00，我们将在工作时间为您解答',
      type: 'SYSTEM'
    }),
    
    transferToAgent: (agentName: string): MessageTemplate => ({
      status: MessageStatus.DELIVERED,
      content: `正在为您转接客服 ${agentName}，请稍候...`,
      type: 'SYSTEM'
    }),
    
    evaluation: (orderNo?: string): MessageTemplate => ({
      status: MessageStatus.DELIVERED,
      content: orderNo 
        ? `订单 ${orderNo} 的服务已完成，请对本次服务进行评价`
        : '本次服务已完成，请对本次服务进行评价',
      type: 'CARD',
      data: { orderNo }
    })
  }
}

/**
 * 创建消息对象
 */
export function createMessage(
  senderId: number,
  receiverId: number,
  template: MessageTemplate,
  localId?: string
): ChatMessage {
  return {
    localId: localId || `msg_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
    senderId,
    receiverId,
    content: template.content,
    type: template.type as 'TEXT' | 'IMAGE' | 'SYSTEM' | 'FILE',
    status: template.status,
    isRead: false,
    createdAt: new Date().toISOString()
  }
}

/**
 * 创建客服消息
 */
export function createAgentMessage(
  receiverId: number,
  template: MessageTemplate,
  localId?: string
): ChatMessage {
  return createMessage(CS_USER_ID, receiverId, template, localId)
}

/**
 * 创建系统通知消息
 */
export function createSystemMessage(
  receiverId: number,
  content: string,
  status: number = MessageStatus.DELIVERED
): ChatMessage {
  return {
    localId: `sys_${Date.now()}`,
    senderId: 0, // 系统 ID
    receiverId,
    content,
    type: 'SYSTEM',
    status,
    isRead: false,
    createdAt: new Date().toISOString()
  }
}

export default MessageTemplates

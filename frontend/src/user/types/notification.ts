/**
 * 通知相关类型定义
 */

// 通知信息
export interface Notification {
  id: number
  type: NotificationType
  title: string
  content: string
  image?: string
  link?: string
  linkType?: 'order' | 'product' | 'shop' | 'activity' | 'system'
  read: boolean
  createTime: string
  readTime?: string
  extra?: Record<string, any>
}

// 通知类型
export type NotificationType = 
  | 'system'       // 系统通知
  | 'activity'     // 活动通知
  | 'order'        // 订单通知
  | 'promotion'    // 促销通知
  | 'reminder'     // 提醒通知
  | 'message'      // 消息通知

// 通知统计
export interface NotificationStats {
  total: number
  unread: number
  systemUnread: number
  activityUnread: number
  orderUnread: number
  promotionUnread: number
}

// 通知偏好设置
export interface NotificationPreference {
  systemNotify: boolean
  activityNotify: boolean
  orderNotify: boolean
  promoNotify: boolean
  emailNotify: boolean
  smsNotify: boolean
  pushNotify: boolean
}

// 通知模板
export interface NotificationTemplate {
  id: number
  type: NotificationType
  title: string
  content: string
  link?: string
  enabled: boolean
}

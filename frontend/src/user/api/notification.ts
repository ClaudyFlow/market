/**
 * 通知相关 API
 */

import { get, post, put, del } from './request'
import type { Notification, NotificationStats } from '@user/types/notification'
import type { PageData, PageParams } from './request'

const BASE_URL = '/notification'

/**
 * 获取通知列表
 */
export function getNotificationList(params?: PageParams & { type?: string }): Promise<PageData<Notification>> {
  return get(BASE_URL, params)
}

/**
 * 获取通知详情
 */
export function getNotificationDetail(notificationId: number | string): Promise<Notification> {
  return get(`${BASE_URL}/${notificationId}`)
}

/**
 * 标记为已读
 */
export function markAsRead(notificationId: number | string): Promise<void> {
  return put(`${BASE_URL}/${notificationId}/read`)
}

/**
 * 批量标记为已读
 */
export function batchMarkAsRead(notificationIds: number[]): Promise<void> {
  return put(`${BASE_URL}/batch-read`, { ids: notificationIds })
}

/**
 * 全部标记为已读
 */
export function markAllAsRead(): Promise<void> {
  return put(`${BASE_URL}/all-read`)
}

/**
 * 删除通知
 */
export function deleteNotification(notificationId: number | string): Promise<void> {
  return del(`${BASE_URL}/${notificationId}`)
}

/**
 * 批量删除通知
 */
export function batchDeleteNotifications(notificationIds: number[]): Promise<void> {
  return del(`${BASE_URL}/batch-delete`, { ids: notificationIds })
}

/**
 * 清空通知
 */
export function clearNotifications(): Promise<void> {
  return del(`${BASE_URL}/clear`)
}

/**
 * 获取未读数量
 */
export function getUnreadCount(): Promise<{ count: number }> {
  return get(`${BASE_URL}/unread-count`)
}

/**
 * 获取通知统计
 */
export function getNotificationStats(): Promise<NotificationStats> {
  return get(`${BASE_URL}/stats`)
}

/**
 * 系统通知列表
 */
export function getSystemNotifications(params?: PageParams): Promise<PageData<Notification>> {
  return get(`${BASE_URL}/system`, params)
}

/**
 * 活动通知列表
 */
export function getActivityNotifications(params?: PageParams): Promise<PageData<Notification>> {
  return get(`${BASE_URL}/activity`, params)
}

/**
 * 订单通知列表
 */
export function getOrderNotifications(params?: PageParams): Promise<PageData<Notification>> {
  return get(`${BASE_URL}/order`, params)
}

/**
 * 获取最新通知
 */
export function getLatestNotifications(limit?: number): Promise<Notification[]> {
  return get(`${BASE_URL}/latest`, { limit })
}

/**
 * 设置通知偏好
 */
export function setNotificationPreference(preference: {
  systemNotify?: boolean
  activityNotify?: boolean
  orderNotify?: boolean
  promoNotify?: boolean
}): Promise<void> {
  return put(`${BASE_URL}/preference`, preference)
}

/**
 * 获取通知偏好
 */
export function getNotificationPreference(): Promise<{
  systemNotify: boolean
  activityNotify: boolean
  orderNotify: boolean
  promoNotify: boolean
}> {
  return get(`${BASE_URL}/preference`)
}

/**
 * 别名导出
 */
export const getNotifications = getNotificationList
export const clearAllNotifications = clearNotifications
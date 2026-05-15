/**
 * 通知状态管理
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Notification, NotificationStats } from '@user/types/notification'
import * as notificationApi from '@user/api/notification'

export const useNotificationStore = defineStore('notification', () => {
  // 状态
  const notifications = ref<Notification[]>([])
  const stats = ref<NotificationStats | null>(null)
  const loading = ref(false)
  const total = ref(0)

  // 计算属性
  const hasNotifications = computed(() => notifications.value.length > 0)
  const unreadCount = computed(() => stats.value?.unread || 0)
  const hasUnread = computed(() => unreadCount.value > 0)

  // 获取通知列表
  async function fetchNotifications(params?: { type?: string; page?: number; size?: number }) {
    try {
      loading.value = true
      const result = await notificationApi.getNotificationList(params)
      notifications.value = result.records
      total.value = result.total
      return result
    } catch (error) {
      console.error('获取通知列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取通知详情
  async function fetchNotificationDetail(notificationId: number | string) {
    try {
      loading.value = true
      const data = await notificationApi.getNotificationDetail(notificationId)
      // 自动标记为已读
      if (!data.read) {
        await markAsRead(notificationId)
      }
      return data
    } catch (error) {
      console.error('获取通知详情失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取未读数量
  async function fetchUnreadCount() {
    try {
      const result = await notificationApi.getUnreadCount()
      if (stats.value) {
        stats.value.unread = result.count
      }
      return result.count
    } catch (error) {
      console.error('获取未读数量失败:', error)
      return 0
    }
  }

  // 获取通知统计
  async function fetchStats() {
    try {
      const data = await notificationApi.getNotificationStats()
      stats.value = data
      return data
    } catch (error) {
      console.error('获取通知统计失败:', error)
      return null
    }
  }

  // 标记为已读
  async function markAsRead(notificationId: number | string) {
    try {
      await notificationApi.markAsRead(notificationId)
      const notification = notifications.value.find(n => n.id === Number(notificationId))
      if (notification) {
        notification.read = true
        notification.readTime = new Date().toISOString()
      }
      await fetchUnreadCount()
    } catch (error) {
      console.error('标记已读失败:', error)
      throw error
    }
  }

  // 批量标记已读
  async function batchMarkAsRead(notificationIds: number[]) {
    try {
      loading.value = true
      await notificationApi.batchMarkAsRead(notificationIds)
      notificationIds.forEach(id => {
        const notification = notifications.value.find(n => n.id === id)
        if (notification) {
          notification.read = true
          notification.readTime = new Date().toISOString()
        }
      })
      await fetchUnreadCount()
    } catch (error) {
      console.error('批量标记已读失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 全部标记已读
  async function markAllAsRead() {
    try {
      await notificationApi.markAllAsRead()
      notifications.value.forEach(n => {
        n.read = true
        n.readTime = new Date().toISOString()
      })
      await fetchUnreadCount()
    } catch (error) {
      console.error('全部标记已读失败:', error)
      throw error
    }
  }

  // 删除通知
  async function deleteNotification(notificationId: number | string) {
    try {
      await notificationApi.deleteNotification(notificationId)
      notifications.value = notifications.value.filter(n => n.id !== Number(notificationId))
      await fetchUnreadCount()
    } catch (error) {
      console.error('删除通知失败:', error)
      throw error
    }
  }

  // 批量删除
  async function batchDeleteNotifications(notificationIds: number[]) {
    try {
      loading.value = true
      await notificationApi.batchDeleteNotifications(notificationIds)
      notifications.value = notifications.value.filter(n => !notificationIds.includes(n.id))
      await fetchUnreadCount()
    } catch (error) {
      console.error('批量删除失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 清空通知
  async function clear() {
    try {
      await notificationApi.clearNotifications()
      notifications.value = []
      stats.value = null
      await fetchUnreadCount()
    } catch (error) {
      console.error('清空通知失败:', error)
      throw error
    }
  }

  // 获取最新通知
  async function fetchLatest(limit: number = 5) {
    try {
      const data = await notificationApi.getLatestNotifications(limit)
      return data
    } catch (error) {
      console.error('获取最新通知失败:', error)
      return []
    }
  }

  // 重置
  function reset() {
    notifications.value = []
    stats.value = null
    loading.value = false
  }

  return {
    // 状态
    notifications,
    stats,
    loading,
    total,
    // 计算属性
    hasNotifications,
    unreadCount,
    hasUnread,
    // 方法
    fetchNotifications,
    fetchNotificationDetail,
    fetchUnreadCount,
    fetchStats,
    markAsRead,
    batchMarkAsRead,
    markAllAsRead,
    deleteNotification,
    batchDeleteNotifications,
    clear,
    fetchLatest,
    reset
  }
})

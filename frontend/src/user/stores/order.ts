/**
 * 订单状态管理
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Order, OrderDetail, OrderStats } from '@user/types/order'
import * as orderApi from '@user/api/order'

export const useOrderStore = defineStore('order', () => {
  // 状态
  const orders = ref<Order[]>([])
  const orderDetail = ref<OrderDetail | null>(null)
  const stats = ref<OrderStats | null>(null)
  const loading = ref(false)
  const total = ref(0)

  // 计算属性
  const hasOrders = computed(() => orders.value.length > 0)
  const unpaidCount = computed(() => stats.value?.unpaid || 0)
  const unshippedCount = computed(() => stats.value?.unshipped || 0)
  const unreceivedCount = computed(() => stats.value?.unreceived || 0)

  // 获取订单列表
  async function fetchOrders(params?: { status?: string; page?: number; size?: number }) {
    try {
      loading.value = true
      const result = await orderApi.getOrderList(params)
      orders.value = result.records
      total.value = result.total
      return result
    } catch (error) {
      console.error('获取订单列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取订单详情
  async function fetchOrderDetail(orderId: number | string) {
    try {
      loading.value = true
      const data = await orderApi.getOrderDetail(orderId)
      orderDetail.value = data
      return data
    } catch (error) {
      console.error('获取订单详情失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 创建订单
  async function createOrder(data: {
    items?: { cartItemId?: number; productId?: number; skuId?: number; quantity: number }[]
    addressId: number
    couponId?: number
    remark?: string
    deliveryType?: 'express' | 'pickup' | 'virtual'
  }) {
    try {
      loading.value = true
      const order = await orderApi.createOrder(data)
      return order
    } catch (error) {
      console.error('创建订单失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 取消订单
  async function cancelOrder(orderId: number | string, reason?: string) {
    try {
      await orderApi.cancelOrder(orderId, reason)
      await fetchOrderDetail(orderId)
      await fetchStats()
    } catch (error) {
      console.error('取消订单失败:', error)
      throw error
    }
  }

  // 确认收货
  async function confirmReceive(orderId: number | string) {
    try {
      await orderApi.confirmReceive(orderId)
      await fetchOrderDetail(orderId)
      await fetchStats()
    } catch (error) {
      console.error('确认收货失败:', error)
      throw error
    }
  }

  // 删除订单
  async function deleteOrder(orderId: number | string) {
    try {
      await orderApi.deleteOrder(orderId)
      orders.value = orders.value.filter(o => o.id !== Number(orderId))
    } catch (error) {
      console.error('删除订单失败:', error)
      throw error
    }
  }

  // 支付订单
  async function payOrder(orderId: number | string, payType?: 'alipay' | 'wechat' | 'card') {
    try {
      const result = await orderApi.payOrder(orderId, payType)
      return result
    } catch (error) {
      console.error('支付订单失败:', error)
      throw error
    }
  }

  // 获取支付状态
  async function getPayStatus(orderId: number | string) {
    try {
      return await orderApi.getPayStatus(orderId)
    } catch (error) {
      console.error('获取支付状态失败:', error)
      throw error
    }
  }

  // 申请退款
  async function refundOrder(orderId: number | string, reason: string, images?: string[]) {
    try {
      await orderApi.refundOrder(orderId, reason, images)
      await fetchOrderDetail(orderId)
    } catch (error) {
      console.error('申请退款失败:', error)
      throw error
    }
  }

  // 提交评价
  async function submitReview(orderId: number | string, data: {
    score: number
    content?: string
    images?: string[]
    items?: { itemId: number; score: number; content?: string; images?: string[] }[]
  }) {
    try {
      await orderApi.submitReview(orderId, data)
      await fetchOrderDetail(orderId)
    } catch (error) {
      console.error('提交评价失败:', error)
      throw error
    }
  }

  // 获取订单物流
  async function fetchLogistics(orderId: number | string) {
    try {
      return await orderApi.getOrderLogistics(orderId)
    } catch (error) {
      console.error('获取物流信息失败:', error)
      throw error
    }
  }

  // 获取订单统计
  async function fetchStats() {
    try {
      const data = await orderApi.getOrderStats()
      stats.value = data
      return data
    } catch (error) {
      console.error('获取订单统计失败:', error)
      return null
    }
  }

  // 再次购买
  async function repurchase(orderId: number | string) {
    try {
      await orderApi.repurchase(orderId)
    } catch (error) {
      console.error('再次购买失败:', error)
      throw error
    }
  }

  // 重置
  function reset() {
    orders.value = []
    orderDetail.value = null
    stats.value = null
    loading.value = false
  }

  return {
    // 状态
    orders,
    orderDetail,
    stats,
    loading,
    total,
    // 计算属性
    hasOrders,
    unpaidCount,
    unshippedCount,
    unreceivedCount,
    // 方法
    fetchOrders,
    fetchOrderDetail,
    createOrder,
    cancelOrder,
    confirmReceive,
    deleteOrder,
    payOrder,
    getPayStatus,
    refundOrder,
    submitReview,
    fetchLogistics,
    fetchStats,
    repurchase,
    reset
  }
})

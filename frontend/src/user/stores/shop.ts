/**
 * 店铺状态管理
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Shop, ShopDetail, ShopStats } from '@user/types/shop'
import * as shopApi from '@user/api/shop'

export const useShopStore = defineStore('shop', () => {
  // 状态
  const shops = ref<Shop[]>([])
  const shopDetail = ref<ShopDetail | null>(null)
  const followedShops = ref<Shop[]>([])
  const loading = ref(false)
  const total = ref(0)

  // 计算属性
  const hasShops = computed(() => shops.value.length > 0)
  const shopMap = computed(() => {
    const map = new Map<number, Shop>()
    shops.value.forEach(s => map.set(s.id, s))
    return map
  })

  // 获取店铺列表
  async function fetchShops(params?: any) {
    try {
      loading.value = true
      const result = await shopApi.getShopList(params)
      shops.value = result.records
      total.value = result.total
      return result
    } catch (error) {
      console.error('获取店铺列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取店铺详情
  async function fetchShopDetail(shopId: number | string) {
    try {
      loading.value = true
      const data = await shopApi.getShopDetail(shopId)
      shopDetail.value = data
      return data
    } catch (error) {
      console.error('获取店铺详情失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取店铺统计
  async function fetchShopStats(shopId: number | string) {
    try {
      return await shopApi.getShopStats(shopId)
    } catch (error) {
      console.error('获取店铺统计失败:', error)
      return null
    }
  }

  // 获取店铺商品
  async function fetchShopProducts(shopId: number | string, params?: any) {
    try {
      loading.value = true
      const result = await shopApi.getShopProducts(shopId, params)
      return result
    } catch (error) {
      console.error('获取店铺商品失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取店铺优惠券
  async function fetchShopCoupons(shopId: number | string) {
    try {
      return await shopApi.getShopCoupons(shopId)
    } catch (error) {
      console.error('获取店铺优惠券失败:', error)
      return []
    }
  }

  // 领取优惠券
  async function receiveCoupon(couponId: number | string) {
    try {
      await shopApi.receiveCoupon(couponId)
    } catch (error) {
      console.error('领取优惠券失败:', error)
      throw error
    }
  }

  // 关注店铺
  async function followShop(shopId: number | string) {
    try {
      await shopApi.followShop(shopId)
      // 更新店铺详情中的关注状态
      if (shopDetail.value && shopDetail.value.id === Number(shopId)) {
        shopDetail.value.followers += 1
      }
    } catch (error) {
      console.error('关注店铺失败:', error)
      throw error
    }
  }

  // 取消关注
  async function unfollowShop(shopId: number | string) {
    try {
      await shopApi.unfollowShop(shopId)
      // 更新店铺详情中的关注状态
      if (shopDetail.value && shopDetail.value.id === Number(shopId)) {
        shopDetail.value.followers -= 1
      }
    } catch (error) {
      console.error('取消关注失败:', error)
      throw error
    }
  }

  // 检查是否已关注
  async function checkFollowing(shopId: number | string) {
    try {
      const result = await shopApi.checkFollowing(shopId)
      return result.following
    } catch (error) {
      console.error('检查关注状态失败:', error)
      return false
    }
  }

  // 获取关注店铺列表
  async function fetchFollowedShops(params?: any) {
    try {
      loading.value = true
      const result = await shopApi.getFollowedShops(params)
      followedShops.value = result.records
      return result
    } catch (error) {
      console.error('获取关注店铺失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 搜索店铺
  async function searchShops(keyword: string, params?: any) {
    try {
      loading.value = true
      const result = await shopApi.searchShops(keyword, params)
      shops.value = result.records
      total.value = result.total
      return result
    } catch (error) {
      console.error('搜索店铺失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 联系商家
  async function contactMerchant(shopId: number | string) {
    try {
      const result = await shopApi.contactMerchant(shopId)
      return result
    } catch (error) {
      console.error('联系商家失败:', error)
      throw error
    }
  }

  // 分享店铺
  async function shareShop(shopId: number | string) {
    try {
      return await shopApi.shareShop(shopId)
    } catch (error) {
      console.error('分享店铺失败:', error)
      throw error
    }
  }

  // 重置
  function reset() {
    shops.value = []
    shopDetail.value = null
    followedShops.value = []
    loading.value = false
  }

  return {
    // 状态
    shops,
    shopDetail,
    followedShops,
    loading,
    total,
    // 计算属性
    hasShops,
    shopMap,
    // 方法
    fetchShops,
    fetchShopDetail,
    fetchShopStats,
    fetchShopProducts,
    fetchShopCoupons,
    receiveCoupon,
    followShop,
    unfollowShop,
    checkFollowing,
    fetchFollowedShops,
    searchShops,
    contactMerchant,
    shareShop,
    reset
  }
})

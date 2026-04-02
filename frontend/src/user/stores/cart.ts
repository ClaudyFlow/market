/**
 * 购物车状态管理
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Cart, CartItem } from '@user/types/cart'
import * as cartApi from '@user/api/cart'

export const useCartStore = defineStore('cart', () => {
  // 状态
  const cart = ref<Cart | null>(null)
  const loading = ref(false)

  // 计算属性
  const itemCount = computed(() => cart.value?.totalCount || 0)
  const selectedCount = computed(() => cart.value?.selectedItems.length || 0)
  const totalAmount = computed(() => cart.value?.totalAmount || 0)
  const hasItems = computed(() => (cart.value?.items.length || 0) > 0)
  const hasSelected = computed(() => selectedCount.value > 0)
  const allSelected = computed(() => {
    if (!cart.value || cart.value.items.length === 0) return false
    return cart.value.items.every(item => item.selected)
  })
  const invalidItems = computed(() => cart.value?.items.filter(item => item.invalid) || [])

  // 获取购物车
  async function fetchCart() {
    try {
      loading.value = true
      const data = await cartApi.getCart()
      cart.value = data
      return data
    } catch (error) {
      console.error('获取购物车失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 添加到购物车
  async function addToCart(productId: number | string, skuId?: number | string, quantity: number = 1) {
    try {
      const item = await cartApi.addToCart(productId, skuId, quantity)
      await fetchCart()
      return item
    } catch (error) {
      console.error('添加到购物车失败:', error)
      throw error
    }
  }

  // 批量添加到购物车
  async function batchAddToCart(items: { productId: number; skuId?: number; quantity: number }[]) {
    try {
      loading.value = true
      const promises = items.map(item => cartApi.addToCart(item.productId, item.skuId, item.quantity))
      await Promise.all(promises)
      await fetchCart()
    } catch (error) {
      console.error('批量添加到购物车失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 更新商品数量
  async function updateQuantity(itemId: number | string, quantity: number) {
    try {
      if (quantity <= 0) {
        await removeItem(itemId)
        return
      }
      await cartApi.updateCartItem(itemId, quantity)
      await fetchCart()
    } catch (error) {
      console.error('更新数量失败:', error)
      throw error
    }
  }

  // 增加数量
  async function increaseQuantity(itemId: number | string) {
    const item = cart.value?.items.find(i => i.id === Number(itemId))
    if (item) {
      await updateQuantity(itemId, item.quantity + 1)
    }
  }

  // 减少数量
  async function decreaseQuantity(itemId: number | string) {
    const item = cart.value?.items.find(i => i.id === Number(itemId))
    if (item && item.quantity > 1) {
      await updateQuantity(itemId, item.quantity - 1)
    }
  }

  // 删除商品
  async function removeItem(itemId: number | string) {
    try {
      await cartApi.deleteCartItem(itemId)
      await fetchCart()
    } catch (error) {
      console.error('删除商品失败:', error)
      throw error
    }
  }

  // 批量删除
  async function removeItems(itemIds: number[]) {
    try {
      loading.value = true
      await cartApi.deleteCartItems(itemIds)
      await fetchCart()
    } catch (error) {
      console.error('批量删除失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 清空购物车
  async function clear() {
    try {
      loading.value = true
      await cartApi.clearCart()
      cart.value = null
    } catch (error) {
      console.error('清空购物车失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 选中/取消选中
  async function selectItem(itemId: number | string, selected: boolean) {
    try {
      await cartApi.selectCartItem(itemId, selected)
      await fetchCart()
    } catch (error) {
      console.error('选中商品失败:', error)
      throw error
    }
  }

  // 全选/取消全选
  async function selectAll(selected: boolean) {
    try {
      await cartApi.selectAll(selected)
      await fetchCart()
    } catch (error) {
      console.error('全选失败:', error)
      throw error
    }
  }

  // 获取选中商品
  function getSelectedItems(): CartItem[] {
    return cart.value?.selectedItems || []
  }

  // 获取购物车数量
  async function fetchCartCount() {
    try {
      const result = await cartApi.getCartCount()
      return result.count
    } catch (error) {
      console.error('获取购物车数量失败:', error)
      return 0
    }
  }

  // 检查库存
  async function checkStock() {
    try {
      const result = await cartApi.checkCartStock()
      return result.invalidItems
    } catch (error) {
      console.error('检查库存失败:', error)
      return []
    }
  }

  // 重置购物车
  function reset() {
    cart.value = null
    loading.value = false
  }

  return {
    // 状态
    cart,
    loading,
    // 计算属性
    itemCount,
    selectedCount,
    totalAmount,
    hasItems,
    hasSelected,
    allSelected,
    invalidItems,
    // 方法
    fetchCart,
    addToCart,
    batchAddToCart,
    updateQuantity,
    increaseQuantity,
    decreaseQuantity,
    removeItem,
    removeItems,
    clear,
    selectItem,
    selectAll,
    getSelectedItems,
    fetchCartCount,
    checkStock,
    reset
  }
})

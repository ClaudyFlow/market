/**
 * 购物车本地存储状态管理
 * 当后端API不可用时，使用localStorage存储购物车数据
 */

import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'

export interface LocalCartItem {
  id: number
  name: string
  price: number
  originalPrice?: number
  image: string
  quantity: number
  selected: boolean
  shopId?: number
  shopName?: string
  spec?: string
}

const CART_STORAGE_KEY = 'market_cart_items'

export const useLocalCartStore = defineStore('localCart', () => {
  // 从localStorage加载购物车数据
  const loadCartFromStorage = (): LocalCartItem[] => {
    try {
      const stored = localStorage.getItem(CART_STORAGE_KEY)
      return stored ? JSON.parse(stored) : []
    } catch (error) {
      console.error('加载购物车数据失败:', error)
      return []
    }
  }

  // 保存购物车数据到localStorage
  const saveCartToStorage = (items: LocalCartItem[]) => {
    try {
      localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(items))
    } catch (error) {
      console.error('保存购物车数据失败:', error)
    }
  }

  // 状态
  const cartItems = ref<LocalCartItem[]>(loadCartFromStorage())
  const loading = ref(false)

  // 计算属性
  const itemCount = computed(() => 
    cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
  )
  
  const selectedCount = computed(() => 
    cartItems.value.filter(item => item.selected).length
  )
  
  const totalAmount = computed(() => 
    cartItems.value
      .filter(item => item.selected)
      .reduce((sum, item) => sum + item.price * item.quantity, 0)
  )
  
  const hasItems = computed(() => cartItems.value.length > 0)
  
  const hasSelected = computed(() => 
    cartItems.value.some(item => item.selected)
  )
  
  const allSelected = computed(() => 
    cartItems.value.length > 0 && cartItems.value.every(item => item.selected)
  )
  
  const selectedItems = computed(() => 
    cartItems.value.filter(item => item.selected)
  )

  // 监听购物车变化，自动保存到localStorage
  watch(cartItems, (newItems) => {
    saveCartToStorage(newItems)
  }, { deep: true })

  // 添加到购物车
  function addToCart(item: Omit<LocalCartItem, 'quantity' | 'selected'>) {
    const existingItem = cartItems.value.find(i => i.id === item.id)
    
    if (existingItem) {
      existingItem.quantity += 1
      ElMessage.success(`${item.name} 数量 +1`)
    } else {
      cartItems.value.push({
        ...item,
        quantity: 1,
        selected: true
      })
      ElMessage.success(`${item.name} 已加入购物车`)
    }
  }

  // 更新商品数量
  function updateQuantity(itemId: number, quantity: number) {
    const item = cartItems.value.find(i => i.id === itemId)
    if (item) {
      if (quantity <= 0) {
        removeItem(itemId)
      } else {
        item.quantity = quantity
      }
    }
  }

  // 增加数量
  function increaseQuantity(itemId: number) {
    const item = cartItems.value.find(i => i.id === itemId)
    if (item) {
      item.quantity += 1
    }
  }

  // 减少数量
  function decreaseQuantity(itemId: number) {
    const item = cartItems.value.find(i => i.id === itemId)
    if (item && item.quantity > 1) {
      item.quantity -= 1
    }
  }

  // 删除商品
  function removeItem(itemId: number) {
    const index = cartItems.value.findIndex(i => i.id === itemId)
    if (index > -1) {
      cartItems.value.splice(index, 1)
    }
  }

  // 批量删除
  function removeItems(itemIds: number[]) {
    cartItems.value = cartItems.value.filter(item => !itemIds.includes(item.id))
  }

  // 清空购物车
  function clearCart() {
    cartItems.value = []
  }

  // 选中/取消选中
  function selectItem(itemId: number, selected: boolean) {
    const item = cartItems.value.find(i => i.id === itemId)
    if (item) {
      item.selected = selected
    }
  }

  // 全选/取消全选
  function selectAll(selected: boolean) {
    cartItems.value.forEach(item => {
      item.selected = selected
    })
  }

  // 切换选中状态
  function toggleSelect(itemId: number) {
    const item = cartItems.value.find(i => i.id === itemId)
    if (item) {
      item.selected = !item.selected
    }
  }

  // 获取选中商品
  function getSelectedItems(): LocalCartItem[] {
    return cartItems.value.filter(item => item.selected)
  }

  // 合并购物车（登录后）
  function mergeCart(serverItems: LocalCartItem[]) {
    serverItems.forEach(serverItem => {
      const localItem = cartItems.value.find(i => i.id === serverItem.id)
      if (localItem) {
        // 如果本地已有，取数量较大的
        localItem.quantity = Math.max(localItem.quantity, serverItem.quantity)
        localItem.selected = serverItem.selected
      } else {
        cartItems.value.push(serverItem)
      }
    })
  }

  // 同步到服务器（登录后调用）
  async function syncToServer() {
    // TODO: 调用API将本地购物车同步到服务器
    console.log('同步购物车到服务器:', cartItems.value)
  }

  return {
    // 状态
    cartItems,
    loading,
    // 计算属性
    itemCount,
    selectedCount,
    totalAmount,
    hasItems,
    hasSelected,
    allSelected,
    selectedItems,
    // 方法
    addToCart,
    updateQuantity,
    increaseQuantity,
    decreaseQuantity,
    removeItem,
    removeItems,
    clearCart,
    selectItem,
    selectAll,
    toggleSelect,
    getSelectedItems,
    mergeCart,
    syncToServer
  }
})
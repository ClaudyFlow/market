import { defineStore } from 'pinia'
import { ref, computed, type Ref } from 'vue'

export interface CartItem {
  id: number
  name: string
  price: number
  quantity: number
  image?: string
  [key: string]: unknown
}

export const useCartStore = defineStore('cart', () => {
  const cartItems: Ref<CartItem[]> = ref([])

  const totalCount = computed(() => {
    return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  const totalPrice = computed(() => {
    return cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  })

  function addToCart(product: CartItem) {
    const existingItem = cartItems.value.find(item => item.id === product.id)
    if (existingItem) {
      existingItem.quantity++
    } else {
      cartItems.value.push({
        ...product,
        quantity: 1
      })
    }
  }

  function removeFromCart(productId: number) {
    cartItems.value = cartItems.value.filter(item => item.id !== productId)
  }

  function updateQuantity(productId: number, quantity: number) {
    const item = cartItems.value.find(item => item.id === productId)
    if (item) {
      item.quantity = Math.max(1, quantity)
    }
  }

  function clearCart() {
    cartItems.value = []
  }

  return {
    cartItems,
    totalCount,
    totalPrice,
    addToCart,
    removeFromCart,
    updateQuantity,
    clearCart
  }
})

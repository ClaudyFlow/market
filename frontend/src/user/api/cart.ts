/**
 * 购物车相关 API
 */

import { get, post, put, del } from './request'
import type { Cart, CartItem } from '@user/types/cart'
import type { PageParams } from './request'

const BASE_URL = '/cart'

/**
 * 获取购物车
 */
export function getCart(): Promise<Cart> {
  return get(BASE_URL)
}

/**
 * 添加商品到购物车
 */
export function addToCart(productId: number | string, skuId?: number | string, quantity?: number): Promise<CartItem> {
  return post(BASE_URL, { productId, skuId, quantity })
}

/**
 * 更新购物车商品数量
 */
export function updateCartItem(itemId: number | string, quantity: number): Promise<CartItem> {
  return put(`${BASE_URL}/${itemId}`, { quantity })
}

/**
 * 删除购物车商品
 */
export function deleteCartItem(itemId: number | string): Promise<void> {
  return del(`${BASE_URL}/${itemId}`)
}

/**
 * 批量删除购物车商品
 */
export function deleteCartItems(itemIds: number[]): Promise<void> {
  return del(`${BASE_URL}/batch`, { ids: itemIds })
}

/**
 * 清空购物车
 */
export function clearCart(): Promise<void> {
  return del(`${BASE_URL}/clear`)
}

/**
 * 选中/取消选中购物车商品
 */
export function selectCartItem(itemId: number | string, selected: boolean): Promise<void> {
  return put(`${BASE_URL}/${itemId}/select`, { selected })
}

/**
 * 全选/取消全选
 */
export function selectAll(selected: boolean): Promise<void> {
  return put(`${BASE_URL}/select-all`, { selected })
}

/**
 * 获取选中商品列表
 */
export function getSelectedItems(): Promise<CartItem[]> {
  return get(`${BASE_URL}/selected`)
}

/**
 * 获取购物车商品总数
 */
export function getCartCount(): Promise<{ count: number }> {
  return get(`${BASE_URL}/count`)
}

/**
 * 合并购物车（登录后）
 */
export function mergeCart(tempCartId: string): Promise<void> {
  return post(`${BASE_URL}/merge`, { tempCartId })
}

/**
 * 批量更新购物车
 */
export function batchUpdateCart(items: { id: number | string; quantity: number }[]): Promise<CartItem[]> {
  return put(`${BASE_URL}/batch`, items)
}

/**
 * 检查购物车商品库存
 */
export function checkCartStock(): Promise<{ invalidItems: { itemId: number; stock: number }[] }> {
  return get(`${BASE_URL}/check-stock`)
}

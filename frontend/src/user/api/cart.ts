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
export function addToCart(productId: number | string, skuId?: number | string, quantity?: number, selectedColor?: string, selectedVersion?: string): Promise<CartItem> {
  return post(`${BASE_URL}/add`, { productId, skuId, quantity, selectedColor, selectedVersion })
}

/**
 * 更新购物车商品数量
 */
export function updateCartItem(itemId: number | string, quantity: number): Promise<CartItem> {
  return put(`${BASE_URL}/update/${itemId}`, { quantity })
}

/**
 * 删除购物车商品
 */
export function deleteCartItem(itemId: number | string): Promise<void> {
  return del(`${BASE_URL}/remove/${itemId}`)
}

/**
 * 批量删除购物车商品（后端无批量接口，使用多次删除）
 * @deprecated 后端不支持，请使用 deleteCartItem 多次调用
 */
export function deleteCartItems(itemIds: number[]): Promise<void> {
  console.warn('deleteCartItems 已废弃，后端无 /batch 接口，请使用 deleteCartItem 多次调用')
  return Promise.resolve()
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
  return put(`${BASE_URL}/select/${itemId}`, { selected })
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
  return get(`${BASE_URL}/total`)
}

/**
 * 合并购物车（登录后）
 */
export function mergeCart(tempCartId: string): Promise<void> {
  return post(`${BASE_URL}/merge`, { tempCartId })
}

/**
 * 批量更新购物车（后端无 /batch 接口，使用多次更新）
 * @deprecated 后端不支持，请使用 updateCartItem 多次调用
 */
export function batchUpdateCart(items: { id: number | string; quantity: number }[]): Promise<CartItem[]> {
  console.warn('batchUpdateCart 已废弃，后端无 /batch 接口，请使用 updateCartItem 多次调用')
  return Promise.resolve([])
}

/**
 * 检查购物车商品库存
 */
export function checkCartStock(): Promise<{ invalidItems: { itemId: number; stock: number }[] }> {
  return get(`${BASE_URL}/check-stock`)
}

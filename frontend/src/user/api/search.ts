/**
 * 搜索相关 API
 */

import { get, post } from './request'
import type { SearchResult, SearchHistory, SearchSuggestion } from '@user/types/search'
import type { PageData, PageParams } from './request'

const BASE_URL = '/search'

/**
 * 综合搜索
 */
export function search(keyword: string, params?: PageParams & { type?: 'all' | 'product' | 'shop' | 'article' }): Promise<PageData<SearchResult>> {
  return get(BASE_URL, { keyword, ...params })
}

/**
 * 搜索商品
 */
export function searchProducts(keyword: string, params?: PageParams): Promise<PageData<SearchResult>> {
  return get(`${BASE_URL}/product`, { keyword, ...params })
}

/**
 * 搜索店铺
 */
export function searchShops(keyword: string, params?: PageParams): Promise<PageData<SearchResult>> {
  return get(`${BASE_URL}/shop`, { keyword, ...params })
}

/**
 * 搜索建议/自动补全
 */
export function getSuggestions(keyword: string, limit?: number): Promise<SearchSuggestion[]> {
  return get(`${BASE_URL}/suggestions`, { keyword, limit })
}

/**
 * 热门搜索
 */
export function getHotSearches(limit?: number): Promise<{ keyword: string; count: number }[]> {
  return get(`${BASE_URL}/hot`, { limit })
}

/**
 * 搜索历史
 */
export function getSearchHistory(): Promise<SearchHistory[]> {
  return get(`${BASE_URL}/history`)
}

/**
 * 添加搜索历史
 */
export function addSearchHistory(keyword: string): Promise<void> {
  return post(`${BASE_URL}/history`, { keyword })
}

/**
 * 清除搜索历史
 */
export function clearSearchHistory(): Promise<void> {
  return post(`${BASE_URL}/history/clear`)
}

/**
 * 删除单条搜索历史
 */
export function deleteSearchHistory(id: number): Promise<void> {
  return post(`${BASE_URL}/history/delete`, { id })
}

/**
 * 搜索联想
 */
export function getRelatedSearches(keyword: string, limit?: number): Promise<string[]> {
  return get(`${BASE_URL}/related`, { keyword, limit })
}

/**
 * 搜索筛选条件
 */
export function getSearchFilters(keyword: string, type?: string): Promise<{
  categories: { id: number; name: string; count: number }[]
  brands: { id: number; name: string; count: number }[]
  priceRanges: { min: number; max: number; label: string; count: number }[]
  attributes: { id: number; name: string; values: { id: number; value: string; count: number }[] }[]
}> {
  return get(`${BASE_URL}/filters`, { keyword, type })
}

/**
 * 商品状态管理
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Product, ProductDetail, ProductCategory, ProductBrand } from '@user/types/product'
import * as productApi from '@user/api/product'

export const useProductStore = defineStore('product', () => {
  // 状态
  const products = ref<Product[]>([])
  const productDetail = ref<ProductDetail | null>(null)
  const categories = ref<ProductCategory[]>([])
  const brands = ref<ProductBrand[]>([])
  const loading = ref(false)
  const total = ref(0)

  // 计算属性
  const hasProducts = computed(() => products.value.length > 0)
  const productMap = computed(() => {
    const map = new Map<number, Product>()
    products.value.forEach(p => map.set(p.id, p))
    return map
  })

  // 获取商品列表
  async function fetchProducts(params?: any) {
    try {
      loading.value = true
      const result = await productApi.getProductList(params)
      products.value = result.records
      total.value = result.total
      return result
    } catch (error) {
      console.error('获取商品列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取商品详情
  async function fetchProductDetail(productId: number | string) {
    try {
      loading.value = true
      const data = await productApi.getProductDetail(productId)
      productDetail.value = data
      return data
    } catch (error) {
      console.error('获取商品详情失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取推荐商品
  async function fetchRecommended(limit: number = 10) {
    try {
      const data = await productApi.getRecommendedProducts(limit)
      return data
    } catch (error) {
      console.error('获取推荐商品失败:', error)
      return []
    }
  }

  // 获取热销商品
  async function fetchHotProducts(limit: number = 10) {
    try {
      const data = await productApi.getHotProducts(limit)
      return data
    } catch (error) {
      console.error('获取热销商品失败:', error)
      return []
    }
  }

  // 获取新品
  async function fetchNewProducts(limit: number = 10) {
    try {
      const data = await productApi.getNewProducts(limit)
      return data
    } catch (error) {
      console.error('获取新品失败:', error)
      return []
    }
  }

  // 获取促销商品
  async function fetchSaleProducts(limit: number = 10) {
    try {
      const data = await productApi.getSaleProducts(limit)
      return data
    } catch (error) {
      console.error('获取促销商品失败:', error)
      return []
    }
  }

  // 搜索商品
  async function searchProducts(keyword: string, params?: any) {
    try {
      loading.value = true
      const result = await productApi.searchProducts(keyword, params)
      products.value = result.records
      total.value = result.total
      return result
    } catch (error) {
      console.error('搜索商品失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取分类
  async function fetchCategories() {
    try {
      const data = await productApi.getCategories()
      categories.value = data
      return data
    } catch (error) {
      console.error('获取分类失败:', error)
      return []
    }
  }

  // 获取品牌
  async function fetchBrands(categoryId?: number) {
    try {
      const data = await productApi.getBrands(categoryId)
      brands.value = data
      return data
    } catch (error) {
      console.error('获取品牌失败:', error)
      return []
    }
  }

  // 收藏商品
  async function toggleFavorite(productId: number | string) {
    try {
      const result = await productApi.checkFavorite(productId)
      if (result.favorite) {
        await productApi.unfavoriteProduct(productId)
        return false
      } else {
        await productApi.favoriteProduct(productId)
        return true
      }
    } catch (error) {
      console.error('切换收藏失败:', error)
      throw error
    }
  }

  // 添加浏览记录
  async function addBrowseHistory(productId: number | string) {
    try {
      await productApi.addBrowseHistory(productId)
    } catch (error) {
      console.error('添加浏览记录失败:', error)
    }
  }

  // 批量获取商品
  async function fetchProductsBatch(ids: number[]) {
    try {
      const data = await productApi.getProductsBatch(ids)
      return data
    } catch (error) {
      console.error('批量获取商品失败:', error)
      return []
    }
  }

  // 重置
  function reset() {
    products.value = []
    productDetail.value = null
    loading.value = false
  }

  return {
    // 状态
    products,
    productDetail,
    categories,
    brands,
    loading,
    total,
    // 计算属性
    hasProducts,
    productMap,
    // 方法
    fetchProducts,
    fetchProductDetail,
    fetchRecommended,
    fetchHotProducts,
    fetchNewProducts,
    fetchSaleProducts,
    searchProducts,
    fetchCategories,
    fetchBrands,
    toggleFavorite,
    addBrowseHistory,
    fetchProductsBatch,
    reset
  }
})

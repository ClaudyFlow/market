/**
 * 店铺商品列表管理
 */

import { ref } from 'vue'
import type { Product, Category, SortOption } from '@user/types/shop'

export function useShopProducts() {
  const categories = ref<Category[]>([
    { id: 0, name: '全部', icon: 'fas fa-th', count: 368 },
    { id: 1, name: '手机数码', icon: 'fas fa-mobile-alt', count: 120 },
    { id: 2, name: '电脑办公', icon: 'fas fa-laptop', count: 85 },
    { id: 3, name: '智能穿戴', icon: 'fas fa-watch', count: 63 },
    { id: 4, name: '配件耗材', icon: 'fas fa-usb', count: 100 }
  ])

  const selectedCategory = ref(0)

  const sortOptions = ref<SortOption[]>([
    { label: '综合', value: 'default' },
    { label: '销量', value: 'sales' },
    { label: '价格', value: 'price' },
    { label: '新品', value: 'new' }
  ])

  const selectedSort = ref('default')

  const products = ref<Product[]>([
    {
      id: 1,
      name: '智能手机 Pro Max 256GB',
      description: '旗舰芯片，超清摄像',
      price: 5999,
      originalPrice: 6999,
      sales: 2580,
      stock: 100,
      image: '/images/product-1.jpg'
    },
    {
      id: 2,
      name: '轻薄笔记本电脑 14 英寸',
      description: '高性能处理器，长续航',
      price: 4599,
      originalPrice: 5299,
      sales: 1860,
      stock: 50,
      image: '/images/product-2.jpg'
    },
    {
      id: 3,
      name: '智能手表运动版',
      description: '心率监测，GPS 定位',
      price: 1299,
      originalPrice: 1599,
      sales: 3200,
      stock: 200,
      image: '/images/product-3.jpg'
    },
    {
      id: 4,
      name: '无线蓝牙耳机',
      description: '主动降噪，长续航',
      price: 599,
      originalPrice: 799,
      sales: 5600,
      stock: 500,
      image: '/images/product-4.jpg'
    }
  ])

  const selectCategory = (categoryId: number) => {
    selectedCategory.value = categoryId
  }

  return {
    categories,
    selectedCategory,
    sortOptions,
    selectedSort,
    products,
    selectCategory
  }
}

/**
 * 商品详情数据管理
 */
import { ref, computed } from 'vue'
import type { Product } from '@user/types/product'
import * as productApi from '@user/api/product'

export function useProductDetail(productId: string) {
  const MOCK_PRODUCTS: Record<string, Product> = {
    '1': {
      id: '1',
      name: 'Aurora X1 无线降噪耳机',
      description: '沉浸式空间音频 · 蓝牙 5.4 · 48 小时续航',
      price: 899,
      originalPrice: 1299,
      discount: 69,
      stock: 156,
      category: '数码电器',
      categoryId: 1,
      brand: 'Cappuccino Lab',
      colors: ['曜石黑', '珍珠白', '天空蓝'],
      versions: ['标准版', '旗舰版', '尊享版'],
      promotions: ['限时直降 ¥400', '买即赠耳机架', '顺丰包邮'],
      activities: [
        { id: 1, name: '618大促', discountRate: 0.85, endTime: '2026-06-30' }
      ],
      specifications: {
        '蓝牙版本': '5.4',
        '续航时间': '48 小时',
        '降噪类型': '主动降噪',
        '驱动单元': '40mm',
        '重量': '250g',
        '充电接口': 'USB-C'
      },
      images: [
        'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=800&q=80',
        'https://images.unsplash.com/photo-1583394838336-acd977736f90?auto=format&fit=crop&w=800&q=80',
        'https://images.unsplash.com/photo-1484704849700-f032a568e944?auto=format&fit=crop&w=800&q=80',
        'https://images.unsplash.com/photo-1524678606370-a47ad25cb82a?auto=format&fit=crop&w=800&q=80'
      ],
      detailImages: [
        'https://images.unsplash.com/photo-1546435770-a3e426bf472b?auto=format&fit=crop&w=1200&q=80',
        'https://images.unsplash.com/photo-1520114878144-6123749968dd?auto=format&fit=crop&w=1200&q=80'
      ],
      detailText: `<h3>产品亮点</h3><p>Aurora X1 采用全新 40mm 驱动单元...</p>`,
      services: ['官方正品', '7天无理由', '一年质保', '顺丰速运'],
      sales: 2458,
      rating: 4.8,
      reviewCount: 128
    },
    '2': {
      id: '2',
      name: 'ProBook 15.6寸轻薄笔记本',
      description: '高性能轻薄本 · RTX 4060 · 144Hz高刷',
      price: 6999,
      originalPrice: 8999,
      discount: 78,
      stock: 45,
      category: '数码电器',
      categoryId: 1,
      brand: 'TechLand',
      colors: ['深空灰', '星河银'],
      versions: ['i5版', 'i7版', 'i9版'],
      promotions: ['学生优惠', '分期免息'],
      activities: [],
      specifications: {
        '屏幕': '15.6寸 144Hz',
        '显卡': 'RTX 4060',
        '内存': '16GB',
        '存储': '512GB SSD'
      },
      images: [
        'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=800&q=80'
      ],
      detailImages: [],
      detailText: '',
      services: ['官方正品', '2年质保'],
      sales: 1234,
      rating: 4.6,
      reviewCount: 89
    }
  }

  const product = ref<Product>(MOCK_PRODUCTS[productId] || MOCK_PRODUCTS['1'])

  // 当前显示图片
  const currentImageIndex = ref(0)
  const currentImage = computed(() => product.value.images[currentImageIndex.value])
  const productImages = computed(() => product.value.images)

  // 格式化价格
  const formatPrice = (price: number): string => {
    return `¥${price.toFixed(2)}`
  }

  // 格式化日期
  const formatDate = (dateStr: string): string => {
    const date = new Date(dateStr)
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  }

  // 格式化规格标签
  const formatSpecLabel = (key: string): string => {
    const labelMap: Record<string, string> = {
      color: '颜色',
      version: '版本',
      storage: '存储',
      size: '尺寸'
    }
    return labelMap[key] || key
  }

  const loadProduct = async () => {
    try {
      const res = await productApi.getProductDetail(productId)
      if (res && res.data) {
        product.value = res.data
      }
    } catch (error) {
      console.error('加载商品数据失败:', error)
    }
  }

  return {
    product,
    currentImage,
    currentImageIndex,
    productImages,
    formatPrice,
    formatDate,
    formatSpecLabel,
    loadProduct
  }
}

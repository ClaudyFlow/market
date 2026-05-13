/**
 * 商品详情数据管理
 */
import { ref, computed } from 'vue'
import type { Product } from '@user/types/product'

export function useProductDetail(productId: string) {
  // 商品数据（模拟数据，后续替换为 API）
  const product = ref<Product>({
    id: productId,
    name: 'Aurora X1 无线降噪耳机',
    description: '沉浸式空间音频 · 蓝牙 5.4 · 48 小时续航',
    price: 899,
    originalPrice: 1299,
    discount: 69,
    stock: 156,
    category: '数码电器',
    brand: 'Cappuccino Lab',
    colors: ['曜石黑', '珍珠白', '天空蓝'],
    versions: ['标准版', '旗舰版', '尊享版'],
    promotions: ['限时直降 ¥400', '买即赠耳机架', '顺丰包邮'],
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
  })

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

  // 加载商品数据（TODO: 替换为 API 调用）
  const loadProduct = async () => {
    try {
      // const data = await productApi.getProductDetail(productId)
      // product.value = data
      console.log('加载商品数据:', productId)
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

/**
 * 折扣计算工具
 */

export interface Product {
  id: number
  name: string
  price: number
  originalPrice: number
  isOnSale?: boolean
  discountRate?: number | null
  [key: string]: unknown
}

/**
 * 计算折扣率
 * @param price - 当前售价
 * @param originalPrice - 原价
 * @returns 折扣率（如 "8.5" 表示 8.5 折）,不打折返回 null
 */
export function 计算折扣 (price: number | undefined | null, originalPrice: number | undefined | null): string | null {
  if (!price || !originalPrice || price <= 0 || originalPrice <= 0) {
    return null
  }

  const 折扣率 = ((price / originalPrice) * 10).toFixed(1)
  return parseFloat(折扣率) >= 10 ? null : 折扣率
}

/**
 * 判断商品是否在促销
 */
export function 是否促销 (price: number | undefined | null, originalPrice: number | undefined | null): boolean {
  return 计算折扣 (price, originalPrice) !== null
}

/**
 * 批量应用折扣
 */
export function 应用批量折扣 (products: Product[], discountRate: number): Product[] {
  if (discountRate < 0 || discountRate > 1) {
    return products
  }

  return products.map(product => ({
    ...product,
    price: Math.round(product.originalPrice * discountRate),
    isOnSale: true,
    discountRate
  }))
}

/**
 * 取消促销，恢复原价
 */
export function 取消促销 (products: Product[]): Product[] {
  return products.map(product => ({
    ...product,
    price: product.originalPrice,
    isOnSale: false,
    discountRate: null
  }))
}

/**
 * 格式化价格显示
 */
export function 格式化价格 (price: number): string {
  if (price >= 100000000) {
    return (price / 100000000).toFixed(2) + '亿'
  }
  if (price >= 10000) {
    return (price / 10000).toFixed(2) + '万'
  }
  return price.toString()
}

/**
 * 根据售罄百分比获取进度条颜色
 */
export function 获取进度颜色 (percent: number): string {
  if (percent === 0) return '#999999'
  if (percent >= 80) return '#00ff88'
  if (percent >= 60) return '#00d4ff'
  if (percent >= 40) return '#ffdd00'
  if (percent >= 20) return '#ff8800'
  return '#ff3366'
}

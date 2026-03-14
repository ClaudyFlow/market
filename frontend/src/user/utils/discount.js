/**
 * 折扣计算工具
 * 
 * 使用方式：
 * 1. 商家设置折扣率（如 0.8 表示 8 折）
 * 2. 系统自动计算折后价（四舍五入取整）
 * 3. 前端自动计算并显示折扣标签
 */

/**
 * 计算折扣率
 * @param {number} price - 当前售价
 * @param {number} originalPrice - 原价
 * @returns {string|null} 折扣率（如 "8.5" 表示 8.5 折），不打折返回 null
 */
export function calculateDiscount(price, originalPrice) {
  if (!price || !originalPrice || price <= 0 || originalPrice <= 0) {
    return null
  }
  
  // 计算折扣率：售价 / 原价 * 10
  const rate = (price / originalPrice * 10).toFixed(1)
  
  // 如果折扣大于等于 10 折，说明没打折
  return rate >= 10 ? null : rate
}

/**
 * 判断商品是否在促销
 * @param {number} price - 当前售价
 * @param {number} originalPrice - 原价
 * @returns {boolean}
 */
export function isOnSale(price, originalPrice) {
  return calculateDiscount(price, originalPrice) !== null
}

/**
 * 批量应用折扣
 * @param {Array} products - 商品数组
 * @param {number} discountRate - 折扣率（0-1 之间，如 0.8 表示 8 折）
 * @returns {Array} 打折后的商品数组
 */
export function applyBatchDiscount(products, discountRate) {
  if (discountRate < 0 || discountRate > 1) {
    console.error('折扣率必须在 0-1 之间')
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
 * @param {Array} products - 商品数组
 * @returns {Array} 恢复原价后的商品数组
 */
export function cancelSale(products) {
  return products.map(product => ({
    ...product,
    price: product.originalPrice,
    isOnSale: false,
    discountRate: null
  }))
}

/**
 * 格式化价格显示
 * @param {number} price - 价格
 * @returns {string} 格式化后的价格字符串
 */
export function formatPrice(price) {
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
 * @param {number} percent - 售罄百分比 (0-100)
 * @returns {string} 颜色值
 */
export function getProgressColor(percent) {
  if (percent === 0) return '#999999'
  if (percent >= 80) return '#00ff88'
  if (percent >= 60) return '#00d4ff'
  if (percent >= 40) return '#ffdd00'
  if (percent >= 20) return '#ff8800'
  return '#ff3366'
}

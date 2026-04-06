/**
 * 店铺信息数据管理
 */

import { ref } from 'vue'
import type { ShopInfo } from '@user/types/shop'

export function useShopInfo() {
  const shop = ref<ShopInfo>({
    id: 1,
    name: '科技数码旗舰店',
    banner: '/images/shop-banner.jpg',
    logo: '/images/shop-logo.jpg',
    rating: 4.8,
    followers: 12580,
    productCount: 368,
    positiveRate: 98.5,
    openYears: 3,
    certified: true,
    tags: ['品牌旗舰', '正品保障', '极速发货'],
    announcement: '本店所有商品均为正品，支持 7 天无理由退换货。新品上架，欢迎选购！',
    coupons: [
      {
        id: 1,
        name: '新人专享券',
        amount: 50,
        condition: 500,
        description: '新人专享优惠券',
        validUntil: '2026-12-31',
        received: false
      },
      {
        id: 2,
        name: '满减优惠券',
        amount: 100,
        condition: 1000,
        description: '全店通用',
        validUntil: '2026-06-30',
        received: false
      }
    ]
  })

  return {
    shop
  }
}

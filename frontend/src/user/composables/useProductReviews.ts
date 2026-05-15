/**
 * 商品评价数据管理
 */
import { ref } from 'vue'
import type { Review, ReviewTag } from '@user/types/product'

export function useProductReviews(productId: string) {
  // 评价统计
  const reviewCount = ref(128)
  const averageRating = ref(4.8)
  const reviewTags = ref<ReviewTag[]>([
    { label: '音质出色', count: 86 },
    { label: '降噪很好', count: 64 },
    { label: '佩戴舒适', count: 52 },
    { label: '续航持久', count: 48 },
    { label: '做工精细', count: 36 },
    { label: '性价比高', count: 28 }
  ])

  // 评价列表（模拟数据）
  const reviews = ref<Review[]>([
    {
      id: '1',
      user: { name: '张**', avatar: '' },
      rating: 5,
      content: '音质真的很好，降噪效果超出预期！通勤路上戴着非常舒服。',
      images: [
        'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=200&q=80'
      ],
      specs: '颜色：曜石黑 | 版本：旗舰版',
      createdAt: '2026-04-03'
    },
    {
      id: '2',
      user: { name: '李**', avatar: '' },
      rating: 4,
      content: '整体不错，就是头梁稍微有点紧，戴久了有点压头。',
      images: [],
      specs: '颜色：珍珠白 | 版本：标准版',
      createdAt: '2026-04-02'
    },
    {
      id: '3',
      user: { name: '王**', avatar: '' },
      rating: 5,
      content: '包装很精美，音质和描述一致，续航确实能达到 40+ 小时。',
      images: [
        'https://images.unsplash.com/photo-1583394838336-acd977736f90?auto=format&fit=crop&w=200&q=80',
        'https://images.unsplash.com/photo-1484704849700-f032a568e944?auto=format&fit=crop&w=200&q=80'
      ],
      specs: '颜色：天空蓝 | 版本：尊享版',
      createdAt: '2026-04-01'
    }
  ])

  // 评价弹窗
  const showReviewDialog = ref(false)

  // 是否已购买（用于控制评价权限）
  const isPurchased = ref(true)

  // 打开评价弹窗
  const openReviewDialog = () => {
    if (!isPurchased.value) {
      console.warn('未购买商品无法评价')
      return
    }
    showReviewDialog.value = true
  }

  // 关闭评价弹窗
  const closeReviewDialog = () => {
    showReviewDialog.value = false
  }

  // 评价提交成功回调
  const handleReviewSuccess = () => {
    showReviewDialog.value = false
    // TODO: 刷新评价列表
    console.log('评价提交成功')
  }

  return {
    reviewCount,
    averageRating,
    reviewTags,
    reviews,
    showReviewDialog,
    isPurchased,
    openReviewDialog,
    closeReviewDialog,
    handleReviewSuccess
  }
}

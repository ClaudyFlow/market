/**
 * 商品操作（加购/收藏/购买/分享）
 */
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { Product, SelectedSpecs } from '@user/types/product'

export function useProductActions(
  product: ReturnType<typeof useProductDetail>['product'],
  selectedSpecs: SelectedSpecs,
  quantity: ReturnType<typeof useProductSpecs>['quantity']
) {
  const router = useRouter()

  // 收藏状态
  const isFavorited = ref(false)
  const favoriteCount = ref(236)

  // 购物车数量（用于悬浮栏显示）
  const cartCount = ref(0)

  // 加入购物车
  const addToCart = () => {
    if (!selectedSpecs.color || !selectedSpecs.version) {
      ElMessage.warning('请选择商品规格')
      return
    }

    // TODO: 调用购物车 API
    // cartStore.addToCart({
    //   productId: product.value.id,
    //   specs: { ...selectedSpecs },
    //   quantity: quantity.value
    // })

    ElMessage.success('已加入购物车')
    cartCount.value += quantity.value
  }

  // 立即购买
  const buyNow = () => {
    if (!selectedSpecs.color || !selectedSpecs.version) {
      ElMessage.warning('请选择商品规格')
      return
    }

    // 跳转到订单确认页，携带商品信息
    router.push({
      path: '/order',
      query: {
        productId: product.value.id,
        quantity: quantity.value.toString()
      }
    })
  }

  // 切换收藏
  const toggleFavorite = () => {
    // TODO: 调用收藏 API
    isFavorited.value = !isFavorited.value
    favoriteCount.value += isFavorited.value ? 1 : -1

    ElMessage.success(isFavorited.value ? '已收藏' : '已取消收藏')
  }

  // 分享商品
  const shareProduct = async () => {
    const shareUrl = `${window.location.origin}/item/${product.value.id}`

    if (navigator.clipboard) {
      try {
        await navigator.clipboard.writeText(shareUrl)
        ElMessage.success('链接已复制，快去分享吧！')
      } catch {
        ElMessage.error('复制失败，请手动复制')
      }
    }

    // 调用分享API获取积分奖励（静默失败）
    try {
      const res = await fetch('/api/product/' + product.value.id + '/share', {
        method: 'POST',
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
      })
      if (res.ok) {
        const data = await res.json()
        if (data.data?.creditReward) {
          setTimeout(() => {
            ElMessage.success('分享成功！+' + data.data.creditReward + '积分')
          }, 500)
        }
      }
    } catch {
      // 静默失败，不影响用户体验
    }
  }

  // 滚动到顶部
  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  return {
    isFavorited,
    favoriteCount,
    cartCount,
    addToCart,
    buyNow,
    toggleFavorite,
    shareProduct,
    scrollToTop
  }
}

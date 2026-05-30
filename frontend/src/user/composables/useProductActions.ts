/**
 * 商品操作（加购/收藏/购买/分享）
 */
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { Product, SelectedSpecs } from '@user/types/product'
import { useCartStore } from '@user/stores/cart'

export function useProductActions(
  product: ReturnType<typeof useProductDetail>['product'],
  selectedSpecs: SelectedSpecs,
  quantity: ReturnType<typeof useProductSpecs>['quantity']
) {
  const router = useRouter()
  const cartStore = useCartStore()

  // 收藏状态
  const isFavorited = ref(false)
  const favoriteCount = ref(236)

  // 购物车数量（用于悬浮栏显示）
  const cartCount = ref(0)

  // 加入购物车
  const addToCart = async () => {
    if (product.value.colors?.length && !selectedSpecs.color) {
      ElMessage.warning('请选择商品颜色')
      return
    }
    if (product.value.versions?.length && !selectedSpecs.version) {
      ElMessage.warning('请选择商品版本')
      return
    }

    try {
      await cartStore.addToCart({
        id: product.value.id,
        quantity: quantity.value,
        selectedColor: selectedSpecs.color,
        selectedVersion: selectedSpecs.version
      })
      ElMessage.success('已加入购物车')
      cartCount.value += quantity.value
    } catch (error) {
      ElMessage.error('添加失败，请重试')
    }
  }

  // 立即购买
  const buyNow = () => {
    if (product.value.colors?.length && !selectedSpecs.color) {
      ElMessage.warning('请选择商品颜色')
      return
    }
    if (product.value.versions?.length && !selectedSpecs.version) {
      ElMessage.warning('请选择商品版本')
      return
    }

    // 跳转到订单确认页，携带商品信息
    router.push({
      path: '/order',
      query: {
        productId: product.value.id,
        quantity: quantity.value.toString(),
        selectedColor: selectedSpecs.color,
        selectedVersion: selectedSpecs.version
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

/**
 * 商品规格选择逻辑
 */
import { ref, reactive, computed } from 'vue'
import type { Product, SelectedSpecs } from '@user/types/product'

export function useProductSpecs(product: ReturnType<typeof useProductDetail>['product']) {
  // 选中的规格
  const selectedSpecs = reactive<SelectedSpecs>({
    color: '',
    version: ''
  })

  // 购买数量
  const quantity = ref(1)

  // 是否有选中规格
  const hasSelectedSpecs = computed(() => {
    const hasColors = product.value?.colors?.length
    const hasVersions = product.value?.versions?.length
    if (hasColors && hasVersions) {
      return selectedSpecs.color !== '' && selectedSpecs.version !== ''
    }
    if (hasColors) {
      return selectedSpecs.color !== ''
    }
    if (hasVersions) {
      return selectedSpecs.version !== ''
    }
    return true
  })

  // 获取选中规格的描述文本
  const selectedSpecsText = computed(() => {
    const specs: string[] = []
    if (selectedSpecs.color) specs.push(`颜色：${selectedSpecs.color}`)
    if (selectedSpecs.version) specs.push(`版本：${selectedSpecs.version}`)
    return specs.join(' | ') || '请选择规格'
  })

  // 选择颜色
  const selectColor = (color: string) => {
    selectedSpecs.color = color
  }

  // 选择版本
  const selectVersion = (version: string) => {
    selectedSpecs.version = version
  }

  // 增加数量
  const increaseQuantity = () => {
    if (quantity.value < product.value.stock) {
      quantity.value++
    }
  }

  // 减少数量
  const decreaseQuantity = () => {
    if (quantity.value > 1) {
      quantity.value--
    }
  }

  // 重置规格选择
  const resetSpecs = () => {
    selectedSpecs.color = ''
    selectedSpecs.version = ''
    quantity.value = 1
  }

  return {
    selectedSpecs,
    quantity,
    hasSelectedSpecs,
    selectedSpecsText,
    selectColor,
    selectVersion,
    increaseQuantity,
    decreaseQuantity,
    resetSpecs
  }
}

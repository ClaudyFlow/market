/**
 * 图片缩放功能
 */
import { ref } from 'vue'

export function useImageZoom() {
  const zoomArea = ref<HTMLElement | null>(null)
  const zoomImg = ref<HTMLImageElement | null>(null)

  // 处理鼠标移动时的缩放效果
  const handleZoom = (e: MouseEvent) => {
    if (!zoomArea.value || !zoomImg.value) return

    const rect = zoomArea.value.getBoundingClientRect()
    const x = ((e.clientX - rect.left) / rect.width) * 100
    const y = ((e.clientY - rect.top) / rect.height) * 100

    zoomImg.value.style.transformOrigin = `${x}% ${y}%`
    zoomImg.value.style.transform = 'scale(2)'
  }

  // 隐藏缩放效果
  const hideZoom = () => {
    if (!zoomImg.value) return
    zoomImg.value.style.transform = 'scale(1)'
  }

  return {
    zoomArea,
    zoomImg,
    handleZoom,
    hideZoom
  }
}

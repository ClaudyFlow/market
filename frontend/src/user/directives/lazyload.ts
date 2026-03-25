// 图片懒加载指令
export const lazyload = {
  mounted(el: HTMLImageElement, binding: any) {
    const src = binding.value
    
    // 创建 Intersection Observer
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          // 图片进入视口，加载图片
          el.src = src
          // 加载完成后停止观察
          if (el.complete) {
            observer.unobserve(el)
          } else {
            el.onload = () => observer.unobserve(el)
            el.onerror = () => observer.unobserve(el)
          }
        }
      })
    }, {
      rootMargin: '50px 0px', // 提前 50px 开始加载
      threshold: 0.01
    })
    
    // 设置占位图
    el.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSJyZ2JhKDI2LDMxLDU4LDAuOCkiLz48dGV4dCB4PSI1MCUiIHk9IjUwJSIgZG9taW5hbnQtYmFzZWxpbmU9Im1pZGRsZSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZmlsbD0icmdiYSgwLDIxMiwyNTUsMC4zKSIgZm9udC1zaXplPSIxNCI+TG9hZGluZy4uLjwvdGV4dD48L3N2Zz4='
    
    // 开始观察
    observer.observe(el)
    
    // 组件卸载时清理
    el._lazyObserver = observer
  },
  unmounted(el: HTMLImageElement) {
    if (el._lazyObserver) {
      el._lazyObserver.unobserve(el)
    }
  }
}

// 扩展 HTMLImageElement 类型
declare global {
  interface HTMLImageElement {
    _lazyObserver?: IntersectionObserver
  }
}

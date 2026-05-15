/**
 * 防抖和节流 composable
 */

import { ref, watch, type Ref, type WatchSource } from 'vue'

/**
 * 防抖
 */
export function useDebounce<T>(source: Ref<T>, delay: number = 300): Ref<T> {
  const debounced = ref<T>(source.value) as Ref<T>
  let timer: ReturnType<typeof setTimeout> | null = null

  watch(source, (value) => {
    if (timer) {
      clearTimeout(timer)
    }
    timer = setTimeout(() => {
      debounced.value = value
    }, delay)
  })

  return debounced
}

/**
 * 防抖函数
 */
export function debounce<T extends (...args: any[]) => any>(
  fn: T,
  delay: number = 300
): (...args: Parameters<T>) => void {
  let timer: ReturnType<typeof setTimeout> | null = null

  return function (this: any, ...args: Parameters<T>) {
    if (timer) {
      clearTimeout(timer)
    }
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

/**
 * 节流
 */
export function useThrottle<T>(source: Ref<T>, delay: number = 300): Ref<T> {
  const throttled = ref<T>(source.value) as Ref<T>
  let lastUpdate = 0

  watch(source, (value) => {
    const now = Date.now()
    if (now - lastUpdate >= delay) {
      throttled.value = value
      lastUpdate = now
    }
  })

  return throttled
}

/**
 * 节流函数
 */
export function throttle<T extends (...args: any[]) => any>(
  fn: T,
  delay: number = 300
): (...args: Parameters<T>) => void {
  let lastCall = 0
  let timeoutId: ReturnType<typeof setTimeout> | null = null

  return function (this: any, ...args: Parameters<T>) {
    const now = Date.now()
    const remaining = delay - (now - lastCall)

    if (remaining <= 0) {
      if (timeoutId) {
        clearTimeout(timeoutId)
        timeoutId = null
      }
      lastCall = now
      fn.apply(this, args)
    } else if (!timeoutId) {
      timeoutId = setTimeout(() => {
        lastCall = Date.now()
        timeoutId = null
        fn.apply(this, args)
      }, remaining)
    }
  }
}

/**
 * 防抖搜索
 */
export function useDebounceSearch(
  searchFn: (keyword: string) => void,
  delay: number = 300
) {
  const keyword = ref('')
  const debouncedKeyword = useDebounce(keyword, delay)

  watch(debouncedKeyword, (value) => {
    if (value) {
      searchFn(value)
    }
  })

  return {
    keyword,
    debouncedKeyword
  }
}

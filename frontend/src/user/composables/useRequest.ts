/**
 * 通用请求处理 composable
 */

import { ref, type Ref } from 'vue'
import { ElMessage } from 'element-plus'

interface UseRequestOptions<T> {
  immediate?: boolean
  onSuccess?: (data: T) => void
  onError?: (error: Error) => void
  errorMessage?: string | false
}

export function useRequest<T>(
  apiFunction: () => Promise<T>,
  options: UseRequestOptions<T> = {}
) {
  const {
    immediate = false,
    onSuccess,
    onError,
    errorMessage = '请求失败'
  } = options

  const data = ref<T | null>(null) as Ref<T | null>
  const loading = ref(false)
  const error = ref<Error | null>(null)

  const execute = async () => {
    loading.value = true
    error.value = null

    try {
      const result = await apiFunction()
      data.value = result
      onSuccess?.(result)
      return result
    } catch (err) {
      error.value = err instanceof Error ? err : new Error('请求失败')
      onError?.(error.value)
      
      if (errorMessage !== false) {
        ElMessage.error(errorMessage || error.value.message)
      }
      
      throw error.value
    } finally {
      loading.value = false
    }
  }

  if (immediate) {
    execute()
  }

  return {
    data,
    loading,
    error,
    execute,
    refresh: execute
  }
}

/**
 * 分页请求处理
 */
interface PageData<T> {
  records: T[]
  total: number
  current: number
  size: number
}

interface UsePageRequestOptions<T> {
  initialPage?: number
  initialSize?: number
  errorMessage?: string | false
}

export function usePageRequest<T>(
  apiFunction: (page: number, size: number) => Promise<PageData<T>>,
  options: UsePageRequestOptions<T> = {}
) {
  const {
    initialPage = 1,
    initialSize = 10,
    errorMessage = '请求失败'
  } = options

  const data = ref<T[]>([])
  const total = ref(0)
  const loading = ref(false)
  const currentPage = ref(initialPage)
  const pageSize = ref(initialSize)

  const execute = async (page?: number, size?: number) => {
    if (page !== undefined) currentPage.value = page
    if (size !== undefined) {
      pageSize.value = size
      currentPage.value = 1
    }

    loading.value = true

    try {
      const result = await apiFunction(currentPage.value, pageSize.value)
      data.value = result.records
      total.value = result.total
      return result
    } catch (err) {
      if (errorMessage !== false) {
        ElMessage.error(errorMessage || (err instanceof Error ? err.message : '请求失败'))
      }
      throw err
    } finally {
      loading.value = false
    }
  }

  const refresh = () => execute()

  const changePage = (page: number) => execute(page)

  const changeSize = (size: number) => execute(1, size)

  // 初始加载
  execute()

  return {
    data,
    total,
    loading,
    currentPage,
    pageSize,
    execute,
    refresh,
    changePage,
    changeSize
  }
}

/**
 * 防抖请求
 */
export function useDebounceRequest<T>(
  apiFunction: () => Promise<T>,
  delay: number = 300
) {
  let timer: ReturnType<typeof setTimeout> | null = null
  const { data, loading, error, execute } = useRequest<T>(apiFunction, { errorMessage: false })

  const debouncedExecute = () => {
    if (timer) {
      clearTimeout(timer)
    }

    timer = setTimeout(() => {
      execute()
    }, delay)
  }

  return {
    data,
    loading,
    error,
    execute: debouncedExecute
  }
}

/**
 * 轮询请求
 */
interface UsePollingOptions<T> {
  interval?: number
  immediate?: boolean
  condition?: (data: T | null) => boolean
  maxAttempts?: number
}

export function usePolling<T>(
  apiFunction: () => Promise<T>,
  options: UsePollingOptions<T> = {}
) {
  const {
    interval = 3000,
    immediate = true,
    condition,
    maxAttempts
  } = options

  const { data, loading, error, execute } = useRequest<T>(apiFunction, { errorMessage: false })
  const isPolling = ref(false)
  const attemptCount = ref(0)
  let timer: ReturnType<typeof setInterval> | null = null

  const start = () => {
    if (isPolling.value) return

    isPolling.value = true
    attemptCount.value = 0

    const poll = async () => {
      try {
        await execute()
        attemptCount.value++

        // 检查是否停止
        if (condition && !condition(data.value)) {
          stop()
          return
        }

        if (maxAttempts && attemptCount.value >= maxAttempts) {
          stop()
          return
        }
      } catch (err) {
        // 错误时继续轮询
      }
    }

    if (immediate) {
      poll()
    }

    timer = setInterval(poll, interval)
  }

  const stop = () => {
    isPolling.value = false
    if (timer) {
      clearInterval(timer)
      timer = null
    }
  }

  // 自动开始
  if (immediate) {
    start()
  }

  return {
    data,
    loading,
    error,
    isPolling,
    start,
    stop
  }
}

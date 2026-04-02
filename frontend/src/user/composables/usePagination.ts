/**
 * 分页相关 composable
 */

import { ref, computed, type Ref } from 'vue'

interface PaginationOptions {
  initialPage?: number
  initialSize?: number
  pageSizes?: number[]
  layout?: string
}

export function usePagination(options: PaginationOptions = {}) {
  const {
    initialPage = 1,
    initialSize = 10,
    pageSizes = [10, 20, 50, 100],
    layout = 'total, sizes, prev, pager, next, jumper'
  } = options

  const currentPage = ref(initialPage)
  const pageSize = ref(initialSize)
  const total = ref(0)

  // 总页数
  const totalPages = computed(() => {
    if (!total.value) return 0
    return Math.ceil(total.value / pageSize.value)
  })

  // 是否有上一页
  const hasPrev = computed(() => currentPage.value > 1)

  // 是否有下一页
  const hasNext = computed(() => currentPage.value < totalPages.value)

  // 起始索引
  const startIndex = computed(() => (currentPage.value - 1) * pageSize.value)

  // 结束索引
  const endIndex = computed(() => Math.min(startIndex.value + pageSize.value, total.value))

  // 跳转第一页
  const first = () => {
    currentPage.value = 1
  }

  // 跳转最后一页
  const last = () => {
    currentPage.value = totalPages.value
  }

  // 上一页
  const prev = () => {
    if (hasPrev.value) {
      currentPage.value--
    }
  }

  // 下一页
  const next = () => {
    if (hasNext.value) {
      currentPage.value++
    }
  }

  // 跳转指定页
  const goTo = (page: number) => {
    const validPage = Math.max(1, Math.min(page, totalPages.value || 1))
    currentPage.value = validPage
  }

  // 设置每页数量
  const setSize = (size: number) => {
    pageSize.value = size
    currentPage.value = 1
  }

  // 设置总数
  const setTotal = (value: number) => {
    total.value = value
  }

  // 重置
  const reset = () => {
    currentPage.value = initialPage
    pageSize.value = initialSize
    total.value = 0
  }

  // 分页参数
  const paginationParams = computed(() => ({
    current: currentPage.value,
    size: pageSize.value
  }))

  return {
    currentPage,
    pageSize,
    total,
    totalPages,
    hasPrev,
    hasNext,
    startIndex,
    endIndex,
    pageSizes,
    layout,
    first,
    last,
    prev,
    next,
    goTo,
    setSize,
    setTotal,
    reset,
    paginationParams
  }
}

/**
 * 列表排序 composable
 */
export function useSort<T>(
  list: Ref<T[]>,
  options: {
    defaultSortField?: string
    defaultSortOrder?: 'asc' | 'desc'
  } = {}
) {
  const {
    defaultSortField = '',
    defaultSortOrder = 'asc'
  } = options

  const sortField = ref(defaultSortField)
  const sortOrder = ref<'asc' | 'desc'>(defaultSortOrder)

  // 排序
  const sort = (field?: string, order?: 'asc' | 'desc') => {
    if (field) {
      // 如果点击的是同一列，切换顺序
      if (field === sortField.value) {
        sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
      } else {
        sortField.value = field
        sortOrder.value = order || 'asc'
      }
    }

    if (!sortField.value) return list.value

    // 执行排序
    return [...list.value].sort((a: any, b: any) => {
      const aVal = a[sortField.value]
      const bVal = b[sortField.value]

      if (aVal === bVal) return 0

      const result = aVal > bVal ? 1 : -1
      return sortOrder.value === 'asc' ? result : -result
    })
  }

  // 获取排序参数
  const getSortParam = () => {
    if (!sortField.value) return ''
    return `${sortField.value}_${sortOrder.value === 'asc' ? 'asc' : 'desc'}`
  }

  // 重置排序
  const reset = () => {
    sortField.value = defaultSortField
    sortOrder.value = defaultSortOrder
  }

  return {
    sortField,
    sortOrder,
    sort,
    getSortParam,
    reset
  }
}

/**
 * 列表筛选 composable
 */
export function useFilter<T>(
  list: Ref<T[]>,
  filterRules: Record<string, (item: T, value: any) => boolean> = {}
) {
  const filters = ref<Record<string, any>>({})

  // 更新筛选条件
  const setFilter = (key: string, value: any) => {
    filters.value[key] = value
  }

  // 清除筛选条件
  const clearFilter = (key?: string) => {
    if (key) {
      delete filters.value[key]
    } else {
      filters.value = {}
    }
  }

  // 获取筛选后的列表
  const filteredList = computed(() => {
    const keys = Object.keys(filters.value)
    if (keys.length === 0) return list.value

    return list.value.filter(item => {
      return keys.every(key => {
        const rule = filterRules[key]
        const value = filters.value[key]
        
        if (!rule) return true
        if (value === '' || value === null || value === undefined) return true
        
        return rule(item, value)
      })
    })
  })

  // 是否有激活的筛选
  const hasActiveFilters = computed(() => {
    return Object.values(filters.value).some(v => v !== '' && v !== null && v !== undefined)
  })

  return {
    filters,
    setFilter,
    clearFilter,
    filteredList,
    hasActiveFilters
  }
}

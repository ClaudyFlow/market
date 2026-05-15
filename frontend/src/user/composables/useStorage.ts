/**
 * 本地存储 composable
 */

import { ref, watch, type Ref } from 'vue'

interface StorageOptions<T> {
  storage?: 'local' | 'session'
  serializer?: (value: T) => string
  parser?: (value: string) => T
}

export function useStorage<T>(
  key: string,
  initialValue: T,
  options: StorageOptions<T> = {}
): Ref<T> {
  const {
    storage = 'local',
    serializer = JSON.stringify,
    parser = JSON.parse
  } = options

  const storageObj = storage === 'local' ? localStorage : sessionStorage

  // 读取存储的值
  const readValue = (): T => {
    try {
      const item = storageObj.getItem(key)
      return item ? parser(item) : initialValue
    } catch (error) {
      console.error(`读取 ${key} 失败:`, error)
      return initialValue
    }
  }

  // 创建响应式引用
  const state = ref<T>(readValue()) as Ref<T>

  // 监听变化并同步到存储
  watch(state, (newValue) => {
    try {
      if (newValue === null || newValue === undefined) {
        storageObj.removeItem(key)
      } else {
        storageObj.setItem(key, serializer(newValue))
      }
    } catch (error) {
      console.error(`保存 ${key} 失败:`, error)
    }
  }, { deep: true })

  return state
}

/**
 * 本地存储快捷方法
 */
export function useLocalStorage<T>(key: string, initialValue: T) {
  return useStorage<T>(key, initialValue, { storage: 'local' })
}

export function useSessionStorage<T>(key: string, initialValue: T) {
  return useStorage<T>(key, initialValue, { storage: 'session' })
}

/**
 * 简单字符串存储
 */
export function useLocalStorageString(key: string, initialValue: string = '') {
  return useStorage<string>(key, initialValue, {
    storage: 'local',
    serializer: (v) => v,
    parser: (v) => v
  })
}

/**
 * 表单处理 composable
 */

import { ref, reactive, type Ref } from 'vue'
import { ElMessage } from 'element-plus'

interface ValidationRule {
  required?: boolean
  pattern?: RegExp
  min?: number
  max?: number
  validator?: (value: any) => boolean | Promise<boolean>
  message: string
  trigger?: 'blur' | 'change' | 'submit'
}

interface FormState {
  [key: string]: any
}

export function useForm<T extends FormState>(
  initialValues: T,
  rules?: Record<keyof T, ValidationRule[]>
) {
  // 表单数据
  const formData = reactive<T>({ ...initialValues })

  // 错误信息
  const errors = ref<Record<string, string>>({})

  // 是否已触碰
  const touched = ref<Record<string, boolean>>({})

  // 加载中
  const loading = ref(false)

  // 验证单个字段
  const validateField = async (field: keyof T, value: any = formData[field]) => {
    const fieldRules = rules?.[field] || []
    const errorMessages: string[] = []

    for (const rule of fieldRules) {
      // 必填验证
      if (rule.required && (value === '' || value === null || value === undefined)) {
        errorMessages.push(rule.message)
        continue
      }

      // 跳过空值的非必填字段
      if (value === '' || value === null || value === undefined) continue

      // 模式验证
      if (rule.pattern && !rule.pattern.test(value)) {
        errorMessages.push(rule.message)
        continue
      }

      // 最小值验证
      if (rule.min !== undefined && String(value).length < rule.min) {
        errorMessages.push(rule.message)
        continue
      }

      // 最大值验证
      if (rule.max !== undefined && String(value).length > rule.max) {
        errorMessages.push(rule.message)
        continue
      }

      // 自定义验证器
      if (rule.validator) {
        const result = await rule.validator(value)
        if (!result) {
          errorMessages.push(rule.message)
        }
      }
    }

    errors.value[field as string] = errorMessages[0] || ''
    return errorMessages.length === 0
  }

  // 验证所有字段
  const validate = async (): Promise<boolean> => {
    const fields = Object.keys(formData) as (keyof T)[]
    const results = await Promise.all(fields.map(field => validateField(field)))
    return results.every(result => result)
  }

  // 重置验证
  const resetValidation = () => {
    errors.value = {}
    touched.value = {}
  }

  // 重置表单
  const reset = () => {
    Object.assign(formData, initialValues)
    resetValidation()
  }

  // 设置字段值
  const setFieldValue = async (field: keyof T, value: any) => {
    formData[field] = value
    touched.value[field as string] = true
    await validateField(field, value)
  }

  // 设置错误
  const setError = (field: keyof T, message: string) => {
    errors.value[field as string] = message
  }

  // 清除错误
  const clearError = (field?: keyof T) => {
    if (field) {
      delete errors.value[field as string]
    } else {
      errors.value = {}
    }
  }

  // 标记为已触碰
  const markTouched = (field: keyof T) => {
    touched.value[field as string] = true
  }

  // 获取字段错误
  const getFieldError = (field: keyof T): string | undefined => {
    return errors.value[field as string]
  }

  // 是否有错误
  const hasError = (field?: keyof T): boolean => {
    if (field) {
      return !!errors.value[field as string]
    }
    return Object.values(errors.value).some(Boolean)
  }

  // 是否有效
  const isValid = computed(() => !hasError())

  return {
    formData,
    errors,
    touched,
    loading,
    validate,
    validateField,
    resetValidation,
    reset,
    setFieldValue,
    setError,
    clearError,
    markTouched,
    getFieldError,
    hasError,
    isValid
  }
}

/**
 * 步骤表单 composable
 */
export function useSteps(totalSteps: number = 3) {
  const currentStep = ref(1)
  const completedSteps = ref<number[]>([])

  // 总步数
  const total = ref(totalSteps)

  // 进度百分比
  const progress = computed(() => (currentStep.value / total.value) * 100)

  // 是否是第一步
  const isFirst = computed(() => currentStep.value === 1)

  // 是否是最后一步
  const isLast = computed(() => currentStep.value === total.value)

  // 下一步
  const next = () => {
    if (currentStep.value < total.value) {
      completedSteps.value.push(currentStep.value)
      currentStep.value++
    }
  }

  // 上一步
  const prev = () => {
    if (currentStep.value > 1) {
      currentStep.value--
    }
  }

  // 跳转到指定步骤
  const goTo = (step: number) => {
    currentStep.value = Math.max(1, Math.min(step, total.value))
  }

  // 重置
  const reset = () => {
    currentStep.value = 1
    completedSteps.value = []
  }

  // 标记步骤完成
  const markCompleted = (step?: number) => {
    const stepToMark = step || currentStep.value
    if (!completedSteps.value.includes(stepToMark)) {
      completedSteps.value.push(stepToMark)
    }
  }

  // 检查步骤是否完成
  const isCompleted = (step: number) => {
    return completedSteps.value.includes(step)
  }

  return {
    currentStep,
    total,
    progress,
    isFirst,
    isLast,
    next,
    prev,
    goTo,
    reset,
    markCompleted,
    isCompleted
  }
}

/**
 * 文件上传 composable
 */
export function useFileUpload(options: {
  maxSize?: number
  accept?: string
  multiple?: boolean
  limit?: number
} = {}) {
  const {
    maxSize = 10 * 1024 * 1024, // 10MB
    accept = 'image/*',
    multiple = false,
    limit = 9
  } = options

  const fileList = ref<File[]>([])
  const uploading = ref(false)
  const error = ref<string | null>(null)

  // 添加文件
  const addFiles = (files: FileList | File[]) => {
    error.value = null
    const newFiles = Array.from(files)

    // 检查数量限制
    if (!multiple && newFiles.length > 1) {
      error.value = '只能上传一个文件'
      return false
    }

    if (fileList.value.length + newFiles.length > limit) {
      error.value = `最多只能上传 ${limit} 个文件`
      return false
    }

    // 验证文件
    for (const file of newFiles) {
      // 检查大小
      if (file.size > maxSize) {
        error.value = `文件大小不能超过 ${maxSize / 1024 / 1024}MB`
        return false
      }

      // 检查类型
      if (accept && !file.type.match(accept)) {
        error.value = '文件类型不符合要求'
        return false
      }
    }

    fileList.value.push(...newFiles)
    return true
  }

  // 移除文件
  const removeFile = (index: number) => {
    fileList.value.splice(index, 1)
  }

  // 清空
  const clear = () => {
    fileList.value = []
    error.value = null
  }

  // 获取文件预览 URL
  const getPreviewUrl = (file: File) => {
    return URL.createObjectURL(file)
  }

  return {
    fileList,
    uploading,
    error,
    addFiles,
    removeFile,
    clear,
    getPreviewUrl
  }
}

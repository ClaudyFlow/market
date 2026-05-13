/**
 * 验证相关工具函数
 */

/**
 * 验证邮箱
 */
export function isEmail(value: string): boolean {
  const reg = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  return reg.test(value)
}

/**
 * 验证手机号（中国大陆）
 */
export function isPhone(value: string): boolean {
  const reg = /^1[3-9]\d{9}$/
  return reg.test(value)
}

/**
 * 验证身份证号（中国大陆）
 */
export function isIdCard(value: string): boolean {
  const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
  return reg.test(value)
}

/**
 * 验证 URL
 */
export function isUrl(value: string): boolean {
  const reg = /^https?:\/\/.+/
  return reg.test(value)
}

/**
 * 验证银行卡号（Luhn 算法）
 */
export function isBankCard(value: string): boolean {
  if (!/^\d{13,19}$/.test(value)) return false
  
  let sum = 0
  let isEven = false
  
  for (let i = value.length - 1; i >= 0; i--) {
    let digit = parseInt(value[i], 10)
    
    if (isEven) {
      digit *= 2
      if (digit > 9) {
        digit -= 9
      }
    }
    
    sum += digit
    isEven = !isEven
  }
  
  return sum % 10 === 0
}

/**
 * 验证密码强度
 */
export function checkPasswordStrength(password: string): 'weak' | 'medium' | 'strong' {
  let strength = 0
  
  if (password.length >= 8) strength++
  if (password.length >= 12) strength++
  if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++
  if (/\d/.test(password)) strength++
  if (/[^a-zA-Z0-9]/.test(password)) strength++
  
  if (strength <= 2) return 'weak'
  if (strength <= 4) return 'medium'
  return 'strong'
}

/**
 * 验证密码复杂度
 */
export function isPasswordValid(password: string, minLength: number = 6): { valid: boolean; message: string } {
  if (password.length < minLength) {
    return { valid: false, message: `密码长度至少为${minLength}位` }
  }
  
  if (!/[a-zA-Z]/.test(password)) {
    return { valid: false, message: '密码必须包含字母' }
  }
  
  if (!/\d/.test(password)) {
    return { valid: false, message: '密码必须包含数字' }
  }
  
  return { valid: true, message: '密码符合要求' }
}

/**
 * 验证验证码
 */
export function isCaptcha(value: string, length: number = 6): boolean {
  const reg = new RegExp(`^\\d{${length}}$`)
  return reg.test(value)
}

/**
 * 验证用户名
 */
export function isUsername(value: string): boolean {
  const reg = /^[a-zA-Z][a-zA-Z0-9_-]{3,19}$/
  return reg.test(value)
}

/**
 * 验证中文
 */
export function isChinese(value: string): boolean {
  const reg = /^[\u4e00-\u9fa5]+$/
  return reg.test(value)
}

/**
 * 验证是否为空
 */
export function isEmpty(value: any): boolean {
  if (value === null || value === undefined) return true
  if (typeof value === 'string') return value.trim() === ''
  if (Array.isArray(value)) return value.length === 0
  if (typeof value === 'object') return Object.keys(value).length === 0
  return false
}

/**
 * 验证是否为数字
 */
export function isNumber(value: any): boolean {
  return !isNaN(parseFloat(value)) && isFinite(value)
}

/**
 * 验证是否在范围内
 */
export function isInRange(value: number, min: number, max: number): boolean {
  return value >= min && value <= max
}

/**
 * 验证数组是否包含某值
 */
export function includes<T>(arr: T[], value: T): boolean {
  return Array.isArray(arr) && arr.includes(value)
}

/**
 * 验证日期是否有效
 */
export function isValidDate(date: any): boolean {
  const d = new Date(date)
  return !isNaN(d.getTime())
}

/**
 * 验证日期范围
 */
export function isDateInRange(date: Date | string, start: Date | string, end: Date | string): boolean {
  const d = new Date(date).getTime()
  const s = new Date(start).getTime()
  const e = new Date(end).getTime()
  return d >= s && d <= e
}

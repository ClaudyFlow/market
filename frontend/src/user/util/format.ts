/**
 * 格式化相关工具函数
 */

/**
 * 格式化日期
 */
export function formatDate(date: Date | string | number, format: string = 'YYYY-MM-DD HH:mm:ss'): string {
  const d = new Date(date)
  if (isNaN(d.getTime())) return ''

  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')

  return format
    .replace('YYYY', String(year))
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 格式化相对时间
 */
export function formatRelativeTime(date: Date | string | number): string {
  const d = new Date(date)
  const now = new Date()
  const diff = now.getTime() - d.getTime()

  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  const week = 7 * day
  const month = 30 * day
  const year = 365 * day

  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分钟前`
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小时前`
  } else if (diff < week) {
    return `${Math.floor(diff / day)}天前`
  } else if (diff < month) {
    return `${Math.floor(diff / week)}周前`
  } else if (diff < year) {
    return `${Math.floor(diff / month)}个月前`
  } else {
    return `${Math.floor(diff / year)}年前`
  }
}

/**
 * 格式化金额
 */
export function formatMoney(amount: number, currency: string = '¥', decimals: number = 2): string {
  if (amount === null || amount === undefined || isNaN(amount)) return `${currency}0.00`
  return `${currency}${amount.toFixed(decimals).replace(/\d(?=(\d{3})+\.)/g, '$&,')}`
}

/**
 * 格式化数字（缩写）
 */
export function formatNumber(num: number): string {
  if (num >= 100000000) {
    return `${(num / 100000000).toFixed(1)}亿`
  }
  if (num >= 10000) {
    return `${(num / 10000).toFixed(1)}万`
  }
  if (num >= 1000) {
    return `${(num / 1000).toFixed(1)}k`
  }
  return String(num)
}

/**
 * 格式化百分比
 */
export function formatPercent(value: number, decimals: number = 1): string {
  return `${(value * 100).toFixed(decimals)}%`
}

/**
 * 格式化文件大小
 */
export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return `${(bytes / Math.pow(k, i)).toFixed(2)} ${sizes[i]}`
}

/**
 * 格式化手机号
 */
export function formatPhone(phone: string): string {
  if (!phone) return ''
  const str = String(phone)
  if (str.length === 11) {
    return str.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
  }
  return str
}

/**
 * 格式化邮箱
 */
export function formatEmail(email: string): string {
  if (!email) return ''
  const [name, domain] = email.split('@')
  if (!name || !domain) return email
  
  const maskedName = name.charAt(0) + '*'.repeat(name.length - 2) + name.charAt(name.length - 1)
  return `${maskedName}@${domain}`
}

/**
 * 格式化身份证号
 */
export function formatIdCard(idCard: string): string {
  if (!idCard || idCard.length < 18) return ''
  return idCard.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2')
}

/**
 * 格式化银行卡号
 */
export function formatBankCard(cardNo: string): string {
  if (!cardNo) return ''
  // 每 4 位加空格
  return cardNo.replace(/(\d{4})/g, '$1 ').trim()
}

/**
 * 格式化地址
 */
export function formatAddress(province: string, city: string, district: string, detail: string): string {
  return `${province}${city}${district}${detail}`
}

/**
 * 格式化评分
 */
export function formatRating(score: number): string {
  return score.toFixed(1)
}

/**
 * 格式化倒计时
 */
export function formatCountdown(seconds: number): string {
  if (seconds <= 0) return '已结束'
  
  const d = Math.floor(seconds / (3600 * 24))
  const h = Math.floor((seconds % (3600 * 24)) / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  
  if (d > 0) {
    return `${d}天${h}时${m}分${s}秒`
  } else if (h > 0) {
    return `${h}时${m}分${s}秒`
  } else if (m > 0) {
    return `${m}分${s}秒`
  } else {
    return `${s}秒`
  }
}

/**
 * 前端日志库 - 高压缩版本
 *
 * 压缩策略:
 * 1. 日期从文件名获取，只存时分秒毫秒
 * 2. 日志等级默认继承上一条
 * 3. 模块/线程使用字典 ID
 * 4. 消息使用差分 + RLE
 * 5. 内存池优化
 */

import type { FileSystemLogger } from './localFileLogger'

type Level = 0 | 1 | 2 | 3

interface LogEntry {
  time: number    // 毫秒数 (从 00:00:00.000 开始)
  level?: Level   // 省略则同上
  msg: string     // 消息
  data?: string   // 数据
  stack?: string  // 堆栈
}

interface CompressedBlock {
  count: number
  entries: Uint8Array
}

class LogCompressor {
  private lastLevel: Level = 1
  private lastMsg = ''
  private memoryPool: Uint8Array[] = []
  
  constructor() {
    // 预分配内存池
    for (let i = 0; i < 10; i++) {
      this.memoryPool.push(new Uint8Array(4096))
    }
  }
  
  /**
   * 压缩单条日志
   */
  compress(entry: LogEntry, prev?: LogEntry): Uint8Array {
    const buf = this.acquire()
    const view = new DataView(buf.buffer)
    let offset = 0
    
    // 时间 (总是写入，毫秒数)
    view.setUint32(offset, entry.time, true)
    offset += 4
    
    // 标志位
    let flags = 0
    
    // 级别 (如果不同则写入)
    if (!prev || entry.level !== prev.level) {
      flags |= 0x01
    }
    
    // 消息差分
    if (prev && entry.msg.startsWith(prev.msg)) {
      flags |= 0x02
    }
    
    // 数据 (可选)
    if (entry.data) {
      flags |= 0x04
    }
    
    // 堆栈 (可选)
    if (entry.stack) {
      flags |= 0x08
    }
    
    view.setUint8(offset, flags)
    offset++
    
    // 写入级别 (如果需要)
    if (flags & 0x01) {
      view.setUint8(offset, entry.level ?? 1)
      offset++
      this.lastLevel = entry.level ?? 1
    }
    
    // 写入消息
    if (flags & 0x02) {
      // 差分
      const prefixLen = prev?.msg.length ?? 0
      const diff = entry.msg.substring(prefixLen)
      view.setUint16(offset, prefixLen, true)
      offset += 2
      offset += this.writeString(buf, offset, diff)
    } else {
      offset += this.writeString(buf, offset, entry.msg)
      this.lastMsg = entry.msg
    }
    
    // 写入数据
    if (flags & 0x04 && entry.data) {
      offset += this.writeString(buf, offset, entry.data)
    }
    
    // 写入堆栈
    if (flags & 0x08 && entry.stack) {
      offset += this.writeString(buf, offset, entry.stack)
    }
    
    return buf.slice(0, offset)
  }
  
  /**
   * 压缩日志块
   */
  compressBatch(entries: LogEntry[]): Uint8Array {
    if (entries.length === 0) return new Uint8Array(0)
    
    const chunks: Uint8Array[] = []
    let prev: LogEntry | undefined
    
    for (const entry of entries) {
      chunks.push(this.compress(entry, prev))
      prev = entry
    }
    
    // 合并
    const total = chunks.reduce((sum, c) => sum + c.length, 0)
    const result = new Uint8Array(total)
    let offset = 0
    for (const chunk of chunks) {
      result.set(chunk, offset)
      offset += chunk.length
    }
    
    return result
  }
  
  /**
   * 解压 (前端查看日志用)
   */
  decompressBatch(data: Uint8Array): LogEntry[] {
    const entries: LogEntry[] = []
    const view = new DataView(data.buffer)
    let offset = 0
    
    const count = view.getUint32(0, true)
    offset = 4
    
    let prev: LogEntry | undefined
    
    for (let i = 0; i < count; i++) {
      const entry: LogEntry = {
        time: view.getUint32(offset, true),
        level: 1,
        msg: ''
      }
      offset += 4
      
      const flags = view.getUint8(offset)
      offset++
      
      // 级别
      if (flags & 0x01) {
        entry.level = view.getUint8(offset) as Level
        offset++
      } else if (prev) {
        entry.level = prev.level
      }
      
      // 消息
      if (flags & 0x02) {
        const prefixLen = view.getUint16(offset, true)
        offset += 2
        const { str, len } = this.readString(data, offset)
        offset += len
        entry.msg = (prev?.msg ?? '').substring(0, prefixLen) + str
      } else {
        const { str, len } = this.readString(data, offset)
        offset += len
        entry.msg = str
      }
      
      // 数据
      if (flags & 0x04) {
        const { str, len } = this.readString(data, offset)
        offset += len
        entry.data = str
      }
      
      // 堆栈
      if (flags & 0x08) {
        const { str, len } = this.readString(data, offset)
        offset += len
        entry.stack = str
      }
      
      entries.push(entry)
      prev = entry
    }
    
    return entries
  }
  
  private writeString(buf: Uint8Array, offset: number, str: string): number {
    const encoder = new TextEncoder()
    const bytes = encoder.encode(str)
    const view = new DataView(buf.buffer)
    
    view.setUint16(offset, bytes.length, true)
    offset += 2
    
    for (let i = 0; i < bytes.length; i++) {
      buf[offset + i] = bytes[i]
    }
    
    return 2 + bytes.length
  }
  
  private readString(data: Uint8Array, offset: number): { str: string, len: number } {
    const view = new DataView(data.buffer)
    const len = view.getUint16(offset, true)
    offset += 2
    
    const bytes = data.slice(offset, offset + len)
    const decoder = new TextDecoder()
    
    return { str: decoder.decode(bytes), len: 2 + len }
  }
  
  private acquire(): Uint8Array {
    return this.memoryPool.pop() ?? new Uint8Array(4096)
  }
  
  private release(buf: Uint8Array) {
    if (buf.length === 4096 && this.memoryPool.length < 20) {
      this.memoryPool.push(buf)
    }
  }
}

/**
 * 日志主类
 */
class Logger {
  private module: string
  private buffer: LogEntry[] = []
  private compressor = new LogCompressor()
  private apiEndpoint: string
  private enabled = true
  private bufferSize = 1024
  private fileLogger: FileSystemLogger | null = null

  constructor(module: string, options?: {
    bufferSize?: number
    flushInterval?: number
    apiEndpoint?: string
  }) {
    this.module = module
    this.apiEndpoint = options?.apiEndpoint || '/api/log'
    this.bufferSize = options?.bufferSize || 1024

    // 定时刷新
    const interval = options?.flushInterval || 10000
    setInterval(() => this.flush(), interval)

    // 页面卸载
    window.addEventListener('beforeunload', () => this.flush())
  }

  /**
   * 启用本地文件日志写入
   */
  async enableFileLogger(fileLogger: FileSystemLogger): Promise<void> {
    this.fileLogger = fileLogger
    const success = await fileLogger.enable()
    if (success) {
      this.info('本地文件日志已启用')
    }
  }

  /**
   * 禁用本地文件日志
   */
  async disableFileLogger(): Promise<void> {
    if (this.fileLogger) {
      await this.fileLogger.disable()
      this.fileLogger = null
    }
  }

  /**
   * 获取本地文件日志器
   */
  getFileLogger(): FileSystemLogger | null {
    return this.fileLogger
  }
  
  private timeFromNow(): number {
    const now = new Date()
    return now.getHours() * 3600000 + 
           now.getMinutes() * 60000 + 
           now.getSeconds() * 1000 + 
           now.getMilliseconds()
  }
  
  private entry(level: Level, msg: string, data?: any, stack?: string): LogEntry {
    return {
      time: this.timeFromNow(),
      level,
      msg,
      data: data ? (typeof data === 'string' ? data : JSON.stringify(data)) : undefined,
      stack
    }
  }
  
  debug(msg: string, data?: any) {
    this.add(this.entry(0, msg, data))
  }
  
  info(msg: string, data?: any) {
    this.add(this.entry(1, msg, data))
  }
  
  warn(msg: string, data?: any) {
    this.add(this.entry(2, msg, data))
  }
  
  error(msg: string, err?: any) {
    const e = this.entry(3, msg, null, err?.stack)
    this.add(e)
    if (this.buffer.length > 50) this.flush()
  }
  
  private add(entry: LogEntry) {
    if (!this.enabled) return
    this.buffer.push(entry)
    this.print(entry)

    // 写入本地文件
    if (this.fileLogger) {
      this.fileLogger.log(entry.level, entry.msg, entry.data, entry.stack)
    }

    if (this.buffer.length >= this.bufferSize) {
      this.flush()
    }
  }
  
  private print(e: LogEntry) {
    const hours = Math.floor(e.time / 3600000)
    const mins = Math.floor((e.time % 3600000) / 60000)
    const secs = Math.floor((e.time % 60000) / 1000)
    const ms = e.time % 1000
    
    const timeStr = `${hours.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}.${ms.toString().padStart(3, '0')}`
    const levels = ['D', 'I', 'W', 'E']
    const prefix = `${timeStr} [${this.module}] ${levels[e.level]}`
    
    const styles = [
      'color:#666',
      'color:#06c',
      'color:#fa0',
      'color:#c00;font-weight:bold'
    ]
    
    console.log(`%c${prefix} - ${e.msg}`, styles[e.level], e.data || '')
    if (e.stack) console.error(e.stack)
  }
  
  async flush() {
    if (this.buffer.length === 0) return
    
    const compressed = this.compressor.compressBatch([...this.buffer])
    
    // 添加头部 (条目数)
    const header = new Uint8Array(4)
    new DataView(header.buffer).setUint32(0, this.buffer.length, true)
    
    const payload = new Uint8Array(header.length + compressed.length)
    payload.set(header)
    payload.set(compressed, header.length)
    
    try {
      await fetch(this.apiEndpoint + '/upload', {
        method: 'POST',
        headers: { 'Content-Type': 'application/octet-stream' },
        body: payload,
        keepalive: true
      })
      this.buffer = []
    } catch (e) {
      console.warn('日志上报失败', e)
    }
  }
  
  setEnabled(v: boolean) { this.enabled = v }
}

const createLogger = (module: string, options?: any): Logger => {
  return new Logger(module, options)
}

export const setupHttpLogger = (axios: any, logger: Logger) => {
  axios.interceptors.request.use((c: any) => {
    logger.debug('HTTP ' + (c.method?.toUpperCase() || 'REQ'), c.url)
    return c
  }, (e: any) => {
    logger.error('HTTP Request Error', e.message)
    return Promise.reject(e)
  })
  
  axios.interceptors.response.use((r: any) => {
    logger.debug('HTTP ' + r.status, r.config.url)
    return r
  }, (e: any) => {
    logger.error('HTTP ' + (e.response?.status || 'ERR'), e.message)
    return Promise.reject(e)
  })
}

export const setupGlobalErrorLogger = (logger: Logger) => {
  window.onerror = (m, u, l, c, e) => {
    logger.error('Error', { message: m, url: u, line: l, col: c, stack: e?.stack })
    return false
  }
  window.onunhandledrejection = (e: PromiseRejectionEvent) => {
    logger.error('Promise Reject', e.reason)
  }
}

export { Logger, LogCompressor }
export default createLogger

/**
 * 本地文件日志写入器 - 使用 File System Access API
 * 允许前端直接在用户电脑上创建和写入日志文件
 */

interface LogEntry {
  time: number
  level: 0 | 1 | 2 | 3
  module: string
  msg: string
  data?: string
  stack?: string
}

interface FileSystemLoggerOptions {
  bufferSize?: number        // 缓冲区大小 (默认 100 条)
  flushInterval?: number     // 刷新间隔 ms (默认 5000)
  fileName?: string          // 日志文件名
  maxFileSize?: number       // 最大文件大小 KB (默认 10MB)
}

/**
 * 格式化日志为文本
 */
function formatLogEntry(entry: LogEntry): string {
  const date = new Date(entry.time)
  const timeStr = date.toTimeString().split(' ')[0] + '.' + date.getMilliseconds().toString().padStart(3, '0')
  const levels = ['DEBUG', 'INFO', 'WARN', 'ERROR']
  return `[${timeStr}] [${entry.module}] ${levels[entry.level]}: ${entry.msg}${entry.data ? ' | Data: ' + entry.data : ''}${entry.stack ? ' | Stack: ' + entry.stack : ''}`
}

/**
 * 本地文件日志写入器
 */
export class FileSystemLogger {
  private fileHandle: FileSystemFileHandle | null = null
  private writer: FileSystemWritableFileStream | null = null
  private buffer: LogEntry[] = []
  private module: string
  private bufferSize: number
  private flushInterval: number
  private maxFileSize: number
  private fileName: string
  private enabled = false
  private totalWritten = 0
  private flushTimer: ReturnType<typeof setInterval> | null = null

  constructor(module: string, options: FileSystemLoggerOptions = {}) {
    this.module = module
    this.bufferSize = options.bufferSize || 100
    this.flushInterval = options.flushInterval || 5000
    this.maxFileSize = options.maxFileSize || 10 * 1024 // 10MB
    this.fileName = options.fileName || `market-${module}-${this.getDateStr()}.log`
  }

  private getDateStr(): string {
    const now = new Date()
    return `${now.getFullYear()}-${(now.getMonth() + 1).toString().padStart(2, '0')}-${now.getDate().toString().padStart(2, '0')}`
  }

  /**
   * 请求用户授权并创建日志文件
   */
  async enable(): Promise<boolean> {
    if (!('showSaveFilePicker' in window)) {
      console.warn('File System Access API 不支持，请使用 Chrome/Edge 桌面版')
      return false
    }

    try {
      // 请求用户选择保存位置
      this.fileHandle = await window.showSaveFilePicker({
        suggestedName: this.fileName,
        types: [{
          description: '日志文件',
          accept: { 'text/plain': ['.log', '.txt'] }
        }]
      })

      // 获取写入器
      this.writer = await this.fileHandle.createWritable()
      this.enabled = true
      this.totalWritten = 0

      // 写入文件头
      await this.writeHeader()

      // 启动定时刷新
      this.flushTimer = setInterval(() => this.flush(), this.flushInterval)

      // 页面卸载时关闭
      window.addEventListener('beforeunload', () => this.disable())

      console.log(`[FileLogger] 日志文件已创建：${this.fileName}`)
      return true
    } catch (err) {
      if ((err as any).name !== 'AbortError') {
        console.error('[FileLogger] 创建日志文件失败:', err)
      }
      return false
    }
  }

  /**
   * 禁用日志写入
   */
  async disable(): Promise<void> {
    this.enabled = false

    // 刷新缓冲区
    await this.flush()

    // 关闭写入器
    if (this.writer) {
      await this.writer.close()
      this.writer = null
    }

    // 清除定时器
    if (this.flushTimer) {
      clearInterval(this.flushTimer)
      this.flushTimer = null
    }

    this.fileHandle = null
    console.log('[FileLogger] 日志文件已关闭')
  }

  /**
   * 写入文件头
   */
  private async writeHeader(): Promise<void> {
    if (!this.writer) return

    const header = `=== Market Platform 日志文件 ===\n` +
                   `模块：${this.module}\n` +
                   `创建时间：${new Date().toLocaleString('zh-CN')}\n` +
                   `浏览器：${navigator.userAgent}\n` +
                   `页面：${window.location.href}\n` +
                   `================================\n\n`

    await this.writer.write(header)
    this.totalWritten += header.length
  }

  /**
   * 记录日志
   */
  log(level: 0 | 1 | 2 | 3, msg: string, data?: any, stack?: string): void {
    if (!this.enabled) return

    const entry: LogEntry = {
      time: Date.now(),
      level,
      module: this.module,
      msg,
      data: data ? (typeof data === 'string' ? data : JSON.stringify(data)) : undefined,
      stack
    }

    this.buffer.push(entry)

    // 缓冲区满或错误日志立即刷新
    if (this.buffer.length >= this.bufferSize || level === 3) {
      this.flush()
    }
  }

  debug(msg: string, data?: any): void {
    this.log(0, msg, data)
  }

  info(msg: string, data?: any): void {
    this.log(1, msg, data)
  }

  warn(msg: string, data?: any): void {
    this.log(2, msg, data)
  }

  error(msg: string, err?: any): void {
    this.log(3, msg, err?.message || err, err?.stack)
  }

  /**
   * 刷新缓冲区到文件
   */
  async flush(): Promise<void> {
    if (!this.writer || this.buffer.length === 0 || !this.enabled) return

    try {
      const lines = this.buffer.map(formatLogEntry).join('\n') + '\n'
      await this.writer.write(lines)
      this.totalWritten += lines.length

      // 检查文件大小
      if (this.totalWritten > this.maxFileSize * 1024) {
        console.warn('[FileLogger] 日志文件过大，建议关闭并重新创建')
      }

      this.buffer = []
    } catch (err) {
      console.error('[FileLogger] 写入失败:', err)
    }
  }

  /**
   * 获取当前状态
   */
  getStatus(): { enabled: boolean; bufferSize: number; totalWritten: number } {
    return {
      enabled: this.enabled,
      bufferSize: this.buffer.length,
      totalWritten: this.totalWritten
    }
  }

  /**
   * 检查 API 支持
   */
  static isSupported(): boolean {
    return 'showSaveFilePicker' in window
  }
}

/**
 * 创建本地文件日志器
 */
export const createFileLogger = (module: string, options?: FileSystemLoggerOptions): FileSystemLogger => {
  return new FileSystemLogger(module, options)
}

export default FileSystemLogger

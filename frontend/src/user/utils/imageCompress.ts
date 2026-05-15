/**
 * 图片压缩工具类
 * 使用 Canvas 进行图片压缩，支持 Web Worker 异步压缩
 */

export interface CompressOptions {
  maxWidth?: number      // 最大宽度
  maxHeight?: number     // 最大高度
  quality?: number       // 压缩质量 (0.1 - 1.0)
  format?: string        // 输出格式 (image/jpeg, image/png, image/webp)
  useWorker?: boolean    // 是否使用 Web Worker
}

const defaultOptions: CompressOptions = {
  maxWidth: 1920,
  maxHeight: 1920,
  quality: 0.8,
  format: 'image/jpeg',
  useWorker: true
}

/**
 * 压缩图片（主线程或 Worker）
 * @param file - 原始文件
 * @param options - 压缩选项
 * @returns Promise<string> - Base64 格式
 */
export function compressImage(
  file: File,
  options: CompressOptions = {}
): Promise<string> {
  const opts = { ...defaultOptions, ...options }
  
  if (opts.useWorker && typeof Worker !== 'undefined') {
    return compressImageWithWorker(file, opts)
  } else {
    return compressImageInMain(file, opts)
  }
}

/**
 * 使用 Web Worker 压缩图片
 */
function compressImageWithWorker(
  file: File,
  options: CompressOptions
): Promise<string> {
  return new Promise((resolve, reject) => {
    // 创建 Worker
    const worker = new Worker(
      new URL('./imageCompress.worker.ts', import.meta.url),
      { type: 'module' }
    )
    
    // 读取文件为 Base64
    const reader = new FileReader()
    reader.onload = (e) => {
      const base64 = e.target?.result as string
      
      // 发送压缩任务到 Worker
      worker.postMessage({
        type: 'compress',
        id: Date.now().toString(),
        base64: base64,
        options: {
          maxWidth: options.maxWidth!,
          maxHeight: options.maxHeight!,
          quality: options.quality!,
          format: options.format!
        }
      })
    }
    
    reader.onerror = () => reject(new Error('文件读取失败'))
    reader.readAsDataURL(file)
    
    // 监听 Worker 结果
    worker.onmessage = (e) => {
      const result = e.data
      
      if (result.success) {
        resolve(result.base64!)
      } else {
        reject(new Error(result.error || '压缩失败'))
      }
      
      worker.terminate()
    }
    
    worker.onerror = (error) => {
      reject(new Error('Worker 错误：' + error.message))
      worker.terminate()
    }
  })
}

/**
 * 在主线程压缩图片（兼容不支持 Worker 的环境）
 */
function compressImageInMain(
  file: File,
  options: CompressOptions
): Promise<string> {
  return new Promise((resolve, reject) => {
    const opts = { ...defaultOptions, ...options }
    
    const reader = new FileReader()
    reader.onload = (e) => {
      const img = new Image()
      img.onload = () => {
        // 计算缩放后的尺寸
        let width = img.width
        let height = img.height
        
        const scale = Math.min(
          opts.maxWidth! / width,
          opts.maxHeight! / height,
          1
        )
        
        if (scale < 1) {
          width = Math.round(width * scale)
          height = Math.round(height * scale)
        }
        
        // 创建 Canvas
        const canvas = document.createElement('canvas')
        canvas.width = width
        canvas.height = height
        
        const ctx = canvas.getContext('2d')
        if (!ctx) {
          reject(new Error('无法获取 Canvas 上下文'))
          return
        }
        
        // 绘制图片
        ctx.drawImage(img, 0, 0, width, height)
        
        // 压缩并输出
        const compressedDataUrl = canvas.toDataURL(opts.format, opts.quality)
        resolve(compressedDataUrl)
      }
      
      img.onerror = () => {
        reject(new Error('图片加载失败'))
      }
      
      if (e.target?.result) {
        img.src = e.target.result as string
      }
    }
    
    reader.onerror = () => {
      reject(new Error('文件读取失败'))
    }
    
    reader.readAsDataURL(file)
  })
}

/**
 * 压缩 Base64 图片
 * @param base64 - 原始 Base64
 * @param options - 压缩选项
 * @returns Promise<string> - 压缩后的 Base64
 */
export function compressBase64(
  base64: string,
  options: CompressOptions = {}
): Promise<string> {
  return new Promise((resolve, reject) => {
    const opts = { ...defaultOptions, ...options }
    
    const img = new Image()
    img.onload = () => {
      // 计算缩放后的尺寸
      let width = img.width
      let height = img.height
      
      if (width > height) {
        if (width > opts.maxWidth!) {
          height = Math.round(height * opts.maxWidth! / width)
          width = opts.maxWidth!
        }
      } else {
        if (height > opts.maxHeight!) {
          width = Math.round(width * opts.maxHeight! / height)
          height = opts.maxHeight!
        }
      }
      
      // 创建 Canvas
      const canvas = document.createElement('canvas')
      canvas.width = width
      canvas.height = height
      
      const ctx = canvas.getContext('2d')
      if (!ctx) {
        reject(new Error('无法获取 Canvas 上下文'))
        return
      }
      
      // 绘制图片
      ctx.drawImage(img, 0, 0, width, height)
      
      // 压缩并输出
      const compressedDataUrl = canvas.toDataURL(opts.format, opts.quality)
      resolve(compressedDataUrl)
    }
    
    img.onerror = () => {
      reject(new Error('图片加载失败'))
    }
    
    img.src = base64
  })
}

/**
 * 计算压缩率
 * @param originalSize - 原始大小 (字节)
 * @param compressedSize - 压缩后大小 (字节)
 * @returns 压缩率百分比
 */
export function getCompressionRate(originalSize: number, compressedSize: number): number {
  if (originalSize === 0) return 0
  return Math.round((1 - compressedSize / originalSize) * 100)
}

/**
 * 获取 Base64 字符串大小 (字节)
 */
export function getBase64Size(base64: string): number {
  const pureBase64 = base64.split(',')[1] || base64
  return Math.round(pureBase64.length * 3 / 4)
}

/**
 * 格式化文件大小
 */
export function formatFileSize(bytes: number): string {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB'
}

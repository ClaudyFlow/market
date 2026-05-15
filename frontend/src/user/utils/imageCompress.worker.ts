/**
 * 图片压缩 Web Worker
 * 在后台线程中压缩图片，不阻塞主线程 UI
 */

interface CompressOptions {
  maxWidth: number
  maxHeight: number
  quality: number
  format: string
}

interface CompressMessage {
  type: 'compress'
  id: string
  base64: string
  options: CompressOptions
}

interface CompressResult {
  id: string
  success: boolean
  base64?: string
  error?: string
  originalSize?: number
  compressedSize?: number
  compressionRate?: number
}

// 监听主线程消息
self.onmessage = async (event: MessageEvent<CompressMessage>) => {
  const { id, base64, options } = event.data
  
  try {
    const result = await compressImageInWorker(base64, options)
    
    const response: CompressResult = {
      id,
      success: true,
      base64: result.base64,
      originalSize: result.originalSize,
      compressedSize: result.compressedSize,
      compressionRate: result.compressionRate
    }
    
    self.postMessage(response)
  } catch (error: any) {
    self.postMessage({
      id,
      success: false,
      error: error.message
    } as CompressResult)
  }
}

/**
 * 在 Worker 中压缩图片
 */
async function compressImageInWorker(
  base64: string,
  options: CompressOptions
): Promise<{
  base64: string
  originalSize: number
  compressedSize: number
  compressionRate: number
}> {
  return new Promise((resolve, reject) => {
    const img = new Image()
    
    img.onload = async () => {
      try {
        // 计算缩放后的尺寸
        let width = img.width
        let height = img.height
        
        const scale = Math.min(
          options.maxWidth / width,
          options.maxHeight / height,
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
        const compressedDataUrl = canvas.toDataURL(options.format, options.quality)
        
        // 计算大小
        const originalSize = getBase64Size(base64)
        const compressedSize = getBase64Size(compressedDataUrl)
        const compressionRate = Math.round((1 - compressedSize / originalSize) * 100)
        
        resolve({
          base64: compressedDataUrl,
          originalSize,
          compressedSize,
          compressionRate
        })
      } catch (error: any) {
        reject(error)
      }
    }
    
    img.onerror = () => {
      reject(new Error('图片加载失败'))
    }
    
    img.src = base64
  })
}

/**
 * 计算 Base64 大小
 */
function getBase64Size(base64: string): number {
  const pureBase64 = base64.split(',')[1] || base64
  return Math.round(pureBase64.length * 3 / 4)
}

// 导出类型
export {}

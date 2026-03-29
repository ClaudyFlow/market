/**
 * 图片解压/解码工具类
 * 用于处理从后端获取的 Base64 图片数据
 */

/**
 * 解码 Base64 图片为 Blob
 * @param base64 - Base64 字符串
 * @returns Blob 对象
 */
export function base64ToBlob(base64: string): Blob {
  const parts = base64.split(',')
  const mime = parts[0].match(/:(.*?);/)![1]
  const bstr = atob(parts[1])
  let n = bstr.length
  const u8arr = new Uint8Array(n)
  
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n)
  }
  
  return new Blob([u8arr], { type: mime })
}

/**
 * 解码 Base64 图片为 File
 * @param base64 - Base64 字符串
 * @param filename - 文件名
 * @returns File 对象
 */
export function base64ToFile(base64: string, filename: string): File {
  const blob = base64ToBlob(base64)
  return new File([blob], filename, {
    type: blob.type
  })
}

/**
 * 从 Base64 获取图片信息
 * @param base64 - Base64 字符串
 * @returns 图片信息（尺寸、格式、大小）
 */
export function getImageInfo(base64: string): Promise<{
  width: number
  height: number
  format: string
  size: number
}> {
  return new Promise((resolve, reject) => {
    const img = new Image()
    
    img.onload = () => {
      const parts = base64.split(',')
      const mime = parts[0].match(/:(.*?);/)![1]
      const size = Math.round(parts[1].length * 3 / 4)
      
      resolve({
        width: img.width,
        height: img.height,
        format: mime,
        size: size
      })
    }
    
    img.onerror = () => {
      reject(new Error('图片加载失败'))
    }
    
    img.src = base64
  })
}

/**
 * 将 Base64 图片绘制到 Canvas 并导出为新格式
 * @param base64 - 原始 Base64
 * @param format - 目标格式 (image/jpeg, image/png, image/webp)
 * @param quality - 质量 (0.1-1.0)
 * @returns 新的 Base64
 */
export function convertImageFormat(
  base64: string,
  format: string = 'image/jpeg',
  quality: number = 0.9
): Promise<string> {
  return new Promise((resolve, reject) => {
    const img = new Image()
    
    img.onload = () => {
      const canvas = document.createElement('canvas')
      canvas.width = img.width
      canvas.height = img.height
      
      const ctx = canvas.getContext('2d')
      if (!ctx) {
        reject(new Error('无法获取 Canvas 上下文'))
        return
      }
      
      ctx.drawImage(img, 0, 0)
      
      const newBase64 = canvas.toDataURL(format, quality)
      resolve(newBase64)
    }
    
    img.onerror = () => {
      reject(new Error('图片加载失败'))
    }
    
    img.src = base64
  })
}

/**
 * 下载 Base64 图片
 * @param base64 - Base64 字符串
 * @param filename - 下载的文件名
 */
export function downloadBase64Image(base64: string, filename: string = 'image.jpg') {
  const link = document.createElement('a')
  link.href = base64
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

/**
 * 预览 Base64 图片（在新窗口打开）
 * @param base64 - Base64 字符串
 */
export function previewBase64Image(base64: string) {
  const win = window.open('')
  if (win) {
    win.document.write(`
      <!DOCTYPE html>
      <html>
        <head><title>图片预览</title></head>
        <body style="margin:0;display:flex;justify-content:center;align-items:center;min-height:100vh;background:#000;">
          <img src="${base64}" style="max-width:100%;max-height:100vh;" />
        </body>
      </html>
    `)
  }
}

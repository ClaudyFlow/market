/**
 * 文件上传相关 API
 */

import { upload } from './request'

const BASE_URL = '/upload'

/**
 * 上传图片
 */
export function uploadImage(file: File, type?: 'avatar' | 'product' | 'review' | 'forum'): Promise<{ url: string; filename: string }> {
  return upload(`${BASE_URL}/image`, file, { type })
}

/**
 * 批量上传图片
 */
export function uploadImages(files: File[], type?: string): Promise<{ url: string; filename: string }[]> {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file)
  })
  if (type) {
    formData.append('type', type)
  }
  
  return upload(`${BASE_URL}/images`, formData as any) as any
}

/**
 * 上传视频
 */
export function uploadVideo(file: File): Promise<{ url: string; filename: string; duration?: number }> {
  return upload(`${BASE_URL}/video`, file)
}

/**
 * 上传文件
 */
export function uploadFile(file: File): Promise<{ url: string; filename: string; size: number }> {
  return upload(`${BASE_URL}/file`, file)
}

/**
 * 获取上传凭证（用于直传 OSS）
 */
export function getUploadToken(type?: string): Promise<{ token: string; uploadUrl: string; bucket: string }> {
  return upload(`${BASE_URL}/token`, {}, { type })
}

/**
 * 前端环境类型定义
 */

// Node.js 环境
declare namespace NodeJS {
  interface ProcessEnv {
    NODE_ENV: 'development' | 'production' | 'test'
    VITE_API_BASE_URL?: string
    VITE_WS_URL?: string
  }
}

// Vite 环境变量类型
interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string
  readonly VITE_WS_URL: string
  readonly DEV: boolean
  readonly PROD: boolean
  readonly MODE: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

// 第三方库类型扩展
declare module '@stomp/stompjs'
declare module 'vue-logger-plugin'
declare module 'pinia-plugin-logger'

// 图片资源类型
declare module '*.png'
declare module '*.jpg'
declare module '*.jpeg'
declare module '*.gif'
declare module '*.svg'
declare module '*.webp'

// 样式资源类型
declare module '*.css'
declare module '*.scss'
declare module '*.less'

// Web Worker 类型
declare module '*.worker.ts' {
  class WorkerConstructor extends Worker {
    constructor()
  }
  export default WorkerConstructor
}

// ECharts 类型扩展
declare module 'echarts' {
  export * from 'echarts/types/dist/echarts'
}

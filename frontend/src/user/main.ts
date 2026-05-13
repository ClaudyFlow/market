import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginLogger from 'pinia-plugin-logger'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import { lazyload } from './directives/lazyload'
import createLogger, { setupHttpLogger, setupGlobalErrorLogger } from '@/utils/logger'
import axios from 'axios'

const app = createApp(App)
const pinia = createPinia()

// 使用 Pinia 日志插件
pinia.use(piniaPluginLogger)

// 初始化应用日志 (1024 条缓冲，禁用上传到后端)
const appLogger = createLogger('APP', {
  bufferSize: 1024,
  flushInterval: 0,
  apiEndpoint: ''
})

appLogger.info('应用启动')

// 注册懒加载指令
app.directive('lazyload', lazyload)

app.use(pinia)
app.use(router)
app.use(ElementPlus)

// 设置 HTTP 日志拦截器
setupHttpLogger(axios, appLogger)

// 设置全局错误捕获
setupGlobalErrorLogger(appLogger)

appLogger.info('应用初始化完成')

app.mount('#app')

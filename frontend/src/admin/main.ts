import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginLogger from 'pinia-plugin-logger'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import createLogger, { setupHttpLogger, setupGlobalErrorLogger } from '@/utils/logger'
import axios from 'axios'

const app = createApp(App)
const pinia = createPinia()

// 使用 Pinia 日志插件
pinia.use(piniaPluginLogger)

// 初始化应用日志 (1024 条缓冲)
const appLogger = createLogger('ADMIN', {
  bufferSize: 1024,
  flushInterval: 10000,
  apiEndpoint: '/api/log'
})

appLogger.info('管理端启动')

app.use(pinia)
app.use(router)
app.use(ElementPlus)

setupHttpLogger(axios, appLogger)
setupGlobalErrorLogger(appLogger)

appLogger.info('管理端初始化完成')

app.mount('#app')

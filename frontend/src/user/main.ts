import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import { lazyload } from './directives/lazyload'

const app = createApp(App)
const pinia = createPinia()

// 注册懒加载指令
app.directive('lazyload', lazyload)

app.use(pinia)
app.use(router)
app.use(ElementPlus)

app.mount('#app')

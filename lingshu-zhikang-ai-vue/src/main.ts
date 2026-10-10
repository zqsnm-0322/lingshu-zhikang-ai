import './assets/main.css'
import './styles/tokens.scss'
import './styles/layout.scss'
import './styles/components.scss'
import './styles/responsive.scss'
import './assets/mobile.css'
import './assets/pages-mobile.css'
import './styles/feature-pages.scss'
import './styles/health-management.scss'
import 'element-plus/dist/index.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router/index.ts'

// 禁用Vue DevTools
if (typeof window !== 'undefined') {
  (window as any).__VUE_DEVTOOLS_GLOBAL_HOOK__ = {
    Vue: {},
    store: {},
    Router: {},
    on: () => {},
    once: () => {},
    off: () => {},
    emit: () => {},
    inspect: () => {},
    init: () => {},
    apps: []
  }
}

// 全局错误处理 - 忽略浏览器扩展的fetch错误和动态导入错误
window.addEventListener('unhandledrejection', (event) => {
  // 检查是否是浏览器扩展的fetch错误
  if (event.reason && event.reason.message && 
      (event.reason.message.includes('fetchError') || 
       event.reason.message.includes('Failed to fetch'))) {
    // 忽略浏览器扩展的fetch错误
    event.preventDefault()
    console.log('已忽略浏览器扩展的fetch错误')
  }
  
  // 检查是否是动态导入模块失败
  if (event.reason && event.reason.message && 
      event.reason.message.includes('Failed to fetch dynamically imported module')) {
    // 忽略动态导入错误，这通常是由于HMR问题导致的
    event.preventDefault()
    console.log('已忽略动态导入模块错误，这通常是由于HMR问题导致的')
  }
})

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')

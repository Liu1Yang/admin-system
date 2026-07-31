import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import { setRouter } from './utils/routerHolder'
import './style.css'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component) // 全局配置 以后可以直接将elementplus的图标作为组件
}

setRouter(router)
app.use(ElementPlus).use(router).mount('#app')

import axios from 'axios'
import { ElMessage } from 'element-plus'
import { clearAuth } from '../utils/auth'
import { getRouter } from '../utils/routerHolder'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 15000
})

function redirectToLogin() {
  clearAuth()
  const router = getRouter()
  if (!router) return

  const { path, fullPath } = router.currentRoute.value
  if (path === '/login') return

  router.push({
    path: '/login',
    query: { redirect: fullPath }
  })
}

request.interceptors.request.use((config) => {   // “发件前检查站”
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(   // “收件后处理站”
  (response) => {
    const result = response.data
    if (result.code === 401) {
      ElMessage.error(result.message || '登录已失效，请重新登录')
      redirectToLogin()
      return Promise.reject(new Error(result.message || '未登录'))
    }
    if (result.code !== 200) {
      ElMessage.error(result.message || '请求失败')
      return Promise.reject(new Error(result.message || '请求失败'))
    }
    return result
  },
  (error) => {
    const message = error.response?.data?.message || error.message || '网络错误'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request

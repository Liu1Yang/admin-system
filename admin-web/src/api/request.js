import axios from 'axios'
import { ElMessage } from 'element-plus'
import { clearAuth, getRefreshToken, getToken, setTokens } from '../utils/auth'
import { getRouter } from '../utils/routerHolder'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 15000
})

/** 刷新 Token 专用，不走响应拦截器，避免死循环 */
const rawRequest = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 15000
})

let isRefreshing = false
let pendingQueue = []

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

function subscribeTokenRefresh(callback) {
  pendingQueue.push(callback)
}

function onTokenRefreshed(newToken) {
  pendingQueue.forEach((cb) => cb(newToken))
  pendingQueue = []
}

async function tryRefreshToken() {
  const refresh = getRefreshToken()
  if (!refresh) {
    throw new Error('无 Refresh Token')
  }
  const res = await rawRequest.post('/api/auth/refresh', { refreshToken: refresh })
  const result = res.data
  if (result.code !== 200) {
    throw new Error(result.message || '刷新失败')
  }
  const { accessToken, refreshToken: newRefresh } = result.data
  setTokens(accessToken, newRefresh)
  return accessToken
}

request.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  async (response) => {
    const result = response.data
    const originalConfig = response.config

    if (result.code !== 401) {
      if (result.code === 429) {
        ElMessage.warning(result.message || '请求过于频繁，请稍后再试')
        return Promise.reject(new Error(result.message || '请求过于频繁'))
      }
      if (result.code !== 200) {
        ElMessage.error(result.message || '请求失败')
        return Promise.reject(new Error(result.message || '请求失败'))
      }
      return result
    }

    if (originalConfig.url?.includes('/api/auth/refresh') || originalConfig._retry) {
      ElMessage.error(result.message || '登录已失效，请重新登录')
      redirectToLogin()
      return Promise.reject(new Error(result.message || '未登录'))
    }

    if (isRefreshing) {
      return new Promise((resolve) => {
        subscribeTokenRefresh((newToken) => {
          originalConfig.headers.Authorization = `Bearer ${newToken}`
          originalConfig._retry = true
          resolve(request(originalConfig))
        })
      })
    }

    isRefreshing = true
    try {
      const newToken = await tryRefreshToken()
      onTokenRefreshed(newToken)
      originalConfig.headers.Authorization = `Bearer ${newToken}`
      originalConfig._retry = true
      return request(originalConfig)
    } catch (e) {
      ElMessage.error('登录已失效，请重新登录')
      redirectToLogin()
      return Promise.reject(e)
    } finally {
      isRefreshing = false
    }
  },
  (error) => {
    const message = error.response?.data?.message || error.message || '网络错误'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request

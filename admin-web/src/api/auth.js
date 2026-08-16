import request from './request'
import { getRefreshToken } from '../utils/auth'

export function login(data) {
  return request.post('/api/auth/login', data)
}

export function refreshToken(refreshTokenValue) {
  return request.post('/api/auth/refresh', { refreshToken: refreshTokenValue })
}

export function getCurrentUser() {
  return request.get('/api/auth/me')
}

export function logout() {
  const refreshTokenValue = getRefreshToken()
  return request.post('/api/auth/logout', refreshTokenValue ? { refreshToken: refreshTokenValue } : {})
}

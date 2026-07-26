import request from './request'

export function login(data) {
  return request.post('/api/auth/login', data)
}

export function getCurrentUser() {
  return request.get('/api/auth/me')
}

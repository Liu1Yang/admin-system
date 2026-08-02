import request from './request'

export function getRoleList() {
  return request.get('/api/roles')
}

export function createRole(data) {
  return request.post('/api/roles', data)
}

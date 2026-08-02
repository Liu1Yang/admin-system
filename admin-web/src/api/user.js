import request from './request'

export function getUserPage(params) {
  return request.get('/api/users', { params })
}

export function deleteUser(id) {
  return request.delete(`/api/users/${id}`)
}

export function getUserRoles(userId) {
  return request.get(`/api/users/${userId}/roles`)
}

export function assignUserRoles(userId, roleIds) {
  return request.post(`/api/users/${userId}/roles`, { roleIds })
}

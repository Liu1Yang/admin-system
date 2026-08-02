import request from './request'

export function getUserPage(params) {
  return request.get('/api/users', { params })
}

export function deleteUser(id) {
  return request.delete(`/api/users/${id}`)
}

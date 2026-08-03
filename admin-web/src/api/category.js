import request from './request'

export function getCategoryTree() {
  return request.get('/api/categories/tree')
}

export function createCategory(data) {
  return request.post('/api/categories', data)
}

export function updateCategory(id, data) {
  return request.put(`/api/categories/${id}`, data)
}

export function deleteCategory(id) {
  return request.delete(`/api/categories/${id}`)
}

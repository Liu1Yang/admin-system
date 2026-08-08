import request from './request'

export function getProductPage(params) {
  return request.get('/api/products', { params })
}

export function getProductById(id) {
  return request.get(`/api/products/${id}`)
}

export function createProduct(data) {
  return request.post('/api/products', data)
}

export function updateProduct(id, data) {
  return request.put(`/api/products/${id}`, data)
}

export function deleteProduct(id) {
  return request.delete(`/api/products/${id}`)
}

export function updateProductStatus(id, status) {
  return request.put(`/api/products/${id}/status`, { status })
}

export function uploadProductCover(id, file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post(`/api/products/${id}/cover`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

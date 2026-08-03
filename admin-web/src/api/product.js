import request from './request'

export function getProductPage(params) {
  return request.get('/api/products', { params })
}

export function deleteProduct(id) {
  return request.delete(`/api/products/${id}`)
}

export function updateProductStatus(id, status) {
  return request.put(`/api/products/${id}/status`, { status })
}

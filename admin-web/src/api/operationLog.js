import request from './request'

export function getOperationLogPage(params) {
  return request.get('/api/operation-logs', { params })
}

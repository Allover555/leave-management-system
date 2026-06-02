import request from '../utils/request'

export function getOverview(params) {
  return request.get('/statistics/overview', { params })
}

export function exportRecords() {
  return request.get('/statistics/export', { responseType: 'blob' })
}

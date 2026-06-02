import request from '../utils/request'

export function applyLeave(data, files) {
  const formData = new FormData()
  formData.append('data', new Blob([JSON.stringify(data)], { type: 'application/json' }))
  if (files && files.length > 0) {
    files.forEach(file => formData.append('files', file))
  }
  return request.post('/leave/apply', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function getMyLeaves(params) {
  return request.get('/leave/my', { params })
}

export function getLeaveDetail(id) {
  return request.get(`/leave/${id}`)
}

export function cancelLeave(id) {
  return request.post(`/leave/${id}/cancel`)
}

export function approveLeave(id, data) {
  return request.post(`/leave/${id}/approve`, data)
}

export function getPendingApprovals(params) {
  return request.get('/leave/pending', { params })
}

export function getAllLeaves(params) {
  return request.get('/leave/all', { params })
}

export function getClassLeaves(params) {
  return request.get('/leave/class', { params })
}

export function submitCancellation(leaveId, data) {
  return request.post(`/leave/${leaveId}/cancellation`, data)
}

export function reviewCancellation(id, data) {
  return request.post(`/leave/cancellation/${id}/review`, data)
}

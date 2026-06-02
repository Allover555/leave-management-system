import request from '../utils/request'

export function getCurrentUser() {
  return request.get('/user/current')
}

export function getUserById(id) {
  return request.get(`/user/${id}`)
}

export function listUsers(params) {
  return request.get('/user/list', { params })
}

export function updateUser(id, data) {
  return request.put(`/user/${id}`, data)
}

export function updateCurrentUser(data) {
  return request.put('/user/update', data)
}

export function changePassword(data) {
  return request.post('/user/password', data)
}

export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/user/avatar', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function deleteUser(id) {
  return request.delete(`/user/${id}`)
}

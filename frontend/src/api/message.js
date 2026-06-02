import request from '../utils/request'

export function getMessages(params) {
  return request.get('/message/list', { params })
}

export function getUnreadCount() {
  return request.get('/message/unread-count')
}

export function markAsRead(id) {
  return request.post(`/message/${id}/read`)
}

export function markAllAsRead() {
  return request.post('/message/read-all')
}

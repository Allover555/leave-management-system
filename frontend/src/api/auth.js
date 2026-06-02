import request from '../utils/request'

export function login(data) {
  return request.post('/auth/login', data)
}

export function register(data) {
  return request.post('/auth/register', data)
}

export function getRoles() {
  return request.get('/auth/roles')
}

export function getDepartments() {
  return request.get('/auth/departments')
}

export function getClasses(departmentId) {
  return request.get('/auth/classes', { params: { departmentId } })
}

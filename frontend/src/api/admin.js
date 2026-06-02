import request from '../utils/request'

export function getAllRoles() {
  return request.get('/admin/roles')
}

export function createRole(data) {
  return request.post('/admin/roles', data)
}

export function updateRole(id, data) {
  return request.put(`/admin/roles/${id}`, data)
}

export function getLeaveTypes() {
  return request.get('/admin/leave-types')
}

export function createLeaveType(data) {
  return request.post('/admin/leave-types', data)
}

export function updateLeaveType(id, data) {
  return request.put(`/admin/leave-types/${id}`, data)
}

export function deleteLeaveType(id) {
  return request.delete(`/admin/leave-types/${id}`)
}

export function getFlows() {
  return request.get('/admin/flows')
}

export function createFlow(data) {
  return request.post('/admin/flows', data)
}

export function updateFlow(id, data) {
  return request.put(`/admin/flows/${id}`, data)
}

export function deleteFlow(id) {
  return request.delete(`/admin/flows/${id}`)
}

export function getDepartments() {
  return request.get('/admin/departments')
}

export function createDepartment(data) {
  return request.post('/admin/departments', data)
}

export function getClasses(departmentId) {
  return request.get('/admin/classes', { params: { departmentId } })
}

export function getCounselorClasses(counselorId) {
  return request.get(`/admin/counselor-classes/${counselorId}`)
}

export function assignCounselorClass(data) {
  return request.post('/admin/counselor-classes', data)
}

export function getCancellationRules() {
  return request.get('/admin/cancellation-rules')
}

export function saveCancellationRule(data) {
  return request.post('/admin/cancellation-rules', data)
}

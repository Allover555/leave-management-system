import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCurrentUser } from '../api/user'
import { getUnreadCount } from '../api/message'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  const token = ref(localStorage.getItem('token') || '')
  const unreadCount = ref(0)

  const isLoggedIn = computed(() => !!token.value)
  const roleCode = computed(() => userInfo.value?.roleCode || '')
  const isStudent = computed(() => roleCode.value === 'STUDENT')
  const isCounselor = computed(() => roleCode.value === 'COUNSELOR')
  const isAdmin = computed(() => roleCode.value === 'ADMIN')
  const isDeptAdmin = computed(() => roleCode.value === 'DEPT_ADMIN')

  function setLogin(data) {
    token.value = data.token
    userInfo.value = data
    localStorage.setItem('token', data.token)
    localStorage.setItem('userInfo', JSON.stringify(data))
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  async function fetchUserInfo() {
    try {
      const res = await getCurrentUser()
      const info = { ...userInfo.value, ...res.data }
      userInfo.value = info
      localStorage.setItem('userInfo', JSON.stringify(info))
    } catch (e) { /* ignore */ }
  }

  async function fetchUnreadCount() {
    try {
      const res = await getUnreadCount()
      unreadCount.value = res.data || 0
    } catch (e) { /* ignore */ }
  }

  return {
    userInfo, token, unreadCount,
    isLoggedIn, roleCode, isStudent, isCounselor, isAdmin, isDeptAdmin,
    setLogin, logout, fetchUserInfo, fetchUnreadCount
  }
})

import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('../layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue'),
        meta: { title: '个人信息' }
      },
      {
        path: 'messages',
        name: 'Messages',
        component: () => import('../views/Messages.vue'),
        meta: { title: '消息通知' }
      },
      // 学生端
      {
        path: 'leave/apply',
        name: 'LeaveApply',
        component: () => import('../views/student/LeaveApply.vue'),
        meta: { title: '请假申请', role: 'STUDENT' }
      },
      {
        path: 'leave/records',
        name: 'LeaveRecords',
        component: () => import('../views/student/LeaveRecords.vue'),
        meta: { title: '请假记录', role: 'STUDENT' }
      },
      {
        path: 'leave/detail/:id',
        name: 'LeaveDetail',
        component: () => import('../views/student/LeaveDetail.vue'),
        meta: { title: '请假详情' }
      },
      // 辅导员端
      {
        path: 'approval/pending',
        name: 'ApprovalPending',
        component: () => import('../views/counselor/ApprovalPending.vue'),
        meta: { title: '待审批', role: 'COUNSELOR' }
      },
      {
        path: 'approval/records',
        name: 'ApprovalRecords',
        component: () => import('../views/counselor/ApprovalRecords.vue'),
        meta: { title: '审批记录', role: 'COUNSELOR' }
      },
      {
        path: 'approval/statistics',
        name: 'ClassStatistics',
        component: () => import('../views/counselor/ClassStatistics.vue'),
        meta: { title: '班级统计', role: 'COUNSELOR' }
      },
      // 管理员端
      {
        path: 'admin/users',
        name: 'UserManagement',
        component: () => import('../views/admin/UserManagement.vue'),
        meta: { title: '用户管理', role: 'ADMIN' }
      },
      {
        path: 'admin/leave-types',
        name: 'LeaveTypeManagement',
        component: () => import('../views/admin/LeaveTypeManagement.vue'),
        meta: { title: '假别管理', role: 'ADMIN' }
      },
      {
        path: 'admin/flows',
        name: 'FlowManagement',
        component: () => import('../views/admin/FlowManagement.vue'),
        meta: { title: '审批流程', role: 'ADMIN' }
      },
      {
        path: 'admin/classes',
        name: 'ClassManagement',
        component: () => import('../views/admin/ClassManagement.vue'),
        meta: { title: '班级管理', role: 'ADMIN' }
      },
      {
        path: 'admin/statistics',
        name: 'AdminStatistics',
        component: () => import('../views/admin/AdminStatistics.vue'),
        meta: { title: '数据统计', role: 'ADMIN' }
      },
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.public) {
    if (token && (to.name === 'Login' || to.name === 'Register')) {
      next('/dashboard')
    } else {
      next()
    }
  } else {
    if (!token) {
      next('/login')
    } else {
      next()
    }
  }
})

export default router

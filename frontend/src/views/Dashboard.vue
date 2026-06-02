<template>
  <div class="dashboard">
    <div class="welcome-section glass-card">
      <div class="welcome-text">
        <h2>👋 你好，{{ userStore.userInfo?.realName }}</h2>
        <p>欢迎使用高校学生请假管理系统</p>
      </div>
      <div class="welcome-info">
        <el-tag :type="roleTagType" size="large" effect="dark" round>{{ userStore.userInfo?.roleName }}</el-tag>
      </div>
    </div>

    <!-- 学生端快捷操作 -->
    <div v-if="userStore.isStudent" class="quick-actions">
      <div class="neu-card action-card" @click="$router.push('/leave/apply')">
        <el-icon :size="36" color="#2563eb"><EditPen /></el-icon>
        <h3>请假申请</h3>
        <p>提交新的请假申请</p>
      </div>
      <div class="neu-card action-card" @click="$router.push('/leave/records')">
        <el-icon :size="36" color="#10b981"><Document /></el-icon>
        <h3>请假记录</h3>
        <p>查看历史请假记录</p>
      </div>
      <div class="neu-card action-card" @click="$router.push('/messages')">
        <el-icon :size="36" color="#f59e0b"><Bell /></el-icon>
        <h3>消息通知</h3>
        <p>{{ userStore.unreadCount }}条未读消息</p>
      </div>
      <div class="neu-card action-card" @click="$router.push('/profile')">
        <el-icon :size="36" color="#3b82f6"><User /></el-icon>
        <h3>个人信息</h3>
        <p>查看和修改个人资料</p>
      </div>
    </div>

    <!-- 辅导员/管理员快捷操作 -->
    <div v-else class="quick-actions">
      <div class="neu-card action-card" @click="$router.push('/approval/pending')">
        <el-icon :size="36" color="#ef4444"><Stamp /></el-icon>
        <h3>待审批</h3>
        <p>处理待审批的请假申请</p>
      </div>
      <div class="neu-card action-card" @click="$router.push('/approval/records')">
        <el-icon :size="36" color="#10b981"><Document /></el-icon>
        <h3>审批记录</h3>
        <p>查看历史审批记录</p>
      </div>
      <div class="neu-card action-card" @click="$router.push('/messages')">
        <el-icon :size="36" color="#f59e0b"><Bell /></el-icon>
        <h3>消息通知</h3>
        <p>{{ userStore.unreadCount }}条未读消息</p>
      </div>
      <div class="neu-card action-card" v-if="userStore.isAdmin" @click="$router.push('/admin/users')">
        <el-icon :size="36" color="#0ea5e9"><Setting /></el-icon>
        <h3>系统管理</h3>
        <p>管理用户、角色与配置</p>
      </div>
      <div class="neu-card action-card" v-if="userStore.isCounselor || userStore.isDeptAdmin" @click="$router.push('/approval/statistics')">
        <el-icon :size="36" color="#0ea5e9"><DataAnalysis /></el-icon>
        <h3>班级统计</h3>
        <p>查看班级请假统计</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()

const roleTagType = computed(() => {
  const map = { STUDENT: 'primary', COUNSELOR: 'success', DEPT_ADMIN: 'warning', ADMIN: 'danger' }
  return map[userStore.roleCode] || 'info'
})
</script>

<style scoped>
.dashboard { margin: 0 auto; }

.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32px;
  margin-bottom: 24px;
  background: linear-gradient(135deg, rgba(37,99,235,0.1), rgba(14,165,233,0.08));
}

.welcome-text h2 { font-size: 24px; color: #1e293b; margin-bottom: 6px; }
.welcome-text p { color: #64748b; font-size: 14px; }

.quick-actions {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
}

.action-card {
  text-align: center;
  padding: 32px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 12px 12px 24px #a3b1c6, -12px -12px 24px #ffffff;
}

.action-card h3 {
  margin: 16px 0 8px;
  font-size: 16px;
  color: #1e293b;
}

.action-card p {
  font-size: 13px;
  color: #94a3b8;
}
</style>

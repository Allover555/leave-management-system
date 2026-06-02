<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '240px'" class="layout-aside">
      <div class="logo-area" :class="{ collapsed: isCollapse }">
        <el-icon :size="28" color="#2563eb"><School /></el-icon>
        <span v-show="!isCollapse" class="logo-text">请假管理系统</span>
      </div>
      <el-menu
        :default-active="currentRoute"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        class="aside-menu"
        active-text-color="#2563eb"
        text-color="#6b7b8d"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <!-- 学生菜单 -->
        <template v-if="userStore.isStudent">
          <el-menu-item index="/leave/apply">
            <el-icon><EditPen /></el-icon>
            <span>请假申请</span>
          </el-menu-item>
          <el-menu-item index="/leave/records">
            <el-icon><Document /></el-icon>
            <span>请假记录</span>
          </el-menu-item>
        </template>

        <!-- 辅导员菜单 -->
        <template v-if="userStore.isCounselor || userStore.isDeptAdmin">
          <el-sub-menu index="approval">
            <template #title>
              <el-icon><Stamp /></el-icon>
              <span>审批管理</span>
            </template>
            <el-menu-item index="/approval/pending">待审批</el-menu-item>
            <el-menu-item index="/approval/records">审批记录</el-menu-item>
            <el-menu-item index="/approval/statistics">班级统计</el-menu-item>
          </el-sub-menu>
        </template>

        <!-- 管理员菜单 -->
        <template v-if="userStore.isAdmin">
          <el-sub-menu index="approval-admin">
            <template #title>
              <el-icon><Stamp /></el-icon>
              <span>审批管理</span>
            </template>
            <el-menu-item index="/approval/pending">待审批</el-menu-item>
            <el-menu-item index="/approval/records">全部记录</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="admin">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/admin/users">用户管理</el-menu-item>
            <el-menu-item index="/admin/classes">班级管理</el-menu-item>
            <el-menu-item index="/admin/leave-types">假别管理</el-menu-item>
            <el-menu-item index="/admin/flows">审批流程</el-menu-item>
            <el-menu-item index="/admin/statistics">数据统计</el-menu-item>
          </el-sub-menu>
        </template>

        <el-menu-item index="/messages">
          <el-icon><Bell /></el-icon>
          <template #title>
            <span class="msg-text-wrap">消息通知<el-badge v-if="userStore.unreadCount > 0" :value="userStore.unreadCount" class="msg-badge" /></span>
          </template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <el-header class="layout-header glass">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse" :size="20">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentMeta.title">{{ currentMeta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-badge :value="userStore.unreadCount" :hidden="!userStore.unreadCount" class="msg-icon">
            <el-icon :size="20" @click="$router.push('/messages')" style="cursor:pointer"><Bell /></el-icon>
          </el-badge>
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="36" :src="userStore.userInfo?.avatar" class="user-avatar">
                {{ userStore.userInfo?.realName?.charAt(0) }}
              </el-avatar>
              <span class="user-name">{{ userStore.userInfo?.realName }}</span>
              <el-tag size="small" :type="roleTagType" effect="plain">{{ userStore.userInfo?.roleName }}</el-tag>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人信息
                </el-dropdown-item>
                <el-dropdown-item command="password">
                  <el-icon><Lock /></el-icon>修改密码
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const isCollapse = ref(false)

const currentRoute = computed(() => route.path)
const currentMeta = computed(() => route.meta || {})

const roleTagType = computed(() => {
  const map = { STUDENT: 'primary', COUNSELOR: 'success', DEPT_ADMIN: 'warning', ADMIN: 'danger' }
  return map[userStore.roleCode] || 'info'
})

function handleCommand(cmd) {
  if (cmd === 'profile') router.push('/profile')
  else if (cmd === 'password') router.push('/profile')
  else if (cmd === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}

let pollTimer = null

onMounted(() => {
  if (userStore.isLoggedIn) {
    userStore.fetchUserInfo()
    userStore.fetchUnreadCount()
    pollTimer = setInterval(() => {
      if (userStore.isLoggedIn) userStore.fetchUnreadCount()
    }, 30000)
  }
})

onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer)
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
  overflow: hidden;
}

.layout-aside {
  background: linear-gradient(180deg, #e3e8ef 0%, #d8dde5 100%);
  box-shadow: 4px 0 12px rgba(0, 0, 0, 0.06);
  transition: width 0.3s ease;
  overflow-y: auto;
  overflow-x: hidden;
  z-index: 10;
}

.logo-area {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 0 16px;
  border-bottom: 1px solid rgba(163, 177, 198, 0.3);
}

.logo-area.collapsed {
  padding: 0;
}

.logo-text {
  font-size: 16px;
  font-weight: 700;
  color: #2c3e50;
  white-space: nowrap;
}

.aside-menu {
  border-right: none !important;
  background: transparent !important;
}

.aside-menu .el-menu-item,
.aside-menu .el-sub-menu__title {
  border-radius: 12px;
  margin: 4px 8px;
  height: 44px;
  line-height: 44px;
}

.aside-menu .el-menu-item:hover,
.aside-menu .el-sub-menu__title:hover {
  background: rgba(37, 99, 235, 0.08) !important;
}

.aside-menu .el-menu-item.is-active {
  background: rgba(37, 99, 235, 0.12) !important;
  font-weight: 600;
}

.layout-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  margin: 8px 12px 0;
  border-radius: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  cursor: pointer;
  color: #6b7b8d;
  transition: color 0.2s;
}

.collapse-btn:hover {
  color: #2563eb;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.msg-icon {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 12px;
  transition: background 0.2s;
}

.user-info:hover {
  background: rgba(37, 99, 235, 0.06);
}

.user-avatar {
  background: linear-gradient(135deg, #2563eb, #3b82f6);
  color: #fff;
  font-weight: 600;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
}

.layout-main {
  padding: 16px;
  overflow-y: auto;
  background: linear-gradient(135deg, #e0e5ec 0%, #d5dae2 100%);
}

.msg-text-wrap {
  position: relative;
  display: inline-block;
}

.msg-badge {
  position: absolute;
  top: -8px;
  right: -20px;
}

.msg-badge :deep(.el-badge__content) {
  font-size: 10px;
  height: 16px;
  line-height: 16px;
  padding: 0 4px;
}
</style>

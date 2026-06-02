<template>
  <div class="login-page">
    <!-- 左侧品牌区 -->
    <div class="brand-side">
      <div class="brand-bg">
        <div class="bg-shape s1"></div>
        <div class="bg-shape s2"></div>
        <div class="bg-shape s3"></div>
        <div class="bg-shape s4"></div>
      </div>
      <div class="brand-content">
        <div class="brand-icon">
          <svg viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="8" y="16" width="64" height="48" rx="6" stroke="white" stroke-width="3"/>
            <path d="M8 28H72" stroke="white" stroke-width="3"/>
            <circle cx="16" cy="22" r="2.5" fill="white"/>
            <circle cx="24" cy="22" r="2.5" fill="white"/>
            <circle cx="32" cy="22" r="2.5" fill="white"/>
            <rect x="18" y="36" width="20" height="3" rx="1.5" fill="white" opacity="0.8"/>
            <rect x="18" y="44" width="28" height="3" rx="1.5" fill="white" opacity="0.6"/>
            <rect x="18" y="52" width="16" height="3" rx="1.5" fill="white" opacity="0.4"/>
            <path d="M50 42L56 48L66 36" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h1>高校学生请假管理系统</h1>
        <p class="brand-subtitle">Student Leave Management System</p>
        <div class="brand-features">
          <div class="feature-item">
            <span class="feature-dot"></span>
            <span>在线提交请假申请</span>
          </div>
          <div class="feature-item">
            <span class="feature-dot"></span>
            <span>多级审批流程管理</span>
          </div>
          <div class="feature-item">
            <span class="feature-dot"></span>
            <span>实时消息通知提醒</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧表单区 -->
    <div class="form-side">
      <div class="form-container">
        <div class="form-header">
          <h2>欢迎登录</h2>
          <p>请输入您的账号信息</p>
        </div>
        <el-form ref="formRef" :model="form" :rules="rules" class="login-form" @submit.prevent="handleLogin">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="请输入学号/工号" prefix-icon="User" size="large" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" :loading="loading" @click="handleLogin" class="login-btn">
              登 录
            </el-button>
          </el-form-item>
        </el-form>
        <div class="login-footer">
          <span>还没有账号？</span>
          <el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { login } from '../api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入学号/工号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login(form)
    userStore.setLogin(res.data)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (e) { /* handled by interceptor */ }
  finally { loading.value = false }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
}

/* 左侧品牌区 */
.brand-side {
  flex: 1;
  background: linear-gradient(135deg, #1e3a5f 0%, #1a56db 50%, #2563eb 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: 40px;
}

.brand-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.12;
}

.s1 { width: 500px; height: 500px; background: #60a5fa; top: -150px; left: -150px; }
.s2 { width: 350px; height: 350px; background: #93c5fd; bottom: -100px; right: -80px; }
.s3 { width: 200px; height: 200px; background: #38bdf8; top: 60%; left: 10%; }
.s4 { width: 120px; height: 120px; background: #e0f2fe; top: 20%; right: 15%; }

.brand-content {
  position: relative;
  z-index: 1;
  color: #fff;
  max-width: 420px;
  animation: fadeInLeft 0.8s ease;
}

@keyframes fadeInLeft {
  from { transform: translateX(-30px); opacity: 0; }
  to { transform: translateX(0); opacity: 1; }
}

.brand-icon {
  width: 80px;
  height: 80px;
  margin-bottom: 32px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
}

.brand-icon svg {
  width: 48px;
  height: 48px;
}

.brand-content h1 {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 12px;
  letter-spacing: 1px;
  line-height: 1.3;
}

.brand-subtitle {
  font-size: 14px;
  opacity: 0.7;
  letter-spacing: 2px;
  margin-bottom: 48px;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  opacity: 0.9;
}

.feature-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #60a5fa;
  flex-shrink: 0;
  box-shadow: 0 0 8px rgba(96, 165, 250, 0.6);
}

/* 右侧表单区 */
.form-side {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f1f5f9;
  padding: 40px;
}

.form-container {
  width: 100%;
  max-width: 400px;
  animation: fadeInRight 0.8s ease;
}

@keyframes fadeInRight {
  from { transform: translateX(30px); opacity: 0; }
  to { transform: translateX(0); opacity: 1; }
}

.form-header {
  margin-bottom: 40px;
}

.form-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 8px;
}

.form-header p {
  font-size: 15px;
  color: #64748b;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 10px !important;
  height: 48px;
  background: #fff !important;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08) !important;
  border: 1px solid #e2e8f0 !important;
  transition: all 0.2s ease;
}

.login-form :deep(.el-input__wrapper:hover),
.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: #2563eb !important;
  box-shadow: 0 0 0 3px rgba(37,99,235,0.1) !important;
}

.login-form :deep(.el-input__prefix) {
  color: #94a3b8;
}

.login-btn {
  width: 100%;
  border-radius: 10px;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #2563eb, #1d4ed8) !important;
  border: none !important;
  letter-spacing: 4px;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 25px rgba(37,99,235,0.35);
}

.login-footer {
  text-align: center;
  font-size: 14px;
  color: #64748b;
  margin-top: 32px;
}

.login-footer :deep(.el-link) {
  font-weight: 600;
}

@media (max-width: 768px) {
  .login-page { flex-direction: column; }
  .brand-side { display: none; }
  .form-side { min-height: 100vh; }
}
</style>

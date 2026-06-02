<template>
  <div class="register-page">
    <!-- 左侧品牌区 -->
    <div class="brand-side">
      <div class="brand-bg">
        <div class="bg-shape s1"></div>
        <div class="bg-shape s2"></div>
        <div class="bg-shape s3"></div>
      </div>
      <div class="brand-content">
        <div class="brand-icon">
          <svg viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="40" cy="30" r="14" stroke="white" stroke-width="3"/>
            <path d="M16 64C16 50.745 26.745 40 40 40C53.255 40 64 50.745 64 64" stroke="white" stroke-width="3" stroke-linecap="round"/>
            <path d="M52 22L58 28L70 16" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h1>加入我们</h1>
        <p class="brand-subtitle">创建账号，开始使用请假管理系统</p>
        <div class="brand-features">
          <div class="feature-item"><span class="feature-dot"></span><span>快速在线注册</span></div>
          <div class="feature-item"><span class="feature-dot"></span><span>多角色身份支持</span></div>
          <div class="feature-item"><span class="feature-dot"></span><span>安全便捷管理</span></div>
        </div>
      </div>
    </div>

    <!-- 右侧表单区 -->
    <div class="form-side">
      <div class="form-container">
        <div class="form-header">
          <h2>用户注册</h2>
          <p>请填写以下信息完成注册</p>
        </div>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="register-form">
          <el-form-item label="学号/工号" prop="username">
            <el-input v-model="form.username" placeholder="请输入学号或工号" />
          </el-form-item>
          <el-form-item label="姓名" prop="realName">
            <el-input v-model="form.realName" placeholder="请输入真实姓名" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
          </el-form-item>
          <el-form-item label="角色" prop="roleId">
            <el-select v-model="form.roleId" placeholder="请选择角色" style="width:100%">
              <el-option v-for="r in roles" :key="r.id" :label="r.roleName" :value="r.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="form.gender">
              <el-radio :value="1">男</el-radio>
              <el-radio :value="2">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="院系">
            <el-select v-model="form.departmentId" placeholder="请选择院系" style="width:100%" @change="onDeptChange" clearable>
              <el-option v-for="d in departments" :key="d.id" :label="d.deptName" :value="d.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="班级" v-if="isStudentRole">
            <el-select v-model="form.classId" placeholder="请选择班级" style="width:100%" clearable>
              <el-option v-for="c in classes" :key="c.id" :label="c.className" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="handleRegister" class="register-btn">
              注 册
            </el-button>
          </el-form-item>
        </el-form>
        <div class="register-footer">
          <span>已有账号？</span>
          <el-link type="primary" @click="$router.push('/login')">返回登录</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { register, getRoles, getDepartments, getClasses } from '../api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const roles = ref([])
const departments = ref([])
const classes = ref([])

const form = reactive({
  username: '', password: '', realName: '', roleId: null,
  gender: 1, departmentId: null, classId: null, phone: ''
})

const rules = {
  username: [{ required: true, message: '请输入学号/工号', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }],
}

const isStudentRole = computed(() => {
  const role = roles.value.find(r => r.id === form.roleId)
  return role?.roleCode === 'STUDENT'
})

async function onDeptChange(val) {
  form.classId = null
  if (val) {
    const res = await getClasses(val)
    classes.value = res.data
  } else {
    classes.value = []
  }
}

async function handleRegister() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

onMounted(async () => {
  const [rolesRes, deptsRes] = await Promise.all([getRoles(), getDepartments()])
  roles.value = rolesRes.data
  departments.value = deptsRes.data
})
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
}

/* 左侧品牌区 */
.brand-side {
  width: 420px;
  flex-shrink: 0;
  background: linear-gradient(135deg, #1e3a5f 0%, #1a56db 50%, #2563eb 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: 40px;
}

.brand-bg { position: absolute; inset: 0; overflow: hidden; }
.bg-shape { position: absolute; border-radius: 50%; opacity: 0.12; }
.s1 { width: 400px; height: 400px; background: #60a5fa; top: -120px; left: -120px; }
.s2 { width: 300px; height: 300px; background: #93c5fd; bottom: -80px; right: -60px; }
.s3 { width: 180px; height: 180px; background: #38bdf8; top: 55%; left: 15%; }

.brand-content {
  position: relative;
  z-index: 1;
  color: #fff;
  max-width: 340px;
  animation: fadeInLeft 0.8s ease;
}

@keyframes fadeInLeft {
  from { transform: translateX(-30px); opacity: 0; }
  to { transform: translateX(0); opacity: 1; }
}

.brand-icon {
  width: 72px;
  height: 72px;
  margin-bottom: 28px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
}

.brand-icon svg { width: 44px; height: 44px; }

.brand-content h1 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 10px;
  line-height: 1.3;
}

.brand-subtitle {
  font-size: 14px;
  opacity: 0.7;
  letter-spacing: 1px;
  margin-bottom: 40px;
}

.brand-features { display: flex; flex-direction: column; gap: 14px; }

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  opacity: 0.9;
}

.feature-dot {
  width: 7px;
  height: 7px;
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
  overflow-y: auto;
}

.form-container {
  width: 100%;
  max-width: 480px;
  animation: fadeInRight 0.8s ease;
}

@keyframes fadeInRight {
  from { transform: translateX(30px); opacity: 0; }
  to { transform: translateX(0); opacity: 1; }
}

.form-header { margin-bottom: 32px; }

.form-header h2 {
  font-size: 26px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 6px;
}

.form-header p {
  font-size: 14px;
  color: #64748b;
}

.register-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.register-form :deep(.el-input__wrapper) {
  border-radius: 10px !important;
  background: #fff !important;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08) !important;
  border: 1px solid #e2e8f0 !important;
  transition: all 0.2s ease;
}

.register-form :deep(.el-input__wrapper:hover),
.register-form :deep(.el-input__wrapper.is-focus) {
  border-color: #2563eb !important;
  box-shadow: 0 0 0 3px rgba(37,99,235,0.1) !important;
}

.register-form :deep(.el-select .el-input__wrapper) {
  border-radius: 10px !important;
}

.register-form :deep(.el-form-item__label) {
  color: #334155 !important;
  font-weight: 500;
}

.register-btn {
  width: 100%;
  border-radius: 10px;
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #2563eb, #1d4ed8) !important;
  border: none !important;
  letter-spacing: 4px;
  transition: all 0.3s ease;
}

.register-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 25px rgba(37,99,235,0.35);
}

.register-footer {
  text-align: center;
  font-size: 14px;
  color: #64748b;
  margin-top: 24px;
}

.register-footer :deep(.el-link) {
  font-weight: 600;
}

@media (max-width: 768px) {
  .register-page { flex-direction: column; }
  .brand-side { display: none; }
  .form-side { min-height: 100vh; }
}
</style>

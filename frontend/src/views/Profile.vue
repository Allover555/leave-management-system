<template>
  <div class="profile-page">
    <div class="neu-card profile-card">
      <h2 class="page-title">个人信息</h2>
      <div class="avatar-section">
        <el-avatar :size="80" :src="userInfo?.avatar" class="profile-avatar">
          {{ userInfo?.realName?.charAt(0) }}
        </el-avatar>
        <el-upload action="#" :auto-upload="false" :show-file-list="false" @change="handleAvatarChange">
          <el-button size="small" type="primary">更换头像</el-button>
        </el-upload>
      </div>
      <el-form :model="form" label-width="100px" class="profile-form">
        <el-form-item label="学号/工号">
          <el-input :model-value="userInfo?.username" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="角色">
          <el-tag :type="roleTagType">{{ userInfo?.roleName }}</el-tag>
        </el-form-item>
        <el-form-item label="院系" v-if="userInfo?.departmentName">
          <el-input :model-value="userInfo?.departmentName" disabled />
        </el-form-item>
        <el-form-item label="班级" v-if="userInfo?.className">
          <el-input :model-value="userInfo?.className" disabled />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存修改</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="neu-card password-card">
      <h2 class="page-title">修改密码</h2>
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleChangePassword" :loading="changingPwd">修改密码</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { updateCurrentUser, changePassword, uploadAvatar } from '../api/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)
const saving = ref(false)
const changingPwd = ref(false)
const pwdFormRef = ref()

const form = reactive({ realName: '', gender: 1, phone: '', email: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const roleTagType = computed(() => {
  const map = { STUDENT: 'primary', COUNSELOR: 'success', DEPT_ADMIN: 'warning', ADMIN: 'danger' }
  return map[userStore.roleCode] || 'info'
})

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (r, v, cb) => v !== pwdForm.newPassword ? cb(new Error('两次密码不一致')) : cb(), trigger: 'blur' }
  ],
}

async function handleSave() {
  saving.value = true
  try {
    await updateCurrentUser(form)
    await userStore.fetchUserInfo()
    ElMessage.success('保存成功')
  } catch (e) { /* handled */ }
  finally { saving.value = false }
}

async function handleChangePassword() {
  const valid = await pwdFormRef.value?.validate().catch(() => false)
  if (!valid) return
  changingPwd.value = true
  try {
    await changePassword({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    ElMessage.success('密码修改成功')
    pwdForm.oldPassword = ''; pwdForm.newPassword = ''; pwdForm.confirmPassword = ''
  } catch (e) { /* handled */ }
  finally { changingPwd.value = false }
}

async function handleAvatarChange(file) {
  try {
    const res = await uploadAvatar(file.raw)
    await userStore.fetchUserInfo()
    ElMessage.success('头像更新成功')
  } catch (e) { /* handled */ }
}

onMounted(() => {
  if (userInfo.value) {
    form.realName = userInfo.value.realName || ''
    form.gender = userInfo.value.gender || 1
    form.phone = userInfo.value.phone || ''
    form.email = userInfo.value.email || ''
  }
})
</script>

<style scoped>
.profile-page {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-title { font-size: 18px; color: #1e293b; margin-bottom: 24px; }

.avatar-section {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.profile-avatar {
  background: linear-gradient(135deg, #2563eb, #3b82f6);
  color: #fff;
  font-size: 28px;
  font-weight: 600;
}

.profile-form :deep(.el-input__wrapper) { max-width: 400px; }
</style>

<template>
  <div class="user-management">
    <div class="neu-card">
      <div class="page-header">
        <h2>用户管理</h2>
        <div class="filter-bar">
          <el-input v-model="keyword" placeholder="搜索姓名/学号" clearable style="width:200px" @clear="loadData" @keyup.enter="loadData">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-select v-model="roleFilter" placeholder="角色筛选" clearable style="width:140px" @change="loadData">
            <el-option label="学生" value="STUDENT" />
            <el-option label="辅导员" value="COUNSELOR" />
            <el-option label="院系管理员" value="DEPT_ADMIN" />
            <el-option label="教务管理员" value="ADMIN" />
          </el-select>
          <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon>搜索</el-button>
        </div>
      </div>

      <el-table :data="users" v-loading="loading" stripe>
        <el-table-column label="学号/工号" prop="username" min-width="130" />
        <el-table-column label="姓名" prop="realName" min-width="100" />
        <el-table-column label="角色" min-width="110">
          <template #default="{ row }">
            <el-tag :type="roleTagType(row.roleCode)" size="small">{{ row.roleName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="院系" prop="departmentName" min-width="180" />
        <el-table-column label="班级" prop="className" min-width="100" />
        <el-table-column label="手机号" prop="phone" min-width="130" />
        <el-table-column label="状态" min-width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '冻结' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button :type="row.status === 1 ? 'warning' : 'success'" link size="small" @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '冻结' : '解冻' }}
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination v-model:current-page="page" :page-size="10" :total="total"
          layout="total, prev, pager, next" @current-change="loadData" />
      </div>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editVisible" title="编辑用户" width="560px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="editForm.realName" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.roleId" style="width:100%">
            <el-option v-for="r in roles" :key="r.id" :label="r.roleName" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="院系">
          <el-select v-model="editForm.departmentId" style="width:100%" clearable @change="onDeptChange">
            <el-option v-for="d in departments" :key="d.id" :label="d.deptName" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="editForm.classId" style="width:100%" clearable>
            <el-option v-for="c in classes" :key="c.id" :label="c.className" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit" :loading="editing">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listUsers, updateUser, deleteUser } from '../../api/user'
import { getAllRoles, getDepartments, getClasses } from '../../api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const users = ref([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const keyword = ref('')
const roleFilter = ref('')
const editVisible = ref(false)
const editing = ref(false)
const currentUserId = ref(null)
const roles = ref([])
const departments = ref([])
const classes = ref([])

const editForm = reactive({
  realName: '', roleId: null, departmentId: null, classId: null, phone: '', email: '', status: 1
})

function roleTagType(code) {
  const map = { STUDENT: 'primary', COUNSELOR: 'success', DEPT_ADMIN: 'warning', ADMIN: 'danger' }
  return map[code] || 'info'
}

async function loadData() {
  loading.value = true
  try {
    const res = await listUsers({ keyword: keyword.value, roleCode: roleFilter.value, page: page.value, size: 10 })
    users.value = res.data.records
    total.value = res.data.total
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

async function handleEdit(row) {
  currentUserId.value = row.id
  Object.assign(editForm, {
    realName: row.realName, roleId: null, departmentId: row.departmentId,
    classId: row.classId, phone: row.phone, email: row.email, status: row.status
  })
  const role = roles.value.find(r => r.roleCode === row.roleCode)
  if (role) editForm.roleId = role.id
  if (row.departmentId) {
    const res = await getClasses(row.departmentId)
    classes.value = res.data
  }
  editVisible.value = true
}

async function onDeptChange(val) {
  editForm.classId = null
  if (val) {
    const res = await getClasses(val)
    classes.value = res.data
  } else {
    classes.value = []
  }
}

async function submitEdit() {
  editing.value = true
  try {
    await updateUser(currentUserId.value, editForm)
    ElMessage.success('更新成功')
    editVisible.value = false
    loadData()
  } catch (e) { /* handled */ }
  finally { editing.value = false }
}

async function handleToggleStatus(row) {
  const action = row.status === 1 ? '冻结' : '解冻'
  try {
    await ElMessageBox.confirm(`确定${action}用户 ${row.realName}？`, '提示', { type: 'warning' })
    await updateUser(row.id, { status: row.status === 1 ? 0 : 1 })
    ElMessage.success(`${action}成功`)
    loadData()
  } catch (e) { /* cancelled */ }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除用户 ${row.realName}？此操作不可恢复！`, '警告', { type: 'error' })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { /* cancelled */ }
}

onMounted(async () => {
  loadData()
  const [rolesRes, deptsRes] = await Promise.all([getAllRoles(), getDepartments()])
  roles.value = rolesRes.data
  departments.value = deptsRes.data
})
</script>

<style scoped>
.user-management { margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 12px; }
.page-header h2 { font-size: 18px; color: #1e293b; }
.filter-bar { display: flex; gap: 12px; align-items: center; flex-wrap: wrap; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 20px; }
</style>

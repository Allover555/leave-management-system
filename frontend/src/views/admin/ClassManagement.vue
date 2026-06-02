<template>
  <div class="class-management">
    <div class="neu-card">
      <div class="page-header">
        <h2>班级管理</h2>
        <div class="filter-bar">
          <el-select v-model="deptFilter" placeholder="按学院筛选" clearable style="width:200px" @change="loadData">
            <el-option v-for="d in departments" :key="d.id" :label="d.deptName" :value="d.id" />
          </el-select>
          <el-button type="primary" @click="openAddDialog">
            <el-icon><Plus /></el-icon>新增班级
          </el-button>
        </div>
      </div>

      <el-table :data="classes" v-loading="loading" stripe>
        <el-table-column label="班级名称" prop="className" min-width="150" />
        <el-table-column label="班级编码" prop="classCode" min-width="120" />
        <el-table-column label="所属学院" prop="departmentName" min-width="200" />
        <el-table-column label="年级" prop="grade" min-width="100" align="center" />
        <el-table-column label="辅导员" min-width="150">
          <template #default="{ row }">
            <span v-if="row.counselorName" style="color:#1e293b;font-weight:500;">{{ row.counselorName }}</span>
            <span v-else style="color:#94a3b8;">未分配</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openAssignDialog(row)">分配辅导员</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增班级对话框 -->
    <el-dialog v-model="addVisible" title="新增班级" width="480px">
      <el-form :model="addForm" label-width="90px" :rules="addRules" ref="addFormRef">
        <el-form-item label="所属学院" prop="departmentId">
          <el-select v-model="addForm.departmentId" placeholder="请选择学院" style="width:100%">
            <el-option v-for="d in departments" :key="d.id" :label="d.deptName" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级名称" prop="className">
          <el-input v-model="addForm.className" placeholder="如：计科2401" />
        </el-form-item>
        <el-form-item label="班级编码" prop="classCode">
          <el-input v-model="addForm.classCode" placeholder="如：CS2401" />
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-input v-model="addForm.grade" placeholder="如：2024" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd" :loading="addLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配辅导员对话框 -->
    <el-dialog v-model="assignVisible" title="分配辅导员" width="480px">
      <p style="margin-bottom:16px;color:#475569;">为 <strong>{{ currentClass?.className }}</strong> 分配辅导员</p>
      <el-select v-model="selectedCounselorId" placeholder="请选择辅导员" style="width:100%" filterable>
        <el-option v-for="c in counselors" :key="c.id" :label="`${c.realName}（${c.username}）`" :value="c.id" />
      </el-select>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign" :loading="assignLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const classes = ref([])
const departments = ref([])
const counselors = ref([])
const loading = ref(false)
const deptFilter = ref(null)

const addVisible = ref(false)
const addLoading = ref(false)
const addFormRef = ref()
const addForm = reactive({ departmentId: null, className: '', classCode: '', grade: '' })
const addRules = {
  departmentId: [{ required: true, message: '请选择学院', trigger: 'change' }],
  className: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
  classCode: [{ required: true, message: '请输入班级编码', trigger: 'blur' }],
  grade: [{ required: true, message: '请输入年级', trigger: 'blur' }],
}

const assignVisible = ref(false)
const assignLoading = ref(false)
const currentClass = ref(null)
const selectedCounselorId = ref(null)

async function loadData() {
  loading.value = true
  try {
    const params = deptFilter.value ? { departmentId: deptFilter.value } : {}
    const res = await request.get('/admin/classes', { params })
    classes.value = res.data || []
    // Load counselor assignments for each class
    await loadCounselorAssignments()
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

async function loadCounselorAssignments() {
  try {
    const counselorRes = await request.get('/user/list', { params: { roleCode: 'COUNSELOR', page: 1, size: 100 } })
    const counselorList = counselorRes.data?.records || []
    counselors.value = counselorList

    // For each counselor, get their classes
    for (const c of counselorList) {
      try {
        const ccRes = await request.get(`/admin/counselor-classes/${c.id}`)
        const assignedClasses = ccRes.data || []
        for (const ac of assignedClasses) {
          const cls = classes.value.find(cl => cl.id === ac.classId)
          if (cls) {
            cls.counselorName = c.realName
            cls.counselorId = c.id
          }
        }
      } catch (e) { /* skip */ }
    }
  } catch (e) { /* handled */ }
}

function openAddDialog() {
  addForm.departmentId = deptFilter.value || null
  addForm.className = ''
  addForm.classCode = ''
  addForm.grade = ''
  addVisible.value = true
}

async function submitAdd() {
  const valid = await addFormRef.value?.validate().catch(() => false)
  if (!valid) return
  addLoading.value = true
  try {
    await request.post('/admin/classes', {
      className: addForm.className,
      classCode: addForm.classCode,
      grade: addForm.grade,
      department: { id: addForm.departmentId },
      status: 1
    })
    ElMessage.success('班级创建成功')
    addVisible.value = false
    loadData()
  } catch (e) { /* handled */ }
  finally { addLoading.value = false }
}

function openAssignDialog(row) {
  currentClass.value = row
  selectedCounselorId.value = row.counselorId || null
  assignVisible.value = true
}

async function submitAssign() {
  if (!selectedCounselorId.value) {
    ElMessage.warning('请选择辅导员')
    return
  }
  assignLoading.value = true
  try {
    await request.post('/admin/counselor-classes', {
      counselorId: selectedCounselorId.value,
      classId: currentClass.value.id
    })
    ElMessage.success('分配成功')
    assignVisible.value = false
    loadData()
  } catch (e) { /* handled */ }
  finally { assignLoading.value = false }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除班级"${row.className}"吗？`, '提示', { type: 'warning' })
    await request.delete(`/admin/classes/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { /* cancelled or handled */ }
}

onMounted(async () => {
  try {
    const deptRes = await request.get('/admin/departments')
    departments.value = deptRes.data || []
  } catch (e) { /* handled */ }
  loadData()
})
</script>

<style scoped>
.class-management { margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 12px; }
.page-header h2 { font-size: 18px; color: #1e293b; }
.filter-bar { display: flex; gap: 12px; align-items: center; }
</style>

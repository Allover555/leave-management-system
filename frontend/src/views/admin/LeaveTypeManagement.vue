<template>
  <div class="leave-type-mgmt">
    <div class="neu-card">
      <div class="page-header">
        <h2>假别管理</h2>
        <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增假别</el-button>
      </div>

      <el-table :data="leaveTypes" v-loading="loading" stripe>
        <el-table-column label="假别名称" prop="typeName" min-width="120" />
        <el-table-column label="假别编码" prop="typeCode" min-width="120" />
        <el-table-column label="最大天数" min-width="100" align="center">
          <template #default="{ row }">{{ row.maxDays || '不限' }}</template>
        </el-table-column>
        <el-table-column label="需要证明" min-width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.needProof ? 'warning' : 'info'" size="small">{{ row.needProof ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="描述" prop="description" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" min-width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑假别' : '新增假别'" width="520px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="假别名称">
          <el-input v-model="form.typeName" />
        </el-form-item>
        <el-form-item label="假别编码">
          <el-input v-model="form.typeCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="最大天数">
          <el-input-number v-model="form.maxDays" :min="0" :max="365" />
          <span style="margin-left:8px;color:#94a3b8;font-size:12px">0表示不限</span>
        </el-form-item>
        <el-form-item label="需要证明">
          <el-switch v-model="form.needProof" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getLeaveTypes, createLeaveType, updateLeaveType, deleteLeaveType } from '../../api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const leaveTypes = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const isEdit = ref(false)
const currentId = ref(null)

const form = reactive({
  typeName: '', typeCode: '', maxDays: 0, needProof: 0, description: '', status: 1
})

async function loadData() {
  loading.value = true
  try {
    const res = await getLeaveTypes()
    leaveTypes.value = res.data
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

function handleAdd() {
  isEdit.value = false
  currentId.value = null
  Object.assign(form, { typeName: '', typeCode: '', maxDays: 0, needProof: 0, description: '', status: 1 })
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  currentId.value = row.id
  Object.assign(form, { typeName: row.typeName, typeCode: row.typeCode, maxDays: row.maxDays, needProof: row.needProof, description: row.description, status: row.status })
  dialogVisible.value = true
}

async function submitForm() {
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateLeaveType(currentId.value, form)
    } else {
      await createLeaveType(form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (e) { /* handled */ }
  finally { submitting.value = false }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除假别 ${row.typeName}？`, '提示', { type: 'warning' })
    await deleteLeaveType(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { /* cancelled */ }
}

onMounted(() => loadData())
</script>

<style scoped>
.leave-type-mgmt { margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 18px; color: #1e293b; }
</style>

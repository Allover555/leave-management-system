<template>
  <div class="flow-mgmt">
    <div class="neu-card">
      <div class="page-header">
        <h2>审批流程管理</h2>
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon>新增流程
        </el-button>
      </div>

      <el-table :data="flows" v-loading="loading" stripe>
        <el-table-column label="流程名称" prop="flowName" min-width="180" />
        <el-table-column label="适用假别" prop="leaveTypeName" min-width="100" align="center" />
        <el-table-column label="适用天数" min-width="130" align="center">
          <template #default="{ row }">{{ row.minDays }} ~ {{ row.maxDays }}天</template>
        </el-table-column>
        <el-table-column label="审批节点" min-width="300">
          <template #default="{ row }">
            <div class="flow-nodes">
              <template v-for="(node, idx) in row.nodes" :key="node.id">
                <el-tag type="primary" effect="plain" size="small" round>{{ node.nodeName }}（{{ node.approverRoleName }}）</el-tag>
                <el-icon v-if="idx < row.nodes.length - 1" :size="14" style="margin:0 4px;color:#94a3b8"><ArrowRight /></el-icon>
              </template>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑流程对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑流程' : '新增流程'" width="600px" @close="resetForm">
      <el-form :model="flowForm" label-width="100px" ref="flowFormRef" :rules="formRules">
        <el-form-item label="流程名称" prop="flowName">
          <el-input v-model="flowForm.flowName" placeholder="如：短期请假审批" />
        </el-form-item>
        <el-form-item label="适用假别">
          <el-select v-model="flowForm.leaveTypeId" placeholder="全部（通用）" clearable style="width:100%">
            <el-option v-for="t in leaveTypes" :key="t.id" :label="t.typeName" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="最少天数" prop="minDays">
          <el-input-number v-model="flowForm.minDays" :min="0" :max="9999" style="width:100%" />
        </el-form-item>
        <el-form-item label="最多天数" prop="maxDays">
          <el-input-number v-model="flowForm.maxDays" :min="0" :max="9999" style="width:100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="flowForm.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
        <el-form-item label="审批节点">
          <div class="nodes-editor">
            <div v-for="(node, idx) in flowForm.nodes" :key="idx" class="node-row">
              <span class="node-order">{{ idx + 1 }}</span>
              <el-input v-model="node.nodeName" placeholder="节点名称" style="flex:1" />
              <el-select v-model="node.approverRoleId" placeholder="审批角色" style="width:160px">
                <el-option v-for="r in roles" :key="r.id" :label="r.roleName" :value="r.id" />
              </el-select>
              <el-button type="danger" :icon="Delete" circle size="small" @click="removeNode(idx)" :disabled="flowForm.nodes.length <= 1" />
            </div>
            <el-button type="primary" link @click="addNode" style="margin-top:8px">
              <el-icon><Plus /></el-icon> 添加审批节点
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitFlow" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getFlows, createFlow, updateFlow, deleteFlow, getLeaveTypes, getAllRoles } from '../../api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'

const flows = ref([])
const leaveTypes = ref([])
const roles = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const flowFormRef = ref()

const flowForm = reactive({
  flowName: '',
  leaveTypeId: null,
  minDays: 0,
  maxDays: 9999,
  status: 1,
  nodes: [{ nodeName: '', approverRoleId: null }]
})

const formRules = {
  flowName: [{ required: true, message: '请输入流程名称', trigger: 'blur' }],
  minDays: [{ required: true, message: '请输入最少天数', trigger: 'change' }],
  maxDays: [{ required: true, message: '请输入最多天数', trigger: 'change' }],
}

function resetForm() {
  flowForm.flowName = ''
  flowForm.leaveTypeId = null
  flowForm.minDays = 0
  flowForm.maxDays = 9999
  flowForm.status = 1
  flowForm.nodes = [{ nodeName: '', approverRoleId: null }]
  isEdit.value = false
  editingId.value = null
}

function openAddDialog() {
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(row) {
  isEdit.value = true
  editingId.value = row.id
  flowForm.flowName = row.flowName
  flowForm.leaveTypeId = row.leaveTypeId || null
  flowForm.minDays = row.minDays
  flowForm.maxDays = row.maxDays
  flowForm.status = row.status
  flowForm.nodes = row.nodes.map(n => ({
    nodeName: n.nodeName,
    approverRoleId: n.approverRoleId || roles.value.find(r => r.roleName === n.approverRoleName)?.id
  }))
  if (flowForm.nodes.length === 0) {
    flowForm.nodes = [{ nodeName: '', approverRoleId: null }]
  }
  dialogVisible.value = true
}

function addNode() {
  flowForm.nodes.push({ nodeName: '', approverRoleId: null })
}

function removeNode(idx) {
  flowForm.nodes.splice(idx, 1)
}

async function submitFlow() {
  const valid = await flowFormRef.value?.validate().catch(() => false)
  if (!valid) return

  const emptyNode = flowForm.nodes.find(n => !n.nodeName || !n.approverRoleId)
  if (emptyNode) {
    ElMessage.warning('请完善所有审批节点的名称和角色')
    return
  }

  const data = {
    flowName: flowForm.flowName,
    leaveType: flowForm.leaveTypeId ? { id: flowForm.leaveTypeId } : null,
    minDays: flowForm.minDays,
    maxDays: flowForm.maxDays,
    status: flowForm.status,
    nodes: flowForm.nodes.map((n, idx) => ({
      nodeName: n.nodeName,
      nodeOrder: idx + 1,
      approverRole: { id: n.approverRoleId }
    }))
  }

  saving.value = true
  try {
    if (isEdit.value) {
      await updateFlow(editingId.value, data)
      ElMessage.success('流程更新成功')
    } else {
      await createFlow(data)
      ElMessage.success('流程创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) { /* handled */ }
  finally { saving.value = false }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除流程"${row.flowName}"？`, '提示', { type: 'warning' })
    await deleteFlow(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { /* cancelled */ }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getFlows()
    flows.value = res.data
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

onMounted(async () => {
  const [ltRes, rolesRes] = await Promise.all([getLeaveTypes(), getAllRoles()])
  leaveTypes.value = ltRes.data || []
  roles.value = rolesRes.data || []
  loadData()
})
</script>

<style scoped>
.flow-mgmt { margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 18px; color: #1e293b; }
.flow-nodes { display: flex; align-items: center; flex-wrap: wrap; gap: 2px; }

.nodes-editor { width: 100%; }

.node-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.node-order {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #2563eb;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}
</style>

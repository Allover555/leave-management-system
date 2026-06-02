<template>
  <div class="approval-pending">
    <div class="neu-card">
      <div class="page-header">
        <h2>待审批列表</h2>
      </div>

      <el-table :data="records" v-loading="loading" stripe>
        <el-table-column label="请假单号" prop="leaveNo" min-width="190" />
        <el-table-column label="申请人" prop="applicantName" min-width="100" />
        <el-table-column label="班级" prop="className" min-width="100" />
        <el-table-column label="请假类型" prop="leaveTypeName" min-width="90" />
        <el-table-column label="开始时间" min-width="150">
          <template #default="{ row }">{{ formatTime(row.startTime) }}</template>
        </el-table-column>
        <el-table-column label="结束时间" min-width="150">
          <template #default="{ row }">{{ formatTime(row.endTime) }}</template>
        </el-table-column>
        <el-table-column label="天数" prop="duration" min-width="65" align="center" />
        <el-table-column label="状态" min-width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'warning' : 'info'" size="small">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewDetail(row)">详情</el-button>
            <el-button type="success" size="small" @click="handleApprove(row, 1)">通过</el-button>
            <el-button type="danger" size="small" @click="handleApprove(row, 2)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination v-model:current-page="page" :page-size="10" :total="total"
          layout="prev, pager, next" @current-change="loadData" />
      </div>
    </div>

    <!-- 审批对话框 -->
    <el-dialog v-model="approvalVisible" :title="approvalAction === 1 ? '审批通过' : '审批驳回'" width="500px">
      <el-form :model="approvalForm" label-width="80px">
        <el-form-item label="审批意见">
          <el-input v-model="approvalForm.comment" type="textarea" :rows="3"
            :placeholder="approvalAction === 2 ? '请填写驳回原因（必填）' : '审批意见（选填）'" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approvalVisible = false">取消</el-button>
        <el-button :type="approvalAction === 1 ? 'success' : 'danger'" @click="submitApproval" :loading="approving">
          {{ approvalAction === 1 ? '确认通过' : '确认驳回' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPendingApprovals, approveLeave } from '../../api/leave'
import { ElMessage } from 'element-plus'

const router = useRouter()
const records = ref([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const approvalVisible = ref(false)
const approving = ref(false)
const approvalAction = ref(1)
const currentId = ref(null)
const approvalForm = reactive({ comment: '' })

function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 16)
}

async function loadData() {
  loading.value = true
  try {
    const res = await getPendingApprovals({ page: page.value, size: 10 })
    records.value = res.data.records
    total.value = res.data.total
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

function viewDetail(row) {
  router.push(`/leave/detail/${row.id}`)
}

function handleApprove(row, action) {
  currentId.value = row.id
  approvalAction.value = action
  approvalForm.comment = ''
  approvalVisible.value = true
}

async function submitApproval() {
  if (approvalAction.value === 2 && !approvalForm.comment?.trim()) {
    ElMessage.warning('驳回时必须填写原因')
    return
  }
  approving.value = true
  try {
    await approveLeave(currentId.value, { action: approvalAction.value, comment: approvalForm.comment })
    ElMessage.success(approvalAction.value === 1 ? '审批通过' : '已驳回')
    approvalVisible.value = false
    loadData()
  } catch (e) { /* handled */ }
  finally { approving.value = false }
}

onMounted(() => loadData())
</script>

<style scoped>
.approval-pending { margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 18px; color: #1e293b; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 20px; }
</style>

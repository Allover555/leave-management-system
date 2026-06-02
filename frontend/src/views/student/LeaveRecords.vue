<template>
  <div class="leave-records">
    <div class="neu-card">
      <div class="page-header">
        <h2>请假记录</h2>
        <div class="filter-bar">
          <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width:140px" @change="loadData">
            <el-option label="待审批" :value="0" />
            <el-option label="审批中" :value="1" />
            <el-option label="已通过" :value="2" />
            <el-option label="已驳回" :value="3" />
            <el-option label="已撤销" :value="4" />
            <el-option label="已销假" :value="5" />
          </el-select>
          <el-button type="primary" @click="$router.push('/leave/apply')">
            <el-icon><Plus /></el-icon>新建申请
          </el-button>
        </div>
      </div>

      <el-table :data="records" v-loading="loading" stripe>
        <el-table-column label="请假单号" prop="leaveNo" min-width="190" />
        <el-table-column label="请假类型" prop="leaveTypeName" min-width="100" />
        <el-table-column label="开始时间" min-width="160">
          <template #default="{ row }">{{ formatTime(row.startTime) }}</template>
        </el-table-column>
        <el-table-column label="结束时间" min-width="160">
          <template #default="{ row }">{{ formatTime(row.endTime) }}</template>
        </el-table-column>
        <el-table-column label="天数" prop="duration" min-width="70" align="center" />
        <el-table-column label="状态" min-width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="当前节点" prop="currentNodeName" min-width="120" />
        <el-table-column label="操作" min-width="200" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewDetail(row)">详情</el-button>
            <el-button v-if="row.status === 0" type="warning" link size="small" @click="handleCancel(row)">撤销</el-button>
            <el-button v-if="row.status === 2" type="success" link size="small" @click="handleCancellation(row)">销假</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination v-model:current-page="page" :page-size="10" :total="total"
          layout="prev, pager, next" @current-change="loadData" />
      </div>
    </div>

    <!-- 销假对话框 -->
    <el-dialog v-model="cancellationVisible" title="销假申请" width="500px">
      <el-form :model="cancellationForm" label-width="120px">
        <el-form-item label="实际返校时间">
          <el-date-picker v-model="cancellationForm.actualReturnTime" type="datetime"
            format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="选择时间" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancellationVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCancellationForm" :loading="cancellationLoading">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyLeaves, cancelLeave, submitCancellation } from '../../api/leave'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const records = ref([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const statusFilter = ref(null)
const cancellationVisible = ref(false)
const cancellationLoading = ref(false)
const currentLeaveId = ref(null)
const cancellationForm = reactive({ actualReturnTime: '' })

function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 16)
}

function statusType(s) {
  const map = { 0: 'warning', 1: 'info', 2: 'success', 3: 'danger', 4: 'info', 5: '' }
  return map[s] || 'info'
}

async function loadData() {
  loading.value = true
  try {
    const params = { page: page.value, size: 10 }
    if (statusFilter.value !== null && statusFilter.value !== '') params.status = statusFilter.value
    const res = await getMyLeaves(params)
    records.value = res.data.records
    total.value = res.data.total
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

function viewDetail(row) {
  router.push(`/leave/detail/${row.id}`)
}

async function handleCancel(row) {
  try {
    await ElMessageBox.confirm('确定要撤销此请假申请吗？', '提示', { type: 'warning' })
    await cancelLeave(row.id)
    ElMessage.success('撤销成功')
    loadData()
  } catch (e) { /* cancelled or handled */ }
}

function handleCancellation(row) {
  currentLeaveId.value = row.id
  cancellationForm.actualReturnTime = ''
  cancellationVisible.value = true
}

async function submitCancellationForm() {
  if (!cancellationForm.actualReturnTime) {
    ElMessage.warning('请选择实际返校时间')
    return
  }
  cancellationLoading.value = true
  try {
    await submitCancellation(currentLeaveId.value, cancellationForm)
    ElMessage.success('销假申请已提交')
    cancellationVisible.value = false
    loadData()
  } catch (e) { /* handled */ }
  finally { cancellationLoading.value = false }
}

onMounted(() => loadData())
</script>

<style scoped>
.leave-records { margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 12px; }
.page-header h2 { font-size: 18px; color: #1e293b; }
.filter-bar { display: flex; gap: 12px; align-items: center; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 20px; }
</style>

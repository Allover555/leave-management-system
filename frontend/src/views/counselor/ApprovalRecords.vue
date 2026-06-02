<template>
  <div class="approval-records">
    <div class="neu-card">
      <div class="page-header">
        <h2>审批记录</h2>
        <div class="filter-bar">
          <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width:140px" @change="loadData">
            <el-option label="全部" :value="null" />
            <el-option label="待审批" :value="0" />
            <el-option label="审批中" :value="1" />
            <el-option label="已通过" :value="2" />
            <el-option label="已驳回" :value="3" />
            <el-option label="已撤销" :value="4" />
            <el-option label="已销假" :value="5" />
          </el-select>
          <el-button type="success" @click="handleExport">
            <el-icon><Download /></el-icon>导出Excel
          </el-button>
        </div>
      </div>

      <el-table :data="records" v-loading="loading" stripe>
        <el-table-column label="请假单号" prop="leaveNo" min-width="190" />
        <el-table-column label="申请人" prop="applicantName" min-width="100" />
        <el-table-column label="班级" prop="className" min-width="100" />
        <el-table-column label="请假类型" prop="leaveTypeName" min-width="90" />
        <el-table-column label="起止时间" min-width="200">
          <template #default="{ row }">
            {{ formatTime(row.startTime) }} ~ {{ formatTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="天数" prop="duration" min-width="65" align="center" />
        <el-table-column label="状态" min-width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="$router.push(`/leave/detail/${row.id}`)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination v-model:current-page="page" :page-size="10" :total="total"
          layout="prev, pager, next" @current-change="loadData" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { getAllLeaves, getClassLeaves } from '../../api/leave'
import { exportRecords } from '../../api/statistics'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const records = ref([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const statusFilter = ref(null)

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
    if (statusFilter.value !== null) params.status = statusFilter.value
    const fetchFn = userStore.isAdmin ? getAllLeaves : getClassLeaves
    const res = await fetchFn(params)
    records.value = res.data.records
    total.value = res.data.total
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

async function handleExport() {
  try {
    const res = await exportRecords()
    const url = window.URL.createObjectURL(new Blob([res]))
    const link = document.createElement('a')
    link.href = url
    link.download = '请假记录.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (e) { ElMessage.error('导出失败') }
}

onMounted(() => loadData())
</script>

<style scoped>
.approval-records { margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 12px; }
.page-header h2 { font-size: 18px; color: #1e293b; }
.filter-bar { display: flex; gap: 12px; align-items: center; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 20px; }
</style>

<template>
  <div class="leave-detail">
    <div class="neu-card" v-loading="loading">
      <div class="page-header">
        <h2>请假详情</h2>
        <el-button @click="$router.back()" size="small">
          <el-icon><ArrowLeft /></el-icon>返回
        </el-button>
      </div>

      <template v-if="detail">
        <el-descriptions :column="2" border class="detail-desc">
          <el-descriptions-item label="请假单号">{{ detail.leaveNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusType(detail.status)">{{ detail.statusText }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请人">{{ detail.applicantName }}（{{ detail.applicantUsername }}）</el-descriptions-item>
          <el-descriptions-item label="班级">{{ detail.className || '-' }}</el-descriptions-item>
          <el-descriptions-item label="请假类型">{{ detail.leaveTypeName }}</el-descriptions-item>
          <el-descriptions-item label="请假天数">{{ detail.duration }} 天</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ formatTime(detail.startTime) }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ formatTime(detail.endTime) }}</el-descriptions-item>
          <el-descriptions-item label="请假事由" :span="2">{{ detail.reason }}</el-descriptions-item>
          <el-descriptions-item label="当前审批节点" v-if="detail.currentNodeName">{{ detail.currentNodeName }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ formatTime(detail.createdAt) }}</el-descriptions-item>
        </el-descriptions>

        <!-- 附件 -->
        <div v-if="detail.attachments?.length" class="section">
          <h3>证明材料</h3>
          <div class="attachment-list">
            <div v-for="att in detail.attachments" :key="att.id" class="attachment-item neu-flat" style="padding:12px;border-radius:12px;margin:6px 0;">
              <el-icon><Document /></el-icon>
              <a :href="att.filePath" target="_blank">{{ att.fileName }}</a>
            </div>
          </div>
        </div>

        <!-- 审批流程 -->
        <div class="section">
          <h3>审批流程</h3>
          <el-timeline>
            <el-timeline-item
              v-for="record in detail.approvalRecords"
              :key="record.id"
              :timestamp="formatTime(record.approvedAt)"
              :type="record.action === 1 ? 'success' : 'danger'"
              placement="top"
            >
              <div class="glass-card" style="padding:16px;">
                <p><strong>{{ record.nodeName || '审批' }}</strong> - {{ record.approverName }}</p>
                <el-tag :type="record.action === 1 ? 'success' : 'danger'" size="small">{{ record.actionText }}</el-tag>
                <p v-if="record.comment" style="color:#64748b;margin-top:8px;">{{ record.comment }}</p>
              </div>
            </el-timeline-item>
            <el-timeline-item v-if="detail.status === 0 || detail.status === 1" type="warning" timestamp="等待中">
              <div class="glass-card" style="padding:16px;">
                <p>{{ detail.currentNodeName || '等待审批' }}</p>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getLeaveDetail } from '../../api/leave'

const route = useRoute()
const detail = ref(null)
const loading = ref(false)

function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 16)
}

function statusType(s) {
  const map = { 0: 'warning', 1: 'info', 2: 'success', 3: 'danger', 4: 'info', 5: '' }
  return map[s] || 'info'
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getLeaveDetail(route.params.id)
    detail.value = res.data
  } catch (e) { /* handled */ }
  finally { loading.value = false }
})
</script>

<style scoped>
.leave-detail { max-width: 900px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 18px; color: #1e293b; }
.section { margin-top: 28px; }
.section h3 { font-size: 16px; color: #1e293b; margin-bottom: 16px; }
.attachment-item { display: flex; align-items: center; gap: 8px; }
.attachment-item a { color: #2563eb; text-decoration: none; }
.detail-desc { margin-bottom: 8px; }
</style>

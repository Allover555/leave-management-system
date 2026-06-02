<template>
  <div class="leave-apply">
    <div class="neu-card">
      <h2 class="page-title">请假申请</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="apply-form">
        <el-form-item label="请假类型" prop="leaveTypeId">
          <el-select v-model="form.leaveTypeId" placeholder="请选择请假类型" style="width:100%;max-width:400px;">
            <el-option v-for="t in leaveTypes" :key="t.id" :label="t.typeName" :value="t.id">
              <span>{{ t.typeName }}</span>
              <span style="color:#94a3b8;font-size:12px;margin-left:8px;">最长{{ t.maxDays || '不限' }}天</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%;max-width:400px;" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%;max-width:400px;" />
        </el-form-item>
        <el-form-item label="请假时长" v-if="duration > 0">
          <el-tag type="info" size="large">{{ duration }} 天</el-tag>
        </el-form-item>
        <el-form-item label="请假事由" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="4" placeholder="请详细描述请假原因" maxlength="500" show-word-limit style="max-width:600px;" />
        </el-form-item>
        <el-form-item label="证明材料">
          <el-upload
            v-model:file-list="fileList"
            action="#"
            :auto-upload="false"
            multiple
            :limit="5"
            accept=".jpg,.jpeg,.png,.gif,.pdf,.doc,.docx"
            list-type="picture-card"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">支持jpg/png/pdf/doc格式，最多5个文件</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit" size="large" style="border-radius:12px;padding:12px 40px;">
            提交申请
          </el-button>
          <el-button @click="handleReset" size="large" style="border-radius:12px;">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { applyLeave } from '../../api/leave'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const submitting = ref(false)
const leaveTypes = ref([])
const fileList = ref([])

const form = reactive({
  leaveTypeId: null,
  startTime: '',
  endTime: '',
  reason: ''
})

const rules = {
  leaveTypeId: [{ required: true, message: '请选择请假类型', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  reason: [{ required: true, message: '请填写请假事由', trigger: 'blur' }],
}

const duration = computed(() => {
  if (!form.startTime || !form.endTime) return 0
  const start = new Date(form.startTime)
  const end = new Date(form.endTime)
  const hours = (end - start) / (1000 * 60 * 60)
  return Math.max(0.5, Math.round(hours / 24 * 10) / 10)
})

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const files = fileList.value.map(f => f.raw).filter(Boolean)
    await applyLeave(form, files)
    ElMessage.success('请假申请提交成功')
    router.push('/leave/records')
  } catch (e) { /* handled */ }
  finally { submitting.value = false }
}

function handleReset() {
  formRef.value?.resetFields()
  fileList.value = []
}

onMounted(async () => {
  try {
    const res = await request.get('/leave/types')
    leaveTypes.value = res.data || []
  } catch (e) { /* handled */ }
})
</script>

<style scoped>
.leave-apply { max-width: 900px; margin: 0 auto; }
.page-title { font-size: 18px; color: #1e293b; margin-bottom: 24px; }
.apply-form :deep(.el-textarea__inner) {
  border-radius: 12px !important;
  background: var(--bg-primary) !important;
  box-shadow: inset 2px 2px 4px #a3b1c6, inset -2px -2px 4px #ffffff !important;
  border: none !important;
}
</style>

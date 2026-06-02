<template>
  <div class="messages-page">
    <div class="neu-card">
      <div class="page-header">
        <h2>消息通知</h2>
        <el-button type="primary" size="small" @click="handleReadAll" :disabled="!userStore.unreadCount">
          全部已读
        </el-button>
      </div>
      <el-table :data="messages" v-loading="loading" stripe>
        <el-table-column label="状态" width="70" align="center">
          <template #default="{ row }">
            <el-badge is-dot :hidden="row.isRead === 1" type="danger">
              <el-icon :size="18"><Bell /></el-icon>
            </el-badge>
          </template>
        </el-table-column>
        <el-table-column label="标题" prop="title" min-width="200">
          <template #default="{ row }">
            <span :style="{ fontWeight: row.isRead === 0 ? '600' : '400' }">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column label="内容" prop="content" min-width="300" show-overflow-tooltip />
        <el-table-column label="时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button v-if="row.isRead === 0" type="primary" link size="small" @click="handleRead(row)">
              标为已读
            </el-button>
            <el-tag v-else type="info" size="small">已读</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination v-model:current-page="page" :page-size="10" :total="total"
          layout="prev, pager, next" @current-change="loadMessages" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { getMessages, markAsRead, markAllAsRead } from '../api/message'

const userStore = useUserStore()
const messages = ref([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)

function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 19)
}

async function loadMessages() {
  loading.value = true
  try {
    const res = await getMessages({ page: page.value, size: 10 })
    messages.value = res.data.records
    total.value = res.data.total
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

async function handleRead(row) {
  await markAsRead(row.id)
  row.isRead = 1
  userStore.fetchUnreadCount()
}

async function handleReadAll() {
  await markAllAsRead()
  messages.value.forEach(m => m.isRead = 1)
  userStore.fetchUnreadCount()
}

onMounted(() => loadMessages())
</script>

<style scoped>
.messages-page { margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 18px; color: #1e293b; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 20px; }
</style>

<template>
  <div class="admin-statistics">
    <div class="neu-card">
      <div class="page-header">
        <h2>数据统计分析</h2>
        <div class="filter-bar">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至"
            start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DDTHH:mm:ss"
            @change="loadData" style="width:360px" />
          <el-button type="success" @click="handleExport">
            <el-icon><Download /></el-icon>导出Excel
          </el-button>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="stat-cards">
        <div class="glass-card stat-item">
          <div class="stat-icon" style="background:linear-gradient(135deg,#2563eb,#3b82f6)">
            <el-icon :size="24" color="#fff"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ stats.totalApplications || 0 }}</p>
            <p class="stat-label">请假总数</p>
          </div>
        </div>
        <div class="glass-card stat-item">
          <div class="stat-icon" style="background:linear-gradient(135deg,#f59e0b,#fbbf24)">
            <el-icon :size="24" color="#fff"><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ stats.pendingApplications || 0 }}</p>
            <p class="stat-label">待审批</p>
          </div>
        </div>
        <div class="glass-card stat-item">
          <div class="stat-icon" style="background:linear-gradient(135deg,#10b981,#34d399)">
            <el-icon :size="24" color="#fff"><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ stats.approvedApplications || 0 }}</p>
            <p class="stat-label">已通过</p>
          </div>
        </div>
        <div class="glass-card stat-item">
          <div class="stat-icon" style="background:linear-gradient(135deg,#ef4444,#f87171)">
            <el-icon :size="24" color="#fff"><CircleClose /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ stats.rejectedApplications || 0 }}</p>
            <p class="stat-label">已驳回</p>
          </div>
        </div>
      </div>

      <!-- 图表区域 -->
      <div class="chart-grid">
        <div class="glass-card chart-box">
          <h3>请假类型分布</h3>
          <div ref="typeChartRef" style="height:320px;"></div>
        </div>
        <div class="glass-card chart-box">
          <h3>班级请假排名</h3>
          <div ref="classChartRef" style="height:320px;"></div>
        </div>
        <div class="glass-card chart-box" style="grid-column: 1 / -1;">
          <h3>月度请假趋势</h3>
          <div ref="trendChartRef" style="height:320px;"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { getOverview } from '../../api/statistics'
import { exportRecords } from '../../api/statistics'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

const dateRange = ref(null)
const stats = reactive({ totalApplications: 0, pendingApplications: 0, approvedApplications: 0, rejectedApplications: 0 })
const typeChartRef = ref()
const classChartRef = ref()
const trendChartRef = ref()
let typeChart, classChart, trendChart

async function loadData() {
  try {
    const params = {}
    if (dateRange.value) {
      params.start = dateRange.value[0]
      params.end = dateRange.value[1]
    }
    const res = await getOverview(params)
    const data = res.data
    Object.assign(stats, data)
    renderTypeChart(data.typeDistribution || [])
    renderClassChart(data.classDistribution || [])
    renderTrendChart(data.monthlyTrend || [])
  } catch (e) { /* handled */ }
}

function renderTypeChart(data) {
  if (!typeChart) typeChart = echarts.init(typeChartRef.value)
  typeChart.setOption({
    tooltip: { trigger: 'item' },
    color: ['#2563eb', '#10b981', '#f59e0b', '#ef4444', '#0ea5e9', '#06b6d4'],
    series: [{
      type: 'pie', radius: ['40%', '70%'], padAngle: 3, itemStyle: { borderRadius: 8 },
      data: data.map(d => ({ name: d.name, value: d.value })),
      label: { formatter: '{b}: {c}次' }
    }]
  })
}

function renderClassChart(data) {
  if (!classChart) classChart = echarts.init(classChartRef.value)
  classChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: data.map(d => d.name), axisLabel: { rotate: 30 } },
    yAxis: { type: 'value', name: '次数' },
    series: [{
      type: 'bar', data: data.map(d => d.value), barWidth: 40,
      itemStyle: {
        borderRadius: [8, 8, 0, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#2563eb' }, { offset: 1, color: '#3b82f6' }
        ])
      }
    }]
  })
}

function renderTrendChart(data) {
  if (!trendChart) trendChart = echarts.init(trendChartRef.value)
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: data.map(d => d.month) },
    yAxis: { type: 'value', name: '次数' },
    series: [{
      type: 'line', data: data.map(d => d.count), smooth: true,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(37,99,235,0.3)' }, { offset: 1, color: 'rgba(37,99,235,0.02)' }
        ])
      },
      lineStyle: { color: '#2563eb', width: 3 },
      itemStyle: { color: '#2563eb' }
    }]
  })
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

onMounted(async () => {
  await nextTick()
  loadData()
  window.addEventListener('resize', () => {
    typeChart?.resize()
    classChart?.resize()
    trendChart?.resize()
  })
})
</script>

<style scoped>
.admin-statistics { margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 12px; }
.page-header h2 { font-size: 18px; color: #1e293b; }
.filter-bar { display: flex; gap: 12px; align-items: center; }

.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
}

.stat-label {
  font-size: 13px;
  color: #94a3b8;
  margin-top: 2px;
}

.chart-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.chart-box h3 { font-size: 15px; color: #1e293b; margin-bottom: 12px; }
</style>

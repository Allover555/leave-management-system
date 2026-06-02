<template>
  <div class="class-statistics">
    <div class="neu-card">
      <div class="page-header">
        <h2>班级请假统计</h2>
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至"
          start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DDTHH:mm:ss"
          @change="loadData" style="width:360px" />
      </div>

      <div class="chart-grid">
        <div class="glass-card chart-box">
          <h3>请假类型分布</h3>
          <div ref="typeChartRef" style="height:320px;"></div>
        </div>
        <div class="glass-card chart-box">
          <h3>班级请假统计</h3>
          <div ref="classChartRef" style="height:320px;"></div>
        </div>
        <div class="glass-card chart-box" style="grid-column: 1 / -1;">
          <h3>月度趋势</h3>
          <div ref="trendChartRef" style="height:320px;"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { getOverview } from '../../api/statistics'
import * as echarts from 'echarts'

const dateRange = ref(null)
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
.class-statistics { margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 12px; }
.page-header h2 { font-size: 18px; color: #1e293b; }
.chart-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.chart-box h3 { font-size: 15px; color: #1e293b; margin-bottom: 12px; }
</style>

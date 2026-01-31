<template>
  <div class="dashboard-page">
    <a-row :gutter="16">
      <a-col :span="6">
        <a-statistic title="变电站总数" :value="stats.stations" :prefix="h(BuildOutlined)" />
      </a-col>
      <a-col :span="6">
        <a-statistic title="设备总数" :value="stats.devices" :prefix="h(AppstoreOutlined)" />
      </a-col>
      <a-col :span="6">
        <a-statistic title="今日任务" :value="stats.todayTasks" :prefix="h(CheckSquareOutlined)" />
      </a-col>
      <a-col :span="6">
        <a-statistic title="待处理缺陷" :value="stats.pendingDefects" :prefix="h(AlertOutlined)" :value-style="{ color: '#ff4d4f' }" />
      </a-col>
    </a-row>

    <a-row :gutter="16" style="margin-top: 24px">
      <a-col :span="12">
        <a-card title="巡检任务趋势" :bordered="false">
          <div ref="taskTrendChart" style="height: 300px"></div>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card title="缺陷类型分布" :bordered="false">
          <div ref="defectTypeChart" style="height: 300px"></div>
        </a-card>
      </a-col>
    </a-row>

    <a-row style="margin-top: 24px">
      <a-col :span="24">
        <a-card title="最近活动" :bordered="false">
          <a-list :data-source="recentActivities" size="small">
            <template #renderItem="{ item }">
              <a-list-item>
                <a-list-item-meta :description="item.time">
                  <template #title>
                    <a-tag :color="item.type">{{ item.action }}</a-tag>
                    {{ item.content }}
                  </template>
                  <template #avatar>
                    <a-avatar>{{ item.user.charAt(0) }}</a-avatar>
                  </template>
                </a-list-item-meta>
              </a-list-item>
            </template>
          </a-list>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { ref, onMounted, h } from 'vue'
import * as echarts from 'echarts'
import {
  BuildOutlined,
  AppstoreOutlined,
  CheckSquareOutlined,
  AlertOutlined
} from '@ant-design/icons-vue'

const stats = ref({
  stations: 5,
  devices: 128,
  todayTasks: 12,
  pendingDefects: 3
})

const recentActivities = ref([
  { action: '任务', type: 'blue', content: '张三完成了 #5001 变电站日巡任务', user: '张三', time: '10分钟前' },
  { action: '缺陷', type: 'red', content: '李四发现设备 DEF-001 存在严重缺陷', user: '李四', time: '30分钟前' },
  { action: '报告', type: 'green', content: '王五审核通过了 #3001 号巡检报告', user: '王五', time: '1小时前' },
  { action: '用户', type: 'orange', content: '管理员添加了新用户：赵六', user: '管理员', time: '2小时前' }
])

const taskTrendChart = ref(null)
const defectTypeChart = ref(null)

onMounted(() => {
  initTaskTrendChart()
  initDefectTypeChart()
})

const initTaskTrendChart = () => {
  const chart = echarts.init(taskTrendChart.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: { type: 'value' },
    series: [{
      data: [12, 15, 10, 18, 14, 8, 6],
      type: 'line',
      smooth: true,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(24, 144, 255, 0.3)' },
          { offset: 1, color: 'rgba(24, 144, 255, 0)' }
        ])
      }
    }]
  })
}

const initDefectTypeChart = () => {
  const chart = echarts.init(defectTypeChart.value)
  chart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: '60%',
      data: [
        { value: 15, name: '一般缺陷' },
        { value: 8, name: '严重缺陷' },
        { value: 3, name: '危急缺陷' }
      ]
    }]
  })
}
</script>

<style scoped>
.dashboard-page {
  padding: 0;
}

:deep(.ant-statistic) {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
}
</style>

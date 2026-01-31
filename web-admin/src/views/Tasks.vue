<template>
  <div class="tasks-page">
    <div class="page-header">
      <a-space>
        <a-input-search
          v-model:value="searchText"
          placeholder="搜索任务"
          style="width: 250px"
          @search="handleSearch"
        />
        <a-select v-model:value="filterStatus" placeholder="状态" style="width: 120px" allow-clear>
          <a-select-option value="">全部</a-select-option>
          <a-select-option value="pending">待执行</a-select-option>
          <a-select-option value="in_progress">进行中</a-select-option>
          <a-select-option value="completed">已完成</a-select-option>
        </a-select>
      </a-space>
      <a-button type="primary" @click="showAddModal">
        <template #icon><PlusOutlined /></template>
        新增任务
      </a-button>
    </div>

    <a-table
      :columns="columns"
      :data-source="dataSource"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
      row-key="taskId"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'title'">
          <a @click="handleViewDetail(record)">{{ record.title }}</a>
        </template>
        <template v-else-if="column.key === 'progress'">
          <a-progress :percent="record.progress || 0" size="small" />
        </template>
        <template v-else-if="column.key === 'status'">
          <a-tag :color="getStatusColor(record.status)">
            {{ getStatusText(record.status) }}
          </a-tag>
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a @click="handleViewDetail(record)">详情</a>
            <a-popconfirm v-if="record.status === 'pending'" title="确定要取消吗？" @confirm="handleCancel(record)">
              <a style="color: #ff4d4f">取消</a>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- 任务详情弹窗 -->
    <a-modal v-model:open="detailVisible" title="任务详情" width="800px" :footer="null">
      <a-descriptions bordered :column="2" v-if="currentTask">
        <a-descriptions-item label="任务编号">{{ currentTask.taskCode }}</a-descriptions-item>
        <a-descriptions-item label="任务标题">{{ currentTask.title }}</a-descriptions-item>
        <a-descriptions-item label="变电站">{{ currentTask.stationName }}</a-descriptions-item>
        <a-descriptions-item label="巡检类型">
          <a-tag>{{ getInspectionType(currentTask.inspectionType) }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="执行人">{{ currentTask.assigneeName }}</a-descriptions-item>
        <a-descriptions-item label="计划开始">{{ formatTime(currentTask.plannedStartTime) }}</a-descriptions-item>
        <a-descriptions-item label="任务进度" :span="2">
          <a-progress :percent="currentTask.progress || 0" />
        </a-descriptions-item>
        <a-descriptions-item label="设备数量">{{ currentTask.deviceCount }}</a-descriptions-item>
        <a-descriptions-item label="已完成">{{ currentTask.completedCount }}</a-descriptions-item>
        <a-descriptions-item label="缺陷数量">{{ currentTask.defectCount }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-badge :status="getStatusBadge(currentTask.status)" :text="getStatusText(currentTask.status)" />
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'

const columns = [
  { title: '任务编号', dataIndex: 'taskCode', key: 'taskCode', width: 120 },
  { title: '任务标题', dataIndex: 'title', key: 'title' },
  { title: '变电站', dataIndex: 'stationName', key: 'stationName' },
  { title: '执行人', dataIndex: 'assigneeName', key: 'assigneeName' },
  { title: '进度', dataIndex: 'progress', key: 'progress', width: 150 },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '计划开始', dataIndex: 'plannedStartTime', key: 'plannedStartTime' },
  { title: '操作', key: 'action', width: 150 }
]

const searchText = ref('')
const filterStatus = ref('')
const dataSource = ref([])
const loading = ref(false)
const pagination = ref({ current: 1, pageSize: 10, total: 0 })
const detailVisible = ref(false)
const currentTask = ref(null)

const getStatusColor = (status) => {
  const map = { pending: 'orange', in_progress: 'blue', completed: 'green', overdue: 'red' }
  return map[status] || 'default'
}

const getStatusBadge = (status) => {
  const map = { pending: 'warning', in_progress: 'processing', completed: 'success', overdue: 'error' }
  return map[status] || 'default'
}

const getStatusText = (status) => {
  const map = { pending: '待执行', in_progress: '进行中', completed: '已完成', overdue: '已逾期' }
  return map[status] || status
}

const getInspectionType = (type) => {
  const map = { daily: '日巡', weekly: '周巡', monthly: '月巡', special: '专项' }
  return map[type] || type
}

const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

const handleSearch = () => {}

const handleTableChange = (pag) => {
  pagination.value.current = pag.current
}

const showAddModal = () => {
  message.info('功能开发中')
}

const handleViewDetail = (record) => {
  currentTask.value = record
  detailVisible.value = true
}

const handleCancel = (record) => {
  message.success('任务已取消')
}
</script>

<style scoped>
.tasks-page {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}
</style>

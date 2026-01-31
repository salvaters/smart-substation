<template>
  <div class="reports-page">
    <div class="page-header">
      <a-space>
        <a-range-picker v-model:value="dateRange" format="YYYY-MM-DD" @change="handleDateChange" />
        <a-select v-model:value="filterType" placeholder="报告类型" style="width: 120px" allow-clear>
          <a-select-option value="">全部</a-select-option>
          <a-select-option value="daily">日报</a-select-option>
          <a-select-option value="weekly">周报</a-select-option>
          <a-select-option value="monthly">月报</a-select-option>
        </a-select>
        <a-select v-model:value="filterStatus" placeholder="状态" style="width: 120px" allow-clear>
          <a-select-option value="">全部</a-select-option>
          <a-select-option value="draft">草稿</a-select-option>
          <a-select-option value="submitted">已提交</a-select-option>
          <a-select-option value="approved">已审核</a-select-option>
        </a-select>
      </a-space>
      <a-button type="primary" @click="handleGenerateReport">
        <template #icon><FileAddOutlined /></template>
        生成报告
      </a-button>
    </div>

    <a-table
      :columns="columns"
      :data-source="dataSource"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
      row-key="reportId"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'reportCode'">
          <a @click="handleViewDetail(record)">{{ record.reportCode }}</a>
        </template>
        <template v-else-if="column.key === 'reportType'">
          <a-tag>{{ getReportType(record.reportType) }}</a-tag>
        </template>
        <template v-else-if="column.key === 'summary'">
          <div class="summary">{{ record.summary }}</div>
        </template>
        <template v-else-if="column.key === 'status'">
          <a-badge :status="getStatusBadge(record.status)" :text="getStatusText(record.status)" />
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a @click="handleViewDetail(record)">详情</a>
            <a @click="handleExport(record)">导出</a>
            <a-dropdown v-if="record.status === 'submitted'">
              <a class="ant-dropdown-link" @click.prevent>
                审核 <DownOutlined />
              </a>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="handleApprove(record)">通过</a-menu-item>
                  <a-menu-item @click="handleReject(record)">驳回</a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- 报告详情弹窗 -->
    <a-modal v-model:open="detailVisible" title="报告详情" width="900px" :footer="null">
      <div v-if="currentReport">
        <a-descriptions bordered :column="2">
          <a-descriptions-item label="报告编号">{{ currentReport.reportCode }}</a-descriptions-item>
          <a-descriptions-item label="报告类型">
            <a-tag>{{ getReportType(currentReport.reportType) }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="变电站">{{ currentReport.stationName }}</a-descriptions-item>
          <a-descriptions-item label="报告周期">{{ currentReport.reportPeriod }}</a-descriptions-item>
          <a-descriptions-item label="开始时间">{{ formatTime(currentReport.startTime) }}</a-descriptions-item>
          <a-descriptions-item label="结束时间">{{ formatTime(currentReport.endTime) }}</a-descriptions-item>
          <a-descriptions-item label="创建人">{{ currentReport.creatorName }}</a-descriptions-item>
          <a-descriptions-item label="创建时间">{{ formatTime(currentReport.createTime) }}</a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-badge :status="getStatusBadge(currentReport.status)" :text="getStatusText(currentReport.status)" />
          </a-descriptions-item>
          <a-descriptions-item label="审核人" v-if="currentReport.approverName">
            {{ currentReport.approverName }}
          </a-descriptions-item>
        </a-descriptions>

        <a-divider>巡检统计</a-divider>

        <a-row :gutter="16">
          <a-col :span="6">
            <a-statistic title="设备总数" :value="currentReport.deviceCount" />
          </a-col>
          <a-col :span="6">
            <a-statistic title="正常数量" :value="currentReport.normalCount" :value-style="{ color: '#3f8600' }" />
          </a-col>
          <a-col :span="6">
            <a-statistic title="异常数量" :value="currentReport.abnormalCount" :value-style="{ color: '#cf1322' }" />
          </a-col>
          <a-col :span="6">
            <a-statistic title="缺陷数量" :value="currentReport.defectCount" />
          </a-col>
        </a-row>

        <a-divider>巡检总结</a-divider>

        <p>{{ currentReport.summary || '暂无总结' }}</p>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { FileAddOutlined, DownOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'

const columns = [
  { title: '报告编号', dataIndex: 'reportCode', key: 'reportCode' },
  { title: '报告类型', dataIndex: 'reportType', key: 'reportType' },
  { title: '变电站', dataIndex: 'stationName', key: 'stationName' },
  { title: '报告周期', dataIndex: 'reportPeriod', key: 'reportPeriod' },
  { title: '巡检总结', dataIndex: 'summary', key: 'summary', ellipsis: true },
  { title: '创建人', dataIndex: 'creatorName', key: 'creatorName' },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'action', width: 200 }
]

const dateRange = ref([])
const filterType = ref('')
const filterStatus = ref('')
const dataSource = ref([])
const loading = ref(false)
const pagination = ref({ current: 1, pageSize: 10, total: 0 })
const detailVisible = ref(false)
const currentReport = ref(null)

const getReportType = (type) => {
  const map = { daily: '日报', weekly: '周报', monthly: '月报', custom: '自定义' }
  return map[type] || type
}

const getStatusBadge = (status) => {
  const map = { draft: 'default', submitted: 'processing', approved: 'success', rejected: 'error' }
  return map[status] || 'default'
}

const getStatusText = (status) => {
  const map = { draft: '草稿', submitted: '已提交', approved: '已审核', rejected: '已驳回' }
  return map[status] || status
}

const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

const handleDateChange = () => {
  // TODO: 根据日期范围筛选
}

const handleTableChange = (pag) => {
  pagination.value.current = pag.current
}

const handleGenerateReport = () => {
  message.info('生成报告功能开发中')
}

const handleViewDetail = (record) => {
  currentReport.value = record
  detailVisible.value = true
}

const handleExport = (record) => {
  message.success('导出成功')
}

const handleApprove = (record) => {
  message.success('审核通过')
}

const handleReject = (record) => {
  message.info('审核驳回')
}
</script>

<style scoped>
.reports-page {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.summary {
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>

<template>
  <div class="defects-page">
    <div class="page-header">
      <a-space>
        <a-input-search
          v-model:value="searchText"
          placeholder="搜索缺陷"
          style="width: 250px"
          @search="handleSearch"
        />
        <a-select v-model:value="filterType" placeholder="缺陷类型" style="width: 120px" allow-clear>
          <a-select-option value="">全部</a-select-option>
          <a-select-option value="general">一般</a-select-option>
          <a-select-option value="serious">严重</a-select-option>
          <a-select-option value="critical">危急</a-select-option>
        </a-select>
        <a-select v-model:value="filterStatus" placeholder="状态" style="width: 120px" allow-clear>
          <a-select-option value="">全部</a-select-option>
          <a-select-option value="pending">待处理</a-select-option>
          <a-select-option value="confirmed">已确认</a-select-option>
          <a-select-option value="repairing">处理中</a-select-option>
          <a-select-option value="completed">已完成</a-select-option>
        </a-select>
      </a-space>
      <a-button type="primary" @click="showAddModal">
        <template #icon><PlusOutlined /></template>
        上报缺陷
      </a-button>
    </div>

    <a-table
      :columns="columns"
      :data-source="dataSource"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
      row-key="defectId"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'defectCode'">
          <a @click="handleViewDetail(record)">{{ record.defectCode }}</a>
        </template>
        <template v-else-if="column.key === 'defectType'">
          <a-tag :color="getTypeColor(record.defectType)">
            {{ getTypeText(record.defectType) }}
          </a-tag>
        </template>
        <template v-else-if="column.key === 'status'">
          <a-badge :status="getStatusBadge(record.status)" :text="getStatusText(record.status)" />
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a @click="handleViewDetail(record)">详情</a>
            <a v-if="record.status === 'pending'" @click="handleConfirm(record)">确认</a>
            <a v-if="record.status === 'confirmed'" @click="handleAssign(record)">派单</a>
            <a v-if="record.status === 'repairing'" @click="handleComplete(record)">完成</a>
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- 缺陷详情弹窗 -->
    <a-modal v-model:open="detailVisible" title="缺陷详情" width="700px" :footer="null">
      <a-descriptions bordered :column="2" v-if="currentDefect">
        <a-descriptions-item label="缺陷编号" :span="2">{{ currentDefect.defectCode }}</a-descriptions-item>
        <a-descriptions-item label="设备名称">{{ currentDefect.deviceName }}</a-descriptions-item>
        <a-descriptions-item label="缺陷类型">
          <a-tag :color="getTypeColor(currentDefect.defectType)">
            {{ getTypeText(currentDefect.defectType) }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="发现人">{{ currentDefect.discovererName }}</a-descriptions-item>
        <a-descriptions-item label="发现时间">{{ formatTime(currentDefect.discoverTime) }}</a-descriptions-item>
        <a-descriptions-item label="缺陷描述" :span="2">
          {{ currentDefect.defectDescription }}
        </a-descriptions-item>
        <a-descriptions-item label="现场照片" :span="2" v-if="currentDefect.photoUrls">
          <a-image-preview-group>
            <a-image
              v-for="(url, index) in getPhotoUrls(currentDefect.photoUrls)"
              :key="index"
              :width="60"
              :src="url"
            />
          </a-image-preview-group>
        </a-descriptions-item>
        <a-descriptions-item label="整改方案" :span="2" v-if="currentDefect.repairPlan">
          {{ currentDefect.repairPlan }}
        </a-descriptions-item>
        <a-descriptions-item label="整改人" v-if="currentDefect.repairPerson">
          {{ currentDefect.repairPerson }}
        </a-descriptions-item>
        <a-descriptions-item label="整改期限" v-if="currentDefect.repairDeadline">
          {{ currentDefect.repairDeadline }}
        </a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-badge :status="getStatusBadge(currentDefect.status)" :text="getStatusText(currentDefect.status)" />
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
  { title: '缺陷编号', dataIndex: 'defectCode', key: 'defectCode' },
  { title: '设备名称', dataIndex: 'deviceName', key: 'deviceName' },
  { title: '缺陷类型', dataIndex: 'defectType', key: 'defectType' },
  { title: '缺陷描述', dataIndex: 'defectDescription', key: 'defectDescription', ellipsis: true },
  { title: '发现人', dataIndex: 'discovererName', key: 'discovererName' },
  { title: '发现时间', dataIndex: 'discoverTime', key: 'discoverTime' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'action', width: 200 }
]

const searchText = ref('')
const filterType = ref('')
const filterStatus = ref('')
const dataSource = ref([])
const loading = ref(false)
const pagination = ref({ current: 1, pageSize: 10, total: 0 })
const detailVisible = ref(false)
const currentDefect = ref(null)

const getTypeColor = (type) => {
  const map = { general: 'blue', serious: 'orange', critical: 'red' }
  return map[type] || 'default'
}

const getTypeText = (type) => {
  const map = { general: '一般', serious: '严重', critical: '危急' }
  return map[type] || type
}

const getStatusBadge = (status) => {
  const map = { pending: 'warning', confirmed: 'processing', repairing: 'processing', completed: 'success' }
  return map[status] || 'default'
}

const getStatusText = (status) => {
  const map = { pending: '待处理', confirmed: '已确认', repairing: '处理中', completed: '已完成' }
  return map[status] || status
}

const getPhotoUrls = (urls) => {
  if (!urls) return []
  return typeof urls === 'string' ? urls.split(',') : urls
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
  currentDefect.value = record
  detailVisible.value = true
}

const handleConfirm = (record) => {
  message.success('已确认缺陷')
}

const handleAssign = (record) => {
  message.info('派单功能开发中')
}

const handleComplete = (record) => {
  message.success('缺陷已完成')
}
</script>

<style scoped>
.defects-page {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}
</style>

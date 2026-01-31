<template>
  <div class="devices-page">
    <div class="page-header">
      <a-space>
        <a-input-search
          v-model:value="searchText"
          placeholder="搜索设备"
          style="width: 250px"
          @search="handleSearch"
        />
        <a-select
          v-model:value="filterStation"
          placeholder="选择变电站"
          style="width: 150px"
          allow-clear
        >
          <a-select-option value="">全部</a-select-option>
          <a-select-option value="ST001">XX变电站</a-select-option>
        </a-select>
      </a-space>
      <a-space>
        <a-button @click="handleBatchQrCode">
          <template #icon><QrcodeOutlined /></template>
          批量二维码
        </a-button>
        <a-button type="primary" @click="showAddModal">
          <template #icon><PlusOutlined /></template>
          新增设备
        </a-button>
      </a-space>
    </div>

    <a-table
      :columns="columns"
      :data-source="dataSource"
      :loading="loading"
      :pagination="pagination"
      :row-selection="rowSelection"
      @change="handleTableChange"
      row-key="deviceId"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'qrCode'">
          <a-space>
            <a-tag color="blue">{{ record.qrCode }}</a-tag>
            <a-button size="small" @click="handleShowQrCode(record)">
              <template #icon><QrcodeOutlined /></template>
            </a-button>
          </a-space>
        </template>
        <template v-else-if="column.key === 'status'">
          <a-badge :status="getStatusBadge(record.status)" :text="getStatusText(record.status)" />
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a @click="handleEdit(record)">编辑</a>
            <a @click="handleViewHistory(record)">历史</a>
            <a-popconfirm title="确定要删除吗？" @confirm="handleDelete(record)">
              <a style="color: #ff4d4f">删除</a>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- 新增/编辑弹窗 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      @ok="handleSubmit"
      width="700px"
    >
      <a-form ref="formRef" :model="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="设备编码" name="deviceCode" :rules="[{ required: true, message: '请输入编码' }]">
          <a-input v-model:value="form.deviceCode" placeholder="请输入设备编码" />
        </a-form-item>
        <a-form-item label="设备名称" name="deviceName" :rules="[{ required: true, message: '请输入名称' }]">
          <a-input v-model:value="form.deviceName" placeholder="请输入设备名称" />
        </a-form-item>
        <a-form-item label="所属变电站" name="stationId" :rules="[{ required: true, message: '请选择变电站' }]">
          <a-select v-model:value="form.stationId" placeholder="请选择变电站">
            <a-select-option :value="1">XX变电站</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="设备分类" name="categoryId" :rules="[{ required: true, message: '请选择分类' }]">
          <a-select v-model:value="form.categoryId" placeholder="请选择分类">
            <a-select-option :value="1">变压器</a-select-option>
            <a-select-option :value="2">开关设备</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="设备型号" name="deviceModel">
          <a-input v-model:value="form.deviceModel" placeholder="请输入设备型号" />
        </a-form-item>
        <a-form-item label="制造商" name="manufacturer">
          <a-input v-model:value="form.manufacturer" placeholder="请输入制造商" />
        </a-form-item>
        <a-form-item label="安装位置" name="location">
          <a-input v-model:value="form.location" placeholder="请输入安装位置" />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="form.status">
            <a-radio :value="1">正常</a-radio>
            <a-radio :value="2">故障</a-radio>
            <a-radio :value="3">检修</a-radio>
            <a-radio :value="4">停用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 二维码弹窗 -->
    <a-modal v-model:open="qrCodeVisible" title="设备二维码" :footer="null">
      <div class="qr-code-container">
        <div ref="qrCodeRef" class="qr-code"></div>
        <p>{{ currentDevice?.deviceName }} ({{ currentDevice?.deviceCode }})</p>
        <a-button type="primary" block @click="handleDownloadQrCode">下载二维码</a-button>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, h } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined, QrcodeOutlined } from '@ant-design/icons-vue'

const columns = [
  { title: '设备编码', dataIndex: 'deviceCode', key: 'deviceCode' },
  { title: '设备名称', dataIndex: 'deviceName', key: 'deviceName' },
  { title: '设备类型', dataIndex: 'categoryName', key: 'categoryName' },
  { title: '变电站', dataIndex: 'stationName', key: 'stationName' },
  { title: '安装位置', dataIndex: 'location', key: 'location' },
  { title: '二维码', dataIndex: 'qrCode', key: 'qrCode' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'action', width: 180 }
]

const searchText = ref('')
const filterStation = ref('')
const dataSource = ref([])
const loading = ref(false)
const pagination = ref({ current: 1, pageSize: 10, total: 0 })
const modalVisible = ref(false)
const modalTitle = ref('新增设备')
const formRef = ref(null)
const rowSelection = {
  selectedRowKeys: [],
  onChange: (selectedRowKeys) => {
    rowSelection.selectedRowKeys = selectedRowKeys
  }
}

const form = ref({
  deviceId: null,
  deviceCode: '',
  deviceName: '',
  stationId: null,
  categoryId: null,
  deviceModel: '',
  manufacturer: '',
  location: '',
  status: 1
})

const qrCodeVisible = ref(false)
const currentDevice = ref(null)
const qrCodeRef = ref(null)

const getStatusBadge = (status) => {
  const map = { 1: 'success', 2: 'error', 3: 'warning', 4: 'default' }
  return map[status] || 'default'
}

const getStatusText = (status) => {
  const map = { 1: '正常', 2: '故障', 3: '检修', 4: '停用' }
  return map[status] || '未知'
}

const handleSearch = () => {}

const handleTableChange = (pag) => {
  pagination.value.current = pag.current
}

const showAddModal = () => {
  modalTitle.value = '新增设备'
  resetForm()
  modalVisible.value = true
}

const handleEdit = (record) => {
  modalTitle.value = '编辑设备'
  form.value = { ...record }
  modalVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    message.success('操作成功')
    modalVisible.value = false
  } catch (error) {
    console.error('验证失败:', error)
  }
}

const resetForm = () => {
  form.value = {
    deviceId: null,
    deviceCode: '',
    deviceName: '',
    stationId: null,
    categoryId: null,
    deviceModel: '',
    manufacturer: '',
    location: '',
    status: 1
  }
}

const handleShowQrCode = (record) => {
  currentDevice.value = record
  qrCodeVisible.value = true
  // TODO: 生成二维码
}

const handleDownloadQrCode = () => {
  message.success('下载成功')
}

const handleBatchQrCode = () => {
  if (rowSelection.selectedRowKeys.length === 0) {
    message.warning('请先选择设备')
    return
  }
  message.info(`批量生成 ${rowSelection.selectedRowKeys.length} 个二维码`)
}

const handleViewHistory = (record) => {
  message.info('查看历史记录')
}

const handleDelete = (record) => {
  message.success('删除成功')
}
</script>

<style scoped>
.devices-page {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.qr-code-container {
  text-align: center;
  padding: 20px;
}

.qr-code {
  width: 200px;
  height: 200px;
  margin: 0 auto 20px;
  border: 1px dashed #d9d9d9;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>

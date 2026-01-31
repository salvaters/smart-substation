<template>
  <div class="stations-page">
    <div class="page-header">
      <a-input-search
        v-model:value="searchText"
        placeholder="搜索变电站"
        style="width: 300px"
        @search="handleSearch"
      />
      <a-button type="primary" @click="showAddModal">
        <template #icon><PlusOutlined /></template>
        新增变电站
      </a-button>
    </div>

    <a-table
      :columns="columns"
      :data-source="dataSource"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
      row-key="stationId"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="getStatusColor(record.status)">
            {{ getStatusText(record.status) }}
          </a-tag>
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a @click="handleEdit(record)">编辑</a>
            <a @click="handleViewDevices(record)">设备</a>
            <a-popconfirm
              title="确定要删除吗？"
              @confirm="handleDelete(record)"
            >
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
      @cancel="handleCancel"
      width="600px"
    >
      <a-form ref="formRef" :model="form" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="变电站编码" name="stationCode" :rules="[{ required: true, message: '请输入编码' }]">
          <a-input v-model:value="form.stationCode" placeholder="请输入变电站编码" />
        </a-form-item>
        <a-form-item label="变电站名称" name="stationName" :rules="[{ required: true, message: '请输入名称' }]">
          <a-input v-model:value="form.stationName" placeholder="请输入变电站名称" />
        </a-form-item>
        <a-form-item label="变电站类型" name="stationType">
          <a-select v-model:value="form.stationType" placeholder="请选择类型">
            <a-select-option value="500kV">500kV</a-select-option>
            <a-select-option value="220kV">220kV</a-select-option>
            <a-select-option value="110kV">110kV</a-select-option>
            <a-select-option value="35kV">35kV</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="地址" name="address">
          <a-input v-model:value="form.address" placeholder="请输入地址" />
        </a-form-item>
        <a-form-item label="负责人" name="responsiblePerson">
          <a-input v-model:value="form.responsiblePerson" placeholder="请输入负责人" />
        </a-form-item>
        <a-form-item label="联系电话" name="responsiblePhone">
          <a-input v-model:value="form.responsiblePhone" placeholder="请输入联系电话" />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="form.status">
            <a-radio :value="1">运行中</a-radio>
            <a-radio :value="2">停运</a-radio>
            <a-radio :value="3">检修中</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'

const columns = [
  { title: '编码', dataIndex: 'stationCode', key: 'stationCode' },
  { title: '名称', dataIndex: 'stationName', key: 'stationName' },
  { title: '类型', dataIndex: 'stationType', key: 'stationType' },
  { title: '地址', dataIndex: 'address', key: 'address' },
  { title: '负责人', dataIndex: 'responsiblePerson', key: 'responsiblePerson' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'action', width: 180 }
]

const searchText = ref('')
const dataSource = ref([
  {
    stationId: 1,
    stationCode: 'ST001',
    stationName: 'XX变电站',
    stationType: '220kV',
    address: 'XX市XX区',
    responsiblePerson: '张三',
    responsiblePhone: '13800138000',
    status: 1
  }
])
const loading = ref(false)
const pagination = ref({ current: 1, pageSize: 10, total: 0 })
const modalVisible = ref(false)
const modalTitle = ref('新增变电站')
const formRef = ref(null)

const form = ref({
  stationId: null,
  stationCode: '',
  stationName: '',
  stationType: '220kV',
  address: '',
  responsiblePerson: '',
  responsiblePhone: '',
  status: 1
})

const getStatusColor = (status) => {
  const map = { 1: 'green', 2: 'red', 3: 'orange' }
  return map[status] || 'default'
}

const getStatusText = (status) => {
  const map = { 1: '运行中', 2: '停运', 3: '检修中' }
  return map[status] || '未知'
}

const handleSearch = () => {
  // TODO: 搜索逻辑
}

const handleTableChange = (pag) => {
  pagination.value.current = pag.current
}

const showAddModal = () => {
  modalTitle.value = '新增变电站'
  form.value = {
    stationId: null,
    stationCode: '',
    stationName: '',
    stationType: '220kV',
    address: '',
    responsiblePerson: '',
    responsiblePhone: '',
    status: 1
  }
  modalVisible.value = true
}

const handleEdit = (record) => {
  modalTitle.value = '编辑变电站'
  form.value = { ...record }
  modalVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    // TODO: 提交数据
    message.success('操作成功')
    modalVisible.value = false
  } catch (error) {
    console.error('验证失败:', error)
  }
}

const handleCancel = () => {
  modalVisible.value = false
}

const handleViewDevices = (record) => {
  // 跳转到设备管理页面
}

const handleDelete = (record) => {
  message.success('删除成功')
}
</script>

<style scoped>
.stations-page {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}
</style>

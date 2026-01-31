<template>
  <div class="plans-page">
    <div class="page-header">
      <a-space>
        <a-select v-model:value="filterStatus" placeholder="状态" style="width: 120px" allow-clear>
          <a-select-option value="">全部</a-select-option>
          <a-select-option value="active">活动中</a-select-option>
          <a-select-option value="paused">暂停</a-select-option>
        </a-select>
      </a-space>
      <a-button type="primary" @click="showAddModal">
        <template #icon><PlusOutlined /></template>
        新增计划
      </a-button>
    </div>

    <a-table
      :columns="columns"
      :data-source="dataSource"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
      row-key="planId"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'inspectionType'">
          <a-tag>{{ getInspectionType(record.inspectionType) }}</a-tag>
        </template>
        <template v-else-if="column.key === 'cycleType'">
          <a-tag color="blue">{{ getCycleType(record.cycleType) }}</a-tag>
        </template>
        <template v-else-if="column.key === 'status'">
          <a-badge :status="getStatusBadge(record.status)" :text="getStatusText(record.status)" />
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a @click="handleEdit(record)">编辑</a>
            <a v-if="record.status === 'active'" @click="handlePause(record)">暂停</a>
            <a v-else @click="handleResume(record)">恢复</a>
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
      <a-form ref="formRef" :model="form" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="计划编码" name="planCode" :rules="[{ required: true, message: '请输入编码' }]">
          <a-input v-model:value="form.planCode" placeholder="请输入计划编码" />
        </a-form-item>
        <a-form-item label="计划名称" name="planName" :rules="[{ required: true, message: '请输入名称' }]">
          <a-input v-model:value="form.planName" placeholder="请输入计划名称" />
        </a-form-item>
        <a-form-item label="变电站" name="stationId" :rules="[{ required: true, message: '请选择变电站' }]">
          <a-select v-model:value="form.stationId" placeholder="请选择变电站">
            <a-select-option :value="1">XX变电站</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="巡检模板" name="templateId" :rules="[{ required: true, message: '请选择模板' }]">
          <a-select v-model:value="form.templateId" placeholder="请选择模板">
            <a-select-option :value="1">变压器日巡模板</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="执行人" name="assigneeId" :rules="[{ required: true, message: '请选择执行人' }]">
          <a-select v-model:value="form.assigneeId" placeholder="请选择执行人">
            <a-select-option :value="3">巡检员A</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="周期类型" name="cycleType">
          <a-select v-model:value="form.cycleType" placeholder="请选择周期类型">
            <a-select-option value="daily">每日</a-select-option>
            <a-select-option value="weekly">每周</a-select-option>
            <a-select-option value="monthly">每月</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="开始日期" name="startDate">
          <a-date-picker v-model:value="form.startDate" style="width: 100%" />
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
  { title: '计划编码', dataIndex: 'planCode', key: 'planCode' },
  { title: '计划名称', dataIndex: 'planName', key: 'planName' },
  { title: '变电站', dataIndex: 'stationName', key: 'stationName' },
  { title: '巡检类型', dataIndex: 'inspectionType', key: 'inspectionType' },
  { title: '周期', dataIndex: 'cycleType', key: 'cycleType' },
  { title: '执行人', dataIndex: 'assigneeName', key: 'assigneeName' },
  { title: '下次执行', dataIndex: 'nextExecuteTime', key: 'nextExecuteTime' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'action', width: 200 }
]

const filterStatus = ref('')
const dataSource = ref([])
const loading = ref(false)
const pagination = ref({ current: 1, pageSize: 10, total: 0 })
const modalVisible = ref(false)
const modalTitle = ref('新增计划')
const formRef = ref(null)

const form = ref({
  planId: null,
  planCode: '',
  planName: '',
  stationId: null,
  templateId: null,
  assigneeId: null,
  cycleType: 'daily',
  startDate: null
})

const getInspectionType = (type) => {
  const map = { daily: '日巡', weekly: '周巡', monthly: '月巡', special: '专项' }
  return map[type] || type
}

const getCycleType = (type) => {
  const map = { daily: '每日', weekly: '每周', monthly: '每月' }
  return map[type] || type
}

const getStatusBadge = (status) => {
  const map = { active: 'processing', paused: 'default', completed: 'success' }
  return map[status] || 'default'
}

const getStatusText = (status) => {
  const map = { active: '活动中', paused: '暂停', completed: '已完成' }
  return map[status] || status
}

const handleTableChange = (pag) => {
  pagination.value.current = pag.current
}

const showAddModal = () => {
  modalTitle.value = '新增计划'
  resetForm()
  modalVisible.value = true
}

const handleEdit = (record) => {
  modalTitle.value = '编辑计划'
  form.value = { ...record }
  modalVisible.value = true
}

const handlePause = (record) => {
  message.success('已暂停')
}

const handleResume = (record) => {
  message.success('已恢复')
}

const handleDelete = (record) => {
  message.success('删除成功')
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
    planId: null,
    planCode: '',
    planName: '',
    stationId: null,
    templateId: null,
    assigneeId: null,
    cycleType: 'daily',
    startDate: null
  }
}
</script>

<style scoped>
.plans-page {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}
</style>

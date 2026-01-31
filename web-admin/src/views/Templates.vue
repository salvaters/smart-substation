<template>
  <div class="templates-page">
    <div class="page-header">
      <a-input-search
        v-model:value="searchText"
        placeholder="搜索模板"
        style="width: 300px"
        @search="handleSearch"
      />
      <a-button type="primary" @click="showAddModal">
        <template #icon><PlusOutlined /></template>
        新增模板
      </a-button>
    </div>

    <a-row :gutter="16">
      <a-col :span="8" v-for="template in templates" :key="template.templateId">
        <a-card :title="template.templateName" class="template-card">
          <template #extra>
            <a-dropdown>
              <a class="ant-dropdown-link" @click.prevent>
                更多 <DownOutlined />
              </a>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="handleEdit(template)">编辑</a-menu-item>
                  <a-menu-item @click="handleCopy(template)">复制</a-menu-item>
                  <a-menu-divider />
                  <a-menu-item @click="handleDelete(template)">删除</a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </template>
          <p><strong>编码：</strong>{{ template.templateCode }}</p>
          <p><strong>类型：</strong>{{ getInspectionType(template.inspectionType) }}</p>
          <p><strong>周期：</strong>{{ template.cycleDays }} 天</p>
          <p><strong>项目数：</strong>{{ template.itemCount }} 项</p>
          <a-divider />
          <a-space>
            <a-button size="small" @click="handleViewItems(template)">查看项目</a-button>
            <a-button size="small" type="primary" @click="handleUseTemplate(template)">使用</a-button>
          </a-space>
        </a-card>
      </a-col>
    </a-row>

    <!-- 新增/编辑弹窗 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      @ok="handleSubmit"
      width="700px"
    >
      <a-form ref="formRef" :model="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="模板编码" name="templateCode" :rules="[{ required: true, message: '请输入编码' }]">
          <a-input v-model:value="form.templateCode" placeholder="请输入模板编码" />
        </a-form-item>
        <a-form-item label="模板名称" name="templateName" :rules="[{ required: true, message: '请输入名称' }]">
          <a-input v-model:value="form.templateName" placeholder="请输入模板名称" />
        </a-form-item>
        <a-form-item label="巡检类型" name="inspectionType" :rules="[{ required: true, message: '请选择类型' }]">
          <a-select v-model:value="form.inspectionType" placeholder="请选择巡检类型">
            <a-select-option value="daily">日巡</a-select-option>
            <a-select-option value="weekly">周巡</a-select-option>
            <a-select-option value="monthly">月巡</a-select-option>
            <a-select-option value="special">专项巡检</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="周期天数" name="cycleDays">
          <a-input-number v-model:value="form.cycleDays" :min="1" style="width: 100%" />
        </a-form-item>
        <a-form-item label="模板描述" name="description">
          <a-textarea v-model:value="form.description" :rows="3" placeholder="请输入模板描述" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined, DownOutlined } from '@ant-design/icons-vue'

const searchText = ref('')
const templates = ref([
  {
    templateId: 1,
    templateCode: 'TPL001',
    templateName: '变压器日巡模板',
    inspectionType: 'daily',
    cycleDays: 1,
    itemCount: 12
  },
  {
    templateId: 2,
    templateCode: 'TPL002',
    templateName: '开关设备周巡模板',
    inspectionType: 'weekly',
    cycleDays: 7,
    itemCount: 15
  }
])
const modalVisible = ref(false)
const modalTitle = ref('新增模板')
const formRef = ref(null)

const form = ref({
  templateId: null,
  templateCode: '',
  templateName: '',
  inspectionType: 'daily',
  cycleDays: 1,
  description: ''
})

const getInspectionType = (type) => {
  const map = { daily: '日巡', weekly: '周巡', monthly: '月巡', special: '专项' }
  return map[type] || type
}

const handleSearch = () => {}

const showAddModal = () => {
  modalTitle.value = '新增模板'
  resetForm()
  modalVisible.value = true
}

const handleEdit = (record) => {
  modalTitle.value = '编辑模板'
  form.value = { ...record }
  modalVisible.value = true
}

const handleCopy = (record) => {
  message.success('复制成功')
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
    templateId: null,
    templateCode: '',
    templateName: '',
    inspectionType: 'daily',
    cycleDays: 1,
    description: ''
  }
}

const handleViewItems = (record) => {
  message.info('查看模板项目')
}

const handleUseTemplate = (record) => {
  message.info('使用模板创建计划')
}
</script>

<style scoped>
.templates-page {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.template-card {
  margin-bottom: 16px;
}

.template-card p {
  margin: 5px 0;
}
</style>

<template>
  <div class="settings-page">
    <a-row :gutter="16">
      <a-col :span="16">
        <!-- 基本设置 -->
        <a-card title="基本设置" class="setting-card">
          <a-form :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
            <a-form-item label="系统名称">
              <a-input v-model:value="settings.systemName" placeholder="智能变电站巡检系统" />
            </a-form-item>
            <a-form-item label="公司名称">
              <a-input v-model:value="settings.companyName" placeholder="请输入公司名称" />
            </a-form-item>
            <a-form-item label="联系电话">
              <a-input v-model:value="settings.contactPhone" placeholder="请输入联系电话" />
            </a-form-item>
          </a-form>
        </a-card>

        <!-- 巡检设置 -->
        <a-card title="巡检设置" class="setting-card">
          <a-form :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
            <a-form-item label="自动创建任务天数">
              <a-input-number v-model:value="settings.autoCreateDays" :min="1" :max="30" style="width: 200px" />
              <span class="text-gray">天</span>
            </a-form-item>
            <a-form-item label="任务默认分配方式">
              <a-radio-group v-model:value="settings.defaultAssignType">
                <a-radio value="auto">自动分配</a-radio>
                <a-radio value="manual">手动分配</a-radio>
              </a-radio-group>
            </a-form-item>
            <a-form-item label="是否需要GPS验证">
              <a-switch v-model:checked="settings.requireGps" />
              <span class="text-gray ml-2">开启后巡检需要GPS位置验证</span>
            </a-form-item>
          </a-form>
        </a-card>

        <!-- 报告设置 -->
        <a-card title="报告设置" class="setting-card">
          <a-form :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
            <a-form-item label="报告审批流程">
              <a-checkbox-group v-model:value="settings.approvalFlow">
                <a-checkbox value="monitor">班长审批</a-checkbox>
                <a-checkbox value="admin">管理员审批</a-checkbox>
              </a-checkbox-group>
            </a-form-item>
            <a-form-item label="自动生成报告">
              <a-switch v-model:checked="settings.autoGenerateReport" />
              <span class="text-gray ml-2">任务完成后自动生成报告</span>
            </a-form-item>
            <a-form-item label="报告保留时间">
              <a-input-number v-model:value="settings.reportRetentionDays" :min="30" :max="3650" style="width: 200px" />
              <span class="text-gray">天</span>
            </a-form-item>
          </a-form>
        </a-card>

        <!-- 文件设置 -->
        <a-card title="文件设置" class="setting-card">
          <a-form :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
            <a-form-item label="最大上传大小">
              <a-input-number v-model:value="settings.maxFileSize" :min="1" :max="100" style="width: 200px" />
              <span class="text-gray">MB</span>
            </a-form-item>
            <a-form-item label="允许的文件类型">
              <a-select
                v-model:value="settings.allowedFileTypes"
                mode="tags"
                placeholder="请选择文件类型"
                style="width: 100%"
              >
                <a-select-option value="jpg">JPG</a-select-option>
                <a-select-option value="jpeg">JPEG</a-select-option>
                <a-select-option value="png">PNG</a-select-option>
                <a-select-option value="pdf">PDF</a-select-option>
              </a-select>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>

      <a-col :span="8">
        <!-- 快捷操作 -->
        <a-card title="快捷操作" class="setting-card">
          <a-space direction="vertical" style="width: 100%">
            <a-button block @click="handleCacheClear">
              <template #icon><DeleteOutlined /></template>
              清除系统缓存
            </a-button>
            <a-button block @click="handleDataBackup">
              <template #icon><CloudDownloadOutlined /></template>
              数据备份
            </a-button>
            <a-button block @click="handleDataRestore">
              <template #icon><CloudUploadOutlined /></template>
              数据恢复
            </a-button>
            <a-button block @click="handleLogClean">
              <template #icon><ClearOutlined /></template>
              清理日志
            </a-button>
          </a-space>
        </a-card>

        <!-- 系统信息 -->
        <a-card title="系统信息" class="setting-card">
          <a-descriptions :column="1" size="small">
            <a-descriptions-item label="系统版本">v1.0.0</a-descriptions-item>
            <a-descriptions-item label="Java版本">17+</a-descriptions-item>
            <a-descriptions-item label="数据库">MySQL 8.0+</a-descriptions-item>
            <a-descriptions-item label="缓存">Redis 7.0+</a-descriptions-item>
            <a-descriptions-item label="运行时间">{{ systemInfo.uptime }}</a-descriptions-item>
            <a-descriptions-item label="内存使用">{{ systemInfo.memory }}</a-descriptions-item>
          </a-descriptions>
        </a-card>
      </a-col>
    </a-row>

    <!-- 保存按钮 -->
    <div class="save-actions">
      <a-space>
        <a-button @click="handleReset">重置</a-button>
        <a-button type="primary" @click="handleSave" :loading="saving">
          保存设置
        </a-button>
      </a-space>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  DeleteOutlined,
  CloudDownloadOutlined,
  CloudUploadOutlined,
  ClearOutlined
} from '@ant-design/icons-vue'

const saving = ref(false)

const settings = ref({
  systemName: '智能变电站巡检系统',
  companyName: '',
  contactPhone: '',
  autoCreateDays: 7,
  defaultAssignType: 'manual',
  requireGps: false,
  approvalFlow: ['monitor', 'admin'],
  autoGenerateReport: true,
  reportRetentionDays: 365,
  maxFileSize: 10,
  allowedFileTypes: ['jpg', 'jpeg', 'png', 'pdf']
})

const systemInfo = ref({
  uptime: '0天',
  memory: '256MB'
})

const handleCacheClear = () => {
  message.success('缓存已清除')
}

const handleDataBackup = () => {
  message.info('数据备份功能开发中')
}

const handleDataRestore = () => {
  message.info('数据恢复功能开发中')
}

const handleLogClean = () => {
  message.success('日志已清理')
}

const handleReset = () => {
  settings.value = {
    systemName: '智能变电站巡检系统',
    companyName: '',
    contactPhone: '',
    autoCreateDays: 7,
    defaultAssignType: 'manual',
    requireGps: false,
    approvalFlow: ['monitor', 'admin'],
    autoGenerateReport: true,
    reportRetentionDays: 365,
    maxFileSize: 10,
    allowedFileTypes: ['jpg', 'jpeg', 'png', 'pdf']
  }
  message.info('设置已重置')
}

const handleSave = () => {
  saving.value = true
  setTimeout(() => {
    saving.value = false
    message.success('设置保存成功')
  }, 1000)
}

onMounted(() => {
  // TODO: 加载系统设置和系统信息
})
</script>

<style scoped>
.settings-page {
  padding: 0;
}

.setting-card {
  margin-bottom: 16px;
}

.text-gray {
  color: #999;
}

.ml-2 {
  margin-left: 8px;
}

.save-actions {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 10;
}
</style>

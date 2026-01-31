<template>
  <div class="defects-page">
    <van-nav-bar title="缺陷上报" left-arrow @click-left="router.back()" />

    <!-- 缺陷列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <van-card
          v-for="defect in defects"
          :key="defect.defectId"
          :title="`缺陷: ${defect.defectCode}`"
          @click="handleViewDetail(defect)"
        >
          <template #desc>
            <div class="defect-desc">{{ defect.defectDescription }}</div>
          </template>
          <template #tags>
            <van-tag :type="getDefectTypeTag(defect.defectType)">
              {{ getDefectTypeText(defect.defectType) }}
            </van-tag>
            <van-tag :type="getStatusTag(defect.status)">
              {{ getStatusText(defect.status) }}
            </van-tag>
          </template>
          <template #footer>
            <div class="defect-footer">
              <span>{{ defect.deviceName }}</span>
              <span>{{ formatTime(defect.discoverTime) }}</span>
            </div>
          </template>
        </van-card>
      </van-list>
    </van-pull-refresh>

    <!-- 浮动按钮 -->
    <van-floating-bubble
      axis="xy"
      icon="plus"
      @click="showReportForm = true"
    />

    <!-- 上报表单弹窗 -->
    <van-popup
      v-model:show="showReportForm"
      position="bottom"
      :style="{ height: '90%' }"
    >
      <van-nav-bar
        title="上报缺陷"
        left-text="取消"
        @click-left="showReportForm = false"
        right-text="提交"
        @click-right="handleSubmit"
      />
      <div class="report-form">
        <van-form ref="formRef">
          <van-cell-group inset>
            <van-field
              v-model="form.deviceCode"
              name="deviceCode"
              label="设备编号"
              placeholder="请输入或扫码"
              readonly
              is-link
              @click="handleScanDevice"
            />
            <van-field
              v-model="form.defectType"
              name="defectType"
              label="缺陷类型"
            >
              <template #input>
                <van-radio-group v-model="form.defectType" direction="horizontal">
                  <van-radio name="general">一般</van-radio>
                  <van-radio name="serious">严重</van-radio>
                  <van-radio name="critical">危急</van-radio>
                </van-radio-group>
              </template>
            </van-field>
            <van-field
              v-model="form.defectDescription"
              name="defectDescription"
              label="缺陷描述"
              type="textarea"
              rows="4"
              placeholder="请详细描述缺陷情况"
              :rules="[{ required: true, message: '请输入缺陷描述' }]"
            />
            <van-field
              name="photos"
              label="现场照片"
            >
              <template #input>
                <van-uploader
                  v-model="form.photos"
                  multiple
                  :max-count="5"
                />
              </template>
            </van-field>
          </van-cell-group>
        </van-form>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import dayjs from 'dayjs'

const router = useRouter()

const defects = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const showReportForm = ref(false)
const formRef = ref(null)

const form = ref({
  deviceCode: '',
  defectType: 'general',
  defectDescription: '',
  photos: []
})

const onLoad = async () => {
  // TODO: 加载缺陷列表
  loading.value = false
}

const onRefresh = () => {
  defects.value = []
  finished.value = false
  loading.value = true
  onLoad()
}

const handleScanDevice = () => {
  // 跳转到扫码页面
  router.push('/scan?mode=defect')
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    // TODO: 提交缺陷数据
    showSuccessToast('上报成功')
    showReportForm.value = false
  } catch (error) {
    showToast('提交失败')
  }
}

const handleViewDetail = (defect) => {
  // 查看缺陷详情
}

const getDefectTypeTag = (type) => {
  const map = {
    general: 'primary',
    serious: 'warning',
    critical: 'danger'
  }
  return map[type] || 'default'
}

const getDefectTypeText = (type) => {
  const map = {
    general: '一般',
    serious: '严重',
    critical: '危急'
  }
  return map[type] || type
}

const getStatusTag = (status) => {
  const map = {
    pending: 'warning',
    confirmed: 'primary',
    repairing: 'primary',
    completed: 'success'
  }
  return map[status] || 'default'
}

const getStatusText = (status) => {
  const map = {
    pending: '待处理',
    confirmed: '已确认',
    repairing: '处理中',
    completed: '已完成'
  }
  return map[status] || status
}

const formatTime = (time) => {
  return dayjs(time).format('MM-DD HH:mm')
}
</script>

<style scoped>
.defects-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.defect-desc {
  margin: 8px 0;
  color: #666;
}

.defect-footer {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}

:deep(.van-card) {
  margin-top: 10px;
  border-radius: 8px;
}

.report-form {
  padding: 20px;
}
</style>

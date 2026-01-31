<template>
  <div class="scan-page">
    <van-nav-bar title="扫码巡检" left-arrow @click-left="router.back()" />

    <!-- 扫码区域 -->
    <div class="scan-area">
      <div class="scan-box" v-if="!scannedDevice">
        <van-icon name="scan" size="80" color="#1890ff" />
        <p>将二维码放入框内即可自动扫描</p>
      </div>

      <div class="device-info" v-else>
        <van-cell-group inset title="设备信息">
          <van-cell title="设备名称" :value="scannedDevice.deviceName" />
          <van-cell title="设备编号" :value="scannedDevice.deviceCode" />
          <van-cell title="设备位置" :value="scannedDevice.location" />
          <van-cell title="设备类型" :value="scannedDevice.categoryName" />
        </van-cell-group>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="actions">
      <van-button
        v-if="!scannedDevice"
        type="primary"
        block
        size="large"
        @click="handleScan"
      >
        开始扫描
      </van-button>
      <template v-else>
        <van-button
          type="primary"
          block
          @click="showRecordForm = true"
        >
          填写巡检记录
        </van-button>
        <van-button
          plain
          block
          @click="scannedDevice = null"
        >
          重新扫描
        </van-button>
      </template>
      <van-button
        plain
        block
        @click="showManualInput = true"
      >
        手动输入设备码
      </van-button>
    </div>

    <!-- 巡检记录弹窗 -->
    <van-popup
      v-model:show="showRecordForm"
      position="bottom"
      :style="{ height: '80%' }"
    >
      <van-nav-bar
        title="填写巡检记录"
        left-text="取消"
        @click-left="showRecordForm = false"
        right-text="提交"
        @click-right="handleSubmitRecord"
      />
      <div class="record-form">
        <van-form ref="formRef">
          <van-cell-group inset>
            <van-field
              v-model="record.checkValue"
              name="checkValue"
              label="检查结果"
              type="textarea"
              rows="3"
              placeholder="请输入检查结果"
              :rules="[{ required: true, message: '请输入检查结果' }]"
            />
            <van-field
              name="checkResult"
              label="结果判定"
            >
              <template #input>
                <van-radio-group v-model="record.checkResult" direction="horizontal">
                  <van-radio name="normal">正常</van-radio>
                  <van-radio name="abnormal">异常</van-radio>
                  <van-radio name="serious">严重</van-radio>
                </van-radio-group>
              </template>
            </van-field>
            <van-field
              v-model="record.description"
              name="description"
              label="说明描述"
              type="textarea"
              rows="2"
              placeholder="详细说明（可选）"
            />
            <van-field
              name="photos"
              label="现场照片"
            >
              <template #input>
                <van-uploader
                  v-model="record.photos"
                  multiple
                  :max-count="3"
                  :before-read="compressImage"
                />
              </template>
            </van-field>
          </van-cell-group>
        </van-form>
      </div>
    </van-popup>

    <!-- 手动输入弹窗 -->
    <van-dialog
      v-model:show="showManualInput"
      title="手动输入设备码"
      show-cancel-button
      @confirm="handleManualInput"
    >
      <van-field
        v-model="manualQrCode"
        placeholder="请输入设备二维码"
      />
    </van-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import { getDeviceByQrCode, submitRecord } from '@/api/task'
import { useOfflineStore } from '@/stores/offline'
import Compressor from 'compressorjs'

const router = useRouter()
const route = useRoute()
const offlineStore = useOfflineStore()

const scannedDevice = ref(null)
const showRecordForm = ref(false)
const showManualInput = ref(false)
const manualQrCode = ref('')
const formRef = ref(null)

const record = ref({
  checkValue: '',
  checkResult: 'normal',
  description: '',
  photos: []
})

const handleScan = async () => {
  try {
    // 模拟扫码（实际应调用扫码库）
    const qrCode = 'DEV001' // 这里应该是扫码结果
    await loadDevice(qrCode)
  } catch (error) {
    showToast('扫码失败，请重试')
  }
}

const loadDevice = async (qrCode) => {
  try {
    const device = await getDeviceByQrCode(qrCode)
    scannedDevice.value = device
  } catch (error) {
    showToast('设备不存在或已停用')
  }
}

const handleManualInput = async () => {
  if (!manualQrCode.value) {
    showToast('请输入设备码')
    return
  }
  await loadDevice(manualQrCode.value)
}

const compressImage = async (file) => {
  return new Promise((resolve) => {
    new Compressor(file, {
      quality: 0.8,
      maxWidth: 1080,
      success(result) {
        resolve(result)
      },
      error() {
        resolve(file)
      }
    })
  })
}

const handleSubmitRecord = async () => {
  try {
    await formRef.value.validate()

    const data = {
      deviceId: scannedDevice.value.deviceId,
      checkValue: record.value.checkValue,
      checkResult: record.value.checkResult,
      description: record.value.description,
      photoUrls: record.value.photos.map(p => p.url).join(',')
    }

    await submitRecord(data)
    showSuccessToast('记录提交成功')
    showRecordForm.value = false

    // 重置表单
    record.value = {
      checkValue: '',
      checkResult: 'normal',
      description: '',
      photos: []
    }
  } catch (error) {
    showToast('提交失败')
  }
}
</script>

<style scoped>
.scan-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.scan-area {
  padding: 40px 20px;
}

.scan-box {
  text-align: center;
}

.scan-box p {
  margin-top: 20px;
  color: #999;
}

.actions {
  padding: 20px;
}

.actions .van-button {
  margin-bottom: 10px;
}

.record-form {
  padding: 20px;
}
</style>

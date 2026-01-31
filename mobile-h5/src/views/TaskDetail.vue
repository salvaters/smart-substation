<template>
  <div class="task-detail-page">
    <van-nav-bar title="任务详情" left-arrow @click-left="router.back()" />

    <div v-if="task" class="content">
      <!-- 任务信息 -->
      <van-cell-group inset title="任务信息">
        <van-cell title="任务编号" :value="task.taskCode" />
        <van-cell title="任务标题" :value="task.title" />
        <van-cell title="变电站" :value="task.stationName" />
        <van-cell title="巡检类型" :value="getInspectionType(task.inspectionType)" />
        <van-cell title="计划开始时间" :value="formatTime(task.plannedStartTime)" />
        <van-cell title="计划结束时间" :value="formatTime(task.plannedEndTime)" />
        <van-cell title="任务进度">
          <template #value>
            <van-progress :percentage="task.progress || 0" />
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 设备列表 -->
      <van-cell-group inset title="巡检设备">
        <van-cell
          v-for="device in devices"
          :key="device.deviceId"
          :title="device.deviceName"
          :label="device.location"
          is-link
          @click="handleInspectDevice(device)"
        >
          <template #icon>
            <van-icon name="qr" size="20" />
          </template>
          <template #right-icon>
            <van-tag :type="device.status === 'completed' ? 'success' : 'default'">
              {{ device.status === 'completed' ? '已检' : '待检' }}
            </van-tag>
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 操作按钮 -->
      <div class="actions">
        <van-button
          v-if="task.status === 'pending'"
          type="primary"
          block
          @click="handleStartTask"
        >
          开始任务
        </van-button>
        <van-button
          v-if="task.status === 'in_progress'"
          type="success"
          block
          @click="handleCompleteTask"
          :disabled="task.progress < 100"
        >
          完成任务
        </van-button>
        <van-button
          v-if="task.status === 'in_progress'"
          plain
          block
          @click="router.push('/scan')"
        >
          扫码巡检
        </van-button>
      </div>
    </div>

    <van-loading v-else size="24px">加载中...</van-loading>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showSuccessToast, showConfirmDialog } from 'vant'
import { getTaskDetail, startTask, completeTask } from '@/api/task'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()

const task = ref(null)
const devices = ref([])

const loadTaskDetail = async () => {
  try {
    const taskId = route.params.id
    const data = await getTaskDetail(taskId)
    task.value = data
    devices.value = data.devices || []
  } catch (error) {
    showToast('加载任务详情失败')
  }
}

const handleStartTask = async () => {
  try {
    await startTask(task.value.taskId)
    showSuccessToast('任务已开始')
    task.value.status = 'in_progress'
  } catch (error) {
    showToast('操作失败')
  }
}

const handleCompleteTask = async () => {
  try {
    await showConfirmDialog({
      title: '确认完成',
      message: '确认完成此巡检任务吗？'
    })

    await completeTask(task.value.taskId, {})
    showSuccessToast('任务已完成')
    router.back()
  } catch (error) {
    if (error !== 'cancel') {
      showToast('操作失败')
    }
  }
}

const handleInspectDevice = (device) => {
  router.push(`/scan?deviceId=${device.deviceId}`)
}

const getInspectionType = (type) => {
  const map = {
    daily: '日巡',
    weekly: '周巡',
    monthly: '月巡',
    special: '专项'
  }
  return map[type] || type
}

const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

onMounted(() => {
  loadTaskDetail()
})
</script>

<style scoped>
.task-detail-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 20px;
}

.content {
  padding-top: 10px;
}

.actions {
  padding: 20px;
}

.actions .van-button {
  margin-bottom: 10px;
}
</style>

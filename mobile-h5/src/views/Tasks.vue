<template>
  <div class="tasks-page">
    <van-nav-bar title="巡检任务" left-arrow @click-left="router.back()" />

    <!-- 状态筛选 -->
    <van-tabs v-model:active="activeTab" @change="handleTabChange">
      <van-tab title="全部" name="all" />
      <van-tab title="待执行" name="pending" />
      <van-tab title="进行中" name="inProgress" />
      <van-tab title="已完成" name="completed" />
    </van-tabs>

    <!-- 任务列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <van-card
          v-for="task in tasks"
          :key="task.taskId"
          :title="task.title"
          :desc="task.description"
          @click="router.push(`/task/${task.taskId}`)"
        >
          <template #tags>
            <van-tag :type="getStatusType(task.status)">
              {{ getStatusText(task.status) }}
            </van-tag>
          </template>
          <template #footer>
            <div class="task-info">
              <span>{{ task.stationName }}</span>
              <span>{{ formatTime(task.plannedStartTime) }}</span>
            </div>
            <div class="task-progress">
              <van-progress :percentage="task.progress || 0" />
            </div>
          </template>
        </van-card>
      </van-list>
    </van-pull-refresh>

    <!-- 空状态 -->
    <van-empty v-if="tasks.length === 0 && !loading" description="暂无任务" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getMyTasks } from '@/api/task'
import dayjs from 'dayjs'

const router = useRouter()

const activeTab = ref('all')
const tasks = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const page = ref(1)

const onLoad = async () => {
  try {
    const data = await getMyTasks({
      page: page.value,
      pageSize: 10,
      status: activeTab.value === 'all' ? undefined : activeTab.value
    })

    if (refreshing.value) {
      tasks.value = []
      refreshing.value = false
    }

    tasks.value.push(...data.records)
    page.value++

    if (tasks.value.length >= data.total) {
      finished.value = true
    }
  } catch (error) {
    console.error('获取任务列表失败:', error)
  } finally {
    loading.value = false
  }
}

const onRefresh = () => {
  finished.value = false
  loading.value = true
  page.value = 1
  onLoad()
}

const handleTabChange = () => {
  page.value = 1
  tasks.value = []
  finished.value = false
  onLoad()
}

const getStatusType = (status) => {
  const map = {
    pending: 'warning',
    in_progress: 'primary',
    completed: 'success',
    overdue: 'danger'
  }
  return map[status] || 'default'
}

const getStatusText = (status) => {
  const map = {
    pending: '待执行',
    in_progress: '进行中',
    completed: '已完成',
    overdue: '已逾期'
  }
  return map[status] || status
}

const formatTime = (time) => {
  return dayjs(time).format('MM-DD HH:mm')
}
</script>

<style scoped>
.tasks-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.task-info {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.task-progress {
  margin-top: 8px;
}

:deep(.van-card) {
  margin-top: 10px;
  border-radius: 8px;
}
</style>

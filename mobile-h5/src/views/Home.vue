<template>
  <div class="home-page">
    <!-- 离线状态横幅 -->
    <van-notice-bar
      v-if="!offlineStore.isOnline"
      left-icon="info-o"
      text="当前网络离线"
      background="#fff2e8"
      color="#ed6a0c"
    />

    <!-- 顶部用户信息 -->
    <div class="header">
      <div class="user-info">
        <van-image
          round
          width="60"
          height="60"
          :src="userStore.userInfo?.avatar || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'"
        />
        <div class="info">
          <div class="name">{{ userStore.userInfo?.realName || '巡检员' }}</div>
          <div class="role">巡检人员</div>
        </div>
      </div>
      <van-button size="small" @click="handleSync" :loading="offlineStore.isSyncing">
        {{ offlineStore.isSyncing ? '同步中...' : '同步数据' }}
      </van-button>
    </div>

    <!-- 待同步提示 -->
    <van-notice-bar
      v-if="offlineStore.pendingCount > 0"
      left-icon="warning-o"
      :text="`有 ${offlineStore.pendingCount} 条数据待同步`"
      background="#e6f7ff"
      color="#1890ff"
      wrapable
    />

    <!-- 功能菜单 -->
    <div class="menu-grid">
      <div class="menu-item" @click="router.push('/tasks')">
        <van-icon name="todo-list-o" size="40" color="#1890ff" />
        <span>巡检任务</span>
        <van-badge v-if="pendingTaskCount > 0" :content="pendingTaskCount" />
      </div>
      <div class="menu-item" @click="router.push('/scan')">
        <van-icon name="scan" size="40" color="#52c41a" />
        <span>扫码巡检</span>
      </div>
      <div class="menu-item" @click="router.push('/defects')">
        <van-icon name="warning-o" size="40" color="#ff4d4f" />
        <span>缺陷上报</span>
      </div>
      <div class="menu-item" @click="router.push('/profile')">
        <van-icon name="user-o" size="40" color="#faad14" />
        <span>我的</span>
      </div>
    </div>

    <!-- 任务统计 -->
    <div class="stats-card">
      <van-cell-group inset title="今日统计">
        <van-cell title="待执行" :value="stats.pending" />
        <van-cell title="进行中" :value="stats.inProgress" />
        <van-cell title="已完成" :value="stats.completed" />
      </van-cell-group>
    </div>

    <!-- 底部导航 -->
    <van-tabbar v-model="activeTab" route>
      <van-tabbar-item to="/home" icon="home-o">首页</van-tabbar-item>
      <van-tabbar-item to="/tasks" icon="todo-list-o">任务</van-tabbar-item>
      <van-tabbar-item to="/scan" icon="scan">扫码</van-tabbar-item>
      <van-tabbar-item to="/profile" icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useOfflineStore } from '@/stores/offline'

const router = useRouter()
const userStore = useUserStore()
const offlineStore = useOfflineStore()

const activeTab = ref(0)
const pendingTaskCount = ref(0)
const stats = ref({
  pending: 0,
  inProgress: 0,
  completed: 0
})

const handleSync = async () => {
  if (!offlineStore.isOnline) {
    showToast('当前网络离线，无法同步')
    return
  }
  await offlineStore.syncData()
}

onMounted(() => {
  // TODO: 获取今日统计数据
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 60px;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.info .name {
  font-size: 18px;
  font-weight: bold;
}

.info .role {
  font-size: 12px;
  opacity: 0.9;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  padding: 20px;
}

.menu-item {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  position: relative;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.menu-item span {
  font-size: 14px;
  color: #333;
}

.stats-card {
  padding: 0 20px;
  margin-top: 10px;
}
</style>

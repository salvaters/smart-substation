<template>
  <div class="profile-page">
    <!-- 用户信息卡片 -->
    <div class="user-card">
      <van-image
        round
        width="80"
        height="80"
        :src="userStore.userInfo?.avatar || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'"
      />
      <div class="user-info">
        <div class="name">{{ userStore.userInfo?.realName || '巡检员' }}</div>
        <div class="role">{{ getRoleText(userStore.userInfo?.roleId) }}</div>
      </div>
    </div>

    <!-- 统计数据 -->
    <div class="stats-row">
      <div class="stat-item">
        <div class="value">{{ stats.totalTasks }}</div>
        <div class="label">总任务数</div>
      </div>
      <div class="stat-item">
        <div class="value">{{ stats.completedTasks }}</div>
        <div class="label">已完成</div>
      </div>
      <div class="stat-item">
        <div class="value">{{ stats.defects }}</div>
        <div class="label">发现缺陷</div>
      </div>
    </div>

    <!-- 功能列表 -->
    <van-cell-group inset>
      <van-cell title="我的任务" is-link to="/tasks" icon="todo-list-o" />
      <van-cell title="我的缺陷" is-link to="/defects" icon="warning-o" />
      <van-cell title="离线数据" is-link @click="handleOfflineData" icon="down" />
    </van-cell-group>

    <van-cell-group inset>
      <van-cell title="修改密码" is-link icon="lock" @click="handleChangePassword" />
      <van-cell title="关于系统" is-link icon="info-o" value="v1.0.0" />
    </van-cell-group>

    <!-- 退出登录 -->
    <div class="logout-section">
      <van-button type="danger" block @click="handleLogout">
        退出登录
      </van-button>
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
import { showToast, showSuccessToast, showConfirmDialog } from 'vant'
import { useUserStore } from '@/stores/user'
import { useOfflineStore } from '@/stores/offline'
import { logout as apiLogout } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()
const offlineStore = useOfflineStore()

const activeTab = ref(3)
const stats = ref({
  totalTasks: 0,
  completedTasks: 0,
  defects: 0
})

const getRoleText = (roleId) => {
  const map = {
    1: '系统管理员',
    2: '班长',
    3: '巡检员',
    4: '访客'
  }
  return map[roleId] || '巡检员'
}

const handleOfflineData = () => {
  showToast(`待同步数据: ${offlineStore.pendingCount} 条`)
}

const handleChangePassword = () => {
  showToast('功能开发中')
}

const handleLogout = async () => {
  try {
    await showConfirmDialog({
      title: '退出登录',
      message: '确定要退出登录吗？'
    })

    await apiLogout()
    userStore.logout()
    showSuccessToast('已退出登录')
    router.replace('/login')
  } catch (error) {
    if (error !== 'cancel') {
      showToast('操作失败')
    }
  }
}

onMounted(() => {
  // TODO: 加载统计数据
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 60px;
}

.user-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  color: #fff;
}

.user-info .name {
  font-size: 20px;
  font-weight: bold;
}

.user-info .role {
  font-size: 14px;
  opacity: 0.9;
}

.stats-row {
  display: flex;
  background: #fff;
  margin: 15px;
  border-radius: 8px;
  padding: 20px 0;
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-item .value {
  font-size: 24px;
  font-weight: bold;
  color: #1890ff;
}

.stat-item .label {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

:deep(.van-cell-group) {
  margin: 15px 0;
}

.logout-section {
  padding: 30px 15px;
}
</style>

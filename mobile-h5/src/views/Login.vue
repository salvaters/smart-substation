<template>
  <div class="login-page">
    <div class="logo-section">
      <h1>智能变电站巡检系统</h1>
      <p>Smart Substation Inspection System</p>
    </div>

    <van-form @submit="handleLogin" class="login-form">
      <van-cell-group inset>
        <van-field
          v-model="formData.username"
          name="username"
          label="用户名"
          placeholder="请输入用户名"
          :rules="[{ required: true, message: '请输入用户名' }]"
          clearable
        />
        <van-field
          v-model="formData.password"
          name="password"
          type="password"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请输入密码' }]"
          clearable
        />
      </van-cell-group>

      <div class="button-group">
        <van-button round block type="primary" native-type="submit" :loading="loading">
          登录
        </van-button>
      </div>
    </van-form>

    <div class="offline-banner" v-if="!offlineStore.isOnline">
      <van-notice-bar
        left-icon="info-o"
        text="当前网络离线，请检查网络连接"
        background="#fff2e8"
        color="#ed6a0c"
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import { useUserStore } from '@/stores/user'
import { useOfflineStore } from '@/stores/offline'
import { login } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()
const offlineStore = useOfflineStore()

const formData = ref({
  username: '',
  password: ''
})

const loading = ref(false)

const handleLogin = async () => {
  loading.value = true
  try {
    const response = await login(formData.value)

    userStore.setToken(response.token)
    userStore.setUserInfo({
      userId: response.userId,
      username: response.username,
      realName: response.realName,
      roleId: response.roleId,
      avatar: response.avatar
    })

    showSuccessToast('登录成功')
    router.replace('/home')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 20px;
}

.logo-section {
  text-align: center;
  margin-bottom: 60px;
  color: #fff;
}

.logo-section h1 {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
}

.logo-section p {
  font-size: 14px;
  opacity: 0.9;
}

.login-form {
  background: #fff;
  border-radius: 16px;
  padding: 30px 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.button-group {
  margin-top: 24px;
}

.offline-banner {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}
</style>

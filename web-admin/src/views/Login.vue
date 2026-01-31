<template>
  <div class="login-container">
    <div class="login-box">
      <div class="logo-section">
        <h1>智能变电站巡检系统</h1>
        <p>管理后台</p>
      </div>

      <a-form
        :model="formData"
        name="login"
        @finish="handleLogin"
        autocomplete="off"
      >
        <a-form-item name="username" :rules="[{ required: true, message: '请输入用户名' }]">
          <a-input
            v-model:value="formData.username"
            size="large"
            placeholder="用户名"
            :prefix="h(UserOutlined)"
            allow-clear
          />
        </a-form-item>

        <a-form-item name="password" :rules="[{ required: true, message: '请输入密码' }]">
          <a-input-password
            v-model:value="formData.password"
            size="large"
            placeholder="密码"
            :prefix="h(LockOutlined)"
          />
        </a-form-item>

        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            block
            :loading="loading"
          >
            登录
          </a-button>
        </a-form-item>
      </a-form>

      <div class="footer">
        <p>默认账号: admin / admin123</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, h } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/request'

const router = useRouter()
const userStore = useUserStore()

const formData = ref({
  username: '',
  password: ''
})

const loading = ref(false)

const handleLogin = async () => {
  loading.value = true
  try {
    const response = await login({
      url: '/auth/login',
      method: 'post',
      data: formData.value
    })

    userStore.setToken(response.token)
    userStore.setUserInfo({
      userId: response.userId,
      username: response.username,
      realName: response.realName,
      roleId: response.roleId,
      avatar: response.avatar
    })

    message.success('登录成功')
    router.replace('/admin/dashboard')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  background: #fff;
  border-radius: 8px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.logo-section {
  text-align: center;
  margin-bottom: 40px;
}

.logo-section h1 {
  font-size: 24px;
  margin-bottom: 8px;
}

.logo-section p {
  color: #999;
}

.footer {
  text-align: center;
  margin-top: 20px;
  color: #999;
  font-size: 12px;
}
</style>

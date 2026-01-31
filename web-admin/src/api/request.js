import axios from 'axios'
import { message } from 'ant-design-vue'
import { useUserStore } from '@/stores/user'

/**
 * Axios实例
 */
const request = axios.create({
  baseURL: '/api',
  timeout: 30000,
})

/**
 * 请求拦截器
 */
request.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 */
request.interceptors.response.use(
  (response) => {
    const { data } = response

    if (data.code === 200) {
      return data.data
    } else {
      message.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response

      switch (status) {
        case 401:
          message.error('登录已过期，请重新登录')
          const userStore = useUserStore()
          userStore.logout()
          window.location.href = '/login'
          break
        case 403:
          message.error('无权限访问')
          break
        case 404:
          message.error('请求资源不存在')
          break
        case 500:
          message.error(data.message || '服务器错误')
          break
        default:
          message.error(data.message || '请求失败')
      }
    }

    return Promise.reject(error)
  }
)

export default request

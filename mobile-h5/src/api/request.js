import axios from 'axios'
import { showToast } from 'vant'
import { useUserStore } from '@/stores/user'
import { useOfflineStore } from '@/stores/offline'
import { OfflineDB } from '@/db'

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
      showToast({
        type: 'fail',
        message: data.message || '请求失败',
      })
      return Promise.reject(new Error(data.message || '请求失败'))
    }
  },
  async (error) => {
    const offlineStore = useOfflineStore()

    // 网络错误处理
    if (!error.response && !offlineStore.isOnline) {
      showToast({
        type: 'fail',
        message: '网络连接失败，已保存到离线',
      })
      // 将请求保存到离线队列
      await handleOfflineRequest(error.config)
      throw new Error('OFFLINE_REQUEST')
    }

    if (error.response) {
      const { status, data } = error.response

      switch (status) {
        case 401:
          showToast({ type: 'fail', message: '登录已过期，请重新登录' })
          const userStore = useUserStore()
          userStore.logout()
          window.location.href = '/login'
          break
        case 403:
          showToast({ type: 'fail', message: '无权限访问' })
          break
        case 404:
          showToast({ type: 'fail', message: '请求资源不存在' })
          break
        case 500:
          showToast({ type: 'fail', message: data.message || '服务器错误' })
          break
        default:
          showToast({ type: 'fail', message: data.message || '请求失败' })
      }
    }

    return Promise.reject(error)
  }
)

/**
 * 处理离线请求
 */
async function handleOfflineRequest(config) {
  try {
    // 生成请求ID
    const requestId = `${config.method}_${config.url}_${Date.now()}`

    // 将请求保存到IndexedDB
    await OfflineDB.addPendingRequest({
      requestId,
      url: config.baseURL + config.url,
      method: config.method?.toUpperCase(),
      data: config.data,
      params: config.params
    })

    // 更新待同步数量
    const offlineStore = useOfflineStore()
    await offlineStore.updatePendingCount()
  } catch (storageError) {
    // 如果IndexedDB也失败，只能提示用户
    showToast({
      type: 'fail',
      message: '离线存储失败，请检查浏览器设置',
    })
  }
}

export default request

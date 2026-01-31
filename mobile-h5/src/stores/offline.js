import { defineStore } from 'pinia'
import { ref } from 'vue'
import { OfflineDB } from '@/db'

export const useOfflineStore = defineStore('offline', () => {
  const isOnline = ref(navigator.onLine)
  const showOfflineBanner = ref(false)
  const isSyncing = ref(false)
  const pendingCount = ref(0)

  /**
   * 初始化网络监听
   */
  function initNetworkListener() {
    window.addEventListener('online', handleOnline)
    window.addEventListener('offline', handleOffline)

    // 定期检查待同步数据数量
    updatePendingCount()
    setInterval(updatePendingCount, 30000)
  }

  /**
   * 处理网络恢复
   */
  function handleOnline() {
    isOnline.value = true
    showOfflineBanner.value = false
    // 触发数据同步
    syncData()
  }

  /**
   * 处理网络断开
   */
  function handleOffline() {
    isOnline.value = false
    showOfflineBanner.value = true
  }

  /**
   * 更新待同步数据数量
   */
  async function updatePendingCount() {
    try {
      const requests = await OfflineDB.getPendingRequests()
      const records = await OfflineDB.getPendingRecords()
      const files = await OfflineDB.getPendingFiles()
      pendingCount.value = requests.length + records.length + files.length
    } catch (error) {
      console.error('获取待同步数据数量失败:', error)
    }
  }

  /**
   * 同步数据
   */
  async function syncData() {
    if (isSyncing.value) {
      return
    }

    isSyncing.value = true

    try {
      // 同步离线请求
      await syncPendingRequests()

      // 同步巡检记录
      await syncPendingRecords()

      // 上传文件
      await syncPendingFiles()

      // 清理旧数据
      await OfflineDB.cleanOldData()

      await updatePendingCount()
    } catch (error) {
      console.error('数据同步失败:', error)
      // 记录同步日志
      await OfflineDB.addSyncLog({
        syncType: 'upload',
        status: 'failed',
        error: error.message,
        dataCount: 0
      })
    } finally {
      isSyncing.value = false
    }
  }

  /**
   * 同步待处理请求
   */
  async function syncPendingRequests() {
    const requests = await OfflineDB.getPendingRequests()

    for (const request of requests) {
      try {
        // 使用fetch重新发送请求
        const response = await fetch(request.url, {
          method: request.method,
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          },
          body: request.data ? JSON.stringify(request.data) : undefined
        })

        if (response.ok) {
          // 标记为已同步
          await OfflineDB.markRequestSynced(request.requestId)
        }
      } catch (error) {
        console.error('同步请求失败:', request, error)
      }
    }
  }

  /**
   * 同步巡检记录
   */
  async function syncPendingRecords() {
    // TODO: 实现记录同步逻辑
    const records = await OfflineDB.getPendingRecords()

    for (const record of records) {
      try {
        // 调用API提交记录
        // await submitRecord(record)
        await OfflineDB.records.update(record.recordId, { isOffline: 0 })
      } catch (error) {
        console.error('同步记录失败:', record, error)
      }
    }
  }

  /**
   * 同步文件
   */
  async function syncPendingFiles() {
    // TODO: 实现文件上传逻辑
    const files = await OfflineDB.getPendingFiles()

    for (const file of files) {
      try {
        // 上传文件
        // await uploadFile(file)
        await OfflineDB.pendingFiles.update(file.fileId, { status: 'uploaded' })
      } catch (error) {
        console.error('上传文件失败:', file, error)
      }
    }
  }

  return {
    isOnline,
    showOfflineBanner,
    isSyncing,
    pendingCount,
    initNetworkListener,
    syncData,
    updatePendingCount
  }
})

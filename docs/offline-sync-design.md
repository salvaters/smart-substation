# 智能变电站巡检系统 - 离线同步机制设计

## 一、离线架构概述

### 1.1 离线功能需求
1. **离线数据缓存**: 缓存任务、设备、模板等基础数据
2. **离线任务创建**: 允许离线创建巡检任务
3. **离线数据填写**: 允许离线填写巡检记录
4. **离线照片缓存**: 离线时照片先存储本地
5. **自动同步**: 恢复网络后自动同步数据
6. **冲突检测**: 处理离线期间的数据冲突

### 1.2 技术选型

| 功能模块 | 技术方案 |
|---------|---------|
| 本地数据库 | IndexedDB (使用Dexie.js封装) |
| 网络检测 | navigator.onLine + window事件监听 |
| 离线缓存 | Service Worker + Cache API |
| 后台同步 | Background Sync API (兼容性处理) |
| 数据压缩 | JSON压缩减少传输量 |
| 冲突解决 | 时间戳 + 版本号策略 |

---

## 二、IndexedDB数据结构设计

### 2.1 数据库初始化

```typescript
// src/db/index.ts
import Dexie, { Table } from 'dexie'

export class InspectionDatabase extends Dexie {
  // 任务表
  tasks!: Table<Task, number>

  // 设备表
  devices!: Table<Device, number>

  // 记录表
  records!: Table<Record, number>

  // 缺陷表
  defects!: Table<Defect, number>

  // 模板表
  templates!: Table<Template, number>

  // 文件表
  files!: Table<OfflineFile, number>

  // 同步队列
  syncQueue!: Table<SyncQueueItem, number>

  // 同步日志
  syncLogs!: Table<SyncLog, number>

  constructor() {
    super('InspectionDB')

    // 定义版本和表结构
    this.version(1).stores({
      tasks: 'taskId, taskCode, status, assigneeId, version, lastSyncTime',
      devices: 'deviceId, deviceCode, stationId, version, lastSyncTime',
      records: 'recordId, taskId, deviceId, version, lastSyncTime',
      defects: 'defectId, deviceId, status, version, lastSyncTime',
      templates: 'templateId, templateCode, version, lastSyncTime',
      files: 'localId, fileId, businessType, status, createTime',
      syncQueue: 'id, dataType, action, status, createTime',
      syncLogs: 'logId, syncType, dataType, status, createTime'
    })
  }
}

export const db = new InspectionDatabase()
```

### 2.2 数据表详细结构

#### 任务表 (tasks)
```typescript
interface Task {
  taskId?: number              // 主键(离线创建时为临时ID)
  taskCode: string             // 任务编码
  planId: number               // 计划ID
  stationId: number            // 变电站ID
  stationName: string          // 变电站名称(冗余)
  templateId: number           // 模板ID
  templateName: string         // 模板名称(冗余)
  inspectionType: string       // 巡检类型
  title: string                // 任务标题
  description: string          // 任务描述
  assigneeId: number           // 执行人ID
  assigneeName: string         // 执行人姓名
  plannedStartTime: string     // 计划开始时间
  plannedEndTime: string       // 计划结束时间
  actualStartTime?: string     // 实际开始时间
  actualEndTime?: string       // 实际结束时间
  status: string               // 状态: pending/in_progress/completed/overdue
  progress: number             // 进度
  deviceCount: number          // 设备数量
  completedCount: number       // 已完成数
  defectCount: number          // 缺陷数量
  version: number              // 版本号
  isOffline: boolean           // 是否离线创建
  offlineCreateTime?: string   // 离线创建时间
  lastSyncTime?: string        // 最后同步时间
  syncStatus: string           // 同步状态: synced/pending/conflict
  devices: TaskDevice[]        // 关联设备
  create_time: string          // 创建时间
  update_time: string          // 更新时间
}

interface TaskDevice {
  deviceId: number
  deviceCode: string
  deviceName: string
  location: string
  status: string
  sortOrder: number
}
```

#### 设备表 (devices)
```typescript
interface Device {
  deviceId: number
  deviceCode: string
  deviceName: string
  deviceModel: string
  manufacturer: string
  categoryId: number
  categoryName: string
  stationId: number
  stationName: string
  location: string
  qrCode: string
  qrCodeUrl: string
  ratedVoltage: string
  ratedCurrent: string
  ratedCapacity: string
  status: number
  version: number
  lastSyncTime?: string
  syncStatus: string
  create_time: string
  update_time: string
}
```

#### 记录表 (records)
```typescript
interface Record {
  recordId?: number            // 离线创建时为临时ID
  recordCode: string           // 记录编码
  taskId: number
  deviceId: number
  deviceCode: string
  deviceName: string
  itemId: number
  itemName: string
  itemType: string
  checkValue: string
  checkResult: string
  photoUrls: string            // JSON数组字符串
  description: string
  defectId?: number
  inspectorId: number
  inspectorName: string
  checkTime: string
  isOffline: boolean
  offlineCreateTime?: string
  version: number
  lastSyncTime?: string
  syncStatus: string
  create_time: string
  update_time: string
}
```

#### 文件表 (files)
```typescript
interface OfflineFile {
  localId: string              // 本地唯一ID
  fileId?: number              // 服务器ID(上传后)
  fileName: string
  fileType: string
  fileExtension: string
  fileSize: number
  localPath: string            // 本地存储路径
  remoteUrl?: string           // 服务器URL(上传后)
  businessType: string
  businessId?: number
  uploadStatus: string         // pending/uploading/success/failed
  offlineCreateTime: string
  syncStatus: string
  createTime: string
}
```

#### 同步队列表 (syncQueue)
```typescript
interface SyncQueueItem {
  id?: number                  // 自增主键
  dataType: string             // 数据类型: task/record/defect/file
  action: string               // 操作类型: create/update/delete
  data: any                    // 数据内容
  localId?: string             // 本地ID(文件用)
  retryCount: number           // 重试次数
  status: string               // pending/syncing/success/failed
  error?: string               // 错误信息
  createTime: string
  updateTime: string
}
```

---

## 三、数据同步策略

### 3.1 增量同步机制

```typescript
// src/db/sync.ts
import { db } from './index'
import { syncService } from '@/services/sync.service'

class SyncManager {
  private lastSyncTime = 0
  private syncInProgress = false
  private readonly SYNC_INTERVAL = 5 * 60 * 1000 // 5分钟

  /**
   * 初始化同步
   */
  async init() {
    // 获取最后同步时间
    const lastSync = await db.syncLogs.orderBy('createTime').last()
    if (lastSync) {
      this.lastSyncTime = new Date(lastSync.createTime).getTime()
    }

    // 监听网络状态
    window.addEventListener('online', () => this.handleOnline())
    window.addEventListener('offline', () => this.handleOffline())

    // 定期同步
    setInterval(() => this.autoSync(), this.SYNC_INTERVAL)

    // 启动时尝试同步
    if (navigator.onLine) {
      await this.syncAll()
    }
  }

  /**
   * 处理在线事件
   */
  async handleOnline() {
    console.log('网络已恢复,开始同步...')
    await this.syncAll()
  }

  /**
   * 处理离线事件
   */
  handleOffline() {
    console.log('网络已断开,进入离线模式')
  }

  /**
   * 自动同步
   */
  async autoSync() {
    if (!navigator.onLine || this.syncInProgress) {
      return
    }

    try {
      await this.syncAll()
    } catch (error) {
      console.error('自动同步失败:', error)
    }
  }

  /**
   * 全量同步
   */
  async syncAll() {
    if (this.syncInProgress) {
      console.log('同步进行中,跳过')
      return
    }

    this.syncInProgress = true

    try {
      // 1. 下载数据
      await this.downloadData()

      // 2. 上传数据
      await this.uploadData()

      // 3. 更新同步时间
      this.lastSyncTime = Date.now()

      // 4. 记录同步日志
      await this.logSync('success', '全量同步完成')
    } catch (error) {
      console.error('同步失败:', error)
      await this.logSync('failed', error.message)
    } finally {
      this.syncInProgress = false
    }
  }

  /**
   * 下载数据
   */
  async downloadData() {
    console.log('开始下载数据...')

    // 下载基础数据
    const baseData = await syncService.downloadBaseData(this.lastSyncTime)

    // 更新设备
    await this.syncDevices(baseData.devices)

    // 更新模板
    await this.syncTemplates(baseData.templates)

    // 下载任务数据
    const taskData = await syncService.downloadTaskData(
      this.getUserId(),
      this.lastSyncTime
    )

    // 更新任务
    await this.syncTasks(taskData.tasks)

    console.log('数据下载完成')
  }

  /**
   * 同步设备数据
   */
  async syncDevices(data: SyncData<Device>) {
    // 处理更新的数据
    if (data.updated?.length) {
      for (const device of data.updated) {
        device.syncStatus = 'synced'
        device.lastSyncTime = new Date().toISOString()
        await db.devices.put(device)
      }
    }

    // 处理删除的数据
    if (data.deleted?.length) {
      await db.devices.where('deviceId').anyOf(data.deleted).delete()
    }
  }

  /**
   * 同步任务数据
   */
  async syncTasks(tasks: Task[]) {
    for (const task of tasks) {
      task.syncStatus = 'synced'
      task.lastSyncTime = new Date().toISOString()

      // 检查是否已存在
      const existing = await db.tasks.where('taskId').equals(task.taskId).first()
      if (existing) {
        // 检查版本冲突
        if (existing.version > task.version) {
          task.syncStatus = 'conflict'
          // 标记冲突,需要用户处理
        }
      }

      await db.tasks.put(task)
    }
  }

  /**
   * 上传数据
   */
  async uploadData() {
    console.log('开始上传数据...')

    // 获取待上传队列
    const queue = await db.syncQueue
      .where('status')
      .equals('pending')
      .toArray()

    if (queue.length === 0) {
      console.log('没有待上传数据')
      return
    }

    // 按数据类型分组
    const grouped = this.groupByType(queue)

    // 上传文件
    await this.uploadFiles(grouped.file || [])

    // 上传任务
    await this.uploadTasks(grouped.task || [])

    // 上传记录
    await this.uploadRecords(grouped.record || [])

    // 上传缺陷
    await this.uploadDefects(grouped.defect || [])

    console.log('数据上传完成')
  }

  /**
   * 上传文件
   */
  async uploadFiles(items: SyncQueueItem[]) {
    for (const item of items) {
      try {
        // 更新状态为上传中
        await db.syncQueue.update(item.id!, { status: 'syncing' })

        // 获取本地文件
        const file = await db.files.where('localId').equals(item.localId!).first()
        if (!file) {
          throw new Error('文件不存在')
        }

        // 上传文件
        const formData = new FormData()
        const blob = await this.getLocalFileBlob(file.localPath)
        formData.append('files', blob, file.fileName)
        formData.append('businessType', file.businessType)
        formData.append('businessId', String(file.businessId || ''))
        formData.append('offlineCreateTime', file.offlineCreateTime)

        const response = await syncService.uploadFiles(formData)

        // 更新文件状态
        if (response.data.successCount > 0) {
          const uploadedFile = response.data.files[0]
          await db.files.update(file.localId, {
            fileId: uploadedFile.fileId,
            remoteUrl: uploadedFile.fileUrl,
            uploadStatus: 'success',
            syncStatus: 'synced'
          })

          // 从队列中移除
          await db.syncQueue.delete(item.id!)
        }
      } catch (error) {
        // 更新失败状态
        await db.syncQueue.update(item.id!, {
          status: 'failed',
          error: error.message,
          retryCount: item.retryCount + 1
        })
      }
    }
  }

  /**
   * 上传记录
   */
  async uploadRecords(items: SyncQueueItem[]) {
    // 构造批量上传数据
    const records = []

    for (const item of items) {
      await db.syncQueue.update(item.id!, { status: 'syncing' })

      // 处理照片URL,将本地路径替换为远程URL
      const record = { ...item.data }
      if (record.photoUrls) {
        const photos = JSON.parse(record.photoUrls)
        const remotePhotos = []

        for (const photo of photos) {
          if (photo.startsWith('blob:') || photo.startsWith('file:')) {
            // 查找已上传的文件
            const file = await db.files
              .where('localPath')
              .equals(photo)
              .first()

            if (file && file.remoteUrl) {
              remotePhotos.push(file.remoteUrl)
            } else {
              // 文件未上传,跳过或等待
              remotePhotos.push(photo)
            }
          } else {
            remotePhotos.push(photo)
          }
        }

        record.photoUrls = JSON.stringify(remotePhotos)
      }

      records.push(record)
    }

    try {
      const response = await syncService.uploadRecords(records)

      // 更新本地记录
      for (let i = 0; i < response.data.success.length; i++) {
        const serverId = response.data.success[i]
        const localId = items[i].data.recordId

        await db.records.update(localId, {
          recordId: serverId,
          syncStatus: 'synced',
          lastSyncTime: new Date().toISOString()
        })

        // 从队列移除
        await db.syncQueue.delete(items[i].id!)
      }
    } catch (error) {
      // 更新失败状态
      for (const item of items) {
        await db.syncQueue.update(item.id!, {
          status: 'failed',
          error: error.message,
          retryCount: item.retryCount + 1
        })
      }
    }
  }

  /**
   * 记录同步日志
   */
  async logSync(status: string, message: string) {
    await db.syncLogs.add({
      syncType: 'auto',
      dataType: 'all',
      status,
      error: status === 'failed' ? message : undefined,
      createTime: new Date().toISOString()
    })
  }

  /**
   * 获取用户ID
   */
  getUserId(): number {
    // 从store获取
    return 1
  }

  /**
   * 按类型分组
   */
  groupByType(items: SyncQueueItem[]) {
    return items.reduce((acc, item) => {
      if (!acc[item.dataType]) {
        acc[item.dataType] = []
      }
      acc[item.dataType].push(item)
      return acc
    }, {} as Record<string, SyncQueueItem[]>)
  }

  /**
   * 获取本地文件Blob
   */
  async getLocalFileBlob(path: string): Promise<Blob> {
    // 从IndexedDB或其他存储获取
    return new Blob()
  }
}

export const syncManager = new SyncManager()
```

### 3.2 冲突检测与解决

```typescript
// src/db/conflict.ts
export class ConflictResolver {
  /**
   * 检测冲突
   */
  async detectConflict(localData: any, serverData: any): Promise<boolean> {
    // 版本号冲突
    if (localData.version && serverData.version) {
      return localData.version < serverData.version
    }

    // 时间戳冲突
    if (localData.update_time && serverData.update_time) {
      const localTime = new Date(localData.update_time)
      const serverTime = new Date(serverData.update_time)
      return localTime < serverTime
    }

    return false
  }

  /**
   * 解决冲突
   */
  async resolveConflict(
    dataType: string,
    localData: any,
    serverData: any
  ): Promise<any> {
    // 策略1: 服务器优先
    // return serverData

    // 策略2: 本地优先
    // return localData

    // 策略3: 时间戳比较
    const localTime = new Date(localData.update_time)
    const serverTime = new Date(serverData.update_time)

    if (localTime > serverTime) {
      // 本地更新,需要上传
      return { action: 'upload', data: localData }
    } else {
      // 服务器更新,需要下载
      return { action: 'download', data: serverData }
    }

    // 策略4: 合并(适用于某些场景)
    // return this.mergeData(localData, serverData)
  }

  /**
   * 合并数据
   */
  mergeData(local: any, server: any): any {
    // 根据业务逻辑合并
    return {
      ...server,
      // 本地特定字段优先
      someLocalField: local.someLocalField
    }
  }
}
```

---

## 四、Service Worker配置

### 4.1 Service Worker注册

```typescript
// src/sw.ts
import { setupWorker } from 'virtual:pwa-plugin'

// Service Worker事件
self.addEventListener('install', (event) => {
  console.log('[SW] 安装')
  event.waitUntil(self.skipWaiting())
})

self.addEventListener('activate', (event) => {
  console.log('[SW] 激活')
  event.waitUntil(self.clients.claim())
})

self.addEventListener('fetch', (event) => {
  // 缓存API请求
  if (event.request.url.includes('/api/')) {
    event.respondWith(handleAPIRequest(event.request))
  }
})

/**
 * 处理API请求
 */
async function handleAPIRequest(request: Request) {
  try {
    // 尝试网络请求
    const response = await fetch(request)

    // 缓存GET请求
    if (request.method === 'GET' && response.ok) {
      const cache = await caches.open('api-cache')
      cache.put(request.url, response.clone())
    }

    return response
  } catch (error) {
    // 网络失败,尝试缓存
    const cached = await caches.match(request.url)
    if (cached) {
      return cached
    }

    throw error
  }
}

// 监听消息
self.addEventListener('message', (event) => {
  if (event.data === 'skipWaiting') {
    self.skipWaiting()
  }
})
```

### 4.2 PWA配置 (Vite)

```typescript
// vite.config.ts
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { VitePWA } from 'vite-plugin-pwa'

export default defineConfig({
  plugins: [
    vue(),
    VitePWA({
      registerType: 'autoUpdate',
      includeAssets: ['favicon.ico', 'robots.txt', 'apple-touch-icon.png'],
      manifest: {
        name: '智能变电站巡检',
        short_name: '巡检',
        description: '变电站智能巡检系统',
        theme_color: '#1890ff',
        background_color: '#ffffff',
        display: 'standalone',
        icons: [
          {
            src: '/icons/icon-192x192.png',
            sizes: '192x192',
            type: 'image/png'
          },
          {
            src: '/icons/icon-512x512.png',
            sizes: '512x512',
            type: 'image/png'
          }
        ]
      },
      workbox: {
        runtimeCaching: [
          {
            urlPattern: /^https:\/\/api\./,
            handler: 'NetworkFirst',
            options: {
              cacheName: 'api-cache',
              expiration: {
                maxEntries: 100,
                maxAgeSeconds: 60 * 60 * 24 // 24小时
              },
              cacheableResponse: {
                statuses: [0, 200]
              }
            }
          },
          {
            urlPattern: /\.(?:png|jpg|jpeg|svg|gif)$/,
            handler: 'CacheFirst',
            options: {
              cacheName: 'image-cache',
              expiration: {
                maxEntries: 200,
                maxAgeSeconds: 60 * 60 * 24 * 30 // 30天
              }
            }
          }
        ]
      }
    })
  ]
})
```

---

## 五、照片延迟上传机制

### 5.1 照片上传组件

```vue
<!-- components/business/PhotoUploader.vue -->
<template>
  <div class="photo-uploader">
    <van-uploader
      v-model="fileList"
      :after-read="handleAfterRead"
      :before-delete="handleBeforeDelete"
      multiple
      :max-count="maxCount"
      accept="image/*"
    />

    <!-- 上传进度 -->
    <van-dialog v-model:show="uploading" title="正在上传" :show-confirm-button="false">
      <van-progress :percentage="uploadProgress" />
    </van-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { compressImage } from '@/utils/image'
import { saveOfflineFile } from '@/db/file.store'
import { addSyncQueue } from '@/db/sync.store'
import { isOnline } from '@/utils/network'

interface Props {
  modelValue: string[]
  maxCount?: number
  businessType?: string
  businessId?: number
}

const props = withDefaults(defineProps<Props>(), {
  maxCount: 9,
  businessType: 'inspection',
  businessId: undefined
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string[]): void
}>()

const fileList = ref([])
const uploading = ref(false)
const uploadProgress = ref(0)

/**
 * 读取文件后的处理
 */
async function handleAfterRead(file: any) {
  try {
    uploading.value = true
    uploadProgress.value = 0

    // 压缩图片
    const compressed = await compressImage(file.file, {
      maxWidth: 1920,
      maxHeight: 1920,
      quality: 0.8
    })

    uploadProgress.value = 30

    if (isOnline()) {
      // 在线,直接上传
      await uploadToServer(compressed)
    } else {
      // 离线,保存到本地
      await saveToLocal(compressed)
    }

    uploadProgress.value = 100
  } catch (error) {
    console.error('文件处理失败:', error)
    showToast('文件处理失败')
  } finally {
    setTimeout(() => {
      uploading.value = false
    }, 500)
  }
}

/**
 * 上传到服务器
 */
async function uploadToServer(blob: Blob) {
  const formData = new FormData()
  formData.append('file', blob)
  formData.append('businessType', props.businessType)
  formData.append('businessId', String(props.businessId || ''))

  const response = await fileService.uploadImage(formData)
  const url = response.data.fileUrl

  // 更新值
  const urls = [...props.modelValue, url]
  emit('update:modelValue', urls)
}

/**
 * 保存到本地
 */
async function saveToLocal(blob: Blob) {
  const localId = `local_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`

  // 保存到IndexedDB
  await saveOfflineFile({
    localId,
    fileName: `photo_${Date.now()}.jpg`,
    fileType: 'image',
    fileExtension: 'jpg',
    fileSize: blob.size,
    localPath: URL.createObjectURL(blob),
    businessType: props.businessType,
    businessId: props.businessId,
    uploadStatus: 'pending',
    offlineCreateTime: new Date().toISOString(),
    syncStatus: 'pending'
  })

  // 添加到同步队列
  await addSyncQueue({
    dataType: 'file',
    action: 'create',
    localId,
    data: { localId },
    retryCount: 0,
    status: 'pending'
  })

  // 使用本地路径
  const urls = [...props.modelValue, localId]
  emit('update:modelValue', urls)

  showToast('图片已保存,将在恢复网络后上传')
}

/**
 * 删除前的处理
 */
async function handleBeforeDelete(file: any) {
  const url = file.url || file.response?.url

  // 如果是本地文件,从IndexedDB删除
  if (url.startsWith('local_')) {
    await db.files.where('localId').equals(url).delete()
    await db.syncQueue.where('localId').equals(url).delete()
  }

  return true
}
</script>
```

### 5.2 图片压缩工具

```typescript
// src/utils/image.ts
/**
 * 压缩图片
 */
export function compressImage(
  file: File,
  options: {
    maxWidth?: number
    maxHeight?: number
    quality?: number
  } = {}
): Promise<Blob> {
  return new Promise((resolve, reject) => {
    const {
      maxWidth = 1920,
      maxHeight = 1920,
      quality = 0.8
    } = options

    const reader = new FileReader()

    reader.onload = (e) => {
      const img = new Image()

      img.onload = () => {
        // 计算压缩后的尺寸
        let width = img.width
        let height = img.height

        if (width > maxWidth || height > maxHeight) {
          const ratio = Math.min(maxWidth / width, maxHeight / height)
          width = width * ratio
          height = height * ratio
        }

        // 创建canvas压缩
        const canvas = document.createElement('canvas')
        canvas.width = width
        canvas.height = height

        const ctx = canvas.getContext('2d')
        if (!ctx) {
          reject(new Error('Canvas context not available'))
          return
        }

        ctx.drawImage(img, 0, 0, width, height)

        // 导出为Blob
        canvas.toBlob(
          (blob) => {
            if (blob) {
              resolve(blob)
            } else {
              reject(new Error('Compression failed'))
            }
          },
          'image/jpeg',
          quality
        )
      }

      img.onerror = () => {
        reject(new Error('Image load failed'))
      }

      img.src = e.target?.result as string
    }

    reader.onerror = () => {
      reject(new Error('File read failed'))
    }

    reader.readAsDataURL(file)
  })
}
```

---

## 六、网络状态检测

```typescript
// src/utils/network.ts
/**
 * 检查网络是否在线
 */
export function isOnline(): boolean {
  return navigator.onLine
}

/**
 * 监听网络状态变化
 */
export function watchNetworkStatus(
  onOnline: () => void,
  onOffline: () => void
): () => void {
  const handleOnline = () => {
    console.log('网络已连接')
    onOnline()
  }

  const handleOffline = () => {
    console.log('网络已断开')
    onOffline()
  }

  window.addEventListener('online', handleOnline)
  window.addEventListener('offline', handleOffline)

  // 返回清理函数
  return () => {
    window.removeEventListener('online', handleOnline)
    window.removeEventListener('offline', handleOffline)
  }
}

/**
 * 测试网络延迟
 */
export async function testNetworkLatency(): Promise<number> {
  const start = Date.now()

  try {
    await fetch('/api/v1/ping', {
      method: 'HEAD',
      cache: 'no-cache'
    })
    return Date.now() - start
  } catch {
    return -1
  }
}

/**
 * 检查服务器连接
 */
export async function checkServerConnection(): Promise<boolean> {
  try {
    const response = await fetch('/api/v1/health', {
      method: 'GET',
      cache: 'no-cache',
      timeout: 5000
    })
    return response.ok
  } catch {
    return false
  }
}
```

---

## 七、同步UI提示

### 7.1 同步状态组件

```vue
<!-- components/business/SyncStatus.vue -->
<template>
  <div class="sync-status" :class="statusClass">
    <van-icon :name="iconName" />
    <span>{{ statusText }}</span>
    <van-button
      v-if="showSyncButton"
      size="mini"
      type="primary"
      :loading="syncing"
      @click="handleSync"
    >
      {{ syncing ? '同步中...' : '立即同步' }}
    </van-button>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useOfflineStore } from '@/stores/offline'

const offlineStore = useOfflineStore()
const syncing = ref(false)

const statusClass = computed(() => {
  return {
    'status-offline': !offlineStore.isOnline,
    'status-pending': offlineStore.pendingSyncCount > 0,
    'status-synced': offlineStore.isOnline && offlineStore.pendingSyncCount === 0
  }
})

const iconName = computed(() => {
  if (!offlineStore.isOnline) return 'warning-o'
  if (offlineStore.pendingSyncCount > 0) return 'cloud-o'
  return 'checked'
})

const statusText = computed(() => {
  if (!offlineStore.isOnline) return '离线模式'
  if (offlineStore.pendingSyncCount > 0) {
    return `待同步 ${offlineStore.pendingSyncCount} 条数据`
  }
  if (offlineStore.lastSyncTime) {
    const time = new Date(offlineStore.lastSyncTime)
    return `已于 ${formatTime(time)} 同步`
  }
  return '已同步'
})

const showSyncButton = computed(() => {
  return offlineStore.canSync
})

async function handleSync() {
  if (syncing.value) return

  syncing.value = true
  try {
    await syncManager.syncAll()
    showToast('同步成功')
  } catch (error) {
    showToast('同步失败')
  } finally {
    syncing.value = false
  }
}

function formatTime(date: Date): string {
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return `${Math.floor(diff / 86400000)}天前`
}
</script>

<style scoped>
.sync-status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 12px;
}

.status-offline {
  background-color: #fff3cd;
  color: #856404;
}

.status-pending {
  background-color: #cce5ff;
  color: #004085;
}

.status-synced {
  background-color: #d4edda;
  color: #155724;
}
</style>
```

import Dexie from 'dexie'

/**
 * 离线数据库
 */
export const db = new Dexie('SmartSubstationDB')

/**
 * 数据库版本
 */
db.version(1).stores({
  // 离线任务
  tasks: 'taskId, taskCode, status, assigneeId, isOffline, version',

  // 设备信息
  devices: 'deviceId, deviceCode, qrCode, stationId, categoryId, version',

  // 变电站信息
  stations: 'stationId, stationCode, version',

  // 巡检模板
  templates: 'templateId, templateCode, version',

  // 巡检记录
  records: 'recordId, recordCode, taskId, deviceId, isOffline, version',

  // 缺陷信息
  defects: 'defectId, defectCode, deviceId, status, version',

  // 待上传文件
  pendingFiles: 'fileId, businessType, businessId, status',

  // 同步日志
  syncLogs: 'logId, syncType, status, timestamp',

  // 离线请求队列
  pendingRequests: 'requestId, url, method, status, timestamp'
})

/**
 * 数据库操作类
 */
export class OfflineDB {
  /**
   * 保存任务列表
   */
  static async saveTasks(tasks) {
    await db.tasks.bulkPut(tasks)
  }

  /**
   * 获取离线任务
   */
  static async getTasks() {
    return await db.tasks.toArray()
  }

  /**
   * 添加离线记录
   */
  static async addRecord(record) {
    return await db.records.add(record)
  }

  /**
   * 获取待同步记录
   */
  static async getPendingRecords() {
    return await db.records.where('isOffline').equals(1).toArray()
  }

  /**
   * 保存设备信息
   */
  static async saveDevices(devices) {
    await db.devices.bulkPut(devices)
  }

  /**
   * 根据二维码查询设备
   */
  static async getDeviceByQrCode(qrCode) {
    return await db.devices.where('qrCode').equals(qrCode).first()
  }

  /**
   * 添加待上传文件
   */
  static async addPendingFile(file) {
    return await db.pendingFiles.add(file)
  }

  /**
   * 获取待上传文件
   */
  static async getPendingFiles() {
    return await db.pendingFiles.where('status').equals('pending').toArray()
  }

  /**
   * 保存离线请求
   */
  static async addPendingRequest(request) {
    return await db.pendingRequests.add({
      ...request,
      status: 'pending',
      timestamp: Date.now()
    })
  }

  /**
   * 获取待同步请求
   */
  static async getPendingRequests() {
    return await db.pendingRequests.where('status').equals('pending').toArray()
  }

  /**
   * 标记请求已同步
   */
  static async markRequestSynced(requestId) {
    await db.pendingRequests.update(requestId, { status: 'synced' })
  }

  /**
   * 记录同步日志
   */
  static async addSyncLog(log) {
    return await db.syncLogs.add({
      ...log,
      timestamp: Date.now()
    })
  }

  /**
   * 清理旧数据
   */
  static async cleanOldData(days = 7) {
    const cutoffDate = Date.now() - (days * 24 * 60 * 60 * 1000)

    await db.syncLogs.where('timestamp').below(cutoffDate).delete()
    await db.pendingRequests.where('timestamp').below(cutoffDate).delete()
  }
}

export default db

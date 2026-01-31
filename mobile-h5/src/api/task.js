import request from './request'

/**
 * 获取我的任务列表
 */
export function getMyTasks(params) {
  return request({
    url: '/tasks/my',
    method: 'get',
    params,
  })
}

/**
 * 获取任务详情
 */
export function getTaskDetail(taskId) {
  return request({
    url: `/tasks/${taskId}`,
    method: 'get',
  })
}

/**
 * 开始任务
 */
export function startTask(taskId) {
  return request({
    url: `/tasks/${taskId}/start`,
    method: 'post',
  })
}

/**
 * 完成任务
 */
export function completeTask(taskId, data) {
  return request({
    url: `/tasks/${taskId}/complete`,
    method: 'post',
    data,
  })
}

/**
 * 提交巡检记录
 */
export function submitRecord(data) {
  return request({
    url: '/records/submit',
    method: 'post',
    data,
  })
}

/**
 * 批量提交记录
 */
export function batchSubmitRecords(data) {
  return request({
    url: '/records/batch',
    method: 'post',
    data,
  })
}

/**
 * 根据二维码查询设备
 */
export function getDeviceByQrCode(qrCode) {
  return request({
    url: `/devices/qr/${encodeURIComponent(qrCode)}`,
    method: 'get',
  })
}

/**
 * 上传文件
 */
export function uploadFile(formData, onProgress) {
  return request({
    url: '/files/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    onUploadProgress: (progressEvent) => {
      if (onProgress) {
        const percent = Math.floor((progressEvent.loaded / progressEvent.total) * 100)
        onProgress(percent)
      }
    },
  })
}

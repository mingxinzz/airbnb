import request from './request'

/** Upload files and start pipeline */
export function uploadAndStart(formData) {
  return request.post('/pipeline/upload-and-start', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 120000
  })
}

/** Query task status */
export function getTaskStatus(taskId) {
  return request.get(`/pipeline/${taskId}/status`)
}

/** Query task logs */
export function getTaskLogs(taskId) {
  return request.get(`/pipeline/${taskId}/log`)
}

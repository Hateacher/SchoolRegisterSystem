import request from './request'

/** 新增活动 */
export function addActivity(data) {
  return request.post('/api/activity/add', data)
}

/** 编辑活动 */
export function updateActivity(data) {
  return request.put('/api/activity/update', data)
}

/** 删除活动（下架） */
export function deleteActivity(id) {
  return request.delete(`/api/activity/delete/${id}`)
}

/** 活动列表 */
export function listActivities() {
  return request.get('/api/activity/list')
}

/** 活动详情 */
export function activityDetail(id) {
  return request.get(`/api/activity/detail/${id}`)
}

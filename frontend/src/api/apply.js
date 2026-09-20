import request from './request'

/** 活动报名 */
export function applyActivity(activityId) {
  return request.post(`/api/apply/${activityId}`)
}

/** 取消报名 */
export function cancelApply(activityId) {
  return request.delete(`/api/apply/${activityId}`)
}

/** 我的报名列表 */
export function myApplies() {
  return request.get('/api/apply/my')
}

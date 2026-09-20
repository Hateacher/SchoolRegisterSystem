/** 日期显示格式化：2026-09-19T12:00:00 -> 2026-09-19 12:00 */
export function formatDateTime(iso) {
  if (!iso) return ''
  return String(iso).replace('T', ' ').slice(0, 16)
}

/** 是否已超过截止时间 */
export function isExpired(deadline) {
  if (!deadline) return false
  return new Date(String(deadline).replace(' ', 'T')).getTime() < Date.now()
}

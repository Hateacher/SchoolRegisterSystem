/** 登录态存取：token 与用户信息保存在 localStorage（契约约定） */

const KEYS = ['token', 'userId', 'username', 'realName']

export function setLoginState(data) {
  localStorage.setItem('token', data.token)
  localStorage.setItem('userId', String(data.userId))
  localStorage.setItem('username', data.username ?? '')
  localStorage.setItem('realName', data.realName ?? '')
}

export function getToken() {
  return localStorage.getItem('token')
}

export function getUserId() {
  return Number(localStorage.getItem('userId'))
}

export function getRealName() {
  return localStorage.getItem('realName') || ''
}

export function clearLoginState() {
  KEYS.forEach((key) => localStorage.removeItem(key))
}

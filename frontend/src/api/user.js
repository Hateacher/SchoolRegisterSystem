import request from './request'

/** 用户登录 */
export function login(data) {
  return request.post('/api/user/login', data)
}

/** 用户注册 */
export function register(data) {
  return request.post('/api/user/register', data)
}

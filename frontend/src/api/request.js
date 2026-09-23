import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { clearLoginState } from '@/utils/auth'

/**
 * 统一 axios 实例（契约约定）：
 * - 请求头携带 token（不使用 Bearer）
 * - 响应统一为 {code, msg, data}：200 成功；400 业务错误；401 未登录/token失效；500 服务器异常
 */
const request = axios.create({
  baseURL: '',
  timeout: 10000,
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.token = token
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res
    }
    if (res.code === 401) {
      clearLoginState()
      ElMessage.error(res.msg || '未登录或登录已过期')
      router.replace('/login')
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
    return Promise.reject(new Error(res.msg || 'Error'))
  },
  (error) => {
    ElMessage.error('网络异常，请稍后重试')
    return Promise.reject(error)
  },
)

export default request

import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

// Result<T> from backend: { code: 0, message: string, data: T, timestamp: number }
export interface ApiResult<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

const http = axios.create({
  timeout: 15000,
  withCredentials: true  // Send session cookie
})

// Response interceptor: unwrap Result<T>
http.interceptors.response.use(
  (response) => {
    const result: ApiResult = response.data
    if (result.code === 0) {
      return result.data
    }
    ElMessage.error(result.message || '请求失败')
    return Promise.reject(new Error(result.message))
  },
  (error) => {
    if (error.response?.status === 401) {
      // Only redirect if user believes they are NOT logged in
      // If user is locally logged in but gets 401, session has expired — clear state silently
      const savedUser = localStorage.getItem('echo_user')
      if (savedUser) {
        // Session expired - clear local state and redirect
        localStorage.removeItem('echo_user')
        ElMessage.warning('登录已过期，请重新登录')
        router.push('/login')
      }
      // else: user knows they're not logged in, just reject quietly
    } else if (error.response?.status === 403) {
      ElMessage.error('没有权限执行此操作')
    } else if (error.response?.status !== undefined) {
      ElMessage.error(error.response?.data?.message || '网络请求失败')
    }
    return Promise.reject(error)
  }
)

export default http

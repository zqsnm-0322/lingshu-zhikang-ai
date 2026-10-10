import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
  // 使用相对路径，配合Vite代理
  baseURL: '/', // API 的 base_url
  timeout: 5000 // 请求超时时间
})

// request拦截器
request.interceptors.request.use(
  config => {
    return config
  },
  error => {
    console.log(error)
    Promise.reject(error)
  }
)

// response拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    console.log('接收到响应:', res)
    
    // 如果响应状态码是2xx，直接返回数据
    if (response.status >= 200 && response.status < 300) {
      // 检查是否有明确的错误标识
      if (res && (res.code === '500' || res.ok === 0 || res.error)) {
        const errorMsg = res.msg || res.message || '请求错误'
        ElMessage({
          message: errorMsg,
          type: 'error',
          duration: 5 * 1000
        })
        return Promise.reject(new Error(errorMsg))
      }
      
      // 正常返回数据
      return res
    }
    
    // HTTP状态码错误
    ElMessage({
      message: '请求失败',
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(new Error('请求失败'))
  },
  error => {
    console.log('请求错误:', error)
    ElMessage({
      message: error.message || '请求错误',
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default request 
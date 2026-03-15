import axios, { type AxiosInstance, type AxiosResponse } from 'axios'

// 创建 axios 实例 - 平台管理端
const request: AxiosInstance = axios.create({
  baseURL: 'http://localhost:8080/api/admin',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('admin_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    return response.data
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('admin_token')
      window.location.href = '/admin.html#/login'
    }
    return Promise.reject(error)
  }
)

export default request

import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Response interceptor
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    }
    if (res.code === 404) {
      console.warn('No data found:', res.message)
    }
    return Promise.reject(new Error(res.message || 'Request failed'))
  },
  (error) => {
    console.error('Request error:', error.message)
    return Promise.reject(error)
  }
)

export default request

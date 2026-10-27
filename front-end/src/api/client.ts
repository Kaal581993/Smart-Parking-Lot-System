import axios from 'axios'

export const apiBaseUrl = (import.meta.env.VITE_API_URL || '/api').replace(/\/$/, '')

const client = axios.create({ baseURL: apiBaseUrl })
client.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

client.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && localStorage.getItem('token') && window.location.pathname !== '/login') {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.assign('/login')
    }
    return Promise.reject(error)
  },
)

export default client

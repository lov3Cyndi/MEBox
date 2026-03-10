import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || null)
  const user = ref(null)

  const api = axios.create({
    baseURL: 'http://localhost:8080/api',
  })

  function setToken(newToken) {
    token.value = newToken
    if (newToken) {
      localStorage.setItem('token', newToken)
      api.defaults.headers.common['Authorization'] = `Bearer ${newToken}`
    } else {
      localStorage.removeItem('token')
      delete api.defaults.headers.common['Authorization']
    }
  }

  async function login(username, password) {
    const res = await api.post('/auth/login', { username, password })
    setToken(res.data.token)
    await fetchUser()
    return res.data
  }

  async function register(username, email, password) {
    const res = await api.post('/auth/register', { username, email, password })
    if (res.data.token) {
      setToken(res.data.token)
      await fetchUser()
    }
    return res.data
  }

  async function fetchUser() {
    if (!token.value) return
    try {
      api.defaults.headers.common['Authorization'] = `Bearer ${token.value}`
      const res = await api.get('/users/me')
      user.value = res.data
    } catch (e) {
      logout()
    }
  }

  function logout() {
    setToken(null)
    user.value = null
  }

  if (token.value) {
    fetchUser()
  }

  return {
    token,
    user,
    api,
    login,
    register,
    logout,
    fetchUser,
  }
})

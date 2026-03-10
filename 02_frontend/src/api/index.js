import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const authApi = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
}

export const heroApi = {
  list: (params) => api.get('/heroes', { params }),
  get: (id) => api.get(`/heroes/${id}`),
  getRelations: (id) => api.get(`/heroes/${id}/relations`),
  getRatings: (id) => api.get(`/heroes/${id}/ratings`),
  rate: (id, data) => api.post(`/heroes/${id}/rate`, data),
  getMedia: (id) => api.get(`/heroes/${id}/media`),
  addMedia: (id, data) => api.post(`/heroes/${id}/media`, data),
  getRoles: () => api.get('/heroes/roles'),
  create: (data) => api.post('/heroes', data),
  update: (id, data) => api.put(`/heroes/${id}`, data),
  delete: (id) => api.delete(`/heroes/${id}`),
}

export const mapApi = {
  list: (params) => api.get('/maps', { params }),
  get: (id) => api.get(`/maps/${id}`),
  getRatings: (id) => api.get(`/maps/${id}/ratings`),
  rate: (id, data) => api.post(`/maps/${id}/rate`, data),
  create: (data) => api.post('/maps', data),
  update: (id, data) => api.put(`/maps/${id}`, data),
  delete: (id) => api.delete(`/maps/${id}`),
}

export const videoApi = {
  list: (params) => api.get('/videos', { params }),
  get: (id) => api.get(`/videos/${id}`),
  upload: (data) => api.post('/videos', data, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  delete: (id) => api.delete(`/videos/${id}`),
  like: (id) => api.post(`/videos/${id}/like`),
  unlike: (id) => api.delete(`/videos/${id}/like`),
  getComments: (id) => api.get(`/videos/${id}/comments`),
  addComment: (id, data) => api.post(`/videos/${id}/comments`, data),
}

export const userApi = {
  getMe: () => api.get('/users/me'),
  updateMe: (data) => api.put('/users/me', data),
}

export default api

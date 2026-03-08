import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import HeroList from '../views/HeroList.vue'
import HeroDetail from '../views/HeroDetail.vue'
import MapList from '../views/MapList.vue'
import MapDetail from '../views/MapDetail.vue'
import VideoList from '../views/VideoList.vue'
import VideoDetail from '../views/VideoDetail.vue'
import VideoUpload from '../views/VideoUpload.vue'
import Profile from '../views/Profile.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/heroes', name: 'HeroList', component: HeroList },
  { path: '/heroes/:id', name: 'HeroDetail', component: HeroDetail },
  { path: '/maps', name: 'MapList', component: MapList },
  { path: '/maps/:id', name: 'MapDetail', component: MapDetail },
  { path: '/videos', name: 'VideoList', component: VideoList },
  { path: '/videos/:id', name: 'VideoDetail', component: VideoDetail },
  { path: '/upload', name: 'VideoUpload', component: VideoUpload },
  { path: '/profile', name: 'Profile', component: Profile },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router

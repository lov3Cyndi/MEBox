<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useThemeStore } from './stores/theme'
import { useUserStore } from './stores/user'
import {
  HomeOutlined,
  TeamOutlined,
  GlobalOutlined,
  PlayCircleOutlined,
  UserOutlined,
  UploadOutlined,
  SwapOutlined,
  LogoutOutlined,
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const themeStore = useThemeStore()
const userStore = useUserStore()

const themeClass = computed(() => themeStore.theme)

const navItems = [
  { key: '/', icon: HomeOutlined, label: '首页' },
  { key: '/heroes', icon: TeamOutlined, label: '英雄' },
  { key: '/maps', icon: GlobalOutlined, label: '地图' },
  { key: '/videos', icon: PlayCircleOutlined, label: '视频' },
]

function handleNav(key) {
  router.push(key)
}

function goHome() {
  router.push('/')
}

function goUpload() {
  router.push('/upload')
}

function goProfile() {
  router.push('/profile')
}

function goLogin() {
  router.push('/login')
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}

function toggleTheme() {
  themeStore.toggleTheme()
}
</script>

<template>
  <div :class="['app-container', themeClass]">
    <a-layout class="layout">
      <a-layout-header class="header">
        <div class="logo" @click="goHome">
          <span class="logo-text">MEBox</span>
        </div>

        <a-menu
          mode="horizontal"
          :selected-keys="[route.path.startsWith('/heroes') ? '/heroes' : route.path.startsWith('/maps') ? '/maps' : route.path.startsWith('/videos') ? '/videos' : route.path]"
          class="nav-menu"
        >
          <a-menu-item
            v-for="item in navItems"
            :key="item.key"
            @click="handleNav(item.key)"
          >
            <component :is="item.icon" />
            {{ item.label }}
          </a-menu-item>
        </a-menu>

        <div class="header-right">
          <a-button
            class="theme-btn"
            @click="toggleTheme"
          >
            <SwapOutlined />
            {{ themeStore.isOverwatch ? '黑爪主题' : '守望先锋' }}
          </a-button>

          <template v-if="userStore.token">
            <a-button type="text" class="user-btn" @click="goUpload">
              <UploadOutlined /> 上传
            </a-button>
            <a-button type="text" class="user-btn" @click="goProfile">
              <UserOutlined /> {{ userStore.user?.username || '用户' }}
            </a-button>
            <a-button type="text" class="user-btn" @click="handleLogout">
              <LogoutOutlined />
            </a-button>
          </template>
          <template v-else>
            <a-button type="text" class="user-btn" @click="goLogin">
              <UserOutlined /> 登录
            </a-button>
          </template>
        </div>
      </a-layout-header>

      <a-layout-content class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </a-layout-content>

      <a-layout-footer class="footer">
        MEBox ©2024 - 守望先锋内容社区
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<style>
.app-container {
  min-height: 100vh;
  background: var(--bg-color);
  color: var(--text-primary);
}

.layout {
  min-height: 100vh;
  background: var(--bg-color);
}

.header {
  display: flex;
  align-items: center;
  background: var(--bg-secondary) !important;
  border-bottom: 1px solid var(--border-color);
  padding: 0 24px;
  height: 64px;
  line-height: 64px;
}

.logo {
  cursor: pointer;
  margin-right: 32px;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  background: linear-gradient(135deg, var(--primary-color), var(--accent));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-family: 'Segoe UI', sans-serif;
  letter-spacing: 2px;
}

.nav-menu {
  flex: 1;
  background: transparent !important;
  border-bottom: none !important;
}

.nav-menu .ant-menu-item {
  color: var(--text-secondary) !important;
}

.nav-menu .ant-menu-item:hover,
.nav-menu .ant-menu-item-selected {
  color: var(--primary-color) !important;
}

.nav-menu .ant-menu-item-selected {
  border-bottom-color: var(--primary-color) !important;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.theme-btn {
  color: var(--text-secondary) !important;
  border-color: var(--border-color) !important;
}

.theme-btn:hover {
  color: var(--primary-color) !important;
  border-color: var(--primary-color) !important;
}

.user-btn {
  color: var(--text-secondary) !important;
}

.user-btn:hover {
  color: var(--primary-color) !important;
}

.content {
  padding: 24px;
  min-height: calc(100vh - 64px - 69px);
  background: var(--bg-color);
}

.footer {
  text-align: center;
  background: var(--bg-secondary) !important;
  color: var(--text-secondary);
  border-top: 1px solid var(--border-color);
  padding: 16px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.ant-card {
  background: var(--bg-card) !important;
  border-color: var(--border-color) !important;
}

.ant-card-head {
  border-bottom-color: var(--border-color) !important;
  color: var(--text-primary) !important;
}

.ant-card-body {
  color: var(--text-secondary) !important;
}

.ant-input,
.ant-select-selector,
.ant-input-password {
  background: var(--bg-card) !important;
  border-color: var(--border-color) !important;
  color: var(--text-primary) !important;
}

.ant-input::placeholder {
  color: var(--text-secondary) !important;
}

.ant-btn-primary {
  background: var(--primary-color) !important;
  border-color: var(--primary-color) !important;
}

.ant-btn-primary:hover {
  background: var(--primary-hover) !important;
  border-color: var(--primary-hover) !important;
}

.ant-rate-star-full .anticon {
  color: var(--accent) !important;
}

.ant-tag {
  background: var(--bg-card) !important;
  border-color: var(--border-color) !important;
  color: var(--text-secondary) !important;
}

.ant-modal-content {
  background: var(--bg-secondary) !important;
}

.ant-modal-header {
  background: var(--bg-secondary) !important;
  border-bottom-color: var(--border-color) !important;
}

.ant-modal-title {
  color: var(--text-primary) !important;
}

.ant-modal-close-x {
  color: var(--text-secondary) !important;
}

.ant-empty-description {
  color: var(--text-secondary) !important;
}

.ant-pagination-item {
  background: var(--bg-card) !important;
  border-color: var(--border-color) !important;
}

.ant-pagination-item a {
  color: var(--text-secondary) !important;
}

.ant-pagination-item-active {
  border-color: var(--primary-color) !important;
}

.ant-pagination-item-active a {
  color: var(--primary-color) !important;
}
</style>

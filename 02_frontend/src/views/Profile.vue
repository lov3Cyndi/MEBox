<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useThemeStore } from '../stores/theme'
import { message } from 'ant-design-vue'
import { UserOutlined, MailOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const themeStore = useThemeStore()
const profile = ref({ username: '', email: '', bio: '', avatar: '' })
const loading = ref(false)

onMounted(async () => {
  if (!userStore.token) { router.push('/login'); return }
  if (userStore.user) { profile.value = { username: userStore.user.username || '', email: userStore.user.email || '', bio: userStore.user.bio || '', avatar: userStore.user.avatar || '' } }
})

async function handleUpdate() {
  loading.value = true
  try { await userStore.api.put('/users/me', profile.value); await userStore.fetchUser(); message.success('更新成功'); }
  catch (e) { console.error(e); message.error('更新失败'); }
  finally { loading.value = false }
}

function handleThemeChange(value) { themeStore.setTheme(value) }
</script>

<template>
  <div class="profile">
    <div class="page-header"><h1 class="page-title">个人中心</h1></div>
    <div class="profile-content">
      <a-card class="profile-card">
        <div class="avatar-section"><a-avatar :size="100" :icon="UserOutlined" :src="profile.avatar" /></div>
        <a-form :model="profile" layout="vertical" @finish="handleUpdate">
          <a-form-item label="用户名"><a-input v-model:value="profile.username"><template #prefix><UserOutlined /></template></a-input></a-form-item>
          <a-form-item label="邮箱"><a-input v-model:value="profile.email"><template #prefix><MailOutlined /></template></a-input></a-form-item>
          <a-form-item label="个人简介"><a-textarea v-model:value="profile.bio" placeholder="介绍一下自己..." :rows="3" /></a-form-item>
          <a-form-item label="头像链接"><a-input v-model:value="profile.avatar" placeholder="输入头像URL" /></a-form-item>
          <a-form-item><a-button type="primary" html-type="submit" :loading="loading">保存修改</a-button></a-form-item>
        </a-form>
      </a-card>
      <a-card class="theme-card"><template #title>主题设置</template><a-radio-group :value="themeStore.theme" @change="(e) => handleThemeChange(e.target.value)"><a-radio value="overwatch">守望先锋蓝</a-radio><a-radio value="talion">黑爪红</a-radio></a-radio-group></a-card>
    </div>
  </div>
</template>

<style scoped>
.profile { max-width: 800px; margin: 0 auto; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 32px; font-weight: 700; color: var(--text-primary); }
.profile-content { display: flex; flex-direction: column; gap: 24px; }
.profile-card, .theme-card { background: var(--bg-card) !important; border-color: var(--border-color) !important; }
.avatar-section { text-align: center; margin-bottom: 24px; }
</style>

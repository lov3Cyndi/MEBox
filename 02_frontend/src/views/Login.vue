<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const form = ref({ username: '', password: '' })
const loading = ref(false)

async function handleLogin() {
  if (!form.value.username || !form.value.password) { message.error('请填写用户名和密码'); return }
  loading.value = true
  try { await userStore.login(form.value.username, form.value.password); message.success('登录成功'); router.push('/'); }
  catch (e) { console.error(e); message.error('用户名或密码错误'); }
  finally { loading.value = false }
}

function goRegister() { router.push('/register') }
</script>

<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header"><h1 class="login-title">登录</h1><p class="login-subtitle">欢迎回到 MEBox</p></div>
      <a-form :model="form" layout="vertical" @finish="handleLogin">
        <a-form-item label="用户名"><a-input v-model:value="form.username" placeholder="输入用户名"><template #prefix><UserOutlined /></template></a-input></a-form-item>
        <a-form-item label="密码"><a-input-password v-model:value="form.password" placeholder="输入密码"><template #prefix><LockOutlined /></template></a-input-password></a-form-item>
        <a-form-item><a-button type="primary" html-type="submit" :loading="loading" block>登录</a-button></a-form-item>
      </a-form>
      <div class="login-footer"><span>还没有账号？</span><a-button type="link" @click="goRegister">立即注册</a-button></div>
    </div>
  </div>
</template>

<style scoped>
.login-page { min-height: calc(100vh - 64px - 69px); display: flex; align-items: center; justify-content: center; }
.login-container { width: 400px; padding: 40px; background: var(--bg-card); border-radius: 16px; border: 1px solid var(--border-color); }
.login-header { text-align: center; margin-bottom: 32px; }
.login-title { font-size: 32px; font-weight: 700; color: var(--text-primary); margin: 0; }
.login-subtitle { color: var(--text-secondary); margin-top: 8px; }
.login-footer { text-align: center; margin-top: 24px; color: var(--text-secondary); }
</style>

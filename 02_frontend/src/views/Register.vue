<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined, MailOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const form = ref({ username: '', email: '', password: '', confirmPassword: '' })
const loading = ref(false)

async function handleRegister() {
  if (!form.value.username || !form.value.email || !form.value.password) { message.error('请填写所有必填项'); return }
  if (form.value.password !== form.value.confirmPassword) { message.error('两次密码输入不一致'); return }
  loading.value = true
  try { await userStore.register(form.value.username, form.value.email, form.value.password); message.success('注册成功'); router.push('/'); }
  catch (e) { console.error(e); message.error('注册失败'); }
  finally { loading.value = false }
}

function goLogin() { router.push('/login') }
</script>

<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-header"><h1 class="register-title">注册</h1><p class="register-subtitle">加入 MEBox 社区</p></div>
      <a-form :model="form" layout="vertical" @finish="handleRegister">
        <a-form-item label="用户名" required><a-input v-model:value="form.username" placeholder="输入用户名"><template #prefix><UserOutlined /></template></a-input></a-form-item>
        <a-form-item label="邮箱" required><a-input v-model:value="form.email" placeholder="输入邮箱"><template #prefix><MailOutlined /></template></a-input></a-form-item>
        <a-form-item label="密码" required><a-input-password v-model:value="form.password" placeholder="输入密码"><template #prefix><LockOutlined /></template></a-input-password></a-form-item>
        <a-form-item label="确认密码" required><a-input-password v-model:value="form.confirmPassword" placeholder="再次输入密码"><template #prefix><LockOutlined /></template></a-input-password></a-form-item>
        <a-form-item><a-button type="primary" html-type="submit" :loading="loading" block>注册</a-button></a-form-item>
      </a-form>
      <div class="register-footer"><span>已有账号？</span><a-button type="link" @click="goLogin">立即登录</a-button></div>
    </div>
  </div>
</template>

<style scoped>
.register-page { min-height: calc(100vh - 64px - 69px); display: flex; align-items: center; justify-content: center; }
.register-container { width: 400px; padding: 40px; background: var(--bg-card); border-radius: 16px; border: 1px solid var(--border-color); }
.register-header { text-align: center; margin-bottom: 32px; }
.register-title { font-size: 32px; font-weight: 700; color: var(--text-primary); margin: 0; }
.register-subtitle { color: var(--text-secondary); margin-top: 8px; }
.register-footer { text-align: center; margin-top: 24px; color: var(--text-secondary); }
</style>

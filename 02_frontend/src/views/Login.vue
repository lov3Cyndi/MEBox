<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined, ArrowLeftOutlined } from '@ant-design/icons-vue'
import { authApi } from '../api'

const router = useRouter()
const userStore = useUserStore()
const form = ref({ username: '', password: '' })
const loading = ref(false)
const currentView = ref('login') // login | reset
const resetForm = ref({ username: '', newPassword: '' })
const resetLoading = ref(false)

async function handleLogin() {
  if (!form.value.username || !form.value.password) { message.error('请填写用户名和密码'); return }
  loading.value = true
  try { await userStore.login(form.value.username, form.value.password); message.success('登录成功'); router.push('/'); }
  catch (e) { console.error(e); message.error('用户名或密码错误'); }
  finally { loading.value = false }
}

function goRegister() { router.push('/register') }

function goToReset() {
  resetForm.value = { username: '', newPassword: '' }
  currentView.value = 'reset'
}

function goBackToLogin() {
  currentView.value = 'login'
}

async function handleResetPassword() {
  if (!resetForm.value.username || !resetForm.value.newPassword) {
    message.error('请填写用户名和新密码')
    return
  }
  resetLoading.value = true
  try {
    await authApi.resetPassword({
      username: resetForm.value.username,
      newPassword: resetForm.value.newPassword
    })
    message.success('密码重置成功，请使用新密码登录')
    currentView.value = 'login'
  } catch (e) {
    console.error(e)
    message.error(e.response?.data?.message || '密码重置失败')
  } finally {
    resetLoading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-bg"></div>
    <div class="login-container">
      <div class="login-header">
        <div class="logo-icon">M</div>
        <h1 class="login-title">{{ currentView === 'login' ? '登录' : '重置密码' }}</h1>
        <p class="login-subtitle">{{ currentView === 'login' ? '欢迎回到 MEBox 游戏社区' : '请输入用户名和新密码' }}</p>
      </div>

      <!-- 登录表单 -->
      <a-form v-if="currentView === 'login'" :model="form" layout="vertical" @finish="handleLogin" class="login-form">
        <a-form-item label="用户名" name="username">
          <a-input v-model:value="form.username" placeholder="请输入用户名" size="large">
            <template #prefix><UserOutlined class="input-icon"/></template>
          </a-input>
        </a-form-item>
        <a-form-item label="密码" name="password">
          <a-input-password v-model:value="form.password" placeholder="请输入密码" size="large">
            <template #prefix><LockOutlined class="input-icon"/></template>
          </a-input-password>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" :loading="loading" block size="large" class="login-btn">登 录</a-button>
        </a-form-item>
      </a-form>

      <!-- 重置密码表单 -->
      <a-form v-else :model="resetForm" layout="vertical" class="login-form">
        <a-form-item label="用户名">
          <a-input v-model:value="resetForm.username" placeholder="请输入用户名" size="large">
            <template #prefix><UserOutlined class="input-icon"/></template>
          </a-input>
        </a-form-item>
        <a-form-item label="新密码">
          <a-input-password v-model:value="resetForm.newPassword" placeholder="请输入新密码" size="large">
            <template #prefix><LockOutlined class="input-icon"/></template>
          </a-input-password>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" :loading="resetLoading" block size="large" class="login-btn" @click="handleResetPassword">确认重置</a-button>
        </a-form-item>
      </a-form>

      <div class="login-footer">
        <template v-if="currentView === 'login'">
          <span class="footer-text">还没有账号？</span>
          <a class="footer-link" @click="goRegister">立即注册</a>
          <span class="footer-divider">|</span>
          <a class="footer-link" @click="goToReset">忘记密码</a>
        </template>
        <template v-else>
          <a class="footer-link" @click="goBackToLogin">
            <ArrowLeftOutlined style="margin-right: 4px;" />返回登录
          </a>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: calc(100vh - 64px - 69px);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0, 153, 204, 0.15) 0%, rgba(10, 10, 15, 0.95) 50%, rgba(0, 153, 204, 0.1) 100%);
  z-index: 0;
}

.login-bg::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at 30% 30%, rgba(0, 153, 204, 0.08) 0%, transparent 50%),
              radial-gradient(circle at 70% 70%, rgba(255, 204, 0, 0.05) 0%, transparent 50%);
  animation: bgMove 20s ease-in-out infinite;
}

@keyframes bgMove {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(-5%, -5%); }
}

.login-container {
  position: relative;
  z-index: 1;
  width: 420px;
  padding: 48px 40px;
  background: linear-gradient(180deg, rgba(26, 26, 37, 0.95) 0%, rgba(20, 20, 30, 0.98) 100%);
  border-radius: 20px;
  border: 1px solid rgba(0, 153, 204, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4),
              0 0 60px rgba(0, 153, 204, 0.1),
              inset 0 1px 0 rgba(255, 255, 255, 0.05);
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.logo-icon {
  width: 72px;
  height: 72px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #0099cc 0%, #00b3f0 100%);
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  font-weight: 800;
  color: #fff;
  box-shadow: 0 4px 20px rgba(0, 153, 204, 0.4);
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 8px;
  letter-spacing: 2px;
}

.login-subtitle {
  color: #c0c0d0;
  font-size: 14px;
  margin-top: 0;
}

.login-form :deep(.ant-form-item-label > label) {
  color: #e0e0f0;
  font-weight: 500;
}

.login-form :deep(.ant-input-affix-wrapper) {
  background: rgba(10, 10, 15, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  padding: 12px 16px;
}

.login-form :deep(.ant-input-affix-wrapper:hover),
.login-form :deep(.ant-input-affix-wrapper:focus),
.login-form :deep(.ant-input-affix-wrapper-focused) {
  border-color: rgba(0, 153, 204, 0.6);
  box-shadow: 0 0 0 3px rgba(0, 153, 204, 0.15);
}

.login-form :deep(.ant-input) {
  background: transparent;
  color: #ffffff;
  font-size: 15px;
}

.login-form :deep(.ant-input::placeholder) {
  color: rgba(160, 160, 176, 0.7);
}

.input-icon {
  color: #0099cc;
  font-size: 18px;
}

.login-btn {
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #0099cc 0%, #00b3f0 100%);
  border: none;
  border-radius: 10px;
  margin-top: 8px;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 153, 204, 0.4);
}

.login-footer {
  text-align: center;
  margin-top: 28px;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.footer-text {
  color: #a0a0b0;
  font-size: 14px;
}

.footer-link {
  color: #00b3f0;
  font-size: 14px;
  font-weight: 500;
  margin-left: 6px;
  cursor: pointer;
  transition: color 0.2s;
}

.footer-link:hover {
  color: #ffcc00;
}

.footer-divider {
  color: rgba(255, 255, 255, 0.2);
  margin: 0 12px;
}
</style>

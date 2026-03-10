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
  try {
    await userStore.register(form.value.username, form.value.email, form.value.password)
    message.success('注册成功')
    router.push('/')
  } catch (e) {
    const errMsg = e.response?.data?.message || e.message || '注册失败'
    message.error(errMsg)
  } finally { loading.value = false }
}

function goLogin() { router.push('/login') }
</script>

<template>
  <div class="register-page">
    <div class="register-bg"></div>
    <div class="register-container">
      <div class="register-header">
        <div class="logo-icon">M</div>
        <h1 class="register-title">注册</h1>
        <p class="register-subtitle">加入 MEBox 游戏社区</p>
      </div>
      <a-form :model="form" layout="vertical" @finish="handleRegister" class="register-form">
        <a-form-item label="用户名" name="username" required>
          <a-input v-model:value="form.username" placeholder="请输入用户名" size="large">
            <template #prefix><UserOutlined class="input-icon"/></template>
          </a-input>
        </a-form-item>
        <a-form-item label="邮箱" name="email" required>
          <a-input v-model:value="form.email" placeholder="请输入邮箱" size="large">
            <template #prefix><MailOutlined class="input-icon"/></template>
          </a-input>
        </a-form-item>
        <a-form-item label="密码" name="password" required>
          <a-input-password v-model:value="form.password" placeholder="请输入密码" size="large">
            <template #prefix><LockOutlined class="input-icon"/></template>
          </a-input-password>
        </a-form-item>
        <a-form-item label="确认密码" name="confirmPassword" required>
          <a-input-password v-model:value="form.confirmPassword" placeholder="请再次输入密码" size="large">
            <template #prefix><LockOutlined class="input-icon"/></template>
          </a-input-password>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" :loading="loading" block size="large" class="register-btn">注 册</a-button>
        </a-form-item>
      </a-form>
      <div class="register-footer">
        <span class="footer-text">已有账号？</span>
        <a class="footer-link" @click="goLogin">立即登录</a>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-page {
  min-height: calc(100vh - 64px - 69px);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.register-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0, 153, 204, 0.15) 0%, rgba(10, 10, 15, 0.95) 50%, rgba(0, 153, 204, 0.1) 100%);
  z-index: 0;
}

.register-bg::before {
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

.register-container {
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

.register-header {
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

.register-title {
  font-size: 28px;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 8px;
  letter-spacing: 2px;
}

.register-subtitle {
  color: #c0c0d0;
  font-size: 14px;
  margin-top: 0;
}

.register-form :deep(.ant-form-item-label > label) {
  color: #e0e0f0;
  font-weight: 500;
}

.register-form :deep(.ant-input-affix-wrapper) {
  background: rgba(10, 10, 15, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  padding: 12px 16px;
}

.register-form :deep(.ant-input-affix-wrapper:hover),
.register-form :deep(.ant-input-affix-wrapper:focus),
.register-form :deep(.ant-input-affix-wrapper-focused) {
  border-color: rgba(0, 153, 204, 0.6);
  box-shadow: 0 0 0 3px rgba(0, 153, 204, 0.15);
}

.register-form :deep(.ant-input) {
  background: transparent;
  color: #ffffff;
  font-size: 15px;
}

.register-form :deep(.ant-input::placeholder) {
  color: rgba(160, 160, 176, 0.7);
}

.input-icon {
  color: #0099cc;
  font-size: 18px;
}

.register-btn {
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #0099cc 0%, #00b3f0 100%);
  border: none;
  border-radius: 10px;
  margin-top: 8px;
  transition: all 0.3s ease;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 153, 204, 0.4);
}

.register-footer {
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
</style>

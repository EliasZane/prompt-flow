<script setup lang="ts">
import { ref, onUnmounted } from 'vue'
import { register, sendSmsCode } from '../../api/user'
import userStore from '../../stores/userStore'
import { useNotification } from '../../composables/useNotification'

const { showNotification } = useNotification()

// 状态
const loading = ref(false)
const countdown = ref(0)
let timer: number | null = null

// 表单数据
const username = ref('')
const phone = ref('')
const smsCode = ref('')
const password = ref('')
const confirmPassword = ref('')

const startCountdown = () => {
  countdown.value = 60
  timer = window.setInterval(() => {
    countdown.value--
    if (countdown.value <= 0 && timer) {
      clearInterval(timer)
      timer = null
    }
  }, 1000)
}

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const handleSendSms = async () => {
  if (!username.value || username.value.length < 2) {
    showNotification('请先填写至少2位长度的用户名', 'warning')
    return
  }
  if (!phone.value || !/^1[3-9]\d{9}$/.test(phone.value)) {
    showNotification('请输入正确的手机号', 'warning')
    return
  }

  try {
    await sendSmsCode({
      phone: phone.value,
      scene: 'REGISTER',
      username: username.value
    })
    showNotification('验证码已发送', 'success')
    startCountdown()
  } catch (error: any) {
    showNotification(error.message || '发送失败', 'error')
  }
}

const handleRegister = async () => {
  if (password.value !== confirmPassword.value) {
    showNotification('两次输入的密码不一致！', 'warning')
    return
  }

  loading.value = true
  try {
    const response = await register({
      username: username.value,
      phone: phone.value,
      code: smsCode.value,
      password: password.value
    })
    userStore.login(response.token, response.username, response.remainingCount, response.totalUsedCount)
    showNotification('注册成功！', 'success')
    window.dispatchEvent(new CustomEvent('navigate', { detail: { path: '/' } }))
  } catch (error: any) {
    showNotification('注册失败：' + (error.message || '未知错误'), 'error')
  } finally {
    loading.value = false
  }
}

const navigateToLogin = () => {
  window.dispatchEvent(new CustomEvent('navigate', { detail: { path: '/login' } }))
}
</script>

<template>
  <div class="auth-container">
    <!-- 左侧：品牌展示 -->
    <div class="brand-side">
      <div class="brand-content">
        <h1 class="brand-title">PromptFlow <span class="ai">AI</span></h1>
        <p class="brand-slogan">让 AI 理解你的奇思妙想</p>
        <div class="brand-features">
          <div class="feature-item">
            <span class="feature-dot"></span>
            支持音乐 / 文案 / 创意表达
          </div>
          <div class="feature-item">
            <span class="feature-dot"></span>
            一键生成专属内容
          </div>
        </div>
      </div>
      <div class="glow-sphere glow-1"></div>
      <div class="glow-sphere glow-2"></div>
    </div>

    <!-- 右侧：表单 -->
    <div class="form-side">
      <div class="auth-card">
        <div class="card-header">
          <h2 class="auth-title">创建账号</h2>
          <p class="auth-subtitle">加入 PromptFlow 开启您的 AI 创作之旅</p>
        </div>
        
        <form @submit.prevent="handleRegister" class="auth-form">
          <div class="form-group">
            <label>用户名</label>
            <div class="input-wrapper">
              <i class="fas fa-user input-icon"></i>
              <input type="text" v-model="username" placeholder="设置您的用户名" required />
            </div>
          </div>

          <div class="form-group">
            <label>手机号</label>
            <div class="input-wrapper">
              <i class="fas fa-mobile-alt input-icon"></i>
              <input type="tel" v-model="phone" placeholder="请输入手机号" required />
            </div>
          </div>

          <div class="form-group">
            <label>验证码</label>
            <div class="input-wrapper">
              <i class="fas fa-shield-alt input-icon"></i>
              <input type="text" v-model="smsCode" placeholder="6位验证码" required maxlength="6" />
              <button 
                type="button" 
                class="btn-send-code" 
                :disabled="countdown > 0"
                @click="handleSendSms"
              >
                {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
              </button>
            </div>
          </div>
          
          <div class="form-group">
            <label>设置密码</label>
            <div class="input-wrapper">
              <i class="fas fa-lock input-icon"></i>
              <input type="password" v-model="password" placeholder="请输入密码" required />
            </div>
          </div>

          <div class="form-group">
            <label>确认密码</label>
            <div class="input-wrapper">
              <i class="fas fa-check-double input-icon"></i>
              <input type="password" v-model="confirmPassword" placeholder="请再次输入密码" required />
            </div>
          </div>
          
          <button type="submit" class="btn-submit" :disabled="loading">
            {{ loading ? '注册中...' : '注册' }}
            <i class="fas fa-arrow-right"></i>
          </button>
        </form>
        
        <p class="auth-footer">
          已有账号？<a href="#" @click.prevent="navigateToLogin">立即登录</a>
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-container {
  display: flex;
  width: 100%;
  min-height: 100vh;
  background-color: #ffffff;
  overflow: hidden;
}

.brand-side {
  flex: 5;
  position: relative;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  overflow: hidden;
}

@media (max-width: 992px) {
  .brand-side { display: none; }
}

.brand-content { position: relative; z-index: 10; text-align: center; }
.brand-title { font-size: 48px; font-weight: 900; color: #111827; margin-bottom: 16px; letter-spacing: -1px; }
.brand-title .ai { color: #10b981; }
.brand-slogan { font-size: 24px; font-weight: 600; color: #374151; margin-bottom: 32px; }
.brand-features { display: flex; flex-direction: column; gap: 16px; align-items: center; }
.feature-item { display: flex; align-items: center; gap: 10px; font-size: 16px; color: #4b5563; font-weight: 500; }
.feature-dot { width: 8px; height: 8px; background-color: #10b981; border-radius: 50%; }

.glow-sphere { position: absolute; border-radius: 50%; filter: blur(80px); z-index: 1; }
.glow-1 { width: 300px; height: 300px; background: rgba(16, 185, 129, 0.2); top: -100px; right: -50px; }
.glow-2 { width: 400px; height: 400px; background: rgba(34, 197, 94, 0.15); bottom: -150px; left: -100px; }

.form-side { flex: 5; display: flex; align-items: center; justify-content: center; padding: 40px; background-color: #ffffff; }
.auth-card { width: 100%; max-width: 440px; }
.card-header { margin-bottom: 32px; }
.auth-title { font-size: 32px; font-weight: 800; color: #111827; margin-bottom: 8px; }
.auth-subtitle { font-size: 16px; color: #6b7280; }

.auth-form { display: flex; flex-direction: column; gap: 20px; }
.form-group { display: flex; flex-direction: column; gap: 8px; }
.form-group label { font-size: 14px; font-weight: 700; color: #374151; margin-left: 4px; }
.input-wrapper { position: relative; display: flex; align-items: center; }
.input-icon { position: absolute; left: 20px; color: #9ca3af; font-size: 16px; }

.form-group input {
  width: 100%;
  padding: 16px 20px 16px 52px;
  background-color: #f9fafb;
  border: 2px solid #f3f4f6;
  border-radius: 18px;
  font-size: 15px;
  color: #111827;
  transition: all 0.2s;
}

.form-group input:focus {
  outline: none;
  background-color: #ffffff;
  border-color: #10b981;
  box-shadow: 0 0 0 4px rgba(16, 185, 129, 0.08);
}

.btn-send-code {
  position: absolute;
  right: 8px;
  padding: 8px 16px;
  background-color: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  color: #10b981;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-send-code:hover:not(:disabled) {
  background-color: #f0fdf4;
  border-color: #10b981;
}

.btn-send-code:disabled {
  color: #9ca3af;
  cursor: not-allowed;
}

.btn-submit {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 16px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: #ffffff;
  border: none;
  border-radius: 18px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 10px 25px -5px rgba(16, 185, 129, 0.3);
  margin-top: 12px;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 20px 30px -10px rgba(16, 185, 129, 0.4);
}

.btn-submit:disabled { opacity: 0.7; cursor: not-allowed; }

.auth-footer { text-align: center; font-size: 15px; color: #6b7280; margin-top: 32px; }
.auth-footer a { color: #10b981; text-decoration: none; font-weight: 700; }
</style>

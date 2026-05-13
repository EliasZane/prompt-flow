<script setup lang="ts">
import { ref, onUnmounted } from 'vue'
import { login, sendSmsCode, resetPassword } from '../../api/user'
import userStore from '../../stores/userStore'
import { useNotification } from '../../composables/useNotification'

const { showNotification } = useNotification()

// 状态控制
const loginType = ref<'PASSWORD' | 'SMS' | 'FORGOT'>('PASSWORD')
const loading = ref(false)

// 表单数据
const username = ref('')
const password = ref('')
const phone = ref('')
const smsCode = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

// 验证码倒计时
const countdown = ref(0)
let timer: number | null = null

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

const handleSendSms = async (scene: 'LOGIN' | 'RESET_PWD') => {
  if (!phone.value || !/^1[3-9]\d{9}$/.test(phone.value)) {
    showNotification('请输入正确的手机号', 'warning')
    return
  }

  try {
    await sendSmsCode({
      phone: phone.value,
      scene: scene
    })
    showNotification('验证码已发送', 'success')
    startCountdown()
  } catch (error: any) {
    showNotification(error.message || '发送失败', 'error')
  }
}

const handleLogin = async () => {
  loading.value = true
  try {
    const loginData: any = {
      loginType: loginType.value
    }

    if (loginType.value === 'PASSWORD') {
      loginData.username = username.value
      loginData.password = password.value
    } else {
      loginData.phone = phone.value
      loginData.code = smsCode.value
    }

    const response = await login(loginData)
    userStore.login(response.token, response.username, response.remainingCount, response.totalUsedCount)
    showNotification('登录成功！', 'success')
    window.dispatchEvent(new CustomEvent('navigate', { detail: { path: '/' } }))
  } catch (error: any) {
    showNotification('登录失败：' + (error.message || '未知错误'), 'error')
  } finally {
    loading.value = false
  }
}

const handleResetPassword = async () => {
  if (newPassword.value !== confirmPassword.value) {
    showNotification('两次输入的密码不一致', 'warning')
    return
  }

  loading.value = true
  try {
    await resetPassword({
      phone: phone.value,
      code: smsCode.value,
      newPassword: newPassword.value
    })
    showNotification('密码重置成功，请登录', 'success')
    loginType.value = 'PASSWORD'
  } catch (error: any) {
    showNotification('重置失败：' + (error.message || '未知错误'), 'error')
  } finally {
    loading.value = false
  }
}

const navigateToRegister = () => {
  window.dispatchEvent(new CustomEvent('navigate', { detail: { path: '/register' } }))
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
          <h2 class="auth-title">
            {{ loginType === 'FORGOT' ? '找回密码' : '欢迎回来' }}
          </h2>
          <p class="auth-subtitle">
            {{ loginType === 'FORGOT' ? '通过手机验证码重置您的密码' : '请登录以继续您的创作之旅' }}
          </p>
        </div>

        <!-- 登录方式切换 -->
        <div v-if="loginType !== 'FORGOT'" class="login-tabs">
          <button 
            :class="{ active: loginType === 'PASSWORD' }" 
            @click="loginType = 'PASSWORD'"
          >账号登录</button>
          <button 
            :class="{ active: loginType === 'SMS' }" 
            @click="loginType = 'SMS'"
          >验证码登录</button>
        </div>
        
        <form v-if="loginType !== 'FORGOT'" @submit.prevent="handleLogin" class="auth-form">
          <!-- 账号登录字段 -->
          <template v-if="loginType === 'PASSWORD'">
            <div class="form-group">
              <label>用户名 / 手机号</label>
              <div class="input-wrapper">
                <i class="fas fa-user input-icon"></i>
                <input type="text" v-model="username" placeholder="请输入用户名或手机号" required />
              </div>
            </div>
            
            <div class="form-group">
              <label>密码</label>
              <div class="input-wrapper">
                <i class="fas fa-lock input-icon"></i>
                <input type="password" v-model="password" placeholder="请输入密码" required />
              </div>
            </div>
          </template>

          <!-- 验证码登录字段 -->
          <template v-else>
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
                  @click="handleSendSms('LOGIN')"
                >
                  {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
                </button>
              </div>
            </div>
          </template>
          
          <div class="form-options">
            <a href="#" @click.prevent="loginType = 'FORGOT'">忘记密码？</a>
          </div>

          <button type="submit" class="btn-submit" :disabled="loading">
            {{ loading ? '登录中...' : '登录' }}
            <i class="fas fa-arrow-right"></i>
          </button>
        </form>

        <!-- 找回密码表单 -->
        <form v-else @submit.prevent="handleResetPassword" class="auth-form">
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
                @click="handleSendSms('RESET_PWD')"
              >
                {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
              </button>
            </div>
          </div>

          <div class="form-group">
            <label>新密码</label>
            <div class="input-wrapper">
              <i class="fas fa-lock input-icon"></i>
              <input type="password" v-model="newPassword" placeholder="设置新密码" required />
            </div>
          </div>

          <div class="form-group">
            <label>确认密码</label>
            <div class="input-wrapper">
              <i class="fas fa-check-double input-icon"></i>
              <input type="password" v-model="confirmPassword" placeholder="请再次输入新密码" required />
            </div>
          </div>

          <button type="submit" class="btn-submit" :disabled="loading">
            {{ loading ? '重置中...' : '重置并登录' }}
          </button>

          <div class="form-options text-center">
            <a href="#" @click.prevent="loginType = 'PASSWORD'">返回登录</a>
          </div>
        </form>
        
        <p class="auth-footer">
          还没有账号？<a href="#" @click.prevent="navigateToRegister">立即注册</a>
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

.login-tabs {
  display: flex;
  gap: 24px;
  margin-bottom: 32px;
  border-bottom: 2px solid #f3f4f6;
}

.login-tabs button {
  padding: 12px 4px;
  background: none;
  border: none;
  font-size: 16px;
  font-weight: 700;
  color: #9ca3af;
  cursor: pointer;
  position: relative;
  transition: all 0.2s;
}

.login-tabs button.active {
  color: #10b981;
}

.login-tabs button.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #10b981;
}

.auth-form { display: flex; flex-direction: column; gap: 24px; }
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

.form-options {
  display: flex;
  justify-content: flex-end;
  font-size: 14px;
}

.form-options a { color: #6b7280; text-decoration: none; }
.form-options a:hover { color: #10b981; }

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
  margin-top: 8px;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 20px 30px -10px rgba(16, 185, 129, 0.4);
}

.btn-submit:disabled { opacity: 0.7; cursor: not-allowed; }

.auth-footer { text-align: center; font-size: 15px; color: #6b7280; margin-top: 32px; }
.auth-footer a { color: #10b981; text-decoration: none; font-weight: 700; }

.text-center { text-align: center; }
</style>

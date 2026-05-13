<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import userStore from '../stores/userStore' // 引入 userStore
import ConfirmModal from '../components/ConfirmModal.vue'
import logoImage from '../assets/logo.png'

interface NavItem {
  name: string
  path: string
}

const navigationItems: NavItem[] = []

// V1: 简单模拟路由状态，实际开发中会被 vue-router 替代
const currentPath = ref('/')
const showLogoutConfirm = ref(false)

const isAuthPage = () => currentPath.value === '/login' || currentPath.value === '/register'

const navigateTo = (path: string) => {
  currentPath.value = path
  window.dispatchEvent(new CustomEvent('navigate', { detail: { path } }))
}

const handleNavigateEvent = (e: Event) => {
  const customEvent = e as CustomEvent
  if (customEvent.detail && customEvent.detail.path) {
    currentPath.value = customEvent.detail.path
  }
}

const handleLogout = () => {
  showLogoutConfirm.value = true
}

const confirmLogout = () => {
  userStore.logout() // 调用 userStore 的登出方法
  showLogoutConfirm.value = false
  navigateTo('/') // 登出后重定向到首页
}

onMounted(() => {
  window.addEventListener('navigate', handleNavigateEvent)
})

onUnmounted(() => {
  window.removeEventListener('navigate', handleNavigateEvent)
})
</script>

<template>
  <div class="layout-shell">
    <header v-if="!isAuthPage()" class="layout-header" :class="{ 'is-home': currentPath === '/' }">
      <div class="logo" @click="navigateTo('/')">
        <img :src="logoImage" alt="Logo" class="logo-img" />
        <span class="logo-text">PromptFlow <span class="ai">AI</span></span>
      </div>
      
      <div class="header-right">
        <nav class="layout-nav" aria-label="Primary">
          <a 
            v-for="item in navigationItems" 
            :key="item.path" 
            :href="item.path"
            @click.prevent="navigateTo(item.path)"
            class="layout-nav-item"
            :class="{ active: currentPath === item.path || (currentPath.startsWith('/template/detail') && item.path === '/') }"
          >
            {{ item.name }}
          </a>
        </nav>
        <div v-if="userStore.state.isLoggedIn" class="user-dropdown-container">
          <div class="user-profile-trigger">
            <div class="user-avatar-small">
              {{ userStore.state.username?.substring(0, 1).toUpperCase() }}
            </div>
            <span class="user-name">{{ userStore.state.username }}</span>
            <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2.5">
              <polyline points="6 9 12 15 18 9"></polyline>
            </svg>
          </div>
          <div class="user-dropdown-menu">
            <div class="dropdown-header">
              <div class="user-avatar-large">
                {{ userStore.state.username?.substring(0, 1).toUpperCase() }}
              </div>
              <div class="user-details">
                <div class="user-full-name">{{ userStore.state.username }}</div>
                <div class="user-role">普通用户</div>
              </div>
            </div>
            <div class="dropdown-divider"></div>
            <div class="dropdown-item" @click="navigateTo('/personal-center')">
              <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
              </svg>
              个人中心
            </div>
            <div class="dropdown-item logout-item" @click="handleLogout">
              <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4M16 17l5-5-5-5M21 12H9"></path>
              </svg>
              退出登录
            </div>
          </div>
        </div>
        <button v-else class="btn-auth" @click="navigateTo('/login')">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
            <circle cx="12" cy="7" r="4"></circle>
          </svg>
          登录 / 注册
        </button>
      </div>
    </header>

    <main class="layout-content" :class="{ 'is-home': currentPath === '/', 'is-auth': isAuthPage() }">
      <div class="layout-content-inner">
        <!-- 路由占位，实际开发将使用 <router-view /> -->
        <slot />
      </div>
    </main>

    <!-- 退出登录确认弹窗 -->
    <ConfirmModal
      :show="showLogoutConfirm"
      title="确认退出登录？"
      description="退出后将需要重新登录才能使用完整功能。"
      confirm-text="确认退出"
      @confirm="confirmLogout"
      @cancel="showLogoutConfirm = false"
    />
  </div>
</template>

<style scoped>
.layout-shell {
  min-height: 100vh;
  background-color: #ffffff;
}

.layout-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 40px;
  background-color: #ffffff;
  border-bottom: 1px solid #f1f5f9;
  position: sticky;
  top: 0;
  z-index: 100;
  width: 100%;
  box-sizing: border-box;
}

.layout-header.is-home {
  padding: 16px 20px;
  border-bottom: none;
}

.logo {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
}

.logo-img {
  width: 24px;
  height: 24px;
  object-fit: cover;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  color: #111;
  letter-spacing: 0.5px;
}

.logo-text .ai {
  color: #10b981;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 40px;
}

.layout-nav {
  display: flex;
  gap: 32px;
}

.layout-nav-item {
  text-decoration: none;
  color: #64748b;
  font-size: 14px;
  font-weight: 700;
  padding: 8px 20px;
  border-radius: 100px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.layout-nav-item:hover {
  color: #00c08b;
  background-color: rgba(0, 192, 139, 0.05);
}

.layout-nav-item.active {
  color: #00c08b;
  background-color: rgba(0, 192, 139, 0.08);
}

.btn-auth {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 24px;
  background-color: #00c08b;
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-auth:hover {
  background-color: #00a376;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 192, 139, 0.25);
}

/* 用户下拉菜单样式 */
.user-dropdown-container {
  position: relative;
  padding: 4px 0;
}

.user-profile-trigger {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 12px;
  padding-right: 8px;
  border-radius: 100px;
  cursor: pointer;
  transition: all 0.2s;
  color: #475569;
  border: 1px solid transparent;
}

.user-profile-trigger:hover {
  background-color: #f8fafc;
  border-color: #e2e8f0;
}

.user-avatar-small {
  width: 28px;
  height: 28px;
  background-color: #00c08b;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 800;
}

.user-name {
  font-size: 14px;
  font-weight: 700;
  color: #1e293b;
}

.user-dropdown-menu {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 240px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
  opacity: 0;
  visibility: hidden;
  transform: translateY(10px);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1000;
  overflow: hidden;
}

.user-dropdown-container:hover .user-dropdown-menu {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.dropdown-header {
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  background-color: #f8fafc;
}

.user-avatar-large {
  width: 44px;
  height: 44px;
  background-color: #00c08b;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 800;
}

.user-full-name {
  font-size: 16px;
  font-weight: 800;
  color: #0f172a;
}

.user-role {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

.dropdown-divider {
  height: 1px;
  background-color: #f1f5f9;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  font-size: 14px;
  font-weight: 600;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s;
}

.dropdown-item:hover {
  background-color: #f8fafc;
  color: #00c08b;
}

.dropdown-item svg {
  color: #94a3b8;
  transition: color 0.2s;
}

.dropdown-item:hover svg {
  color: #00c08b;
}

.logout-item {
  color: #ef4444;
}

.logout-item:hover {
  background-color: #fef2f2;
  color: #dc2626;
}

.logout-item:hover svg {
  color: #dc2626;
}

.layout-content {
  padding: 0;
  width: 100%;
  max-width: none;
}

.layout-content.is-home,
.layout-content.is-auth {
  max-width: none;
  margin: 0;
}

.layout-content-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 20px;
}

.is-home .layout-content-inner,
.is-auth .layout-content-inner {
  max-width: none;
  padding: 0;
  margin: 0;
}
</style>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

interface NavItem {
  name: string
  path: string
}

const navigationItems: NavItem[] = [
  // { name: '首页', path: '/' },
  // { name: '历史记录', path: '/history' },
]

// V1: 简单模拟路由状态，实际开发中会被 vue-router 替代
const currentPath = ref('/')

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

onMounted(() => {
  window.addEventListener('navigate', handleNavigateEvent)
})

onUnmounted(() => {
  window.removeEventListener('navigate', handleNavigateEvent)
})
</script>

<template>
  <div class="layout-shell">
    <header class="layout-header" :class="{ 'is-home': currentPath === '/' }">
      <div class="layout-logo-container" @click="navigateTo('/')">
        <div class="layout-logo-icon">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
            <path d="M12 3v10.55c-.59-.34-1.27-.55-2-.55-2.21 0-4 1.79-4 4s1.79 4 4 4 4-1.79 4-4V7h4V3h-6z"/>
          </svg>
        </div>
        <span class="layout-logo-text">AI音乐提示词</span>
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
        <button class="btn-auth">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
            <circle cx="12" cy="7" r="4"></circle>
          </svg>
          登录 / 注册
        </button>
      </div>
    </header>

    <main class="layout-content" :class="{ 'is-home': currentPath === '/' }">
      <div class="layout-content-inner">
        <!-- 路由占位，实际开发将使用 <router-view /> -->
        <slot />
      </div>
    </main>
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

.layout-logo-container {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.layout-logo-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background-color: #00c08b;
  color: white;
  border-radius: 10px;
}

.layout-logo-text {
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.02em;
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
  font-size: 15px;
  font-weight: 600;
  padding: 8px 0;
  transition: all 0.2s ease;
  position: relative;
}

.layout-nav-item:hover {
  color: #00c08b;
}

.layout-nav-item.active {
  color: #0f172a;
}

.layout-nav-item.active::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #00c08b;
  border-radius: 2px;
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
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-auth:hover {
  background-color: #00a376;
  transform: translateY(-3px);
  box-shadow: 0 20px 30px -10px rgba(0, 192, 139, 0.4);
}

.layout-content {
  padding: 0;
  width: 100%;
}

.layout-content-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 20px;
}

.is-home .layout-content-inner {
  max-width: none;
  padding: 0;
}
</style>

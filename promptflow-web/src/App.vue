<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import DefaultLayout from './layout/DefaultLayout.vue'
import HomeView from './views/home/index.vue'
import TemplateDetailView from './views/template/detail.vue'
import HistoryView from './views/history/index.vue'
import ResultView from './views/result/index.vue'
import LoginView from './views/auth/Login.vue'
import RegisterView from './views/auth/Register.vue'
import PersonalCenterView from './views/user/PersonalCenter.vue'
import Notification from './components/Notification.vue'
import { useNotification } from './composables/useNotification'
import { getUserInfo } from './api/user'
import userStore from './stores/userStore'

const { message, type, isVisible, showNotification } = useNotification()

// 状态同步
const syncUserInfo = async () => {
  if (userStore.state.isLoggedIn) {
    try {
      const info = await getUserInfo()
      userStore.updateUserInfo(info.username, info.remainingCount, info.totalUsedCount)
    } catch (error) {
      console.error('同步用户信息失败:', error)
    }
  }
}

// V1 简单模拟路由
const currentPath = ref('/')
const currentParams = ref<Record<string, any>>({})
const scrollPositions = new Map<string, number>()

const handleNavigate = (e: Event) => {
  const customEvent = e as CustomEvent
  if (customEvent.detail && customEvent.detail.path) {
    // 记录当前页面的滚动位置
    scrollPositions.set(currentPath.value, window.scrollY)

    currentPath.value = customEvent.detail.path
    currentParams.value = customEvent.detail

    // 在 DOM 更新后恢复目标页面的滚动位置
    nextTick(() => {
      const savedPosition = scrollPositions.get(currentPath.value) || 0
      window.scrollTo({
        top: savedPosition,
        behavior: 'instant'
      })
    })
  }
}

const handleGlobalNotification = (e: Event) => {
  const customEvent = e as CustomEvent
  if (customEvent.detail && customEvent.detail.message) {
    showNotification(customEvent.detail.message, customEvent.detail.type || 'info')
  }
}

onMounted(() => {
  window.addEventListener('navigate', handleNavigate)
  window.addEventListener('notification', handleGlobalNotification)
  syncUserInfo() // 初始化同步用户信息
})

onUnmounted(() => {
  window.removeEventListener('navigate', handleNavigate)
  window.removeEventListener('notification', handleGlobalNotification)
})
</script>

<template>
  <DefaultLayout>
    <HomeView v-if="currentPath === '/'" />
    <TemplateDetailView v-else-if="currentPath === '/template/detail'" :template-code="currentParams.templateCode" />
    <HistoryView v-else-if="currentPath === '/history'" />
    <ResultView v-else-if="currentPath === '/result'" :params="currentParams" />
    <LoginView v-else-if="currentPath === '/login'" />
    <RegisterView v-else-if="currentPath === '/register'" />
    <PersonalCenterView v-else-if="currentPath === '/personal-center'" :params="currentParams" />
    <div v-else>
      <h2>页面未找到</h2>
    </div>
    <Notification :message="message" :type="type" :is-visible="isVisible" />
  </DefaultLayout>
</template>

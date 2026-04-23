<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import DefaultLayout from './layout/DefaultLayout.vue'
import HomeView from './views/home/index.vue'
import TemplateDetailView from './views/template/detail.vue'
import HistoryView from './views/history/index.vue'
import ResultView from './views/result/index.vue'

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

onMounted(() => {
  window.addEventListener('navigate', handleNavigate)
})

onUnmounted(() => {
  window.removeEventListener('navigate', handleNavigate)
})
</script>

<template>
  <DefaultLayout>
    <HomeView v-if="currentPath === '/'" />
    <TemplateDetailView v-else-if="currentPath === '/template/detail'" :template-code="currentParams.templateCode" />
    <HistoryView v-else-if="currentPath === '/history'" />
    <ResultView v-else-if="currentPath === '/result'" :params="currentParams" />
    <div v-else>
      <h2>页面未找到</h2>
    </div>
  </DefaultLayout>
</template>

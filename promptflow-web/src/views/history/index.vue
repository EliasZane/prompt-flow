<template>
  <div class="personal-center-page">
    <!-- 面包屑 -->
    <div class="page-breadcrumb">
      <span class="breadcrumb-item" @click="handleBack">首页</span>
      <span class="breadcrumb-separator">›</span>
      <span class="breadcrumb-item active">生成历史</span>
    </div>

    <div class="personal-layout">
      <!-- 侧边栏 (复用 PersonalCenter 风格) -->
      <aside class="personal-sidebar">
        <div class="user-profile">
          <div class="user-avatar">
            {{ userStore.state.username?.substring(0, 1).toUpperCase() }}
          </div>
          <div class="user-info">
            <div class="username">{{ userStore.state.username }}</div>
            <div class="user-status">剩余生成次数: <span class="highlight">{{ userStore.state.remainingCount }}</span> 次</div>
          </div>
        </div>
        <nav class="sidebar-nav">
          <div class="nav-item" @click="navigateTo('/personal-center')">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
            </svg>
            <span>个人中心</span>
          </div>
          <div 
            class="nav-item" 
            @click="navigateTo('/personal-center', { tab: 'recharge' })"
          >
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 8c-1.657 0-3 1.343-3 3s1.343 3 3 3 3-1.343 3-3-1.343-3-3-3z"></path>
              <path d="M12 2C6.477 2 2 6.477 2 12s4.477 10 10 10 10-4.477 10-10S17.523 2 12 2zm0 18c-4.411 0-8-3.589-8-8s3.589-8 8-8 8 3.589 8 8-3.589 8-8 8z"></path>
            </svg>
            <span>充值次数</span>
          </div>
          <div class="nav-item active">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
            <span>生成历史</span>
          </div>
          <div class="nav-item" @click="navigateTo('/personal-center', { tab: 'settings' })">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"></path>
              <path d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
            </svg>
            <span>账号设置</span>
          </div>
          <div class="nav-item" @click="handleLogout">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path>
            </svg>
            <span>退出登录</span>
          </div>
        </nav>
      </aside>

      <!-- 主内容区: 表格展示 -->
      <main class="personal-main">
        <div class="history-container">
          <div class="section-header">
            <h2 class="section-title">我的生成历史</h2>
            <div class="tag-filters">
              <button
                class="filter-chip"
                :class="{ active: selectedTag === '' }"
                @click="handleTagSelect('')"
              >
                全部
              </button>
              <button
                v-for="tag in uniqueTags"
                :key="tag"
                class="filter-chip"
                :class="{ active: selectedTag === tag }"
                @click="handleTagSelect(tag)"
              >
                {{ tag }}
              </button>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="history-loading">
            <div class="loading-spinner"></div>
            <span>正在加载历史记录...</span>
          </div>

          <!-- 无数据状态 -->
          <div v-else-if="userHistory.length === 0" class="empty-state">
            <div class="empty-icon">
              <svg viewBox="0 0 24 24" width="64" height="64" fill="none" stroke="#e2e8f0" stroke-width="1.5">
                <path d="M9 13h6m-3-3v6m-9 1V7a2 2 0 012-2h6l2 2h6a2 2 0 012 2v8a2 2 0 01-2 2H5a2 2 0 01-2-2z"></path>
              </svg>
            </div>
            <p>暂无生成历史，快去尝试创作吧</p>
            <button class="btn-primary" @click="handleBack">去创作</button>
          </div>

          <!-- 历史列表: 表格 -->
          <div v-else class="table-container">
            <table class="history-table">
              <thead>
                <tr>
                  <th style="width: 100px">类型</th>
                  <th>标题</th>
                  <th>关联内容</th>
                  <th style="width: 200px">生成时间</th>
                  <th style="width: 140px; text-align: center;">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in userHistory" :key="item.id">
                  <td>
                    <span class="type-badge">{{ getTemplateName(item.templateCode) }}</span>
                  </td>
                  <td class="col-title">{{ getItemTitle(item) }}</td>
                  <td class="col-subtitle">{{ getItemSubtitle(item) }}</td>
                  <td class="col-date">{{ formatDate(item.createdAt) }}</td>
                  <td style="text-align: center;">
                    <button class="btn-action" @click="viewDetail(item)">查看详情</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </main>
    </div>

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

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getHistoryList } from '../../api/history'
import { getUserInfo } from '../../api/user'
import userStore from '../../stores/userStore'
import { useNotification } from '../../composables/useNotification'
import ConfirmModal from '../../components/ConfirmModal.vue'

const { showNotification } = useNotification()

const userHistory = ref<any[]>([])
const loading = ref(true)
const selectedTag = ref('')
const showLogoutConfirm = ref(false)

const fetchUserInfo = async () => {
  try {
    const info = await getUserInfo()
    userStore.updateUserInfo(info.username, info.remainingCount, info.totalUsedCount)
  } catch (error) {
    console.error('Failed to fetch user info:', error)
  }
}

const fetchUserHistory = async () => {
  if (!userStore.state.isLoggedIn) {
    showNotification('请先登录以查看历史记录', 'warning')
    loading.value = false
    return
  }

  loading.value = true
  try {
    const data = await getHistoryList()
    userHistory.value = data
  } catch (error: any) {
    console.error('获取历史记录失败:', error)
    showNotification(error.message || '获取历史记录失败，请稍后再试', 'error')
  } finally {
    loading.value = false
  }
}

const uniqueTags = computed(() => {
  const tags = new Set<string>()
  userHistory.value.forEach(item => {
    if (item.tag) {
      tags.add(item.tag)
    }
  })
  return Array.from(tags)
})

const handleTagSelect = (tag: string) => {
  selectedTag.value = tag
  fetchUserHistory()
}

const navigateTo = (path: string, params: Record<string, any> = {}) => {
  window.dispatchEvent(new CustomEvent('navigate', { 
    detail: { path, ...params } 
  }))
}

const handleBack = () => {
  navigateTo('/')
}

const handleLogout = () => {
  showLogoutConfirm.value = true
}

const confirmLogout = () => {
  userStore.logout()
  handleBack()
}

const viewDetail = (item: any) => {
  window.dispatchEvent(new CustomEvent('navigate', { 
    detail: { 
      path: '/result', 
      taskId: item.id,
      historyData: item
    } 
  }))
}

const getItemTitle = (item: any) => {
  return item.outputData?.title || '未命名生成'
}

const getItemSubtitle = (item: any) => {
  const input = item.inputData || {}
  if (input.songName) return input.songName
  if (input.prompt) return input.prompt.substring(0, 30) + '...'
  return '-'
}

const getTemplateName = (code: string) => {
  if (code === 'ai_cover_prompt') return 'AI 翻唱'
  return code
}

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', { 
    year: 'numeric',
    month: '2-digit', 
    day: '2-digit', 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

onMounted(() => {
  fetchUserInfo()
  fetchUserHistory()
})
</script>

<style scoped>
.personal-center-page {
  max-width: 1600px;
  margin: 0 auto;
  padding: 0 16px 40px;
}

.page-breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 20px 0;
  font-size: 14px;
  color: #94a3b8;
}

.breadcrumb-item {
  cursor: pointer;
  transition: color 0.2s;
}

.breadcrumb-item:hover:not(.active) {
  color: #00c08b;
}

.breadcrumb-item.active {
  color: #64748b;
  cursor: default;
}

.breadcrumb-separator {
  color: #cbd5e1;
}

.personal-layout {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 24px;
  align-items: start;
}

/* 侧边栏复用 */
.personal-sidebar {
  background: #ffffff;
  border-radius: 24px;
  border: 1px solid #e2e8f0;
  padding: 30px 20px;
  position: sticky;
  top: 20px;
}

.user-profile {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 30px;
  border-bottom: 1px solid #f1f5f9;
}

.user-avatar {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #00c08b, #00d9a5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 800;
  color: white;
  margin-bottom: 12px;
  box-shadow: 0 8px 16px rgba(0, 192, 139, 0.15);
}

.username {
  font-size: 18px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 4px;
}

.user-status {
  font-size: 13px;
  color: #64748b;
  margin-top: 4px;
}

.highlight {
  color: #00c08b;
  font-weight: 700;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 12px;
  color: #64748b;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}

.nav-item:hover {
  background: #f8fafc;
  color: #0f172a;
}

.nav-item.active {
  background: #f0fdf9;
  color: #00c08b;
}

/* 表格样式 */
.table-container {
  background: white;
  border-radius: 24px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
}

.history-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

.history-table th {
  background: #f8fafc;
  padding: 16px 20px;
  font-size: 13px;
  font-weight: 700;
  color: #64748b;
  border-bottom: 1px solid #f1f5f9;
}

.history-table td {
  padding: 16px 20px;
  font-size: 14px;
  color: #334155;
  border-bottom: 1px solid #f1f5f9;
  vertical-align: middle;
}

.history-table tr:last-child td {
  border-bottom: none;
}

.history-table tr:hover td {
  background: #fbfdfe;
}

.type-badge {
  font-size: 11px;
  font-weight: 700;
  color: #00c08b;
  background: #f0fdf9;
  padding: 4px 10px;
  border-radius: 6px;
}

.col-title {
  font-weight: 700;
  color: #0f172a;
  max-width: 500px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.col-subtitle {
  color: #64748b;
  font-size: 13px;
  max-width: 400px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.item-tag {
  font-size: 11px;
  color: #64748b;
  background: #f1f5f9;
  padding: 2px 8px;
  border-radius: 4px;
}

.col-date {
  color: #94a3b8;
  font-size: 13px;
  white-space: nowrap;
}

.btn-action {
  padding: 6px 14px;
  background: transparent;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  color: #64748b;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-action:hover {
  border-color: #00c08b;
  color: #00c08b;
  background: #f0fdf9;
}

/* 原有样式复用 */
.section-header {
  margin-bottom: 24px;
}

.section-title {
  font-size: 24px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 16px;
}

.tag-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.filter-chip {
  padding: 6px 16px;
  border-radius: 100px;
  background: white;
  border: 1px solid #e2e8f0;
  color: #64748b;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-chip.active {
  background: #00c08b;
  border-color: #00c08b;
  color: white;
}

.history-loading, .empty-state {
  padding: 80px 0;
  text-align: center;
  color: #94a3b8;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #f1f5f9;
  border-top-color: #00c08b;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.btn-primary {
  margin-top: 20px;
  padding: 10px 24px;
  background: #00c08b;
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
}

@media (max-width: 992px) {
  .personal-layout {
    grid-template-columns: 1fr;
  }
}
</style>

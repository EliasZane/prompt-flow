<template>
  <div class="personal-center-page">
    <!-- 面包屑 -->
    <div class="page-breadcrumb">
      <span class="breadcrumb-item" @click="handleBack">首页</span>
      <span class="breadcrumb-separator">›</span>
      <span class="breadcrumb-item active">个人中心</span>
    </div>

    <div class="personal-layout">
      <!-- 侧边栏 -->
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
          <div 
            class="nav-item" 
            :class="{ active: activeTab === 'profile' }" 
            @click="activeTab = 'profile'"
          >
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
            </svg>
            <span>个人中心</span>
          </div>
          <div 
            class="nav-item" 
            :class="{ active: activeTab === 'recharge' }" 
            @click="activeTab = 'recharge'"
          >
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 8c-1.657 0-3 1.343-3 3s1.343 3 3 3 3-1.343 3-3-1.343-3-3-3z"></path>
              <path d="M12 2C6.477 2 2 6.477 2 12s4.477 10 10 10 10-4.477 10-10S17.523 2 12 2zm0 18c-4.411 0-8-3.589-8-8s3.589-8 8-8 8 3.589 8 8-3.589 8-8 8z"></path>
            </svg>
            <span>充值次数</span>
          </div>
          <div 
            class="nav-item" 
            @click="navigateTo('/history')"
          >
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
            <span>生成历史</span>
          </div>
          <div 
            class="nav-item" 
            :class="{ active: activeTab === 'settings' }" 
            @click="activeTab = 'settings'"
          >
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

      <!-- 主内容区 -->
      <main class="personal-main">
        <!-- 个人资料页 -->
        <div v-if="activeTab === 'profile'" class="content-card">
          <h2 class="section-title">基本信息</h2>
          <div class="profile-details">
            <div class="detail-item">
              <label>用户名</label>
              <div class="value">{{ userStore.state.username }}</div>
            </div>
            <div class="detail-item">
              <label>注册时间</label>
              <div class="value">2026-04-27</div>
            </div>
            <div class="detail-item">
              <label>剩余次数</label>
              <div class="value">{{ userStore.state.remainingCount }} 次</div>
            </div>
            <div class="detail-item">
              <label>累计使用</label>
              <div class="value">{{ userStore.state.totalUsedCount }} 次</div>
            </div>
            <div class="detail-item">
              <label>用户角色</label>
              <div class="value">普通用户</div>
            </div>
          </div>
        </div>

        <!-- 充值页 -->
        <div v-else-if="activeTab === 'recharge'" class="content-card">
          <h2 class="section-title">充值套餐</h2>
          <div class="package-grid">
            <div 
              v-for="pkg in packages" 
              :key="pkg.id" 
              class="package-card"
              :class="{ selected: selectedPackage?.id === pkg.id }"
              @click="selectedPackage = pkg"
            >
              <div class="package-name">{{ pkg.packageName }}</div>
              <div class="package-count">{{ pkg.generateCount }} 次</div>
              <div class="package-price">¥{{ pkg.price }}</div>
            </div>
          </div>

          <div v-if="selectedPackage" class="recharge-form">
            <h3 class="sub-section-title">确认订单</h3>
            <div class="order-info">
              <div class="order-item">
                <span>套餐名称：</span>
                <span>{{ selectedPackage.packageName }}</span>
              </div>
              <div class="order-item">
                <span>包含次数：</span>
                <span>{{ selectedPackage.generateCount }} 次</span>
              </div>
              <div class="order-item">
                <span>支付金额：</span>
                <span class="price">¥{{ selectedPackage.price }}</span>
              </div>
            </div>

            <div class="pay-method">
              <label>支付方式</label>
              <div class="pay-options">
                <div 
                  class="pay-option disabled" 
                  title="暂不支持"
                >微信支付 (暂不支持)</div>
                <div 
                  class="pay-option" 
                  :class="{ active: payChannel === 'ALIPAY' }"
                  @click="payChannel = 'ALIPAY'"
                >支付宝</div>
              </div>
            </div>

            <button 
              class="btn-primary recharge-btn" 
              :disabled="loading"
              @click="handleRecharge"
            >
              {{ loading ? '跳转支付中...' : '立即支付' }}
            </button>
          </div>
        </div>

        <!-- 账号设置 (占位) -->
        <div v-else-if="activeTab === 'settings'" class="content-card">
          <h2 class="section-title">账号设置</h2>
          <p class="placeholder-text" style="color: #64748b; padding: 20px 0;">账号设置功能正在开发中...</p>
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
import { ref, onMounted, watch } from 'vue'
import { getUserInfo } from '../../api/user'
import { 
  getPackages, 
  createRecharge, 
  type RechargePackage 
} from '../../api/membership'
import userStore from '../../stores/userStore'
import { useNotification } from '../../composables/useNotification'
import ConfirmModal from '../../components/ConfirmModal.vue'

const props = defineProps<{
  params?: Record<string, any>
}>()

const activeTab = ref('profile')
const showLogoutConfirm = ref(false)
const { showNotification } = useNotification()

const packages = ref<RechargePackage[]>([])
const selectedPackage = ref<RechargePackage | null>(null)
const payChannel = ref('ALIPAY')
const loading = ref(false)

const fetchUserInfo = async () => {
  try {
    const info = await getUserInfo()
    userStore.updateUserInfo(info.username, info.remainingCount, info.totalUsedCount)
  } catch (error) {
    console.error('Failed to fetch user info:', error)
  }
}

const fetchPackages = async () => {
  try {
    packages.value = await getPackages()
  } catch (error) {
    console.error('Failed to fetch packages:', error)
  }
}

// 处理初始 tab 和 tab 切换
const updateTabFromParams = () => {
  if (props.params?.tab) {
    activeTab.value = props.params.tab
  } else {
    activeTab.value = 'profile'
  }
}

watch(() => props.params?.tab, (newTab) => {
  if (newTab) {
    activeTab.value = newTab
  }
})

onMounted(() => {
  updateTabFromParams()
  fetchUserInfo()
  fetchPackages()
})

const handleRecharge = async () => {
  if (!selectedPackage.value) return

  loading.value = true
  try {
    const payData = await createRecharge({
      packageId: selectedPackage.value.id,
      payChannel: payChannel.value
    })
    
    showNotification('正在跳转支付...', 'success')
    
    // 支付宝电脑网站支付返回的是一个 HTML Form 表单
    if (payChannel.value === 'ALIPAY') {
      const div = document.createElement('div')
      div.innerHTML = payData // payData 是后端返回的 Form 表单字符串
      document.body.appendChild(div)
      document.forms[0].submit()
    } else {
      // 其他支付方式处理
      window.location.href = payData
    }
  } catch (error: any) {
    showNotification('支付跳转失败：' + (error.message || '未知错误'), 'error')
  } finally {
    loading.value = false
  }
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

/* 侧边栏样式 */
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

/* 充值页样式 */
.package-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 24px;
}

.package-card {
  background: #f8fafc;
  border: 2px solid transparent;
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}

.package-card:hover {
  transform: translateY(-4px);
  background: #f1f5f9;
}

.package-card.selected {
  border-color: #00c08b;
  background: #ecfdf5;
}

.package-name {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
}

.package-count {
  font-size: 24px;
  font-weight: 800;
  color: #00c08b;
  margin: 12px 0;
}

.package-price {
  font-size: 16px;
  color: #64748b;
}

.recharge-form {
  margin-top: 40px;
  padding-top: 40px;
  border-top: 1px solid #e2e8f0;
}

.sub-section-title {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 20px;
}

.order-info {
  background: #f8fafc;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  font-size: 15px;
  color: #64748b;
}

.order-item .price {
  color: #ef4444;
  font-weight: 700;
  font-size: 18px;
}

.pay-method, .screenshot-upload {
  margin-top: 24px;
}

.pay-method label, .screenshot-upload label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 12px;
}

.pay-options {
  display: flex;
  gap: 12px;
}

.pay-option {
  flex: 1;
  padding: 12px;
  text-align: center;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.pay-option.active {
  border-color: #00c08b;
  background: #ecfdf5;
  color: #00c08b;
  font-weight: 600;
}

.pay-option.disabled {
  background: #f1f5f9;
  color: #94a3b8;
  cursor: not-allowed;
  border-style: dashed;
}

.upload-placeholder {
  border: 2px dashed #e2e8f0;
  border-radius: 12px;
  padding: 30px;
  text-align: center;
  color: #94a3b8;
  cursor: pointer;
  transition: all 0.2s;
}

.upload-placeholder:hover {
  border-color: #00c08b;
  color: #00c08b;
}
.recharge-btn {
  width: 100%;
  margin-top: 32px;
  padding: 16px;
  font-size: 16px;
  font-weight: 700;
  color: white;
  background: linear-gradient(135deg, #00c08b, #00d9a5);
  border: none;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 8px 20px rgba(0, 192, 139, 0.2);
}

.recharge-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(0, 192, 139, 0.3);
  filter: brightness(1.05);
}

.recharge-btn:active:not(:disabled) {
  transform: translateY(0);
}

.recharge-btn:disabled {
  background: #cbd5e1;
  cursor: not-allowed;
  box-shadow: none;
}

/* 审核页样式 */
.audit-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 24px;
}

.audit-item {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.audit-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-row {
  display: flex;
  gap: 8px;
  font-size: 14px;
}

.info-row .label {
  color: #94a3b8;
  width: 60px;
}

.info-row .value {
  color: #1e293b;
  font-weight: 500;
}

.info-row .value.success { color: #00c08b; }
.info-row .value.rejected { color: #ef4444; }
.info-row .value.pending { color: #f59e0b; }

.audit-screenshot img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
}

.audit-actions {
  display: flex;
  gap: 12px;
}

.btn-audit {
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-audit.approve {
  background: #00c08b;
  color: white;
  border: none;
}

.btn-audit.approve:hover {
  background: #00a87a;
}

.btn-audit.reject {
  background: white;
  color: #ef4444;
  border: 1px solid #ef4444;
}

.btn-audit.reject:hover {
  background: #fef2f2;
}

/* 侧边栏样式 */
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

/* 主内容区样式 */
.content-card {
  background: white;
  border-radius: 24px;
  border: 1px solid #e2e8f0;
  padding: 32px;
  min-height: 400px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
}

.section-title {
  font-size: 22px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 24px;
}

.profile-details {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-item label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #94a3b8;
  margin-bottom: 6px;
}

.detail-item .value {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #f1f5f9;
}

.placeholder-text {
  color: #94a3b8;
  font-size: 14px;
  text-align: center;
  padding: 40px 0;
}

@media (max-width: 992px) {
  .personal-layout {
    grid-template-columns: 1fr;
  }
}
</style>

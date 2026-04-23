<script setup lang="ts">
import { ref, onMounted } from 'vue'
import DynamicForm from '../../components/DynamicForm.vue'
import type { TemplateDetail } from '../../types/template'
import { getTemplateDetail } from '../../api/template'

const props = defineProps<{
  templateCode: string
}>()

// 状态控制
const loading = ref(true)
const isGenerating = ref(false)
const templateData = ref<TemplateDetail | null>(null)

// 获取模板详情
const fetchDetail = async () => {
  if (!props.templateCode) return
  
  loading.value = true
  try {
    const data = await getTemplateDetail(props.templateCode)
    
    // 为音乐模板注入图标和特殊组件类型 (Mock Logic for V1 UI Matching)
    const fieldsWithIcons = data.formSchema.map((field: any) => {
      const fieldKey = field.fieldKey.toLowerCase()
      if (fieldKey.includes('name') || fieldKey.includes('song')) {
        field.icon = '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 18V5l12-2v13"></path><circle cx="6" cy="18" r="3"></circle><circle cx="18" cy="16" r="3"></circle></svg>'
      } else if (fieldKey.includes('style')) {
        field.icon = '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><line x1="4" y1="21" x2="4" y2="14"></line><line x1="4" y1="10" x2="4" y2="3"></line><line x1="12" y1="21" x2="12" y2="12"></line><line x1="12" y1="8" x2="12" y2="3"></line><line x1="20" y1="21" x2="20" y2="16"></line><line x1="20" y1="12" x2="20" y2="3"></line><line x1="1" y1="14" x2="7" y2="14"></line><line x1="9" y1="8" x2="15" y2="8"></line><line x1="17" y1="16" x2="23" y2="16"></line></svg>'
      } else if (fieldKey.includes('singer') || fieldKey.includes('voice') || fieldKey.includes('artist')) {
        field.icon = '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>'
      } else if (fieldKey.includes('emotion') || fieldKey.includes('mood') || fieldKey.includes('vibe')) {
        field.icon = '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><path d="M8 14s1.5 2 4 2 4-2 4-2"></path><line x1="9" y1="9" x2="9.01" y2="9"></line><line x1="15" y1="9" x2="15.01" y2="9"></line></svg>'
      } else if (fieldKey.includes('tempo') || fieldKey.includes('bpm')) {
        field.icon = '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><polyline points="13 17 18 12 13 7"></polyline><polyline points="6 17 11 12 6 7"></polyline></svg>'
        field.componentType = 'slider' // 强制转为滑块
      } else if (fieldKey.includes('language') || fieldKey.includes('lang')) {
        field.icon = '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="2" y1="12" x2="22" y2="12"></line><path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"></path></svg>'
      } else if (fieldKey.includes('extra') || fieldKey.includes('info')) {
        field.icon = '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>'
      }
      return field
    })

    data.formFields = fieldsWithIcons
    templateData.value = data
  } catch (error) {
    console.error('获取模板详情失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDetail()
})

const handleFormSubmit = async (formData: Record<string, any>) => {
  if (!templateData.value) return
  
  // V1：立即跳转到结果页，将输入参数带过去
  window.dispatchEvent(new CustomEvent('navigate', { 
    detail: { 
      path: '/result', 
      templateCode: templateData.value.templateCode,
      inputData: formData
    } 
  }))
}

const handleBack = () => {
  window.dispatchEvent(new CustomEvent('navigate', { detail: { path: '/' } }))
}

// 返回统一的品牌绿色背景
const getIconBgColor = (status?: number) => {
  if (status === 0) return '#f1f5f9'
  return '#00c08b15'
}
</script>

<template>
  <div class="template-detail-page">
    <div class="page-breadcrumb">
      <span class="breadcrumb-item" @click="handleBack">首页</span>
      <span class="breadcrumb-separator">›</span>
      <span class="breadcrumb-item active">提示词生成</span>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>正在加载模板配置...</p>
    </div>

    <div v-else-if="templateData" class="layout-split">
      <!-- 左侧：模板说明区 -->
      <aside class="info-sidebar">
        <div class="info-card">
          <div class="template-header">
            <div 
              class="template-icon-wrapper"
              :style="{ backgroundColor: getIconBgColor(templateData.status) }"
            >
              <!-- 支持 FontAwesome 图标或 Emoji -->
              <i 
                v-if="templateData.icon && templateData.icon.includes('fa-')" 
                :class="templateData.icon"
                class="template-icon-fa"
                :style="{ 
                  color: templateData.status === 0 ? '#94a3b8' : '#00c08b',
                  opacity: templateData.status === 0 ? 0.4 : 1
                }"
              ></i>
              <span v-else class="template-icon">{{ templateData.icon || '📝' }}</span>
            </div>
            <h1 class="template-name">{{ templateData.templateName }}</h1>
            <p class="template-desc">{{ templateData.description }}</p>
          </div>

          <div class="info-sections">
            <div class="info-section" v-if="templateData.sceneDescription">
              <h3 class="section-title">适用场景</h3>
              <p class="section-content">{{ templateData.sceneDescription }}</p>
            </div>

            <div class="info-section" v-if="templateData.outputDescription">
              <h3 class="section-title">输出说明</h3>
              <p class="section-content">{{ templateData.outputDescription }}</p>
            </div>
          </div>
        </div>
      </aside>

      <!-- 右侧：表单输入区 -->
      <main class="form-main">
        <div class="form-card">
          <DynamicForm 
            v-if="templateData.formFields"
            :fields="templateData.formFields"
            :is-generating="isGenerating"
            @submit="handleFormSubmit"
          />
          <div v-else class="no-fields">
            该模板无需额外参数，可以直接点击执行。
            <button class="btn-direct-run" :disabled="isGenerating" @click="handleFormSubmit({})">
              {{ isGenerating ? '正在执行...' : '立即执行' }}
            </button>
          </div>
        </div>
      </main>
    </div>

    <div v-else class="error-state">
      <p>未能加载模板信息，请稍后重试。</p>
      <button @click="fetchDetail">重试</button>
    </div>
  </div>
</template>

<style scoped>
.template-detail-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
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

.layout-split {
  display: flex;
  gap: 32px;
  align-items: flex-start;
}

.info-sidebar {
  flex: 0 0 360px;
  position: sticky;
  top: 32px;
}

.form-main {
  flex: 1;
  min-width: 0;
}

.info-card, .form-card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 24px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
}

.form-card {
  padding: 40px;
  font-size: 14px;
}

.no-fields {
  text-align: center;
  color: #64748b;
  padding: 20px 0;
}

.btn-direct-run {
  display: block;
  margin: 24px auto 0;
  padding: 14px 40px;
  background-color: #00c08b;
  color: #ffffff;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-direct-run:hover {
  background-color: #00a376;
  transform: translateY(-2px);
}

/* 模板说明区样式 */
.template-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 16px;
  margin-bottom: 32px;
  padding-bottom: 32px;
  border-bottom: 1px solid #f1f5f9;
}

.template-icon-wrapper {
  width: 72px;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 20px;
  font-size: 32px;
  overflow: hidden;
}

.template-icon-fa {
   font-size: 32px;
   transition: all 0.3s ease;
 }

.template-icon {
  color: #00c08b;
}

.template-name {
  margin: 0;
  font-size: 24px;
  font-weight: 800;
  color: #0f172a;
}

.template-desc {
  font-size: 15px;
  color: #64748b;
  line-height: 1.6;
  margin: 0;
}

.info-sections {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.info-section .section-title {
  margin: 0 0 8px;
  font-size: 15px;
  font-weight: 700;
  color: #334155;
}

.info-section .section-content {
  margin: 0;
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
}

/* 加载和错误状态 */
.loading-state, .error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px;
  background: #ffffff;
  border-radius: 24px;
  border: 1px solid #e2e8f0;
}

.spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #00c08b;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@media (max-width: 1024px) {
  .layout-split {
    flex-direction: column;
  }
  .info-sidebar {
    flex: auto;
    width: 100%;
    position: static;
  }
}
</style>

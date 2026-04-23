<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import TemplateCard from '../../components/TemplateCard.vue'
import EmptyState from '../../components/EmptyState.vue'
import type { Template } from '../../types/template'
import { getTemplates } from '../../api/template'

// 分类数据：从模板中动态提取并映射为中文
const templates = ref<Template[]>([])
const loading = ref(false)

const categoryMap: Record<string, string> = {
  'PROMPT_GENERATION': '生成提示词',
  'CONTENT_GENERATION': '生成文案'
}

const categories = computed(() => {
  const types = new Set(templates.value.map(t => t.templateType))
  return [
    { code: '全部', name: '全部' },
    ...Array.from(types).map(type => ({
      code: type,
      name: categoryMap[type] || type
    }))
  ]
})
const activeCategory = ref('全部')

// 获取后端模板列表
const fetchTemplates = async () => {
  loading.value = true
  try {
    const data = await getTemplates()
    templates.value = data
  } catch (error) {
    console.error('获取模板列表失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchTemplates()
})

// 分类过滤计算属性
const filteredTemplates = computed(() => {
  if (activeCategory.value === '全部') {
    return templates.value
  }
  return templates.value.filter(t => t.templateType === activeCategory.value)
})

// 滚动到模板列表区
const scrollToTemplates = () => {
  document.getElementById('template-section')?.scrollIntoView({ behavior: 'smooth' })
}

// 点击立即使用
const handleUseTemplate = (id: number) => {
  const template = templates.value.find(t => t.id === id)
  if (!template) return

  // 检查模板状态
  if (template.status === 0) {
    alert('该功能正在开发中，敬请期待！')
    return
  }

  // V1: 抛出事件给父组件（App.vue）来切换视图
  window.dispatchEvent(new CustomEvent('navigate', { 
    detail: { 
      path: '/template/detail', 
      id: template.id,
      templateCode: template.templateCode 
    } 
  }))
}
</script>

<template>
  <div class="home-page">
    <!-- Hero 区域 -->
    <section class="hero-section">
      <div class="hero-content">
        <div class="tagline">
          <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
            <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
          </svg>
          AI 驱动的多模态内容生成平台
        </div>
        <h1 class="hero-title">
          让 AI 理解<br/>
          <span class="highlight">你的奇思妙想</span>
        </h1>
        <p class="hero-subtitle">
          输入你的想法，配置风格参数，生成专业级 AI 提示词。<br/>
          支持音乐、文案、创意表达一键生成
        </p>
        <div class="hero-actions">
          <button class="btn-primary-lg" @click="scrollToTemplates">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M5 12h14M12 5l7 7-7 7"/>
            </svg>
            立即开始创作
          </button>
        </div>
      </div>
    </section>

    <!-- 模板列表区域 -->
    <section id="template-section" class="template-section">
      <div class="container">
        <div class="section-header">
          <div class="section-badge">
            <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="3" width="7" height="7"></rect>
              <rect x="14" y="3" width="7" height="7"></rect>
              <rect x="14" y="14" width="7" height="7"></rect>
              <rect x="3" y="14" width="7" height="7"></rect>
            </svg>
            提示词模板库
          </div>
          <h2 class="section-title">选择你的内容风格</h2>
          <p class="section-desc">点击任意风格卡片，进入参数配置页生成专属内容</p>
        </div>
        
        <!-- 分类切换 -->
        <div class="category-tabs-container">
          <div class="category-tabs">
            <button 
              v-for="cat in categories" 
              :key="cat.code"
              class="category-tab"
              :class="{ active: activeCategory === cat.code }"
              @click="activeCategory = cat.code"
            >
              {{ cat.name }}
            </button>
          </div>
        </div>

        <!-- 列表展示 -->
        <div v-if="filteredTemplates.length > 0" class="template-grid">
          <TemplateCard 
            v-for="template in filteredTemplates" 
            :key="template.id"
            :template="template"
            @use="handleUseTemplate"
          />
        </div>

        <!-- 空状态 -->
        <div v-else class="empty-container">
          <EmptyState title="暂无模板" description="当前分类下还没有可用的模板，请查看其他分类。" />
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
}

/* Hero 区域样式 */
.hero-section {
  position: relative;
  padding: 120px 20px;
  text-align: center;
  background-color: #ffffff;
  overflow: hidden;
  min-height: 560px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-content {
  position: relative;
  z-index: 2;
  max-width: 800px;
  margin: 0 auto;
}

.tagline {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 18px;
  background-color: rgba(0, 192, 139, 0.1);
  color: #00c08b;
  border-radius: 100px;
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 24px;
  border: 1px solid rgba(0, 192, 139, 0.15);
}

.hero-title {
  font-size: 64px;
  font-weight: 800;
  color: #0f172a;
  line-height: 1.15;
  margin: 0 0 28px;
  letter-spacing: -0.03em;
}

.hero-title .highlight {
  color: #00c08b;
  position: relative;
  display: inline-block;
}

.hero-title .highlight::after {
  content: '';
  position: absolute;
  bottom: 8px;
  left: 0;
  width: 100%;
  height: 12px;
  background-color: rgba(0, 192, 139, 0.15);
  z-index: -1;
  border-radius: 4px;
}

.hero-subtitle {
  font-size: 20px;
  color: #475569;
  line-height: 1.7;
  margin: 0 0 44px;
  max-width: 640px;
  margin-left: auto;
  margin-right: auto;
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.btn-primary-lg {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 18px 44px;
  font-size: 18px;
  font-weight: 700;
  background-color: #00c08b;
  color: #ffffff;
  border: none;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 10px 25px -5px rgba(0, 192, 139, 0.4);
}

.btn-primary-lg:hover {
  background-color: #00a376;
  transform: translateY(-3px);
  box-shadow: 0 20px 30px -10px rgba(0, 192, 139, 0.4);
}

.btn-primary-lg:active {
  transform: translateY(-1px);
}

/* 模板列表区域样式 */
.template-section {
  display: flex;
  flex-direction: column;
  padding: 80px 0 120px;
  background-color: #ffffff;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 60px;
}

.section-header {
  text-align: center;
  margin-bottom: 48px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.section-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background-color: #00c08b10;
  color: #00c08b;
  border-radius: 100px;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 16px;
}

.section-title {
  font-size: 42px;
  font-weight: 850;
  color: #0f172a;
  margin: 0 0 16px;
  letter-spacing: -0.02em;
}

.section-desc {
  font-size: 17px;
  color: #64748b;
  margin: 0;
}

.category-tabs-container {
  display: flex;
  justify-content: center;
  margin-bottom: 48px;
}

.category-tabs {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.category-tab {
  padding: 8px 24px;
  font-size: 14px;
  font-weight: 600;
  color: #64748b;
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 100px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.category-tab:hover {
  color: #0f172a;
  border-color: #cbd5e1;
}

.category-tab.active {
  background-color: #00c08b;
  color: #ffffff;
  border-color: #00c08b;
}

/* 模板列表网格 */
.template-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.empty-container {
  padding: 64px 0;
  text-align: center;
}

@media (max-width: 1280px) {
  .template-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 1024px) {
  .template-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 40px;
  }
  .section-title {
    font-size: 32px;
  }
  .container {
    padding: 0 24px;
  }
  .template-grid {
    grid-template-columns: 1fr;
  }
}
</style>

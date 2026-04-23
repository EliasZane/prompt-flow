<script setup lang="ts">
import type { Template } from '../types/template'

defineProps<{
  template: Template
}>()

const emit = defineEmits<{
  (e: 'use', id: number): void
}>()

// 根据名称生成一些模拟标签，增强视觉效果
const getMockTags = (template: any) => {
  const tags: string[] = []
  const name = template.templateName
  if (name.includes('流行')) tags.push('流行', '情感', '治愈')
  else if (name.includes('摇滚')) tags.push('摇滚', '激情', '力量')
  else if (name.includes('电子')) tags.push('电子', '科技', '未来')
  else if (name.includes('爵士')) tags.push('爵士', '慵懒', '高级')
  else if (name.includes('民谣')) tags.push('民谣', '故事', '质朴')
  else if (name.includes('R&B')) tags.push('R&B', '灵魂', '丝滑')
  else if (name.includes('Lo-Fi')) tags.push('Lo-Fi', '放松', '专注')
  else tags.push('AI创作', '灵感', '内容生成')
  return tags
}

// 返回统一的品牌绿色背景
const getIconBgColor = (status: number) => {
  if (status === 0) return '#f1f5f9' // 即将上线：浅灰背景
  return '#00c08b15' // 可用状态：品牌绿背景（15% 透明度）
}
</script>

<template>
  <div 
    class="template-card" 
    :class="{ 'is-disabled': template.status === 0 }"
    @click="emit('use', template.id)"
  >
    <!-- 即将上线角标 -->
    <div v-if="template.status === 0" class="coming-soon-badge">
      即将上线
    </div>

    <div class="card-top">
      <div 
        class="card-icon-wrapper" 
        :style="{ backgroundColor: getIconBgColor(template.status) }"
      >
        <!-- 支持 FontAwesome 图标或 Emoji -->
        <i 
          v-if="template.icon && template.icon.includes('fa-')" 
          :class="template.icon"
          class="card-icon-fa"
          :style="{ 
            color: template.status === 0 ? '#94a3b8' : '#00c08b',
            opacity: template.status === 0 ? 0.4 : 1
          }"
        ></i>
        <span 
          v-else 
          class="card-icon"
          :style="{ opacity: template.status === 0 ? 0.4 : 1 }"
        >{{ template.icon || '📝' }}</span>
      </div>
      <div class="card-arrow" v-if="template.status !== 0">
        <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <line x1="7" y1="17" x2="17" y2="7"></line>
          <polyline points="7 7 17 7 17 17"></polyline>
        </svg>
      </div>
      <div class="card-lock" v-else>
        <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
          <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
        </svg>
      </div>
    </div>
    
    <div class="card-content">
      <h3 class="card-title">{{ template.templateName }}</h3>
      <p class="card-desc">{{ template.description }}</p>
    </div>
    
    <div class="card-tags">
      <span class="tag-pill" v-for="tag in getMockTags(template)" :key="tag">
        {{ tag }}
      </span>
    </div>
  </div>
</template>

<style scoped>
.template-card {
  display: flex;
  flex-direction: column;
  background: #ffffff;
  border: 1px solid #f1f5f9;
  border-radius: 20px;
  padding: 28px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  height: 100%;
  box-sizing: border-box;
  overflow: hidden;
}

.template-card.is-disabled {
  cursor: not-allowed;
  background-color: #fcfcfc;
  filter: grayscale(1);
  opacity: 0.6;
}

.coming-soon-badge {
  position: absolute;
  top: 0;
  right: 0;
  background-color: #94a3b8;
  color: white;
  padding: 4px 12px;
  font-size: 11px;
  font-weight: 700;
  border-bottom-left-radius: 12px;
  z-index: 10;
}

.template-card:hover:not(.is-disabled) {
  transform: translateY(-6px);
  box-shadow: 0 16px 32px -8px rgba(0, 0, 0, 0.06);
  border-color: #e2e8f0;
}

.template-card:hover .card-arrow {
  background-color: #f8fafc;
  color: #00c08b;
  transform: translate(1px, -1px);
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.card-icon-wrapper {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  font-size: 22px;
  overflow: hidden;
}

.card-icon-fa {
  font-size: 20px;
  transition: all 0.3s ease;
}

.card-arrow, .card-lock {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #cbd5e1;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.card-lock {
  color: #94a3b8;
}

.card-content {
  margin-bottom: 20px;
  flex-grow: 1;
}

.card-title {
  font-size: 19px;
  font-weight: 800;
  color: #0f172a;
  margin: 0 0 10px;
  letter-spacing: -0.01em;
}

.card-desc {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tag-pill {
  padding: 3px 10px;
  background-color: #f8fafc;
  color: #94a3b8;
  border-radius: 100px;
  font-size: 11px;
  font-weight: 600;
  transition: all 0.2s ease;
}

.template-card:hover .tag-pill {
  background-color: #f1f5f9;
  color: #64748b;
}
</style>

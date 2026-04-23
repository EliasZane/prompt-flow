<script setup lang="ts">
import { ref, computed } from 'vue'

interface PromptObject {
  Mood?: string
  Tempo?: string
  Groove?: string
  Style?: string
  Vocal?: string
  'Original Style Analysis'?: string
  Key?: string
  Arrangement?: string
  [key: string]: string | undefined
}

interface ResultData {
  title?: string
  coverText?: string
  tags?: string[]
  suggestion?: string
  prompt?: string | PromptObject
}

const props = defineProps<{
  content?: ResultData | any
}>()

const emit = defineEmits<{
  (e: 'retry'): void
}>()

const isExpanded = ref(false)
const copiedField = ref<string | null>(null)

// 数据解析
const result = computed<ResultData>(() => {
  if (!props.content) return {}
  return props.content as ResultData
})

const promptText = computed(() => {
  const p = result.value.prompt
  if (!p) return ''
  if (typeof p === 'string') return p
  
  // 如果是对象，转为结构化文本
  return Object.entries(p)
    .map(([key, value]) => `[${key}]\n${value}`)
    .join('\n\n')
})

const styleDetails = computed(() => {
  const p = result.value.prompt
  if (!p || typeof p === 'string') return []
  
  const labels: Record<string, string> = {
    'Style': '曲风',
    'Tempo': '节奏',
    'Vocal': '人声',
    'Mood': '情绪',
    'Key': '调性',
    'Arrangement': '编曲'
  }
  
  return Object.entries(labels)
    .filter(([key]) => p[key as keyof PromptObject])
    .map(([key, label]) => ({
      label,
      value: p[key as keyof PromptObject]
    }))
})

// 复制功能
const copyToClipboard = (text: string, fieldId: string) => {
  if (!text) return
  navigator.clipboard.writeText(text).then(() => {
    copiedField.value = fieldId
    setTimeout(() => {
      copiedField.value = null
    }, 2000)
  })
}

const copyAllTags = () => {
  if (!result.value.tags) return
  copyToClipboard(result.value.tags.join(' '), 'tags')
}

// 下载功能
const downloadPrompt = () => {
  const blob = new Blob([promptText.value], { type: 'text/plain' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `AI-Prompt-${result.value.title || 'export'}.txt`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
}
</script>

<template>
  <div class="result-panel-v2">
    <!-- 1. 顶部状态区 -->
    <header class="status-section">
      <div class="status-info">
        <h1 class="status-title">
          <span class="status-icon">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="20 6 9 17 4 12"></polyline>
            </svg>
          </span>
          生成成功
        </h1>
        <p class="status-subtitle">已为你生成完整 AI 翻唱方案，可直接用于音乐生成与短视频发布</p>
      </div>
      <button class="btn-secondary" @click="emit('retry')">
        <svg viewBox="0 0 24 24" width="16" height="16" stroke="currentColor" stroke-width="2" fill="none"><path d="M23 4v6h-6M1 20v-6h6M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"></path></svg>
        再生成一版
      </button>
    </header>

    <div class="panel-layout">
      <!-- 2. 核心结果卡 -->
      <section class="result-card main-card">
        <h2 class="card-title">🎬 内容方案</h2>
        
        <div class="content-item">
          <div class="item-header">
            <span class="item-label">推荐视频标题</span>
            <button class="btn-copy-small" @click="copyToClipboard(result.title || '', 'title')">
              {{ copiedField === 'title' ? '已复制' : '复制标题' }}
            </button>
          </div>
          <div class="item-content highlight-text">
            {{ result.title }}
          </div>
        </div>

        <div class="content-item">
          <div class="item-header">
            <span class="item-label">封面文案（适合视频封面）</span>
            <button class="btn-copy-small" @click="copyToClipboard(result.coverText || '', 'cover')">
              {{ copiedField === 'cover' ? '已复制' : '复制文案' }}
            </button>
          </div>
          <div class="item-content secondary-text">
            {{ result.coverText }}
          </div>
        </div>

        <div class="content-item">
          <div class="item-header">
            <span class="item-label">热门标签（可直接用于发布）</span>
            <button class="btn-copy-small" @click="copyAllTags">
              {{ copiedField === 'tags' ? '已复制' : '一键复制全部' }}
            </button>
          </div>
          <div class="tag-list">
            <span v-for="tag in result.tags" :key="tag" class="tag-item">{{ tag }}</span>
          </div>
        </div>
      </section>

      <!-- 3. 风格解析卡 -->
      <section class="result-card style-card">
        <h2 class="card-title">🎧 音乐风格解析</h2>
        <ul class="style-list">
          <li v-for="item in styleDetails" :key="item.label" class="style-item">
            <span class="style-label">{{ item.label }}：</span>
            <span class="style-value">{{ item.value }}</span>
          </li>
        </ul>
      </section>

      <!-- 4. AI Prompt 卡 -->
      <section class="result-card prompt-card">
        <div class="card-header-with-desc">
          <h2 class="card-title">🧠 AI音乐生成 Prompt</h2>
          <span class="card-subtitle">已适配 Suno / Udio / Mureka，可直接复制使用</span>
        </div>

        <div class="prompt-content-wrapper" :class="{ expanded: isExpanded }">
          <pre class="prompt-text">{{ promptText }}</pre>
          <div v-if="!isExpanded" class="content-overlay"></div>
        </div>

        <div class="prompt-actions">
          <button class="btn-primary" @click="copyToClipboard(promptText, 'prompt')">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path></svg>
            {{ copiedField === 'prompt' ? '已复制' : '复制完整 Prompt' }}
          </button>
          <button class="btn-secondary" @click="downloadPrompt">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M7 10l5 5 5-5M12 15V3"/></svg>
            下载 .txt
          </button>
          <button class="btn-ghost" @click="isExpanded = !isExpanded">
            {{ isExpanded ? '收起' : '展开全部' }}
            <svg :class="{ rotate: isExpanded }" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"></polyline></svg>
          </button>
        </div>
      </section>

      <!-- 5. 使用建议卡 -->
      <section class="result-card suggestion-card">
        <h2 class="card-title">📌 使用建议（提高出片效果）</h2>
        <ul class="suggestion-list">
          <li v-if="result.suggestion" class="suggestion-item highlight-suggestion">{{ result.suggestion }}</li>
          <li class="suggestion-item">推荐平台：Suno（适合快速生成完整歌曲）</li>
          <li class="suggestion-item">人声建议：更贴合当前风格的人声处理</li>
          <li class="suggestion-item">视频画面：推荐夜景、街景或低饱和色调</li>
          <li class="suggestion-item">剪辑建议：建议增加字幕并配合情绪镜头切换</li>
          <li class="suggestion-item">发布时间：建议晚上 10 点后发布，更容易获得播放量</li>
        </ul>
      </section>

      <!-- 6. 下一步操作区 -->
      <section class="next-steps-section">
        <h2 class="section-title">🚀 下一步怎么做</h2>
        <div class="steps-grid">
          <div class="step-item">
            <div class="step-num">1</div>
            <div class="step-text">复制上方 AI Prompt</div>
          </div>
          <div class="step-item">
            <div class="step-num">2</div>
            <div class="step-text">打开 Suno / Udio</div>
          </div>
          <div class="step-item">
            <div class="step-num">3</div>
            <div class="step-text">粘贴并生成音乐</div>
          </div>
          <div class="step-item">
            <div class="step-num">4</div>
            <div class="step-text">下载音频</div>
          </div>
          <div class="step-item">
            <div class="step-num">5</div>
            <div class="step-text">使用剪映生成视频</div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.result-panel-v2 {
  display: flex;
  flex-direction: column;
  gap: 32px;
  animation: fadeIn 0.6s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 顶部状态区 */
.status-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  padding-bottom: 8px;
  border-bottom: 1px solid #f1f5f9;
}

.status-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 26px;
  font-weight: 800;
  color: #0f172a;
  margin: 0 0 8px 0;
  letter-spacing: -0.02em;
}

.status-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background-color: #00c08b;
  color: white;
  border-radius: 50%;
  flex-shrink: 0;
  animation: scaleIn 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes scaleIn {
  from { transform: scale(0); }
  to { transform: scale(1); }
}

.status-subtitle {
  font-size: 15px;
  color: #64748b;
  margin: 0;
}

/* 卡片通用样式 */
.result-card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 24px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.card-title {
  font-size: 18px;
  font-weight: 800;
  color: #0f172a;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.panel-layout {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 核心结果卡 */
.content-item {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-label {
  font-size: 14px;
  font-weight: 600;
  color: #94a3b8;
}

.highlight-text {
  font-size: 24px;
  font-weight: 800;
  color: #00c08b;
  line-height: 1.4;
}

.secondary-text {
  font-size: 18px;
  font-weight: 600;
  color: #334155;
  line-height: 1.5;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  padding: 6px 14px;
  background-color: #f1f5f9;
  color: #64748b;
  border-radius: 100px;
  font-size: 13px;
  font-weight: 600;
}

/* 风格解析卡 */
.style-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.style-item {
  font-size: 15px;
  line-height: 1.6;
}

.style-label {
  color: #94a3b8;
  font-weight: 600;
}

.style-value {
  color: #334155;
  font-weight: 500;
}

/* AI Prompt 卡 */
.card-header-with-desc {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.card-subtitle {
  font-size: 13px;
  color: #94a3b8;
}

.prompt-content-wrapper {
  position: relative;
  background-color: #f8fafc;
  border: 1px solid #f1f5f9;
  border-radius: 16px;
  max-height: 200px;
  overflow: hidden;
  transition: max-height 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.prompt-content-wrapper.expanded {
  max-height: 2000px;
}

.prompt-text {
  padding: 24px;
  margin: 0;
  font-family: 'Fira Code', 'Courier New', Courier, monospace;
  font-size: 14px;
  line-height: 1.7;
  color: #475569;
  white-space: pre-wrap;
  word-break: break-all;
}

.content-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 80px;
  background: linear-gradient(transparent, #f8fafc);
  pointer-events: none;
}

.prompt-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

/* 使用建议卡 */
.suggestion-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.suggestion-item {
  position: relative;
  padding-left: 24px;
  font-size: 15px;
  color: #475569;
  line-height: 1.6;
}

.suggestion-item.highlight-suggestion {
  color: #00c08b;
  font-weight: 700;
}

.suggestion-item::before {
  content: "•";
  position: absolute;
  left: 8px;
  color: #00c08b;
  font-weight: bold;
}

/* 下一步操作区 */
.next-steps-section {
  margin-top: 16px;
  padding: 0 8px;
}

.section-title {
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 24px;
}

.steps-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 20px;
}

.step-item {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px;
  background-color: #f8fafc;
  border-radius: 20px;
  transition: all 0.2s;
}

.step-item:hover {
  background-color: #ffffff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.04);
  transform: translateY(-2px);
}

.step-num {
  width: 28px;
  height: 28px;
  background-color: #00c08b;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 800;
}

.step-text {
  font-size: 14px;
  font-weight: 600;
  color: #334155;
  line-height: 1.4;
}

/* 按钮样式 */
.btn-primary {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 28px;
  background-color: #00c08b;
  color: #ffffff;
  border: none;
  border-radius: 14px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(0, 192, 139, 0.2);
}

.btn-primary:hover {
  background-color: #00a376;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 192, 139, 0.3);
}

.btn-secondary {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  font-size: 15px;
  font-weight: 600;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary:hover {
  color: #0f172a;
  border-color: #cbd5e1;
  background-color: #f8fafc;
}

.btn-ghost {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 12px;
  background: transparent;
  border: none;
  color: #94a3b8;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

.btn-ghost:hover {
  color: #64748b;
}

.btn-copy-small {
  padding: 6px 12px;
  background-color: rgba(0, 192, 139, 0.08);
  color: #00c08b;
  border: none;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-copy-small:hover {
  background-color: rgba(0, 192, 139, 0.15);
}

.rotate {
  transform: rotate(180deg);
}

@media (max-width: 640px) {
  .status-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  .prompt-actions {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>

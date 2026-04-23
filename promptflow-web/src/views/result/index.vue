<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import StepList from '../../components/StepList.vue'
import ResultPanel from '../../components/ResultPanel.vue'
import type { WorkflowResult, WorkflowStep } from '../../types/workflow'
import { getWorkflowRunDetail, runWorkflow } from '../../api/workflow'

const props = defineProps<{
  params?: {
    taskId?: string | number
    templateCode?: string
    inputData?: Record<string, any>
  }
}>()

const loading = ref(true)
const resultData = ref<WorkflowResult | null>(null)
const animationFinished = ref(false)
const errorType = ref<'NONE' | 'TIMEOUT' | 'FAILURE'>('NONE')
const pollCount = ref(0)
const MAX_POLLS = 20

// 步骤定义
const steps = ref<WorkflowStep[]>([
  { id: '1', stepName: '输入解析', status: 'PENDING', summary: '正在解析用户填写的原曲、风格、情绪等参数' },
  { id: '2', stepName: '风格分析', status: 'PENDING', summary: '匹配核心元素与乐器编排' },
  { id: '3', stepName: 'Prompt生成', status: 'PENDING', summary: '组装成 AI 音乐大模型可识别的结构化指令' },
  { id: '4', stepName: '结果优化', status: 'PENDING', summary: '润色中英文对照词汇，确保模型理解准确' },
  { id: '5', stepName: '最终输出', status: 'PENDING', summary: '正在生成最终内容' }
])

let animationTimer: number | null = null
let pollTimer: number | null = null

// 步骤动画控制：1s 切换一个
const startStepAnimation = () => {
  let currentStep = 0
  
  // 重置步骤
  steps.value.forEach(s => s.status = 'PENDING')
  
  animationTimer = window.setInterval(() => {
    // 前 4 步每秒切换一次
    if (currentStep < 4) {
      if (currentStep > 0) {
        steps.value[currentStep - 1].status = 'SUCCESS'
      }
      steps.value[currentStep].status = 'RUNNING'
      currentStep++
    } else if (currentStep === 4) {
      // 第 5 步开始运行，并停止定时器
      steps.value[3].status = 'SUCCESS'
      steps.value[4].status = 'RUNNING'
      if (animationTimer) {
        clearInterval(animationTimer)
        animationTimer = null
      }
    }
  }, 1000)
}

// 轮询结果
const startPolling = async (runId: string | number) => {
  pollCount.value = 0
  
  pollTimer = window.setInterval(async () => {
    pollCount.value++
    
    try {
      const data = await getWorkflowRunDetail(runId)
      
      if (data.runStatus === 'SUCCESS') {
        if (pollTimer) clearInterval(pollTimer)
        resultData.value = data
        steps.value[4].status = 'SUCCESS'
        steps.value[4].summary = '生成完毕'
        setTimeout(() => {
          loading.value = false
        }, 500)
      } else if (data.runStatus === 'FAILED') {
        if (pollTimer) clearInterval(pollTimer)
        errorType.value = 'FAILURE'
        loading.value = false
      }
      
      // 达到最大轮询次数
      if (pollCount.value >= MAX_POLLS) {
        if (pollTimer) clearInterval(pollTimer)
        if (!resultData.value) {
          errorType.value = 'TIMEOUT'
          loading.value = false
        }
      }
    } catch (error) {
      console.error('轮询失败:', error)
      // 轮询中的单次失败不直接退出，除非连续失败多次，这里简化处理
    }
  }, 2000)
}

// 获取现有结果
const fetchExistingResult = async (id: string | number) => {
  try {
    const data = await getWorkflowRunDetail(id)
    resultData.value = data
    loading.value = false
  } catch (error) {
    console.error('获取结果失败:', error)
    errorType.value = 'FAILURE'
    loading.value = false
  }
}

// 执行新的工作流
const executeNewWorkflow = async (code: string, input: Record<string, any>) => {
  try {
    errorType.value = 'NONE'
    startStepAnimation()
    
    // 2. 调用真实接口获取 taskId (runId)
    const result = await runWorkflow({
      templateCode: code,
      inputData: input
    })
    
    if (result && result.runId) {
      // 拿到 runId 后开始轮询
      startPolling(result.runId)
    } else {
      throw new Error('No runId returned')
    }
  } catch (error) {
    console.error('执行失败:', error)
    if (animationTimer) clearInterval(animationTimer)
    if (pollTimer) clearInterval(pollTimer)
    errorType.value = 'FAILURE'
    loading.value = false
  }
}

onMounted(() => {
  const p = props.params
  if (p?.templateCode && p?.inputData) {
    executeNewWorkflow(p.templateCode, p.inputData)
  } else if (p?.taskId) {
    fetchExistingResult(p.taskId)
  } else {
    loading.value = false
  }
})

onUnmounted(() => {
  if (animationTimer) clearInterval(animationTimer)
  if (pollTimer) clearInterval(pollTimer)
})

const handleRetry = () => {
  const templateCode = props.params?.templateCode || resultData.value?.templateCode
  const inputData = props.params?.inputData

  if (templateCode && inputData) {
    loading.value = true
    resultData.value = null
    animationFinished.value = false
    errorType.value = 'NONE'
    executeNewWorkflow(templateCode, inputData)
  } else {
    handleBackToTemplate()
  }
}

const handleBack = () => {
  window.dispatchEvent(new CustomEvent('navigate', { detail: { path: '/' } }))
}

const handleBackToTemplate = () => {
  const templateCode = resultData.value?.templateCode || props.params?.templateCode
  if (templateCode) {
    window.dispatchEvent(new CustomEvent('navigate', { 
      detail: { 
        path: '/template/detail', 
        templateCode: templateCode 
      } 
    }))
  } else {
    handleBack()
  }
}
</script>

<template>
  <div class="result-page">
    <div class="page-breadcrumb">
      <span class="breadcrumb-item" @click="handleBack">首页</span>
      <span class="breadcrumb-separator">›</span>
      <span class="breadcrumb-item" @click="handleBackToTemplate">提示词生成</span>
      <span class="breadcrumb-separator">›</span>
      <span class="breadcrumb-item active">生成结果</span>
    </div>

    <!-- 执行流程 UI (渐进展示) -->
    <div v-if="loading" class="execution-layout">
      <div class="execution-card">
        <div class="execution-header">
          <div class="loading-icon">
            <svg class="spin" viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2.5" fill="none">
              <path d="M12 2v4m0 12v4M4.93 4.93l2.83 2.83m8.48 8.48l2.83 2.83M2 12h4m12 0h4M4.93 19.07l2.83-2.83m8.48-8.48l2.83-2.83"></path>
            </svg>
          </div>
          <h2 class="loading-title">正在生成内容...</h2>
        </div>
        
        <div class="steps-container">
          <StepList :steps="steps" />
        </div>

        <div class="execution-footer">
          <p class="status-msg">正在生成内容，预计需要 10–20 秒，请稍候</p>
        </div>
      </div>
    </div>

    <template v-else-if="resultData">
      <div class="result-display-layout">
        <ResultPanel 
          :content="resultData.result"
          @retry="handleRetry"
        />
      </div>
    </template>

    <div v-else class="error-container">
      <div class="error-card">
        <template v-if="errorType === 'TIMEOUT'">
          <svg viewBox="0 0 24 24" width="48" height="48" fill="none" stroke="#f59e0b" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
          <p class="error-message">生成时间较长，请稍后刷新重试</p>
        </template>
        <template v-else>
          <svg viewBox="0 0 24 24" width="48" height="48" fill="none" stroke="#ef4444" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
          <p class="error-message">结果获取失败，请返回重试</p>
        </template>
        <div class="error-actions">
          <button class="btn-retry" @click="handleRetry">重新生成</button>
          <button class="btn-back-home" @click="handleBack">返回首页</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.result-page {
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

/* 执行流程 UI 样式 */
.execution-layout {
  display: flex;
  justify-content: center;
  padding: 40px 0;
}

.execution-card {
  width: 100%;
  max-width: 900px;
  background: #ffffff;
  border-radius: 32px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.02);
  padding: 40px;
}

.execution-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.loading-icon {
  color: #00c08b;
  display: flex;
  align-items: center;
}

.loading-title {
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
  margin: 0;
  letter-spacing: -0.01em;
}

.steps-container {
  background-color: #f8fafc;
  border-radius: 24px;
  border: 1px solid #f1f5f9;
  margin-bottom: 24px;
}

.execution-footer {
  text-align: center;
}

.status-msg {
  color: #94a3b8;
  font-size: 14px;
  font-weight: 500;
}

/* 结果显示布局 */
.result-display-layout {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
}

/* 错误状态 */
.error-container {
  display: flex;
  justify-content: center;
  padding: 80px 0;
}

.error-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  background: white;
  padding: 48px;
  border-radius: 24px;
  border: 1px solid #e2e8f0;
  text-align: center;
}

.error-message {
  font-size: 16px;
  color: #475569;
  font-weight: 500;
  margin: 0;
}

.error-actions {
  display: flex;
  gap: 12px;
}

.btn-retry {
  padding: 12px 32px;
  background-color: #ffffff;
  color: #00c08b;
  border: 1px solid #00c08b;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-retry:hover {
  background-color: #f0fdf9;
}

.btn-back-home {
  padding: 12px 32px;
  background-color: #00c08b;
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-back-home:hover {
  background-color: #00a376;
}

.spin {
  animation: spin 2s linear infinite;
}

@keyframes spin {
  100% { transform: rotate(360deg); }
}
</style>

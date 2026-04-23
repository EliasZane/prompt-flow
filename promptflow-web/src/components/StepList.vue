<script setup lang="ts">
import type { WorkflowStep } from '../types/workflow'

defineProps<{
  steps: WorkflowStep[]
}>()
</script>

<template>
  <div class="step-list-horizontal">
    <div 
      v-for="(step, index) in steps" 
      :key="step.id"
      class="step-item"
      :class="[`status-${step.status.toLowerCase()}`]"
    >
      <!-- 连接线 -->
      <div class="step-connector" v-if="index !== steps.length - 1"></div>
      
      <div class="step-node">
        <div class="step-indicator">
          <!-- Pending: 默认圆圈 + 灰图标 -->
          <template v-if="step.status.toLowerCase() === 'pending'">
            <svg v-if="index === 2" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 18l2-2 4 4M10 6L8 8l-4-4M16 6l2 2 4-4M10 18L8 16l-4 4"/></svg>
            <svg v-else-if="index === 3" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
            <svg v-else-if="index === 4" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M7 10l5 5 5-5M12 15V3"/></svg>
            <div v-else class="dot"></div>
          </template>

          <!-- Running: 亮绿色背景 + 动态图标 -->
          <template v-else-if="step.status.toLowerCase() === 'running'">
            <svg v-if="index === 2" class="pulse" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><path d="M8 9l3 3-3 3m5 0h3"/></svg>
            <svg v-else class="spin" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2v4m0 12v4M4.93 4.93l2.83 2.83m8.48 8.48l2.83 2.83M2 12h4m12 0h4M4.93 19.07l2.83-2.83m8.48-8.48l2.83-2.83"/></svg>
          </template>

          <!-- Success: 满绿色背景 + 打钩 -->
          <template v-else-if="step.status.toLowerCase() === 'success'">
            <svg viewBox="0 0 24 24" width="14" height="14" stroke="currentColor" stroke-width="4" fill="none" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="20 6 9 17 4 12"></polyline>
            </svg>
          </template>

          <!-- Failed -->
          <template v-else-if="step.status.toLowerCase() === 'failed' || step.status.toLowerCase() === 'error'">
            <svg viewBox="0 0 24 24" width="14" height="14" stroke="currentColor" stroke-width="2" fill="none"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </template>
        </div>
        
        <div class="step-label">{{ step.stepName }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.step-list-horizontal {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 40px 20px;
}

.step-item {
  position: relative;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 0;
}

.step-connector {
  position: absolute;
  top: 21px;
  left: calc(50% + 21px);
  width: calc(100% - 42px);
  height: 2px;
  background-color: #f1f5f9;
  z-index: 0;
}

.status-success .step-connector {
  background-color: #00c08b;
}

.step-node {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.step-indicator {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  color: #94a3b8;
  transition: all 0.3s ease;
}

.status-success .step-indicator {
  background-color: #00c08b;
  border-color: #00c08b;
  color: #ffffff;
}

.status-running .step-indicator {
  background-color: #e6f9f4;
  border-color: #00c08b;
  color: #00c08b;
}

.status-failed .step-indicator {
  background-color: #fef2f2;
  border-color: #ef4444;
  color: #ef4444;
}

.step-label {
  font-size: 13px;
  font-weight: 600;
  color: #94a3b8;
  white-space: nowrap;
}

.status-success .step-label {
  color: #00c08b;
}

.status-running .step-label {
  color: #0f172a;
}

.dot {
  width: 6px;
  height: 6px;
  background-color: #cbd5e1;
  border-radius: 50%;
}

.spin {
  animation: spin 2s linear infinite;
}

.pulse {
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes spin {
  100% { transform: rotate(360deg); }
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(0.9); }
}
</style>

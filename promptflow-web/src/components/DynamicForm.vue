<script setup lang="ts">
import { ref, watch } from 'vue'
import type { FormField } from '../types/template'

const props = defineProps<{
  fields: FormField[]
  isGenerating?: boolean
}>()

const emit = defineEmits<{
  (e: 'submit', formData: Record<string, any>): void
}>()

// 表单数据
const formData = ref<Record<string, any>>({})
const errors = ref<Record<string, string>>({})

// 初始化表单默认值
const initForm = () => {
  const data: Record<string, any> = {}
  props.fields.forEach(field => {
    if (field.componentType === 'tags' || field.componentType === 'tag') {
      data[field.fieldKey] = field.defaultValue || []
    } else {
      data[field.fieldKey] = field.defaultValue || ''
    }
  })
  formData.value = data
  errors.value = {}
}

watch(() => props.fields, initForm, { immediate: true })

const validate = () => {
  let isValid = true
  const newErrors: Record<string, string> = {}
  let firstErrorField: string | null = null

  props.fields.forEach(field => {
    if (field.required) {
      const val = formData.value[field.fieldKey]
      if (
        val === undefined || 
        val === null || 
        val === '' || 
        (Array.isArray(val) && val.length === 0)
      ) {
        newErrors[field.fieldKey] = `${field.label} 是必填项`
        isValid = false
        if (!firstErrorField) firstErrorField = field.fieldKey
      }
    }
  })

  errors.value = newErrors

  // 如果校验失败，滚动到第一个错误项
  if (!isValid && firstErrorField) {
    const errorEl = document.querySelector(`[data-field="${firstErrorField}"]`)
    if (errorEl) {
      errorEl.scrollIntoView({ behavior: 'smooth', block: 'center' })
    }
  }

  return isValid
}

const handleSubmit = () => {
  if (props.isGenerating) return
  if (validate()) {
    emit('submit', { ...formData.value })
  }
}

const toggleTag = (fieldKey: string, value: string) => {
  const current = formData.value[fieldKey] || []
  if (current.includes(value)) {
    formData.value[fieldKey] = current.filter((item: string) => item !== value)
  } else {
    formData.value[fieldKey] = [...current, value]
  }
  if (errors.value[fieldKey]) {
    errors.value[fieldKey] = ''
  }
}
</script>

<template>
  <form class="dynamic-form" @submit.prevent="handleSubmit">
    <div 
      v-for="field in fields" 
      :key="field.fieldKey" 
      class="form-item"
      :class="{ 'has-error': !!errors[field.fieldKey] }"
      :data-field="field.fieldKey"
    >
      <label class="form-label">
        <span v-if="field.icon" class="field-icon" v-html="field.icon"></span>
        <span v-if="field.required" class="required-star">*</span>
        {{ field.label }}
        <span v-if="field.fieldKey === 'tempo'" class="tempo-value">
          <span class="value">{{ formData[field.fieldKey] || 120 }}</span>
          <span class="unit">BPM 参考</span>
        </span>
        <span v-if="field.fieldKey === 'emotion'" class="optional-tag">可选</span>
        <span v-if="field.fieldKey === 'extraInfo'" class="optional-tag">选填</span>
      </label>

      <!-- Input -->
      <template v-if="field.componentType === 'input'">
        <input 
          v-model="formData[field.fieldKey]" 
          type="text" 
          class="form-control"
          :placeholder="field.placeholder || `例如：周杰伦 - 晴天`"
          :disabled="isGenerating"
          @input="errors[field.fieldKey] = ''"
        />
      </template>

      <!-- Slider (Special for BPM) -->
      <template v-else-if="field.componentType === 'slider'">
        <div class="slider-container">
          <div class="slider-labels">
            <span>慢板</span>
            <span>快板</span>
          </div>
          <input 
            type="range" 
            v-model="formData[field.fieldKey]" 
            :min="field.min || 40" 
            :max="field.max || 200" 
            step="1"
            class="range-slider"
            :disabled="isGenerating"
          />
          <div class="slider-ticks">
            <span>40</span>
            <span>80</span>
            <span>120</span>
            <span>160</span>
            <span>200</span>
          </div>
        </div>
      </template>

      <!-- Textarea -->
      <template v-else-if="field.componentType === 'textarea'">
        <div class="textarea-wrapper">
          <textarea 
            v-model="formData[field.fieldKey]" 
            class="form-control"
            rows="4"
            :placeholder="field.placeholder || `补充任何特殊要求，例如：需要加入中国传统乐器、避免使用某些和弦...`"
            :disabled="isGenerating"
            maxlength="500"
            @input="errors[field.fieldKey] = ''"
          ></textarea>
          <div class="char-count">{{ (formData[field.fieldKey] || '').length }}/500</div>
        </div>
      </template>

      <!-- Select / Radio (Language Selection) -->
      <template v-else-if="field.componentType === 'select' || field.componentType === 'radio'">
        <div class="radio-group-modern">
          <label 
            v-for="opt in field.options" 
            :key="opt.value" 
            class="radio-btn-modern"
            :class="{ active: formData[field.fieldKey] === opt.value, disabled: isGenerating }"
          >
            <input 
              type="radio" 
              :value="opt.value" 
              v-model="formData[field.fieldKey]"
              :disabled="isGenerating"
              @change="errors[field.fieldKey] = ''"
            />
            <span class="radio-dot"></span>
            {{ opt.label }}
          </label>
        </div>
      </template>

      <!-- Tags (Multi-select) -->
      <template v-else-if="field.componentType === 'tags' || field.componentType === 'tag'">
        <div class="tags-group">
          <button 
            v-for="opt in field.options" 
            :key="opt.value"
            type="button"
            class="tag-btn"
            :class="{ 
              active: (formData[field.fieldKey] || []).includes(opt.value) || formData[field.fieldKey] === opt.value,
              disabled: isGenerating
            }"
            :disabled="isGenerating"
            @click="toggleTag(field.fieldKey, opt.value)"
          >
            {{ opt.label }}
          </button>
        </div>
      </template>

      <div v-if="errors[field.fieldKey]" class="error-text">
        {{ errors[field.fieldKey] }}
      </div>
    </div>

    <div class="form-actions">
      <button 
        type="submit" 
        class="btn-submit-large" 
        :disabled="isGenerating"
      >
        <template v-if="isGenerating">
          <svg class="spin" viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 2v4m0 12v4M4.93 4.93l2.83 2.83m8.48 8.48l2.83 2.83M2 12h4m12 0h4M4.93 19.07l2.83-2.83m8.48-8.48l2.83-2.83"></path>
          </svg>
          正在生成中...
        </template>
        <template v-else>
          <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
            <path d="M7.5 5.6L10 7L8.6 4.5L10 2L7.5 3.4L5 2L6.4 4.5L5 7L7.5 5.6ZM19.5 15.4L17 14L18.4 16.5L17 19L19.5 17.6L22 19L20.6 16.5L22 14L19.5 15.4ZM22 2L19.5 3.4L17 2L18.4 4.5L17 7L19.5 5.6L22 7L20.6 4.5L22 2ZM13.35 6.75L2.3 17.8C1.9 18.2 1.9 18.8 2.3 19.2L4.8 21.7C5.2 22.1 5.8 22.1 6.2 21.7L17.25 10.65L13.35 6.75ZM16.3 7.7L17.3 8.7L18.8 7.2C19.2 6.8 19.2 6.2 18.8 5.8L18.2 5.2C17.8 4.8 17.2 4.8 16.8 5.2L16.3 7.7Z"/>
          </svg>
          生成 AI 提示词
        </template>
      </button>
    </div>
  </form>
</template>

<style scoped>
.dynamic-form {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.has-error .form-control {
  border-color: #ef4444;
  background-color: #fffafb;
}

.has-error .form-control:focus {
  box-shadow: 0 0 0 4px rgba(239, 68, 68, 0.1);
}

.error-text {
  font-size: 12px;
  color: #ef4444;
  font-weight: 500;
  margin-top: 4px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 700;
  color: #334155;
}

.field-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #00c08b;
}

.tempo-value {
  margin-left: 4px;
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.tempo-value .value {
  color: #00c08b;
  font-size: 16px;
}

.tempo-value .unit {
  color: #94a3b8;
  font-size: 12px;
  font-weight: 500;
}

.optional-tag {
  font-size: 12px;
  font-weight: 500;
  color: #94a3b8;
  margin-left: 4px;
}

.required-star {
  color: #ef4444;
}

.form-control {
  width: 100%;
  padding: 16px 20px;
  font-size: 14px;
  color: #334155;
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  transition: all 0.2s ease;
  font-family: inherit;
}

.form-control::placeholder {
  color: #94a3b8;
}

.form-control:focus {
  outline: none;
  background-color: #ffffff;
  border-color: #00c08b;
  box-shadow: 0 0 0 4px rgba(0, 192, 139, 0.1);
}

.form-control:disabled {
  background-color: #f1f5f9;
  color: #94a3b8;
  cursor: not-allowed;
}

/* Slider styles */
.slider-container {
  padding: 10px 4px 20px;
}

.slider-labels {
  display: flex;
  justify-content: space-between;
  margin-bottom: 24px;
  color: #94a3b8;
  font-size: 13px;
  font-weight: 500;
}

.range-slider {
  -webkit-appearance: none;
  width: 100%;
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  outline: none;
}

.range-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 20px;
  height: 20px;
  background: #00c08b;
  border-radius: 50%;
  cursor: pointer;
  border: 4px solid #ffffff;
  box-shadow: 0 2px 6px rgba(0, 192, 139, 0.3);
  transition: all 0.2s;
}

.range-slider::-webkit-slider-thumb:hover {
  transform: scale(1.1);
}

.slider-ticks {
  display: flex;
  justify-content: space-between;
  margin-top: 12px;
  padding: 0 2px;
  color: #cbd5e1;
  font-size: 12px;
  font-weight: 500;
}

/* Textarea counter */
.textarea-wrapper {
  position: relative;
}

.char-count {
  position: absolute;
  right: 16px;
  bottom: -24px;
  font-size: 12px;
  color: #94a3b8;
}

/* Modern Radio/Select styles */
.radio-group-modern {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.radio-btn-modern {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}

.radio-btn-modern input {
  display: none;
}

.radio-dot {
  width: 16px;
  height: 16px;
  border: 1.5px solid #cbd5e1;
  border-radius: 50%;
  position: relative;
  transition: all 0.2s;
}

.radio-btn-modern.active {
  background-color: rgba(0, 192, 139, 0.05);
  border-color: #00c08b;
  color: #00c08b;
}

.radio-btn-modern.active .radio-dot {
  border-color: #00c08b;
  background-color: #00c08b;
}

.radio-btn-modern.active .radio-dot::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 6px;
  height: 6px;
  background-color: #ffffff;
  border-radius: 50%;
}

/* Tags group styles */
.tags-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.tag-btn {
  padding: 10px 20px;
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.tag-btn:hover:not(.disabled) {
  border-color: #00c08b;
  color: #00c08b;
  background-color: rgba(0, 192, 139, 0.02);
}

.tag-btn.active {
  background-color: #00c08b;
  color: #ffffff;
  border-color: #00c08b;
  box-shadow: 0 4px 12px rgba(0, 192, 139, 0.2);
}

/* Form actions styles */
.form-actions {
  margin-top: 20px;
}

.btn-submit-large {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 18px;
  font-size: 17px;
  font-weight: 700;
  background-color: #00c08b;
  color: #ffffff;
  border: none;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 10px 25px -5px rgba(0, 192, 139, 0.4);
}

.btn-submit-large:hover:not(:disabled) {
  background-color: #00a376;
  transform: translateY(-3px);
  box-shadow: 0 20px 30px -10px rgba(0, 192, 139, 0.4);
}

.btn-submit-large:active:not(:disabled) {
  transform: translateY(-1px);
}

.btn-submit-large:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.spin {
  animation: spin 2s linear infinite;
}

@keyframes spin {
  100% { transform: rotate(360deg); }
}
</style>

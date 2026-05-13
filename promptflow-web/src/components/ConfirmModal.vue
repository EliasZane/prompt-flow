<script setup lang="ts">
defineProps<{
  show: boolean;
  title: string;
  description: string;
  confirmText?: string;
  cancelText?: string;
  type?: 'danger' | 'primary';
}>();

const emit = defineEmits<{
  (e: 'confirm'): void;
  (e: 'cancel'): void;
}>();
</script>

<template>
  <div v-if="show" class="modal-overlay" @click="emit('cancel')">
    <div class="confirm-modal" @click.stop>
      <div class="modal-icon" :class="type || 'danger'">
        <slot name="icon">
          <svg viewBox="0 0 24 24" width="32" height="32" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path>
          </svg>
        </slot>
      </div>
      <h3 class="modal-title">{{ title }}</h3>
      <p class="modal-desc">{{ description }}</p>
      <div class="modal-actions">
        <button class="modal-btn cancel" @click="emit('cancel')">{{ cancelText || '取消' }}</button>
        <button 
          class="modal-btn confirm" 
          :class="type || 'danger'" 
          @click="emit('confirm')"
        >
          {{ confirmText || '确认' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  animation: fadeIn 0.2s ease;
}

.confirm-modal {
  background: white;
  padding: 40px;
  border-radius: 32px;
  width: 90%;
  max-width: 400px;
  text-align: center;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.15);
  animation: slideUp 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.modal-icon {
  width: 64px;
  height: 64px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.modal-icon.danger {
  background: #fef2f2;
  color: #ef4444;
}

.modal-icon.primary {
  background: #f0fdf9;
  color: #00c08b;
}

.modal-title {
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 12px;
}

.modal-desc {
  font-size: 15px;
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 32px;
}

.modal-actions {
  display: flex;
  gap: 12px;
}

.modal-btn {
  flex: 1;
  padding: 14px;
  border-radius: 14px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.modal-btn.cancel {
  background: #f1f5f9;
  color: #475569;
}

.modal-btn.cancel:hover {
  background: #e2e8f0;
  color: #1e293b;
}

.modal-btn.confirm.danger {
  background: #ef4444;
  color: white;
}

.modal-btn.confirm.danger:hover {
  background: #dc2626;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(239, 68, 68, 0.2);
}

.modal-btn.confirm.primary {
  background: #00c08b;
  color: white;
}

.modal-btn.confirm.primary:hover {
  background: #00a87a;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 192, 139, 0.2);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
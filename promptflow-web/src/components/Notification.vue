<template>
  <Transition name="fade">
    <div
      v-if="isVisible"
      :class="['notification-container', typeClass]"
      role="alert"
    >
      <div class="notification-content">
        <svg v-if="type === 'success'" class="icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <svg v-if="type === 'error'" class="icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <svg v-if="type === 'warning'" class="icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
        <svg v-if="type === 'info'" class="icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <p class="message">{{ message }}</p>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  message: string;
  type: 'success' | 'error' | 'warning' | 'info';
  isVisible: boolean;
}>();

const typeClass = computed(() => {
  return {
    'notification-success': props.type === 'success',
    'notification-error': props.type === 'error',
    'notification-warning': props.type === 'warning',
    'notification-info': props.type === 'info',
  };
});
</script>

<style scoped>
.notification-container {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  padding: 12px 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  z-index: 1000;
  font-size: 15px;
  font-weight: 500;
  color: #fff;
}

.notification-success {
  background-color: #4CAF50; /* Green */
}

.notification-error {
  background-color: #F44336; /* Red */
}

.notification-warning {
  background-color: #FFC107; /* Yellow */
  color: #333;
}

.notification-info {
  background-color: #2196F3; /* Blue */
}

.notification-content {
  display: flex;
  align-items: center;
  gap: 10px;
}

.icon {
  width: 20px;
  height: 20px;
}

.message {
  margin: 0;
}

/* Transition styles */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(-20px);
}
</style>

import { ref } from 'vue'

const message = ref('')
const type = ref<'success' | 'error' | 'warning' | 'info'>('info')
const isVisible = ref(false)
let timer: number | null = null

export function useNotification() {
  const showNotification = (msg: string, notificationType: 'success' | 'error' | 'warning' | 'info' = 'info', duration: number = 3000) => {
    message.value = msg
    type.value = notificationType
    isVisible.value = true

    if (timer) {
      clearTimeout(timer)
    }
    timer = setTimeout(() => {
      isVisible.value = false
    }, duration)
  }

  return {
    message,
    type,
    isVisible,
    showNotification
  }
}

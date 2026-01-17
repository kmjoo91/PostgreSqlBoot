<script setup lang="ts">
import { ref, watch, onUnmounted } from 'vue'
import type { UserRequest } from '@/types/user'

interface Props {
  show: boolean
  isEditMode: boolean
  initialData?: UserRequest
  error?: string | null
  loading?: boolean
}

interface Emits {
  (e: 'close'): void
  (e: 'submit', data: UserRequest): void
}

const props = withDefaults(defineProps<Props>(), {
  error: null,
  loading: false,
})

const emit = defineEmits<Emits>()

const formData = ref<UserRequest>({
  email: '',
  name: '',
})

// show와 initialData 변경을 감시하여 폼 데이터 초기화
watch([() => props.show, () => props.initialData], ([isOpen, initialData]) => {
  if (isOpen) {
    if (initialData) {
      formData.value = { ...initialData }
    } else {
      formData.value = { email: '', name: '' }
    }
  } else {
    formData.value = { email: '', name: '' }
  }
}, { immediate: true })

const handleSubmit = () => {
  if (!formData.value.email || !formData.value.name) {
    return
  }
  emit('submit', { ...formData.value })
}

const handleClose = () => {
  emit('close')
}

// ESC 키로 모달 닫기
const handleEscape = (event: KeyboardEvent) => {
  if (event.key === 'Escape' && props.show) {
    handleClose()
  }
}

watch(() => props.show, (isOpen) => {
  if (isOpen) {
    window.addEventListener('keydown', handleEscape)
  } else {
    window.removeEventListener('keydown', handleEscape)
  }
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleEscape)
})
</script>

<template>
  <div v-if="show" class="modal-overlay" @click.self="handleClose">
    <div class="modal-content">
      <div class="modal-header">
        <h2>{{ isEditMode ? '사용자 수정' : '새 사용자 추가' }}</h2>
        <button @click="handleClose" class="modal-close">×</button>
      </div>

      <!-- 에러 메시지 -->
      <div v-if="error" class="error-message">
        {{ error }}
      </div>

      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="email">이메일</label>
          <input
            id="email"
            v-model="formData.email"
            type="email"
            placeholder="example@email.com"
            required
            :disabled="loading"
          />
        </div>
        <div class="form-group">
          <label for="name">이름</label>
          <input
            id="name"
            v-model="formData.name"
            type="text"
            placeholder="홍길동"
            maxlength="100"
            required
            :disabled="loading"
          />
        </div>
        <div class="form-actions">
          <button type="submit" :disabled="loading">
            {{ isEditMode ? '수정' : '추가' }}
          </button>
          <button type="button" @click="handleClose" :disabled="loading">
            취소
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.error-message {
  background-color: #fee;
  color: #c33;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
  border: 1px solid #fcc;
}

/* 모달 스타일 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  padding: 2rem;
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.modal-header h2 {
  color: #34495e;
  margin: 0;
  font-size: 1.5rem;
}

.modal-close {
  background: none;
  border: none;
  font-size: 2rem;
  color: #666;
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.modal-close:hover {
  background-color: #f0f0f0;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #555;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  box-sizing: border-box;
}

.form-group input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.form-actions {
  display: flex;
  gap: 0.5rem;
  margin-top: 1.5rem;
}

.form-actions button[type="submit"] {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  background-color: #42b983;
  color: white;
  transition: background-color 0.2s;
}

.form-actions button[type="submit"]:hover:not(:disabled) {
  background-color: #35a372;
}

.form-actions button[type="button"] {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  background-color: #6c757d;
  color: white;
  transition: background-color 0.2s;
}

.form-actions button[type="button"]:hover:not(:disabled) {
  background-color: #5a6268;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>


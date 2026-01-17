<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { userService } from '@/services/userService'
import type { User, UserRequest } from '@/types/user'

const users = ref<User[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const showModal = ref(false)
const isEditMode = ref(false)
const editingUserId = ref<number | null>(null)

const formData = ref<UserRequest>({
  email: '',
  name: '',
})

// 사용자 목록 불러오기
const loadUsers = async () => {
  loading.value = true
  error.value = null
  try {
    users.value = await userService.getAllUsers()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '알 수 없는 오류가 발생했습니다.'
  } finally {
    loading.value = false
  }
}

// 사용자 생성
const createUser = async () => {
  if (!formData.value.email || !formData.value.name) {
    error.value = '이메일과 이름을 입력해주세요.'
    return
  }

  loading.value = true
  error.value = null
  try {
    await userService.createUser(formData.value)
    resetForm()
    showModal.value = false
    await loadUsers()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '사용자 생성에 실패했습니다.'
  } finally {
    loading.value = false
  }
}

// 사용자 수정 모드로 전환
const editUser = (user: User) => {
  isEditMode.value = true
  editingUserId.value = user.id
  formData.value = {
    email: user.email,
    name: user.name,
  }
  showModal.value = true
}

// 모달 열기 (새 사용자 추가)
const openAddModal = () => {
  resetForm()
  showModal.value = true
}

// 사용자 수정
const updateUser = async () => {
  if (!editingUserId.value) return
  if (!formData.value.email || !formData.value.name) {
    error.value = '이메일과 이름을 입력해주세요.'
    return
  }

  loading.value = true
  error.value = null
  try {
    await userService.updateUser(editingUserId.value, formData.value)
    resetForm()
    showModal.value = false
    await loadUsers()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '사용자 수정에 실패했습니다.'
  } finally {
    loading.value = false
  }
}

// 사용자 삭제
const deleteUser = async (id: number) => {
  if (!confirm('정말 삭제하시겠습니까?')) return

  loading.value = true
  error.value = null
  try {
    await userService.deleteUser(id)
    await loadUsers()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '사용자 삭제에 실패했습니다.'
  } finally {
    loading.value = false
  }
}

// 폼 리셋
const resetForm = () => {
  formData.value = {
    email: '',
    name: '',
  }
  isEditMode.value = false
  editingUserId.value = null
  error.value = null
}

// 모달 닫기
const closeModal = () => {
  showModal.value = false
  resetForm()
}

// 날짜 포맷팅
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleString('ko-KR')
}

// 컴포넌트 마운트 시 사용자 목록 불러오기
onMounted(() => {
  loadUsers()
})
</script>

<template>
  <main class="container">
    <h1>사용자 관리</h1>

    <!-- 사용자 목록 -->
    <div class="users-container">
      <div v-if="loading && users.length === 0" class="loading">로딩 중...</div>
      <div v-else-if="users.length === 0" class="empty-message">등록된 사용자가 없습니다.</div>
      <table v-else>
        <thead>
          <tr>
            <th>ID</th>
            <th>이메일</th>
            <th>이름</th>
            <th>생성일</th>
            <th>수정일</th>
            <th class="action-header">
              <span>작업</span>
              <button @click="openAddModal" class="btn-add" title="사용자 추가">
                +
              </button>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.name }}</td>
            <td>{{ formatDate(user.createdAt) }}</td>
            <td>{{ formatDate(user.updatedAt) }}</td>
            <td class="actions">
              <button @click="editUser(user)" :disabled="loading" class="btn-edit">
                수정
              </button>
              <button @click="deleteUser(user.id)" :disabled="loading" class="btn-delete">
                삭제
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 모달 -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ isEditMode ? '사용자 수정' : '새 사용자 추가' }}</h2>
          <button @click="closeModal" class="modal-close">×</button>
        </div>

        <!-- 에러 메시지 -->
        <div v-if="error" class="error-message">
          {{ error }}
        </div>

        <form @submit.prevent="isEditMode ? updateUser() : createUser()">
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
            <button type="button" @click="closeModal" :disabled="loading">
              취소
            </button>
          </div>
        </form>
      </div>
    </div>
  </main>
</template>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 100vh;
}

h1 {
  color: #2c3e50;
  margin-bottom: 2rem;
  text-align: center;
}

h2 {
  color: #34495e;
  margin-bottom: 1rem;
  font-size: 1.5rem;
}

.error-message {
  background-color: #fee;
  color: #c33;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
  border: 1px solid #fcc;
}

.users-container {
  background-color: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  width: 100%;
}

.loading,
.empty-message {
  text-align: center;
  padding: 2rem;
  color: #666;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
}

thead {
  background-color: #f8f9fa;
}

th,
td {
  padding: 0.75rem;
  text-align: left;
  border-bottom: 1px solid #ddd;
  vertical-align: middle;
}

th {
  font-weight: 600;
  color: #555;
}

th.action-header {
  position: relative;
  text-align: left;
}

th.action-header .btn-add {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
}

tbody tr:last-child td {
  border-bottom: none;
}

.btn-add {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background-color: #42b983;
  color: white;
  font-size: 20px;
  line-height: 1;
  padding: 0;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.btn-add:hover:not(:disabled) {
  background-color: #35a372;
  transform: scale(1.1);
}

.btn-add:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

tbody tr:hover {
  background-color: #f8f9fa;
}

.actions {
  display: flex;
  gap: 0.5rem;
}

.btn-edit,
.btn-delete {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-edit {
  background-color: #007bff;
  color: white;
}

.btn-edit:hover:not(:disabled) {
  background-color: #0056b3;
}

.btn-delete {
  background-color: #dc3545;
  color: white;
}

.btn-delete:hover:not(:disabled) {
  background-color: #c82333;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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

@media (max-width: 768px) {
  .container {
    padding: 1rem;
  }

  table {
    font-size: 0.875rem;
  }

  th,
  td {
    padding: 0.5rem;
  }

  .actions {
    flex-direction: column;
  }
}
</style>

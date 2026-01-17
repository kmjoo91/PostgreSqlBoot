<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { userService } from '@/services/userService'
import type { User, UserRequest } from '@/types/user'
import UserTable from '@/components/UserTable.vue'
import UserModal from '@/components/UserModal.vue'

const users = ref<User[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const showModal = ref(false)
const isEditMode = ref(false)
const editingUserId = ref<number | null>(null)
const modalInitialData = ref<UserRequest | null>(null)

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
const handleCreateUser = async (userRequest: UserRequest) => {
  loading.value = true
  error.value = null
  try {
    await userService.createUser(userRequest)
    showModal.value = false
    modalInitialData.value = null
    await loadUsers()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '사용자 생성에 실패했습니다.'
  } finally {
    loading.value = false
  }
}

// 사용자 수정
const handleUpdateUser = async (userRequest: UserRequest) => {
  if (!editingUserId.value) return

  loading.value = true
  error.value = null
  try {
    await userService.updateUser(editingUserId.value, userRequest)
    showModal.value = false
    editingUserId.value = null
    modalInitialData.value = null
    await loadUsers()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '사용자 수정에 실패했습니다.'
  } finally {
    loading.value = false
  }
}

// 모달에서 submit 이벤트 처리
const handleModalSubmit = (userRequest: UserRequest) => {
  if (isEditMode.value) {
    handleUpdateUser(userRequest)
  } else {
    handleCreateUser(userRequest)
  }
}

// 사용자 추가 모달 열기
const handleAddUser = () => {
  isEditMode.value = false
  editingUserId.value = null
  modalInitialData.value = null
  error.value = null
  showModal.value = true
}

// 사용자 수정 모달 열기
const handleEditUser = (user: User) => {
  isEditMode.value = true
  editingUserId.value = user.id
  modalInitialData.value = {
    email: user.email,
    name: user.name,
  }
  error.value = null
  showModal.value = true
}

// 사용자 삭제
const handleDeleteUser = async (id: number) => {
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

// 모달 닫기
const handleCloseModal = () => {
  showModal.value = false
  isEditMode.value = false
  editingUserId.value = null
  modalInitialData.value = null
  error.value = null
}

// 컴포넌트 마운트 시 사용자 목록 불러오기
onMounted(() => {
  loadUsers()
})
</script>

<template>
  <main class="container">
    <h1>사용자 관리</h1>

    <UserTable
      :users="users"
      :loading="loading"
      @add="handleAddUser"
      @edit="handleEditUser"
      @delete="handleDeleteUser"
    />

    <UserModal
      :show="showModal"
      :is-edit-mode="isEditMode"
      :initial-data="modalInitialData || undefined"
      :error="error"
      :loading="loading"
      @close="handleCloseModal"
      @submit="handleModalSubmit"
    />
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

@media (max-width: 768px) {
  .container {
    padding: 1rem;
  }
}
</style>

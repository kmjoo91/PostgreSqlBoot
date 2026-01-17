<script setup lang="ts">
import type { User } from '@/types/user'

interface Props {
  users: User[]
  loading?: boolean
}

interface Emits {
  (e: 'add'): void
  (e: 'edit', user: User): void
  (e: 'delete', id: number): void
}

withDefaults(defineProps<Props>(), {
  loading: false,
})

const emit = defineEmits<Emits>()

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleString('ko-KR')
}

const handleAdd = () => {
  emit('add')
}

const handleEdit = (user: User) => {
  emit('edit', user)
}

const handleDelete = (id: number) => {
  if (confirm('정말 삭제하시겠습니까?')) {
    emit('delete', id)
  }
}
</script>

<template>
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
            <button @click="handleAdd" class="btn-add" title="사용자 추가">
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
            <button @click="handleEdit(user)" :disabled="loading" class="btn-edit">
              수정
            </button>
            <button @click="handleDelete(user.id)" :disabled="loading" class="btn-delete">
              삭제
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
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
}

th.action-header .btn-add:hover:not(:disabled) {
  transform: translateY(-50%) scale(1.1);
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

@media (max-width: 768px) {
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


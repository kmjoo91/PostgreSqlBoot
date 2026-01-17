import type { User, UserRequest } from '@/types/user'

const API_BASE_URL = '/api/users'

export const userService = {
  // 모든 사용자 조회
  async getAllUsers(): Promise<User[]> {
    const response = await fetch(API_BASE_URL)
    if (!response.ok) {
      throw new Error('사용자 목록을 불러오는데 실패했습니다.')
    }
    return response.json()
  },

  // ID로 사용자 조회
  async getUserById(id: number): Promise<User> {
    const response = await fetch(`${API_BASE_URL}/${id}`)
    if (!response.ok) {
      throw new Error('사용자를 찾을 수 없습니다.')
    }
    return response.json()
  },

  // 사용자 생성
  async createUser(userRequest: UserRequest): Promise<User> {
    const response = await fetch(API_BASE_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(userRequest),
    })
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || '사용자 생성에 실패했습니다.')
    }
    return response.json()
  },

  // 사용자 수정
  async updateUser(id: number, userRequest: UserRequest): Promise<User> {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(userRequest),
    })
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || '사용자 수정에 실패했습니다.')
    }
    return response.json()
  },

  // 사용자 삭제
  async deleteUser(id: number): Promise<void> {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: 'DELETE',
    })
    if (!response.ok) {
      throw new Error('사용자 삭제에 실패했습니다.')
    }
  },
}


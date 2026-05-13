import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface UserInfo {
  id: number
  name: string
  email: string
  avatarUrl: string
  credit: number
  vipLevel: number
}

export const useUserStore = defineStore('user', () => {
  const user = ref<UserInfo | null>(null)
  const token = ref<string | null>(localStorage.getItem('token'))

  function setUser(userInfo: UserInfo) {
    user.value = userInfo
  }

  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function logout() {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
  }

  return {
    user,
    token,
    setUser,
    setToken,
    logout
  }
})

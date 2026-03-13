import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as apiLogin, logout as apiLogout, register as apiRegister } from '../api/auth'
import type { AuthUser, LoginData, RegisterData } from '../api/auth'

export const useUserStore = defineStore('user', () => {
  // Restore from sessionStorage if available
  const savedUser = localStorage.getItem('echo_user')
  const userInfo = ref<AuthUser | null>(savedUser ? JSON.parse(savedUser) : null)
  const isLoggedIn = computed(() => userInfo.value !== null)

  // Theme
  const isDark = ref(false)

  const toggleTheme = () => {
    isDark.value = !isDark.value
    if (isDark.value) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  if (isDark.value) {
    document.documentElement.classList.add('dark')
  }

  const login = async (data: LoginData) => {
    const user = await apiLogin(data)
    userInfo.value = user
    localStorage.setItem('echo_user', JSON.stringify(user))
    return user
  }

  const registerUser = async (data: RegisterData) => {
    const user = await apiRegister(data)
    userInfo.value = user
    localStorage.setItem('echo_user', JSON.stringify(user))
    return user
  }

  const logoutUser = async () => {
    try {
      await apiLogout()
    } finally {
      userInfo.value = null
      localStorage.removeItem('echo_user')
    }
  }

  // Check-in state
  const hasCheckedInToday = ref(false)

  return { userInfo, isLoggedIn, isDark, toggleTheme, login, registerUser, logoutUser, hasCheckedInToday }
})

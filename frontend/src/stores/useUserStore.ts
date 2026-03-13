import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref({
    id: 1,
    username: 'TechExplorer',
    avatar: 'https://api.dicebear.com/7.x/bottts/svg?seed=Felix',
    bio: 'Java Backend Developer | Spring Boot · Redis 爱好者'
  })
  // State
  const isDark = ref(false)

  const toggleTheme = () => {
    isDark.value = !isDark.value
    if (isDark.value) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  // Initialize theme
  if (isDark.value) {
    document.documentElement.classList.add('dark')
  }

  return { token, userInfo, isDark, toggleTheme }
})

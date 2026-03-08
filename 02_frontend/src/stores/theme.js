import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const theme = ref(localStorage.getItem('theme') || 'overwatch')

  const isOverwatch = computed(() => theme.value === 'overwatch')
  const isTalion = computed(() => theme.value === 'talion')

  function setTheme(newTheme) {
    theme.value = newTheme
    localStorage.setItem('theme', newTheme)
  }

  function toggleTheme() {
    setTheme(theme.value === 'overwatch' ? 'talion' : 'overwatch')
  }

  return {
    theme,
    isOverwatch,
    isTalion,
    setTheme,
    toggleTheme,
  }
})

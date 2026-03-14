import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    loading: false,
    message: null
  }),
  
  actions: {
    setLoading(status) {
      this.loading = status
    },
    
    showMessage(msg) {
      this.message = msg
      setTimeout(() => {
        this.message = null
      }, 3000)
    }
  }
})

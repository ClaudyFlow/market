import { defineStore } from 'pinia'

interface AppState {
  loading: boolean
  message: string | null
}

export const useAppStore = defineStore('app', {
  state: (): AppState => ({
    loading: false,
    message: null
  }),

  actions: {
    setLoading(status: boolean) {
      this.loading = status
    },

    showMessage(msg: string) {
      this.message = msg
      setTimeout(() => {
        this.message = null
      }, 3000)
    }
  }
})

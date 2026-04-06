/**
 * 验证码倒计时组合式函数
 */
import { ref, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'

export function useCaptchaCountdown(initialSeconds: number = 60) {
  const countdown = ref(0)
  const isCounting = ref(false)
  let timer: ReturnType<typeof setInterval> | null = null

  const startCountdown = () => {
    if (isCounting.value) return

    countdown.value = initialSeconds
    isCounting.value = true

    timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        stopCountdown()
      }
    }, 1000)
  }

  const stopCountdown = () => {
    if (timer) {
      clearInterval(timer)
      timer = null
    }
    countdown.value = 0
    isCounting.value = false
  }

  const sendCaptcha = async (sendFn: () => Promise<void>) => {
    if (isCounting.value) {
      ElMessage.warning('请等待倒计时结束')
      return
    }

    try {
      await sendFn()
      ElMessage.success('验证码已发送')
      startCountdown()
    } catch (error) {
      ElMessage.error('发送失败，请重试')
    }
  }

  onUnmounted(() => {
    stopCountdown()
  })

  return {
    countdown,
    isCounting,
    sendCaptcha,
    stopCountdown
  }
}

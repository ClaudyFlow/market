import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getUserInfo, getVipInfo, getUserCredit, checkIn } from '@user/api/user'
import type { UserInfo, VipInfo, UserCredit } from '@user/types'
import { ElMessage } from 'element-plus'

/**
 * 用户状态管理
 */
export const useUserStore = defineStore('user', () => {
  const userCredit = ref(0)
  const userVipLevel = ref(0)
  const vipExpireTime = ref<string | null>(null)
  const vipBenefits = ref<string[]>([])
  const userInfo = ref<UserInfo | null>(null)
  const loading = ref(false)
  const hasCheckedIn = ref(false)

  const vipLevelName = computed(() => {
    const names = ['普通会员', '白银会员', '黄金会员', '铂金会员', '钻石会员', '至尊会员']
    return names[userVipLevel.value] || '普通会员'
  })

  const isVip = computed(() => userVipLevel.value > 0)

  const loadUserInfo = async () => {
    loading.value = true
    try {
      const [infoResult, vipResult, creditResult] = await Promise.all([
        getUserInfo(),
        getVipInfo(),
        getUserCredit()
      ])
      // axios 响应已经是 .data 后的结果(见 request.ts 拦截器)
      userInfo.value = (infoResult as any).data || null
      userVipLevel.value = (vipResult as any).data?.level || 0
      vipExpireTime.value = (vipResult as any).data?.expireTime || null
      vipBenefits.value = (vipResult as any).data?.benefits || []
      userCredit.value = (creditResult as any).data?.credit || 0
      hasCheckedIn.value = (creditResult as any).data?.hasCheckedIn || false
    } catch {
      userCredit.value = 0
      userVipLevel.value = 0
    } finally {
      loading.value = false
    }
  }

  const doCheckIn = async (): Promise<boolean> => {
    if (hasCheckedIn.value) {
      ElMessage.info('今天已经签到过了哦~')
      return false
    }
    try {
      const result = await checkIn()
      const credit = (result as any).data?.credit || 10
      userCredit.value += credit
      hasCheckedIn.value = true
      ElMessage.success(`签到成功!获得 ${credit} 积分奖励`)
      return true
    } catch {
      ElMessage.error('签到失败,请稍后重试')
      return false
    }
  }

  const addCredit = (amount: number) => {
    userCredit.value += amount
  }

  const consumeCredit = (amount: number): boolean => {
    if (userCredit.value >= amount) {
      userCredit.value -= amount
      return true
    }
    return false
  }

  const logout = () => {
    userCredit.value = 0
    userVipLevel.value = 0
    vipExpireTime.value = null
    vipBenefits.value = []
    userInfo.value = null
    hasCheckedIn.value = false
  }

  return {
    userCredit,
    userVipLevel,
    vipExpireTime,
    vipBenefits,
    userInfo,
    loading,
    hasCheckedIn,
    vipLevelName,
    isVip,
    loadUserInfo,
    doCheckIn,
    addCredit,
    consumeCredit,
    logout
  }
})

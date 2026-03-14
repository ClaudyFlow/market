import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getUserInfo, getVipInfo, getUserPoints, checkIn } from '@user/api/user'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const userPoints = ref(0)
  const userVipLevel = ref(0)
  const vipExpireTime = ref(null)
  const vipBenefits = ref([])
  const userInfo = ref(null)
  const isLoading = ref(false)

  // 是否已签到
  const hasCheckedIn = ref(false)

  // VIP 等级名称
  const vipLevelName = computed(() => {
    const names = ['普通会员', '白银会员', '黄金会员', '铂金会员', '钻石会员', '至尊会员']
    return names[userVipLevel.value] || '普通会员'
  })

  // 是否 VIP
  const isVip = computed(() => userVipLevel.value > 0)

  // 加载用户信息
  const loadUserInfo = async () => {
    isLoading.value = true
    try {
      const [info, vip, points] = await Promise.all([
        getUserInfo(),
        getVipInfo(),
        getUserPoints()
      ])
      userInfo.value = info.data || {}
      userVipLevel.value = vip.data?.level || 0
      vipExpireTime.value = vip.data?.expireTime || null
      vipBenefits.value = vip.data?.benefits || []
      userPoints.value = points.data?.points || 0
      hasCheckedIn.value = points.data?.hasCheckedIn || false
    } catch (error) {
      console.error('加载用户信息失败:', error)
      // 使用默认值
      userPoints.value = 0
      userVipLevel.value = 0
    } finally {
      isLoading.value = false
    }
  }

  // 签到
  const doCheckIn = async () => {
    if (hasCheckedIn.value) {
      ElMessage.info('今天已经签到过了哦~')
      return false
    }
    try {
      const result = await checkIn()
      const points = result.data?.points || 10
      userPoints.value += points
      hasCheckedIn.value = true
      ElMessage.success(`签到成功！获得 ${points} 积分奖励`)
      return true
    } catch (error) {
      ElMessage.error('签到失败，请稍后重试')
      return false
    }
  }

  // 添加积分
  const addPoints = (amount) => {
    userPoints.value += amount
  }

  // 消费积分
  const spendPoints = (amount) => {
    if (userPoints.value >= amount) {
      userPoints.value -= amount
      return true
    }
    return false
  }

  // 登出
  const logout = () => {
    userPoints.value = 0
    userVipLevel.value = 0
    vipExpireTime.value = null
    vipBenefits.value = []
    userInfo.value = null
    hasCheckedIn.value = false
  }

  return {
    userPoints,
    userVipLevel,
    vipExpireTime,
    vipBenefits,
    userInfo,
    isLoading,
    hasCheckedIn,
    vipLevelName,
    isVip,
    loadUserInfo,
    doCheckIn,
    addPoints,
    spendPoints,
    logout
  }
})

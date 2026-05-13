/**
 * 权限检查 composable
 */

import { computed } from 'vue'
import { useUserStore } from '@user/stores/user'

export function usePermission() {
  const userStore = useUserStore()

  // 检查是否登录
  const isLoggedIn = computed(() => userStore.isLoggedIn)

  // 检查是否 VIP
  const isVip = computed(() => userStore.isVip)

  // 检查用户等级
  const userLevel = computed(() => userStore.level)

  // 检查是否有权限
  const hasPermission = (requiredLevel: number = 1): boolean => {
    return userLevel.value >= requiredLevel
  }

  // 检查是否是管理员
  const isAdmin = computed(() => userLevel.value >= 10)

  // 检查是否可以访问
  const canAccess = (options: { requireLogin?: boolean; requireVip?: boolean; requireLevel?: number } = {}): boolean => {
    const { requireLogin = false, requireVip = false, requireLevel = 1 } = options

    if (requireLogin && !isLoggedIn.value) return false
    if (requireVip && !isVip.value) return false
    if (requireLevel && !hasPermission(requireLevel)) return false

    return true
  }

  // 权限指令
  const permission = (value: number | string) => {
    if (typeof value === 'string') {
      // 字符串权限（预留）
      return true
    }
    return hasPermission(value)
  }

  return {
    isLoggedIn,
    isVip,
    userLevel,
    hasPermission,
    isAdmin,
    canAccess,
    permission
  }
}

/**
 * 角色检查 composable
 */
export function useRole() {
  const userStore = useUserStore()

  // 用户角色（预留）
  const roles = computed(() => {
    // 这里可以根据实际需求从用户信息中获取角色
    const userRoles: string[] = []
    
    if (userStore.isLoggedIn) {
      userRoles.push('user')
    }
    
    if (userStore.level >= 10) {
      userRoles.push('admin')
    }
    
    if (userStore.isVip) {
      userRoles.push('vip')
    }

    return userRoles
  })

  // 检查是否有角色
  const hasRole = (role: string | string[]): boolean => {
    const rolesToCheck = Array.isArray(role) ? role : [role]
    return rolesToCheck.some(r => roles.value.includes(r))
  }

  // 检查是否有任意角色
  const hasAnyRole = (...roles: string[]): boolean => {
    return roles.some(role => roles.value.includes(role))
  }

  // 检查是否有所有角色
  const hasAllRoles = (...roles: string[]): boolean => {
    return roles.every(role => roles.value.includes(role))
  }

  return {
    roles,
    hasRole,
    hasAnyRole,
    hasAllRoles
  }
}

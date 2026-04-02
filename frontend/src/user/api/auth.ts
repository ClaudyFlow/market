/**
 * 认证相关 API
 */

import { post } from './request'
import type { LoginParams, RegisterParams, ResetPasswordParams } from '@user/types/auth'
import type { User, AuthToken } from '@user/types/user'

const BASE_URL = '/auth'

/**
 * 账号密码登录
 */
export function login(data: LoginParams): Promise<AuthToken> {
  return post(`${BASE_URL}/login`, data)
}

/**
 * 手机号验证码登录
 */
export function loginByPhone(phone: string, code: string): Promise<AuthToken> {
  return post(`${BASE_URL}/login/phone`, { phone, code })
}

/**
 * 第三方登录（微信、QQ、微博等）
 */
export function loginByThirdParty(provider: 'wechat' | 'qq' | 'weibo', code: string): Promise<AuthToken & { isNewUser: boolean }> {
  return post(`${BASE_URL}/login/${provider}`, { code })
}

/**
 * 退出登录
 */
export function logout(): Promise<void> {
  return post(`${BASE_URL}/logout`)
}

/**
 * 注册账号
 */
export function register(data: RegisterParams): Promise<AuthToken> {
  return post(`${BASE_URL}/register`, data)
}

/**
 * 发送验证码
 */
export function sendCaptcha(target: string, type: 'phone' | 'email', scene?: 'register' | 'login' | 'reset'): Promise<void> {
  return post(`${BASE_URL}/captcha`, { target, type, scene })
}

/**
 * 验证验证码
 */
export function verifyCaptcha(target: string, code: string, type: 'phone' | 'email'): Promise<void> {
  return post(`${BASE_URL}/captcha/verify`, { target, code, type })
}

/**
 * 重置密码
 */
export function resetPassword(data: ResetPasswordParams): Promise<void> {
  return post(`${BASE_URL}/reset-password`, data)
}

/**
 * 修改密码（已登录）
 */
export function changePassword(oldPassword: string, newPassword: string): Promise<void> {
  return post(`${BASE_URL}/change-password`, { oldPassword, newPassword })
}

/**
 * 绑定手机号
 */
export function bindPhone(phone: string, code: string): Promise<void> {
  return post(`${BASE_URL}/bind/phone`, { phone, code })
}

/**
 * 绑定邮箱
 */
export function bindEmail(email: string, code: string): Promise<void> {
  return post(`${BASE_URL}/bind/email`, { email, code })
}

/**
 * 解绑手机号
 */
export function unbindPhone(code?: string): Promise<void> {
  return post(`${BASE_URL}/unbind/phone`, { code })
}

/**
 * 解绑邮箱
 */
export function unbindEmail(code?: string): Promise<void> {
  return post(`${BASE_URL}/unbind/email`, { code })
}

/**
 * 刷新 token
 */
export function refreshToken(refreshToken: string): Promise<AuthToken> {
  return post(`${BASE_URL}/refresh`, { refreshToken })
}

/**
 * 验证 token 有效性
 */
export function validateToken(): Promise<{ valid: boolean; user?: User }> {
  return post(`${BASE_URL}/validate`)
}

/**
 * 账号注销
 */
export function cancelAccount(reason?: string): Promise<void> {
  return post(`${BASE_URL}/cancel`, { reason })
}

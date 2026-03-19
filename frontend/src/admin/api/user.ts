import request from './request'

export interface User {
  id: number
  name: string
  phone: string
  email: string
  credit: number
  registerTime: string
  status: string
  avatar?: string
}

export interface UserListParams {
  page?: number
  size?: number
  userId?: string
  userName?: string
  phone?: string
  status?: string
  startDate?: string
  endDate?: string
}

export interface UserListResponse {
  list: User[]
  total: number
}

/**
 * 获取用户列表
 */
export function getUserList(params: UserListParams): Promise<UserListResponse> {
  return request.get('/user/list', { params })
}

/**
 * 获取用户详情
 */
export function getUserDetail(id: number): Promise<User> {
  return request.get(`/user/${id}`)
}

/**
 * 创建用户
 */
export function createUser(data: Partial<User>): Promise<User> {
  return request.post('/user', data)
}

/**
 * 更新用户
 */
export function updateUser(id: number, data: Partial<User>): Promise<User> {
  return request.put(`/user/${id}`, data)
}

/**
 * 删除用户
 */
export function deleteUser(id: number): Promise<void> {
  return request.delete(`/user/${id}`)
}

/**
 * 封禁用户
 */
export function banUser(id: number): Promise<void> {
  return request.put(`/user/${id}/ban`)
}

/**
 * 解封用户
 */
export function unbanUser(id: number): Promise<void> {
  return request.put(`/user/${id}/unban`)
}

/**
 * 获取用户统计
 */
export function getUserStats(): Promise<{
  total: number
  active: number
  todayNew: number
  banned: number
}> {
  return request.get('/user/stats')
}

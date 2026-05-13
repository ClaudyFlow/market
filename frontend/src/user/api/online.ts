import { get, post } from './request'

const BASE_URL = '/online'

export function heartbeat(): Promise<void> {
  return post(`${BASE_URL}/heartbeat`)
}

export function getOnlineCount(): Promise<{ count: number }> {
  return get(`${BASE_URL}/count`)
}
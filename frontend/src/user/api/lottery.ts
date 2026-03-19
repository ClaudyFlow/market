import request from './request'

interface ApiResponse<T> {
  data: T
  message?: string
  success?: boolean
}

// 抽奖
export function drawLottery() {
  return request<ApiResponse<{
    prizeId: number
    prizeName: string
    prizeType: number
    cost: number
    remainingCredit: number
  }>>({
    url: '/lottery/draw',
    method: 'post'
  })
}

// 获取抽奖记录
export function getLotteryRecords() {
  return request<ApiResponse<Array<{
    id: number
    prizeName: string
    prizeType: number
    cost: number
    createdAt: string
  }>>>({
    url: '/lottery/records',
    method: 'get'
  })
}

// 获取用户积分(复用现有的)
export function getUserCredit() {
  return request<ApiResponse<{ credit: number }>>({
    url: '/user/credit',
    method: 'get'
  })
}

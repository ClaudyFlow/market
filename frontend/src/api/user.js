import request from './request'

// 获取用户 VIP 信息
export function getVipInfo() {
  return request({
    url: '/user/vip',
    method: 'get'
  })
}

// 获取用户积分信息
export function getUserPoints() {
  return request({
    url: '/user/points',
    method: 'get'
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

// 签到
export function checkIn() {
  return request({
    url: '/user/checkin',
    method: 'post'
  })
}

// 使用积分
export function consumePoints(amount) {
  return request({
    url: '/user/points/consume',
    method: 'post',
    params: { amount }
  })
}

import request from '@merchant/api/request'

export interface MerchantActivitySetting {
  activity: any
  optedOut: boolean
  customDiscountRate: number | null
  customDiscountAmount: number | null
  discountType: string | null
  remark: string | null
}

export const merchantActivityApi = {
  // 获取商家参与的平台活动列表（包含自己的设置）
  getMyActivities: () => request({
    url: '/activity',
    method: 'get',
  }),

  // 设置参与方式（金额或折扣）或退出活动
  setSetting: (activityId: number, data: {
    optedOut: boolean
    discountType?: string
    customDiscountAmount?: number
    customDiscountRate?: number
    remark?: string
  }) => request({
    url: \`/activity/\${activityId}/setting\`,
    method: 'put',
    data,
  }),

  // 快速退出活动
  optOut: (activityId: number, data: { remark?: string }) => request({
    url: \`/activity/\${activityId}/optout\`,
    method: 'post',
    data,
  }),
}

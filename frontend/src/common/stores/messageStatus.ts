/**
 * 消息状态码定义 (1000-9999)
 * 覆盖客服聊天所有可能的场景
 */

// ========== 基础消息状态 (1000-1999) ==========
export const MessageStatus = {
  SENDING: 1000,          // 发送中
  SENDING_RETRY: 1001,    // 发送中（重试）
  QUEUED: 1002,           // 排队中
  SENT: 2000,             // 已发送
  SENT_TO_SERVER: 2001,   // 已发送到服务器
  DELIVERED: 3000,        // 已送达
  DELIVERED_TO_PHONE: 3001, // 已送达手机
  READ: 4000,             // 已读
  READ_BY_USER: 4001,     // 用户已读
  READ_BY_SYSTEM: 4002,   // 系统已读
  FAILED: 5000,           // 发送失败
  FAILED_NETWORK: 5001,   // 网络错误
  FAILED_TIMEOUT: 5002,   // 超时
  FAILED_REJECTED: 5003,  // 被拒绝
  EXPIRED: 5004,          // 已过期
  DELETED: 5005           // 已删除
} as const

// ========== 订单相关消息状态 (6000-6499) ==========
export const OrderMessageStatus = {
  // 订单流程
  ORDER_CREATED: 6000,        // 订单已创建
  ORDER_PENDING: 6001,        // 订单待处理
  ORDER_CONFIRMED: 6002,      // 订单已确认
  ORDER_PROCESSING: 6003,     // 订单处理中
  ORDER_PACKING: 6004,        // 订单打包中
  ORDER_SHIPPED: 6005,        // 订单已发货
  ORDER_DELIVERING: 6006,     // 订单配送中
  ORDER_DELIVERED: 6007,      // 订单已送达
  ORDER_COMPLETED: 6008,      // 订单已完成
  ORDER_CANCELLED: 6009,      // 订单已取消
  ORDER_CLOSED: 6010,         // 订单已关闭
  
  // 订单修改
  ORDER_MODIFYING: 6100,      // 订单修改中
  ORDER_MODIFIED: 6101,       // 订单已修改
  ORDER_MODIFY_REJECTED: 6102, // 订单修改被拒绝
  
  // 订单异常
  ORDER_ABNORMAL: 6200,       // 订单异常
  ORDER_ADDRESS_ERROR: 6201,  // 地址错误
  ORDER_PHONE_ERROR: 6202,    // 电话错误
  ORDER_PAYMENT_ERROR: 6203,  // 支付错误
  ORDER_STOCK_ERROR: 6204,    // 库存不足
  ORDER_LOGISTICS_ERROR: 6205 // 物流异常
} as const

// ========== 支付相关消息状态 (6500-6799) ==========
export const PaymentMessageStatus = {
  PAYMENT_PENDING: 6500,      // 待支付
  PAYMENT_PROCESSING: 6501,   // 支付处理中
  PAYMENT_SUCCESS: 6502,      // 支付成功
  PAYMENT_FAILED: 6503,       // 支付失败
  PAYMENT_TIMEOUT: 6504,      // 支付超时
  PAYMENT_CANCELLED: 6505,    // 支付已取消
  PAYMENT_REFUNDED: 6506,     // 已退款
  PAYMENT_REFUNDING: 6507,    // 退款处理中
  PAYMENT_PARTIAL_REFUND: 6508 // 部分退款
} as const

// ========== 物流相关消息状态 (6800-6999) ==========
export const LogisticsMessageStatus = {
  LOGISTICS_PENDING: 6800,    // 待发货
  LOGISTICS_PICKED_UP: 6801,  // 已揽件
  LOGISTICS_IN_TRANSIT: 6802, // 运输中
  LOGISTICS_ARRIVED_CITY: 6803, // 已到达城市
  LOGISTICS_OUT_FOR_DELIVERY: 6804, // 派送中
  LOGISTICS_DELIVERED: 6805,  // 已签收
  LOGISTICS_EXCEPTION: 6806,  // 物流异常
  LOGISTICS_RETURNED: 6807,   // 已退回
  LOGISTICS_LOST: 6808,       // 包裹丢失
  LOGISTICS_DAMAGED: 6809     // 包裹破损
} as const

// ========== 售后相关消息状态 (7000-7999) ==========
export const AfterSalesMessageStatus = {
  // 退换货
  RETURN_PENDING: 7000,       // 待处理退货
  RETURN_APPROVED: 7001,      // 退货已批准
  RETURN_REJECTED: 7002,      // 退货被拒绝
  RETURN_SHIPPED: 7003,       // 退货已寄出
  RETURN_RECEIVED: 7004,      // 退货已收到
  RETURN_COMPLETED: 7005,     // 退货已完成
  
  // 换货
  EXCHANGE_PENDING: 7100,     // 待处理换货
  EXCHANGE_APPROVED: 7101,    // 换货已批准
  EXCHANGE_SHIPPED: 7102,     // 换货已寄出
  EXCHANGE_RECEIVED: 7103,    // 换货已收到
  EXCHANGE_COMPLETED: 7104,   // 换货已完成
  
  // 维修
  REPAIR_PENDING: 7200,       // 待处理维修
  REPAIR_IN_PROGRESS: 7201,   // 维修中
  REPAIR_COMPLETED: 7202,     // 维修已完成
  REPAIR_RETURNED: 7203,      // 维修已寄回
  
  // 投诉
  COMPLAINT_PENDING: 7300,    // 待处理投诉
  COMPLAINT_PROCESSING: 7301, // 投诉处理中
  COMPLAINT_RESOLVED: 7302,   // 投诉已解决
  COMPLAINT_CLOSED: 7303      // 投诉已关闭
} as const

// ========== 优惠券/促销相关消息状态 (8000-8299) ==========
export const PromotionMessageStatus = {
  COUPON_AVAILABLE: 8000,     // 优惠券可用
  COUPON_USED: 8001,          // 优惠券已使用
  COUPON_EXPIRED: 8002,       // 优惠券已过期
  COUPON_LOCKED: 8003,        // 优惠券已锁定
  PROMOTION_ACTIVE: 8100,     // 促销活动进行中
  PROMOTION_ENDED: 8101,      // 促销活动已结束
  PROMOTION_SUSPENDED: 8102   // 促销活动已暂停
} as const

// ========== VIP/会员相关消息状态 (8300-8599) ==========
export const VipMessageStatus = {
  VIP_ACTIVATED: 8300,        // VIP 已激活
  VIP_EXPIRING: 8301,         // VIP 即将过期
  VIP_EXPIRED: 8302,          // VIP 已过期
  VIP_UPGRADED: 8303,         // VIP 已升级
  VIP_DOWNGRADED: 8304,       // VIP 已降级
  VIP_RENEWED: 8305,          // VIP 已续费
  VIP_POINTS_EARNED: 8400,    // 获得积分
  VIP_POINTS_USED: 8401,      // 积分已使用
  VIP_POINTS_EXPIRED: 8402    // 积分已过期
} as const

// ========== 系统通知相关消息状态 (8600-8999) ==========
export const SystemMessageStatus = {
  SYSTEM_NOTICE: 8600,        // 系统通知
  SYSTEM_MAINTENANCE: 8601,   // 系统维护
  SYSTEM_UPDATE: 8602,        // 系统更新
  SYSTEM_ERROR: 8603,         // 系统错误
  SYSTEM_WARNING: 8604,       // 系统警告
  ACCOUNT_SECURITY: 8700,     // 账户安全
  ACCOUNT_VERIFICATION: 8701, // 账户验证
  ACCOUNT_LOCKED: 8702,       // 账户已锁定
  ACCOUNT_UNLOCKED: 8703      // 账户已解锁
} as const

// ========== 预留扩展 (9000-9999) ==========
export const ReservedMessageStatus = {
  CUSTOM_1: 9000,
  CUSTOM_2: 9001,
  CUSTOM_3: 9002,
  CUSTOM_4: 9003,
  CUSTOM_5: 9004
} as const

// ========== 合并所有状态码类型 ==========
export type MessageStatusCode = 
  | typeof MessageStatus[keyof typeof MessageStatus]
  | typeof OrderMessageStatus[keyof typeof OrderMessageStatus]
  | typeof PaymentMessageStatus[keyof typeof PaymentMessageStatus]
  | typeof LogisticsMessageStatus[keyof typeof LogisticsMessageStatus]
  | typeof AfterSalesMessageStatus[keyof typeof AfterSalesMessageStatus]
  | typeof PromotionMessageStatus[keyof typeof PromotionMessageStatus]
  | typeof VipMessageStatus[keyof typeof VipMessageStatus]
  | typeof SystemMessageStatus[keyof typeof SystemMessageStatus]

// ========== 状态码工具函数 ==========
export const MessageStatusUtils = {
  /**
   * 获取状态码分类
   */
  getCategory(code: MessageStatusCode): string {
    if (code >= 1000 && code < 2000) return '基础消息'
    if (code >= 2000 && code < 3000) return '发送状态'
    if (code >= 3000 && code < 4000) return '送达状态'
    if (code >= 4000 && code < 5000) return '读取状态'
    if (code >= 5000 && code < 6000) return '失败状态'
    if (code >= 6000 && code < 6500) return '订单消息'
    if (code >= 6500 && code < 6800) return '支付消息'
    if (code >= 6800 && code < 7000) return '物流消息'
    if (code >= 7000 && code < 8000) return '售后消息'
    if (code >= 8000 && code < 8300) return '促销消息'
    if (code >= 8300 && code < 8600) return '会员消息'
    if (code >= 8600 && code < 9000) return '系统消息'
    if (code >= 9000 && code < 10000) return '自定义消息'
    return '未知消息'
  },

  /**
   * 获取状态码描述
   */
  getDescription(code: MessageStatusCode): string {
    const descriptions: Record<number, string> = {
      // 基础消息 (1000-1999)
      1000: '发送中...',
      1001: '重新发送中...',
      1002: '消息排队中',
      2000: '已发送',
      2001: '已发送到服务器',
      3000: '已送达',
      3001: '已送达手机',
      4000: '已读',
      4001: '用户已读',
      4002: '系统已读',
      5000: '发送失败',
      5001: '网络错误',
      5002: '请求超时',
      5003: '被拒绝',
      5004: '消息已过期',
      5005: '消息已删除',
      
      // 订单消息 (6000-6499)
      6000: '订单已创建',
      6001: '订单待处理',
      6002: '订单已确认',
      6003: '订单处理中',
      6004: '订单打包中',
      6005: '订单已发货',
      6006: '订单配送中',
      6007: '订单已送达',
      6008: '订单已完成',
      6009: '订单已取消',
      6010: '订单已关闭',
      6100: '订单修改中',
      6101: '订单已修改',
      6102: '订单修改被拒绝',
      6200: '订单异常',
      6201: '地址错误',
      6202: '电话错误',
      6203: '支付错误',
      6204: '库存不足',
      6205: '物流异常',
      
      // 支付消息 (6500-6799)
      6500: '待支付',
      6501: '支付处理中',
      6502: '支付成功',
      6503: '支付失败',
      6504: '支付超时',
      6505: '支付已取消',
      6506: '已退款',
      6507: '退款处理中',
      6508: '部分退款',
      
      // 物流消息 (6800-6999)
      6800: '待发货',
      6801: '已揽件',
      6802: '运输中',
      6803: '已到达城市',
      6804: '派送中',
      6805: '已签收',
      6806: '物流异常',
      6807: '已退回',
      6808: '包裹丢失',
      6809: '包裹破损',
      
      // 售后消息 (7000-7999)
      7000: '待处理退货',
      7001: '退货已批准',
      7002: '退货被拒绝',
      7003: '退货已寄出',
      7004: '退货已收到',
      7005: '退货已完成',
      7100: '待处理换货',
      7101: '换货已批准',
      7102: '换货已寄出',
      7103: '换货已收到',
      7104: '换货已完成',
      7200: '待处理维修',
      7201: '维修中',
      7202: '维修已完成',
      7203: '维修已寄回',
      7300: '待处理投诉',
      7301: '投诉处理中',
      7302: '投诉已解决',
      7303: '投诉已关闭',
      
      // 促销消息 (8000-8299)
      8000: '优惠券可用',
      8001: '优惠券已使用',
      8002: '优惠券已过期',
      8003: '优惠券已锁定',
      8100: '促销活动进行中',
      8101: '促销活动已结束',
      8102: '促销活动已暂停',
      
      // 会员消息 (8300-8599)
      8300: 'VIP 已激活',
      8301: 'VIP 即将过期',
      8302: 'VIP 已过期',
      8303: 'VIP 已升级',
      8304: 'VIP 已降级',
      8305: 'VIP 已续费',
      8400: '获得积分',
      8401: '积分已使用',
      8402: '积分已过期',
      
      // 系统消息 (8600-8999)
      8600: '系统通知',
      8601: '系统维护',
      8602: '系统更新',
      8603: '系统错误',
      8604: '系统警告',
      8700: '账户安全',
      8701: '账户验证',
      8702: '账户已锁定',
      8703: '账户已解锁'
    }
    return descriptions[code] || '未知状态'
  },

  /**
   * 获取状态码颜色
   */
  getColor(code: MessageStatusCode): string {
    if (code >= 1000 && code < 2000) return '#909399' // 灰色
    if (code >= 2000 && code < 3000) return '#67c23a' // 绿色
    if (code >= 3000 && code < 4000) return '#409eff' // 蓝色
    if (code >= 4000 && code < 5000) return '#e6a23c' // 橙色
    if (code >= 5000 && code < 6000) return '#f56c6c' // 红色
    if (code >= 6000 && code < 6500) return '#409eff' // 蓝色
    if (code >= 6500 && code < 6800) return code >= 6502 && code < 6505 ? '#67c23a' : '#f56c6c'
    if (code >= 6800 && code < 7000) return code >= 6805 ? '#67c23a' : (code >= 6806 ? '#f56c6c' : '#e6a23c')
    if (code >= 7000 && code < 8000) return '#409eff'
    if (code >= 8000 && code < 8300) return '#e6a23c'
    if (code >= 8300 && code < 8600) return '#9c27b0' // 紫色
    if (code >= 8600 && code < 9000) return code >= 8603 ? '#f56c6c' : '#909399'
    if (code >= 9000 && code < 10000) return '#606266'
    return '#909399'
  },

  /**
   * 获取状态码图标
   */
  getIcon(code: MessageStatusCode): string {
    const icons: Record<number, string> = {
      1000: 'loading',
      1001: 'loading',
      2000: 'check',
      3000: 'circle-check',
      4000: 'view',
      5000: 'warning',
      6000: 'document',
      6005: 'van',
      6008: 'circle-check',
      6502: 'money',
      6503: 'warning',
      6506: 'refresh-left',
      6805: 'circle-check',
      6806: 'warning',
      7000: 'refresh',
      7005: 'circle-check',
      8000: 'ticket',
      8300: 'star',
      8600: 'bell',
      8603: 'warning'
    }
    return icons[code] || 'info'
  }
}

// 导出所有状态码
export * from './chat'

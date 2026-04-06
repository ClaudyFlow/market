/**
 * 商家端订单管理组合式函数
 */
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import type { OrderItem, OrderFilterForm, OrderStats, Pagination, ShippingForm } from '../types/order'

/** 模拟订单数据 */
const MOCK_ORDERS: OrderItem[] = [
  { orderNo: 'DD202603180001', productImage: '', productName: '无线蓝牙耳机', customerName: '张先生', amount: 199, orderTime: '2026-03-18 10:30:00', status: 'paid' },
  { orderNo: 'DD202603180002', productImage: '', productName: '智能手环', customerName: '李女士', amount: 149, orderTime: '2026-03-18 09:15:00', status: 'pending' },
  { orderNo: 'DD202603170003', productImage: '', productName: '机械键盘', customerName: '王先生', amount: 329, orderTime: '2026-03-17 16:45:00', status: 'shipped' },
  { orderNo: 'DD202603170004', productImage: '', productName: '空气净化器', customerName: '赵女士', amount: 999, orderTime: '2026-03-17 14:20:00', status: 'completed' },
  { orderNo: 'DD202603170005', productImage: '', productName: '运动跑鞋', customerName: '刘先生', amount: 299, orderTime: '2026-03-17 11:00:00', status: 'refunding' },
  { orderNo: 'DD202603160006', productImage: '', productName: '护肤套装', customerName: '陈女士', amount: 459, orderTime: '2026-03-16 20:30:00', status: 'completed' },
  { orderNo: 'DD202603160007', productImage: '', productName: '智能手表', customerName: '杨先生', amount: 899, orderTime: '2026-03-16 15:10:00', status: 'shipped' },
  { orderNo: 'DD202603160008', productImage: '', productName: '办公椅', customerName: '周女士', amount: 599, orderTime: '2026-03-16 10:00:00', status: 'cancelled' }
]

/** 物流公司列表 */
export const LOGISTICS_COMPANIES = [
  { value: 'sf', label: '顺丰速运' },
  { value: 'zto', label: '中通快递' },
  { value: 'yto', label: '圆通速递' },
  { value: 'sto', label: '申通快递' },
  { value: 'yunda', label: '韵达快递' }
]

/** 状态映射 */
const STATUS_MAP: Record<string, { type: string; text: string }> = {
  pending: { type: 'warning', text: '待付款' },
  paid: { type: 'primary', text: '待发货' },
  shipped: { type: 'success', text: '已发货' },
  completed: { type: 'info', text: '已完成' },
  cancelled: { type: 'warning', text: '已取消' },
  refunding: { type: 'danger', text: '退款中' }
}

export function useOrderList() {
  const loading = ref(false)
  const orders = ref<OrderItem[]>([])
  
  const filterForm = reactive<OrderFilterForm>({
    orderNo: '',
    productName: '',
    status: '',
    dateRange: null
  })

  const stats = ref<OrderStats>({
    all: 1256,
    pending: 86,
    paid: 124,
    shipped: 358,
    completed: 688
  })

  const pagination = reactive<Pagination>({
    currentPage: 1,
    pageSize: 10,
    total: 0
  })

  const shippingDialog = reactive({
    visible: false,
    currentOrder: null as OrderItem | null
  })

  const shippingForm = reactive<ShippingForm>({
    company: '',
    trackingNo: '',
    remark: ''
  })

  /** 加载订单列表 */
  const loadOrders = async () => {
    loading.value = true
    try {
      // TODO: 替换为真实 API 调用
      // const res = await getMerchantOrders({ page, size, ...filterForm })
      await new Promise(resolve => setTimeout(resolve, 500))
      orders.value = MOCK_ORDERS
      pagination.total = MOCK_ORDERS.length
    } catch (error) {
      ElMessage.error('加载订单失败')
    } finally {
      loading.value = false
    }
  }

  /** 筛选订单状态 */
  const filterByStatus = (status: string) => {
    filterForm.status = status as any
    loadOrders()
  }

  /** 搜索订单 */
  const searchOrders = () => {
    ElMessage.success('搜索功能演示')
    loadOrders()
  }

  /** 重置筛选 */
  const resetFilter = () => {
    filterForm.orderNo = ''
    filterForm.productName = ''
    filterForm.status = ''
    filterForm.dateRange = null
  }

  /** 获取状态类型 */
  const getStatusType = (status: string) => {
    return STATUS_MAP[status]?.type || 'info'
  }

  /** 获取状态文本 */
  const getStatusText = (status: string) => {
    return STATUS_MAP[status]?.text || status
  }

  /** 查看订单详情 */
  const viewOrderDetail = (order: OrderItem) => {
    ElMessage.info(`查看订单详情: ${order.orderNo}`)
    // TODO: 跳转到订单详情页
    // router.push(`/merchant/order/${order.orderNo}`)
  }

  /** 发货操作 */
  const shipOrder = (order: OrderItem) => {
    shippingDialog.visible = true
    shippingDialog.currentOrder = order
    shippingForm.company = ''
    shippingForm.trackingNo = ''
    shippingForm.remark = ''
  }

  /** 确认发货 */
  const confirmShipping = async () => {
    if (!shippingForm.company || !shippingForm.trackingNo) {
      ElMessage.warning('请选择物流公司并填写物流单号')
      return
    }
    
    try {
      // TODO: 替换为真实 API 调用
      // await shipOrderAPI(shippingDialog.currentOrder!.orderNo, shippingForm)
      ElMessage.success('发货成功')
      shippingDialog.visible = false
      loadOrders()
    } catch (error) {
      ElMessage.error('发货失败')
    }
  }

  /** 查看物流 */
  const viewLogistics = (order: OrderItem) => {
    ElMessage.info(`查看物流: ${order.orderNo}`)
    // TODO: 打开物流信息弹窗
  }

  /** 处理退款 */
  const handleRefund = (order: OrderItem) => {
    ElMessage.info(`处理退款: ${order.orderNo}`)
    // TODO: 打开退款处理弹窗
  }

  /** 导出订单 */
  const exportOrders = () => {
    ElMessage.success('订单导出功能演示')
    // TODO: 调用导出 API
  }

  return {
    loading,
    orders,
    filterForm,
    stats,
    pagination,
    shippingDialog,
    shippingForm,
    loadOrders,
    filterByStatus,
    searchOrders,
    resetFilter,
    getStatusType,
    getStatusText,
    viewOrderDetail,
    shipOrder,
    confirmShipping,
    viewLogistics,
    handleRefund,
    exportOrders
  }
}

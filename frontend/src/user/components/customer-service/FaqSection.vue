<template>
  <section ref="section" class="faq-section">
    <h2><el-icon><Document /></el-icon> 常见问题</h2>
    <div class="faq-list">
      <el-collapse v-model="activeFaqs" accordion>
        <el-collapse-item title="如何申请退款？" name="1">
          <div class="faq-answer">
            <p>1. 进入「我的订单」找到对应订单</p>
            <p>2. 点击「申请售后」选择退款类型</p>
            <p>3. 填写退款原因并提交</p>
            <p>4. 等待商家审核，审核通过后退款将原路返回</p>
          </div>
        </el-collapse-item>
        <el-collapse-item title="优惠券如何使用？" name="2">
          <div class="faq-answer">
            <p>1. 在商品详情页或购物车页面选择可用优惠券</p>
            <p>2. 结算时系统会自动计算最优优惠方案</p>
            <p>3. 部分优惠券可叠加使用，具体以页面显示为准</p>
          </div>
        </el-collapse-item>
        <el-collapse-item title="如何修改收货地址？" name="3">
          <div class="faq-answer">
            <p>1. 未发货订单：进入订单详情页点击「修改地址」</p>
            <p>2. 已发货订单：请联系客服协助处理</p>
            <p>3. 建议提前确认好收货地址，避免不必要的麻烦</p>
          </div>
        </el-collapse-item>
        <el-collapse-item title="VIP 会员有什么权益？" name="4">
          <div class="faq-answer">
            <p>• 专属折扣：部分商品享受会员价</p>
            <p>• 免邮券：每月赠送免邮券</p>
            <p>• 专属客服：优先响应</p>
            <p>• 生日礼包：生日当月赠送优惠券</p>
            <p>• 积分翻倍：购物享受双倍积分</p>
          </div>
        </el-collapse-item>
        <el-collapse-item title="如何联系客服？" name="5">
          <div class="faq-answer">
            <p>• 在线客服：点击页面右上角客服图标</p>
            <p>• 电话客服：400-123-4567（工作日 9:00-18:00）</p>
            <p>• 邮件客服：support@market.com</p>
            <p>• 微信公众号：关注「Market 商城」</p>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Document } from '@element-plus/icons-vue'

const activeFaqs = ref('')
const section = ref<HTMLElement | null>(null)

defineExpose({ section })
</script>

<style scoped>
.faq-section {
  margin-bottom: 30px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.faq-section h2 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 20px;
  font-size: 18px;
  color: #1a1a1a;
}

.faq-answer p {
  margin: 8px 0;
  color: #666;
  line-height: 1.6;
}
</style>

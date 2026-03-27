<template>
  <footer class="footer">
    <div class="container">
      <!-- 服务承诺 -->
      <div class="service-bar">
        <div class="service-item">
          <el-icon><CircleCheck /></el-icon>
          <span>正品保障</span>
        </div>
        <div class="service-item">
          <el-icon><Van /></el-icon>
          <span>极速配送</span>
        </div>
        <div class="service-item">
          <el-icon><RefreshLeft /></el-icon>
          <span>7 天无理由退换</span>
        </div>
        <div class="service-item">
          <el-icon><Headset /></el-icon>
          <span>24 小时客服</span>
        </div>
      </div>

      <!-- 页脚链接 -->
      <div class="footer-links">
        <div class="footer-column">
          <h4>平台管理</h4>
          <a href="#">平台规则</a>
          <a href="#">管理规范</a>
          <a href="#">常见问题</a>
        </div>
        <div class="footer-column">
          <h4>审核管理</h4>
          <a href="#">商品审核</a>
          <a href="#">商家审核</a>
          <a href="#">评价审核</a>
        </div>
        <div class="footer-column">
          <h4>监控中心</h4>
          <a href="#">订单监控</a>
          <a href="#">数据监控</a>
          <a href="#">系统监控</a>
        </div>
        <div class="footer-column">
          <h4>联系我们</h4>
          <a href="#">平台客服</a>
          <a href="#">技术支持</a>
          <a href="#">投诉建议</a>
        </div>
        <div class="footer-column">
          <h4>关注我们</h4>
          <div class="social-icons">
            <el-icon size="24"><ChatDotRound /></el-icon>
            <el-icon size="24"><VideoCamera /></el-icon>
            <el-icon size="24"><Share /></el-icon>
          </div>
        </div>
      </div>

      <!-- 版权信息 -->
      <div class="copyright">
        <p>© 2024-2026 购物商城系统 管理端 版权所有</p>
        <p>专业的综合网上购物商城管理平台 - 为您提供高效便捷的运营工具</p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { CircleCheck, Van, RefreshLeft, Headset, ChatDotRound, VideoCamera, Share } from '@element-plus/icons-vue'
</script>

<style scoped>
.footer {
  background: rgba(10, 14, 26, 0.9);
  border-top: 1px solid rgba(0, 212, 255, 0.2);
  padding: 40px 0 20px;
  
}

.service-bar {
  display: flex;
  justify-content: center;
  gap: 60px;
  padding-bottom: 30px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
  
}

.service-item {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--mall-primary);
  font-size: 14px;
}

.service-item .el-icon {
  font-size: 28px;
  color: var(--mall-secondary);
}

.footer-links {
  display: flex;
  justify-content: center;
  gap: 80px;
  
}

.footer-column {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.footer-column h4 {
  font-size: 15px;
  color: var(--mall-primary);
  
}

.footer-column a {
  color: #888;
  text-decoration: none;
  font-size: 13px;
  transition: color 0.3s;
}

.footer-column a:hover {
  color: var(--mall-secondary);
}

.social-icons {
  display: flex;
  gap: 15px;
  color: var(--mall-primary);
}

.copyright {
  text-align: center;
  color: #666;
  font-size: 12px;
  line-height: 28px;
}

.copyright p {
  
}
</style>

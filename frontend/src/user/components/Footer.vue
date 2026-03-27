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
          <h4>购物指南</h4>
          <a href="#">购物流程</a>
          <a href="#">会员介绍</a>
          <a href="#">常见问题</a>
        </div>
        <div class="footer-column">
          <h4>配送方式</h4>
          <a href="#">配送说明</a>
          <a href="#">配送范围</a>
          <a href="#">验货与签收</a>
        </div>
        <div class="footer-column">
          <h4>售后服务</h4>
          <a href="#">退换货政策</a>
          <a href="#">退款说明</a>
          <a href="#">价格保护</a>
        </div>
        <div class="footer-column">
          <h4>关于我们</h4>
          <a href="#">公司介绍</a>
          <a href="#">联系我们</a>
          <a href="#">商务合作</a>
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
        <p>© 2024-2026 购物商城系统 版权所有</p>
        <p>专业的综合网上购物商城 - 为您提供正品低价的优质商品</p>
      </div>
    </div>
  </footer>
</template>

<script setup></script>

<style scoped>
@import '@user/assets/mall-style.css';

.footer {
  background: var(--mall-bg-medium);
  border-top: 1px solid var(--mall-border-light);
  padding: 40px 0 20px;
}

.service-bar {
  display: flex;
  justify-content: center;
  gap: 60px;
  padding-bottom: 30px;
  border-bottom: 1px solid var(--mall-border-light);
  
}

.service-item {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--mall-text-secondary);
  font-size: 14px;
}

.service-item .el-icon {
  font-size: 28px;
  color: var(--mall-primary);
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
  color: var(--mall-text-primary);
  
  font-weight: bold;
}

.footer-column a {
  color: var(--mall-text-muted);
  text-decoration: none;
  font-size: 13px;
  transition: all 0.3s;
}

.footer-column a:hover {
  color: var(--mall-primary);
  padding-left: 5px;
}

.social-icons {
  display: flex;
  gap: 15px;
  color: var(--mall-primary);
}

.social-icons .el-icon {
  cursor: pointer;
  transition: all 0.3s;
}

.social-icons .el-icon:hover {
  color: var(--mall-secondary);
  transform: scale(1.2);
}

.copyright {
  text-align: center;
  color: var(--mall-text-muted);
  font-size: 12px;
  line-height: 28px;
}

.copyright p {
  
}
</style>

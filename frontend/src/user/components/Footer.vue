<template>
  <footer class="footer">
    <div class="container">
      <!-- 服务承诺 -->
      <ServiceBar />

      <!-- 页脚链接 -->
      <div class="footer-links">
        <FooterColumn 
          title="购物指南" 
          :links="[
            { text: '购物流程', href: '#' },
            { text: '会员介绍', href: '#' },
            { text: '常见问题', href: '#' }
          ]" 
        />
        <FooterColumn 
          title="配送方式" 
          :links="[
            { text: '配送说明', href: '#' },
            { text: '配送范围', href: '#' },
            { text: '验货与签收', href: '#' }
          ]" 
        />
        <FooterColumn 
          title="售后服务" 
          :links="[
            { text: '退换货政策', href: '#' },
            { text: '退款说明', href: '#' },
            { text: '价格保护', href: '#' }
          ]" 
        />
        <FooterColumn 
          title="关于我们" 
          :links="[
            { text: '公司介绍', href: '#' },
            { text: '联系我们', href: '#' },
            { text: '商务合作', href: '#' }
          ]" 
        />
        <FooterColumn title="关注我们">
          <SocialLinks />
        </FooterColumn>
      </div>

      <!-- 版权信息 -->
      <Copyright />
    </div>
  </footer>
</template>

<script setup>
import ServiceBar from './footer/ServiceBar.vue'
import FooterColumn from './footer/FooterColumn.vue'
import SocialLinks from './footer/SocialLinks.vue'
import Copyright from './footer/Copyright.vue'
</script>

<style scoped>
@import '@user/assets/mall-style.css';

.footer {
  background: var(--mall-bg-medium);
  border-top: 1px solid var(--mall-border-light);
  padding: 40px 0 20px;
  margin-top: 60px;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.footer-links {
  display: flex;
  justify-content: center;
  gap: 80px;
}
</style>

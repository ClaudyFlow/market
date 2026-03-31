package com.market.config;

import com.market.entity.User;
import com.market.entity.Product;
import com.market.entity.Coupon;
import com.market.entity.VipLevel;
import com.market.entity.SystemMessage;
import com.market.repository.UserRepository;
import com.market.repository.ProductRepository;
import com.market.repository.CouponRepository;
import com.market.repository.VipLevelRepository;
import com.market.repository.SystemMessageRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 数据库初始化配置
 * 仅在 H2 环境下启动时初始化基础数据
 */
@Configuration
@Profile("h2")
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            ProductRepository productRepository,
            CouponRepository couponRepository,
            VipLevelRepository vipLevelRepository,
            SystemMessageRepository systemMessageRepository,
            PasswordEncoder passwordEncoder) {
        
        return args -> {
            // 1. 创建管理员账户
            User admin = new User("admin", "admin@market.com", passwordEncoder.encode("123456"));
            admin.setRole("ADMIN");
            admin.setStatus("ACTIVE");
            userRepository.save(admin);

            // 2. 创建测试商家
            User merchant = new User("merchant1", "merchant@test.com", passwordEncoder.encode("123456"));
            merchant.setRole("MERCHANT");
            merchant.setStatus("ACTIVE");
            merchant.setIsMerchant(true);
            merchant.setMerchantStatus("ACTIVE");
            merchant.setShopName("测试店铺");
            merchant.setShopDescription("优质商品供应商");
            userRepository.save(merchant);

            // 3. 创建测试用户
            User user = new User("user1", "user@test.com", passwordEncoder.encode("123456"));
            user.setRole("USER");
            user.setStatus("ACTIVE");
            userRepository.save(user);

            // 4. 创建测试商品
            Product p1 = new Product();
            p1.setName("无线蓝牙耳机");
            p1.setDescription("高品质音质，降噪设计");
            p1.setPrice(new BigDecimal("199.00"));
            p1.setOriginalPrice(new BigDecimal("299.00"));
            p1.setStock(500);
            p1.setCategory("手机数码");
            p1.setBrand("BrandA");
            p1.setImageUrl("/images/product1.jpg");
            p1.setStatus(1);
            p1.setAuditStatus(1);
            p1.setMerchant(merchant);
            p1.setUser(user);
            productRepository.save(p1);

            Product p2 = new Product();
            p2.setName("智能手环");
            p2.setDescription("运动健康监测，长续航");
            p2.setPrice(new BigDecimal("149.00"));
            p2.setOriginalPrice(new BigDecimal("199.00"));
            p2.setStock(300);
            p2.setCategory("手机数码");
            p2.setBrand("BrandB");
            p2.setImageUrl("/images/product2.jpg");
            p2.setStatus(1);
            p2.setAuditStatus(1);
            p2.setMerchant(merchant);
            p2.setUser(user);
            productRepository.save(p2);

            Product p3 = new Product();
            p3.setName("机械键盘");
            p3.setDescription("RGB 背光，青轴手感");
            p3.setPrice(new BigDecimal("329.00"));
            p3.setOriginalPrice(new BigDecimal("429.00"));
            p3.setStock(200);
            p3.setCategory("电脑办公");
            p3.setBrand("BrandC");
            p3.setImageUrl("/images/product3.jpg");
            p3.setStatus(1);
            p3.setAuditStatus(1);
            p3.setMerchant(merchant);
            p3.setUser(user);
            productRepository.save(p3);

            Product p4 = new Product();
            p4.setName("空气净化器");
            p4.setDescription("除甲醛 PM2.5，静音设计");
            p4.setPrice(new BigDecimal("999.00"));
            p4.setOriginalPrice(new BigDecimal("1299.00"));
            p4.setStock(100);
            p4.setCategory("家用电器");
            p4.setBrand("BrandD");
            p4.setImageUrl("/images/product4.jpg");
            p4.setStatus(1);
            p4.setAuditStatus(1);
            p4.setMerchant(merchant);
            p4.setUser(user);
            productRepository.save(p4);

            Product p5 = new Product();
            p5.setName("运动跑鞋");
            p5.setDescription("轻便透气，减震舒适");
            p5.setPrice(new BigDecimal("299.00"));
            p5.setOriginalPrice(new BigDecimal("399.00"));
            p5.setStock(0);
            p5.setCategory("服装鞋包");
            p5.setBrand("BrandE");
            p5.setImageUrl("/images/product5.jpg");
            p5.setStatus(0);
            p5.setAuditStatus(1);
            p5.setMerchant(merchant);
            p5.setUser(user);
            productRepository.save(p5);

            // 5. 创建测试优惠券
            Coupon c1 = new Coupon();
            c1.setName("新人专享券");
            c1.setType("FIXED");
            c1.setDiscountValue(new BigDecimal("20.00"));
            c1.setMinPurchase(new BigDecimal("100.00"));
            c1.setTotalCount(1000);
            c1.setRemainCount(800);
            c1.setUsedCount(200);
            c1.setStatus("ACTIVE");
            c1.setValidFrom(LocalDateTime.now());
            c1.setValidTo(LocalDateTime.now().plusDays(30));
            c1.setScope("ALL");
            c1.setDescription("新用户专享满减券");
            couponRepository.save(c1);

            Coupon c2 = new Coupon();
            c2.setName("全场 8 折券");
            c2.setType("PERCENT");
            c2.setDiscountValue(new BigDecimal("0.80"));
            c2.setMinPurchase(new BigDecimal("200.00"));
            c2.setMaxDiscount(new BigDecimal("50.00"));
            c2.setTotalCount(500);
            c2.setRemainCount(400);
            c2.setUsedCount(100);
            c2.setStatus("ACTIVE");
            c2.setValidFrom(LocalDateTime.now());
            c2.setValidTo(LocalDateTime.now().plusDays(15));
            c2.setScope("ALL");
            c2.setDescription("全场商品 8 折优惠");
            couponRepository.save(c2);

            Coupon c3 = new Coupon();
            c3.setName("店铺满减券");
            c3.setType("FIXED");
            c3.setDiscountValue(new BigDecimal("50.00"));
            c3.setMinPurchase(new BigDecimal("300.00"));
            c3.setTotalCount(200);
            c3.setRemainCount(150);
            c3.setUsedCount(50);
            c3.setStatus("ACTIVE");
            c3.setValidFrom(LocalDateTime.now());
            c3.setValidTo(LocalDateTime.now().plusDays(60));
            c3.setScope("SHOP");
            c3.setMerchant(merchant);
            c3.setDescription("店铺专享满减券");
            couponRepository.save(c3);

            // 6. 创建 VIP 等级配置
            createVipLevel(vipLevelRepository, 0, "普通会员", "基础会员等级", 0, 1.00, 5, 100, 0);
            createVipLevel(vipLevelRepository, 1, "白银会员", "进阶会员，享受更多权益", 1000, 0.98, 10, 200, 2);
            createVipLevel(vipLevelRepository, 2, "黄金会员", "高级会员，专属优惠", 5000, 0.95, 20, 500, 5);
            createVipLevel(vipLevelRepository, 3, "铂金会员", "尊贵会员，优先服务", 20000, 0.90, 50, 1000, 10);
            createVipLevel(vipLevelRepository, 4, "钻石会员", "顶级会员，至尊体验", 100000, 0.85, 100, 2000, 20);
            createVipLevel(vipLevelRepository, 5, "至尊会员", "限量版会员，专属定制", 500000, 0.80, 200, 5000, 50);

            // 7. 创建系统消息
            SystemMessage msg1 = new SystemMessage();
            msg1.setTitle("欢迎注册 Market 平台");
            msg1.setContent("感谢您注册 Market 平台，祝您购物愉快！");
            msg1.setType("SYSTEM");
            msg1.setPriority(1);
            msg1.setIsBroadcast(true);
            msg1.setSendTime(LocalDateTime.now());
            systemMessageRepository.save(msg1);

            SystemMessage msg2 = new SystemMessage();
            msg2.setTitle("新用户优惠券已发放");
            msg2.setContent("您已获得新人专享优惠券，快去选购吧！");
            msg2.setType("COUPON");
            msg2.setPriority(2);
            msg2.setIsBroadcast(false);
            msg2.setSendTime(LocalDateTime.now());
            msg2.setJumpUrl("/coupon");
            systemMessageRepository.save(msg2);

            System.out.println("===========================================");
            System.out.println("数据库初始化完成！");
            System.out.println("测试账号:");
            System.out.println("  管理员：admin / 123456");
            System.out.println("  商家：merchant1 / 123456");
            System.out.println("  用户：user1 / 123456");
            System.out.println("===========================================");
        };
    }

    private void createVipLevel(VipLevelRepository repo, int level, String name, String desc, 
                                int growthValue, double discount, int dailyCredit, 
                                int monthlyCredit, int freeShipping) {
        VipLevel vip = new VipLevel();
        vip.setLevel(level);
        vip.setName(name);
        vip.setDescription(desc);
        vip.setGrowthValueRequired(growthValue);
        vip.setDiscountRate(new BigDecimal(discount));
        vip.setDailyCredit(dailyCredit);
        vip.setMonthlyCredit(monthlyCredit);
        vip.setFreeShippingCount(freeShipping);
        vip.setRefundPriority(level >= 2);
        vip.setExclusiveService(level >= 3);
        repo.save(vip);
    }
}

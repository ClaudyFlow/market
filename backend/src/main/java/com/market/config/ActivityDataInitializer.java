package com.market.config;

import com.market.entity.Activity;
import com.market.repository.ActivityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 活动数据初始化配置
 * 仅在非 test profile 时初始化活动数据
 */
@Configuration
@Profile("!test & !h2")
public class ActivityDataInitializer {

    @Bean
    CommandLineRunner initActivities(ActivityRepository activityRepository) {
        return args -> {
            if (activityRepository.count() > 0) {
                return;
            }

            LocalDateTime now = LocalDateTime.now();

            Activity flashSale = new Activity();
            flashSale.setName("限时秒杀");
            flashSale.setType("FLASH_SALE");
            flashSale.setDescription("热门商品限时秒杀，超低价格");
            flashSale.setStatus("ACTIVE");
            flashSale.setStartTime(now);
            flashSale.setEndTime(now.plusDays(7));
            flashSale.setDiscount(new BigDecimal("0.50"));
            flashSale.setDiscountType("PERCENT");
            flashSale.setMaxQuantity(100);
            flashSale.setUsedQuantity(23);
            flashSale.setMaxPerUser(1);
            flashSale.setSortOrder(1);
            activityRepository.save(flashSale);

            Activity discount = new Activity();
            discount.setName("夏日折扣");
            discount.setType("DISCOUNT");
            discount.setDescription("夏季商品全面打折");
            discount.setStatus("ACTIVE");
            discount.setStartTime(now.minusDays(3));
            discount.setEndTime(now.plusDays(30));
            discount.setDiscount(new BigDecimal("0.80"));
            discount.setDiscountType("PERCENT");
            discount.setMaxQuantity(500);
            discount.setUsedQuantity(156);
            discount.setMaxPerUser(3);
            discount.setSortOrder(2);
            activityRepository.save(discount);

            Activity fullReduce = new Activity();
            fullReduce.setName("满减活动");
            fullReduce.setType("FULL_REDUCE");
            fullReduce.setDescription("满300减50，满500减100");
            fullReduce.setStatus("ACTIVE");
            fullReduce.setStartTime(now);
            fullReduce.setEndTime(now.plusDays(60));
            fullReduce.setDiscount(new BigDecimal("50.00"));
            fullReduce.setDiscountType("FIXED");
            fullReduce.setMaxQuantity(1000);
            fullReduce.setUsedQuantity(234);
            fullReduce.setMaxPerUser(5);
            fullReduce.setSortOrder(3);
            activityRepository.save(fullReduce);

            Activity groupBuy = new Activity();
            groupBuy.setName("拼团活动");
            groupBuy.setType("GROUP_BUY");
            groupBuy.setDescription("邀请好友一起拼团，享受更低价格");
            groupBuy.setStatus("ACTIVE");
            groupBuy.setStartTime(now);
            groupBuy.setEndTime(now.plusDays(14));
            groupBuy.setMaxQuantity(200);
            groupBuy.setUsedQuantity(45);
            groupBuy.setMaxPerUser(2);
            groupBuy.setSortOrder(4);
            activityRepository.save(groupBuy);

            Activity lottery = new Activity();
            lottery.setName("幸运抽奖");
            lottery.setType("LOTTERY");
            lottery.setDescription("每日抽奖，惊喜好礼等你拿");
            lottery.setStatus("ACTIVE");
            lottery.setStartTime(now);
            lottery.setEndTime(now.plusDays(30));
            lottery.setMaxQuantity(1000);
            lottery.setUsedQuantity(567);
            lottery.setMaxPerUser(1);
            lottery.setSortOrder(5);
            activityRepository.save(lottery);

            Activity vipDay = new Activity();
            vipDay.setName("VIP会员日");
            vipDay.setType("VIP_DAY");
            vipDay.setDescription("VIP会员专享优惠日");
            vipDay.setStatus("ACTIVE");
            vipDay.setStartTime(now);
            vipDay.setEndTime(now.plusDays(7));
            vipDay.setDiscount(new BigDecimal("0.85"));
            vipDay.setDiscountType("PERCENT");
            vipDay.setMaxQuantity(300);
            vipDay.setUsedQuantity(89);
            vipDay.setMaxPerUser(2);
            vipDay.setSortOrder(0);
            activityRepository.save(vipDay);

            System.out.println("活动数据初始化完成，共创建 " + activityRepository.count() + " 个活动");
        };
    }
}
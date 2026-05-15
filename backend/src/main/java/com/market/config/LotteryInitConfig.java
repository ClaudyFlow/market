package com.market.config;

import com.market.entity.LotteryPrize;
import com.market.repository.LotteryPrizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 抽奖奖品初始化配置
 */
@Configuration
public class LotteryInitConfig {

    @Autowired
    private LotteryPrizeRepository prizeRepository;

    @Bean
    public CommandLineRunner initLotteryPrizes() {
        return args -> {
            if (prizeRepository.count() == 0) {
                // 积分奖品（3 个）
                savePrize("50 积分", "奖励 50 积分", 1, 30);
                savePrize("75 积分", "奖励 75 积分", 1, 20);
                savePrize("100 积分", "奖励 100 积分", 1, 10);
                
                // 实物奖品（5 个，共 8 个奖品）
                savePrize("抽纸（一袋）", "家用抽纸，柔软舒适", 2, 15);
                savePrize("洗衣液（一瓶）", "深层洁净，持久留香", 2, 15);
                savePrize("电饭锅（一个）", "智能电饭煲，多功能烹饪", 2, 5);
                savePrize("电脑（一台）", "高性能游戏电脑", 2, 1);
                savePrize("人类下一颗类地行星命名权（遥遥无期）", "联合国并不承认", 2, 1);
                
                System.out.println("抽奖奖品初始化完成！");
            }
        };
    }

    private void savePrize(String name, String description, int type, int weight) {
        LotteryPrize prize = new LotteryPrize();
        prize.setName(name);
        prize.setDescription(description);
        prize.setType(type);
        prize.setWeight(weight);
        prize.setAvailable(true);
        prizeRepository.save(prize);
    }
}

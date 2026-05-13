package com.market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot 应用启动类
 * 整个二手市场交易平台的入口点
 *
 * @author market-team
 * @since 1.0
 */
@SpringBootApplication
@EnableScheduling
public class MarketApplication {
    /**
     * 应用入口方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(MarketApplication.class, args);
    }
}

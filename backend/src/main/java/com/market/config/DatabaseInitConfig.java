package com.market.config;

import com.market.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据库初始化配置
 */
@Configuration
public class DatabaseInitConfig {
    
    @Autowired
    private ProductService productService;
    
    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            System.out.println("正在初始化示例商品数据...");
            productService.seedSampleData();
            System.out.println("示例商品数据初始化完成！");
        };
    }
}

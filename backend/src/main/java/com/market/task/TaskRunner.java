package com.market.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.market.service.ScheduledService;

/**
 * 任务启动器
 * 实现 CommandLineRunner 接口，在 Spring Boot 应用启动完成后执行初始化任务
 *
 * @author market-team
 * @since 1.0
 */
@Component
public class TaskRunner implements CommandLineRunner {

    @Autowired
    private ScheduledService scheduledService;

    /**
     * 应用启动后执行的方法
     *
     * @param args 命令行参数
     */
    @Override
    public void run(String... args) {
        // 启动时执行一次初始化任务
        System.out.println("系统启动完成，定时任务已就绪");
    }
}

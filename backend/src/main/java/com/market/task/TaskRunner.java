package com.market.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.market.service.ScheduledService;

/**
 * 任务启动器
 */
@Component
public class TaskRunner implements CommandLineRunner {

    @Autowired
    private ScheduledService scheduledService;

    @Override
    public void run(String... args) {
        // 启动时执行一次初始化任务
        System.out.println("系统启动完成，定时任务已就绪");
    }
}

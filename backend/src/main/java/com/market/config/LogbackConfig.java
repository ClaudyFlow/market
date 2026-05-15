package com.market.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.File;

/**
 * Logback 日志配置
 * 配置日志路径和格式
 */
@Configuration
public class LogbackConfig {

    @PostConstruct
    public void init() {
        // 确保 log 目录存在
        File logDir = new File("log");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        // 设置日志级别
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger("ROOT");
        rootLogger.setLevel(ch.qos.logback.classic.Level.INFO);
        
        ch.qos.logback.classic.Logger appLogger = loggerContext.getLogger("com.market");
        appLogger.setLevel(ch.qos.logback.classic.Level.DEBUG);
    }
}

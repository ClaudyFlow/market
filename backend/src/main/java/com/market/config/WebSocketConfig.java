package com.market.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 配置类
 * 启用 STOMP 协议支持客服聊天功能
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册 WebSocket 端点，前端通过此端点连接
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();  // 启用 SockJS 回退，兼容不支持 WebSocket 的浏览器
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 启用简单的内存消息代理
        registry.enableSimpleBroker("/topic", "/queue", "/user");
        // 设置客户端发送消息的目标前缀
        registry.setApplicationDestinationPrefixes("/app");
        // 设置用户私有消息的前缀
        registry.setUserDestinationPrefix("/user");
    }
}

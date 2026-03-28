package com.market.service;

import com.market.entity.MessageReceive;
import com.market.entity.SystemMessage;
import com.market.entity.User;
import com.market.repository.MessageReceiveRepository;
import com.market.repository.SystemMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息通知服务类
 */
@Service
public class MessageService {

    @Autowired
    private SystemMessageRepository systemMessageRepository;

    @Autowired
    private MessageReceiveRepository messageReceiveRepository;

    /**
     * 发送系统消息
     */
    @Transactional
    public SystemMessage sendMessage(String title, String content, String type,
                                      Integer priority, String jumpUrl, String imageUrl) {
        SystemMessage message = new SystemMessage();
        message.setTitle(title);
        message.setContent(content);
        message.setType(type);
        message.setPriority(priority != null ? priority : 3);
        message.setJumpUrl(jumpUrl);
        message.setImageUrl(imageUrl);
        message.setIsBroadcast(true);
        message.setSendTime(LocalDateTime.now());

        return systemMessageRepository.save(message);
    }

    /**
     * 发送消息给指定用户
     */
    @Transactional
    public SystemMessage sendMessageToUser(String title, String content, String type,
                                            Long userId, Integer priority, String jumpUrl) {
        SystemMessage message = new SystemMessage();
        message.setTitle(title);
        message.setContent(content);
        message.setType(type);
        message.setPriority(priority != null ? priority : 3);
        message.setJumpUrl(jumpUrl);
        message.setIsBroadcast(false);
        message.setTargetUserIds(userId.toString());
        message.setSendTime(LocalDateTime.now());

        SystemMessage savedMessage = systemMessageRepository.save(message);

        // 创建接收记录
        MessageReceive receive = new MessageReceive(userId, savedMessage.getId());
        messageReceiveRepository.save(receive);

        return savedMessage;
    }

    /**
     * 发送消息给多个用户
     */
    @Transactional
    public SystemMessage sendMessageToUsers(String title, String content, String type,
                                             List<Long> userIds, Integer priority, String jumpUrl) {
        SystemMessage message = new SystemMessage();
        message.setTitle(title);
        message.setContent(content);
        message.setType(type);
        message.setPriority(priority != null ? priority : 3);
        message.setJumpUrl(jumpUrl);
        message.setIsBroadcast(false);

        // 用户 ID 逗号分隔
        StringBuilder targetIds = new StringBuilder();
        for (int i = 0; i < userIds.size(); i++) {
            if (i > 0) targetIds.append(",");
            targetIds.append(userIds.get(i));
        }
        message.setTargetUserIds(targetIds.toString());
        message.setSendTime(LocalDateTime.now());

        SystemMessage savedMessage = systemMessageRepository.save(message);

        // 创建接收记录
        for (Long userId : userIds) {
            MessageReceive receive = new MessageReceive(userId, savedMessage.getId());
            messageReceiveRepository.save(receive);
        }

        return savedMessage;
    }

    /**
     * 获取用户的消息列表
     */
    public Page<MessageReceive> getUserMessages(User user, Pageable pageable) {
        // 先同步广播消息
        syncBroadcastMessages(user);

        return messageReceiveRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
    }

    /**
     * 获取用户的未读消息
     */
    public Page<MessageReceive> getUnreadMessages(User user, Pageable pageable) {
        syncBroadcastMessages(user);
        return messageReceiveRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId(), pageable);
    }

    /**
     * 获取未读消息数量
     */
    public long getUnreadCount(User user) {
        syncBroadcastMessages(user);
        return messageReceiveRepository.countByUserIdAndIsReadFalse(user.getId());
    }

    /**
     * 标记消息为已读
     */
    @Transactional
    public int markAsRead(User user, List<Long> receiveIds) {
        return messageReceiveRepository.markAsRead(user.getId(), receiveIds);
    }

    /**
     * 标记所有消息为已读
     */
    @Transactional
    public int markAllAsRead(User user) {
        return messageReceiveRepository.markAllAsRead(user.getId());
    }

    /**
     * 获取消息详情
     */
    public SystemMessage getMessageDetail(Long messageId) {
        return systemMessageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("消息不存在"));
    }

    /**
     * 同步广播消息给用户
     */
    private void syncBroadcastMessages(User user) {
        // 获取所有广播消息
        List<SystemMessage> broadcastMessages = systemMessageRepository.findAll();

        for (SystemMessage message : broadcastMessages) {
            // 检查用户是否已有该消息
            boolean exists = messageReceiveRepository.existsByUserIdAndMessageId(user.getId(), message.getId());
            if (!exists && message.getSendTime().isBefore(LocalDateTime.now())) {
                // 创建接收记录
                MessageReceive receive = new MessageReceive(user.getId(), message.getId());
                messageReceiveRepository.save(receive);
            }
        }
    }

    /**
     * 定时清理过期消息（每天凌晨 2 点执行）
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanOldMessages() {
        LocalDateTime beforeTime = LocalDateTime.now().minusDays(90);
        int deleted = systemMessageRepository.deleteOldMessages(beforeTime);
        System.out.println("清理了 " + deleted + " 条过期消息");
    }

    /**
     * 获取消息统计
     */
    public Map<String, Object> getMessageStats(User user) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", messageReceiveRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), Pageable.ofSize(1)).getTotalElements());
        stats.put("unread", getUnreadCount(user));
        return stats;
    }
}

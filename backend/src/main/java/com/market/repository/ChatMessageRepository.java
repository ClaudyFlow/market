package com.market.repository;

import com.market.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 聊天消息数据访问接口
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * 分页查询两个用户之间的聊天记录
     */
    Page<ChatMessage> findBySenderIdAndReceiverIdOrderByCreatedAtDesc(
        Long senderId, Long receiverId, Pageable pageable);

    /**
     * 查询两个用户之间的所有聊天记录（按时间排序）
     */
    List<ChatMessage> findBySenderIdAndReceiverIdOrderByCreatedAtAsc(
        Long senderId, Long receiverId);

    /**
     * 查询用户收到的未读消息
     */
    List<ChatMessage> findByReceiverIdAndIsRead(Long receiverId, Boolean isRead);

    /**
     * 查询用户收到的未读消息数量
     */
    long countByReceiverIdAndIsRead(Long receiverId, Boolean isRead);

    /**
     * 批量标记消息为已读
     */
    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.receiverId = :receiverId AND m.id IN :messageIds")
    int markAsRead(@Param("receiverId") Long receiverId, @Param("messageIds") List<Long> messageIds);

    /**
     * 批量标记某个发送者的消息为已读
     */
    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.receiverId = :receiverId AND m.senderId = :senderId")
    int markAllAsRead(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);

    /**
     * 查询两个用户之间最新的聊天记录
     */
    @Query("SELECT m FROM ChatMessage m WHERE " +
           "(m.senderId = :user1Id AND m.receiverId = :user2Id) OR " +
           "(m.senderId = :user2Id AND m.receiverId = :user1Id) " +
           "ORDER BY m.createdAt DESC")
    Page<ChatMessage> findConversation(@Param("user1Id") Long user1Id,
                                       @Param("user2Id") Long user2Id,
                                       Pageable pageable);
}

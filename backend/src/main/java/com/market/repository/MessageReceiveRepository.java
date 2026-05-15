package com.market.repository;

import com.market.entity.MessageReceive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 消息接收记录数据访问层
 * 对应实体：MessageReceive
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface MessageReceiveRepository extends JpaRepository<MessageReceive, Long> {

    /**
     * 获取用户的消息接收记录（分页）
     */
    Page<MessageReceive> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 获取用户的未读消息
     */
    Page<MessageReceive> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 获取用户的未读消息数量
     */
    long countByUserIdAndIsReadFalse(Long userId);

    /**
     * 检查用户是否已接收某消息
     */
    boolean existsByUserIdAndMessageId(Long userId, Long messageId);

    /**
     * 批量标记消息为已读
     */
    @Modifying
    @Query("UPDATE MessageReceive r SET r.isRead = true, r.readAt = CURRENT_TIMESTAMP " +
           "WHERE r.userId = :userId AND r.id IN :receiveIds")
    int markAsRead(@Param("userId") Long userId, @Param("receiveIds") List<Long> receiveIds);

    /**
     * 标记用户的所有消息为已读
     */
    @Modifying
    @Query("UPDATE MessageReceive r SET r.isRead = true, r.readAt = CURRENT_TIMESTAMP " +
           "WHERE r.userId = :userId AND r.isRead = false")
    int markAllAsRead(@Param("userId") Long userId);

    /**
     * 删除用户的消息
     */
    void deleteByUserId(Long userId);

    /**
     * 删除指定消息的接收记录
     */
    void deleteByMessageId(Long messageId);
}

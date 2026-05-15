package com.market.repository;

import com.market.entity.SystemMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统消息数据访问层
 * 对应实体：SystemMessage
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface SystemMessageRepository extends JpaRepository<SystemMessage, Long> {

    /**
     * 获取用户可接收的消息（分页）
     */
    @Query("SELECT m FROM SystemMessage m WHERE m.isBroadcast = true " +
           "OR m.targetUserIds LIKE CONCAT('%', :userId, '%') " +
           "ORDER BY m.priority DESC, m.sendTime DESC")
    Page<SystemMessage> findUserMessages(@Param("userId") String userId, Pageable pageable);

    /**
     * 获取用户的未读消息数量
     */
    @Query("SELECT COUNT(r) FROM MessageReceive r WHERE r.userId = :userId AND r.isRead = false")
    long countUnreadMessages(@Param("userId") Long userId);

    /**
     * 获取指定类型的消息
     */
    Page<SystemMessage> findByTypeAndIsBroadcast(String type, Boolean isBroadcast, Pageable pageable);

    /**
     * 获取指定时间之后的消息
     */
    @Query("SELECT m FROM SystemMessage m WHERE m.sendTime > :sinceTime ORDER BY m.sendTime DESC")
    List<SystemMessage> findRecentMessages(@Param("sinceTime") LocalDateTime sinceTime, Pageable pageable);

    /**
     * 删除过期消息
     */
    @Query("DELETE FROM SystemMessage m WHERE m.sendTime < :beforeTime")
    int deleteOldMessages(@Param("beforeTime") LocalDateTime beforeTime);
}

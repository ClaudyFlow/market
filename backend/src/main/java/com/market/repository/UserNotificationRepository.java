package com.market.repository;

import com.market.entity.UserNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户通知消息数据访问层
 * 对应实体：UserNotification
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    /**
     * 获取用户的通知列表（分页）
     *
     * @param userId 用户 ID
     * @param pageable 分页参数
     * @return 通知列表分页
     */
    Page<UserNotification> findByUserId(Long userId, Pageable pageable);

    /**
     * 获取用户的通知列表
     *
     * @param userId 用户 ID
     * @return 通知列表
     */
    List<UserNotification> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 获取用户的未读通知数量
     *
     * @param userId 用户 ID
     * @return 未读通知数量
     */
    long countByUserIdAndIsReadFalse(Long userId);

    /**
     * 获取用户的通知数量
     *
     * @param userId 用户 ID
     * @return 通知数量
     */
    long countByUserId(Long userId);

    /**
     * 获取用户指定类型的通知
     *
     * @param userId 用户 ID
     * @param type 消息类型
     * @param pageable 分页参数
     * @return 通知列表分页
     */
    Page<UserNotification> findByUserIdAndType(Long userId, String type, Pageable pageable);

    /**
     * 获取用户的未读通知
     *
     * @param userId 用户 ID
     * @param pageable 分页参数
     * @return 未读通知列表分页
     */
    Page<UserNotification> findByUserIdAndIsReadFalse(Long userId, Pageable pageable);

    /**
     * 批量标记通知为已读
     *
     * @param notificationIds 通知 ID 列表
     * @return 更新的记录数
     */
    @Modifying
    @Query("UPDATE UserNotification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP " +
           "WHERE n.id IN :notificationIds")
    int markAsRead(@Param("notificationIds") List<Long> notificationIds);

    /**
     * 标记用户的所有通知为已读
     *
     * @param userId 用户 ID
     * @return 更新的记录数
     */
    @Modifying
    @Query("UPDATE UserNotification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP " +
           "WHERE n.userId = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") Long userId);

    /**
     * 删除用户的通知
     *
     * @param userId 用户 ID
     */
    void deleteByUserId(Long userId);

    /**
     * 删除指定时间之前的通知
     *
     * @param userId 用户 ID
     * @param createdAt 创建时间
     * @return 删除的记录数
     */
    @Modifying
    @Query("DELETE FROM UserNotification n WHERE n.userId = :userId AND n.createdAt < :createdAt")
    int deleteOldNotifications(@Param("userId") Long userId,
                                @Param("createdAt") java.time.LocalDateTime createdAt);

    /**
     * 获取用户指定类型的未读通知数量
     *
     * @param userId 用户 ID
     * @param type 通知类型
     * @param isRead 是否已读
     * @return 未读通知数量
     */
    long countByUserIdAndTypeAndIsReadFalse(Long userId, String type, boolean isRead);
}

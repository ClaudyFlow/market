package com.market.repository;

import com.market.entity.CreditHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 积分历史数据访问接口
 * <p>
 * 提供积分历史记录的数据访问操作，包括基本的CRUD操作和自定义统计查询方法。
 * 继承JpaRepository以获得标准的JPA数据访问功能。
 * </p>
 *
 * @author Market Team
 * @since 1.0.0
 */
@Repository
public interface CreditHistoryRepository extends JpaRepository<CreditHistory, Long> {

    /**
     * 查询用户的所有积分历史记录
     * 按创建时间倒序排列，最新的记录在前
     *
     * @param userId 用户ID
     * @return 积分历史记录列表，按时间倒序排列
     */
    List<CreditHistory> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 查询用户积分变化的总量
     * 计算用户所有积分变化的总和（包括增加和扣除）
     *
     * @param userId 用户ID
     * @return 积分变化总量
     */
    @Query("SELECT COALESCE(SUM(ph.creditChange), 0) FROM CreditHistory ph WHERE ph.userId = :userId")
    Integer getTotalCreditChange(@Param("userId") Long userId);

    /**
     * 统计用户积分获得总量
     * 计算用户所有正向积分变化的总和（只计算增加的积分）
     *
     * @param userId 用户ID
     * @return 积分获得总量
     */
    @Query("SELECT COALESCE(SUM(ph.creditChange), 0) FROM CreditHistory ph WHERE ph.userId = :userId AND ph.creditChange > 0")
    Integer getTotalCreditEarned(@Param("userId") Long userId);

    /**
     * 统计用户积分兑换总量
     * 计算用户所有负向积分变化的绝对值总和（只计算扣除的积分）
     *
     * @param userId 用户ID
     * @return 积分兑换总量
     */
    @Query("SELECT COALESCE(SUM(ABS(ph.creditChange)), 0) FROM CreditHistory ph WHERE ph.userId = :userId AND ph.creditChange < 0")
    Integer getTotalCreditRedeemed(@Param("userId") Long userId);
}
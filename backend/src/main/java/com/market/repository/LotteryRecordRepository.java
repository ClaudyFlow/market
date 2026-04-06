package com.market.repository;

import com.market.entity.LotteryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 抽奖记录数据访问层
 * 对应实体：LotteryRecord
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface LotteryRecordRepository extends JpaRepository<LotteryRecord, Long> {

    /**
     * 根据用户ID查询抽奖记录列表，按创建时间倒序
     *
     * @param userId 用户ID
     * @return 抽奖记录列表
     */
    List<LotteryRecord> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 统计用户抽奖记录数量
     *
     * @param userId 用户ID
     * @return 抽奖记录数量
     */
    Long countByUserId(Long userId);

    /**
     * 统计用户保底奖品数量（抽纸和洗衣液）
     *
     * @param userId 用户ID
     * @return 保底奖品数量
     */
    @Query("SELECT COUNT(r) FROM LotteryRecord r WHERE r.userId = ?1 AND r.prizeType = 2 AND r.prizeName IN ('抽纸（一袋）', '洗衣液（一瓶）')")
    Long countGuaranteedPrizes(Long userId);

    /**
     * 查询用户当天的抽奖次数
     *
     * @param userId 用户ID
     * @return 当天抽奖次数
     */
    @Query("SELECT COUNT(r) FROM LotteryRecord r WHERE r.userId = ?1 AND r.createdAt >= CURRENT_DATE")
    Long countTodayByUserId(Long userId);
}

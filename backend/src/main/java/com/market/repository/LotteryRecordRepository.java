package com.market.repository;

import com.market.entity.LotteryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 抽奖记录数据访问接口
 */
@Repository
public interface LotteryRecordRepository extends JpaRepository<LotteryRecord, Long> {
    List<LotteryRecord> findByUserIdOrderByCreatedAtDesc(Long userId);

    Long countByUserId(Long userId);

    @Query("SELECT COUNT(r) FROM LotteryRecord r WHERE r.userId = ?1 AND r.prizeType = 2 AND r.prizeName IN ('抽纸（一袋）', '洗衣液（一瓶）')")
    Long countGuaranteedPrizes(Long userId);

    /**
     * 查询用户当天的抽奖次数
     * @param userId 用户 ID
     * @return 当天抽奖次数
     */
    @Query("SELECT COUNT(r) FROM LotteryRecord r WHERE r.userId = ?1 AND r.createdAt >= CURRENT_DATE")
    Long countTodayByUserId(Long userId);
}

package com.market.repository;

import com.market.entity.LotteryPrize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 抽奖奖品数据访问接口
 */
@Repository
public interface LotteryPrizeRepository extends JpaRepository<LotteryPrize, Long> {
    List<LotteryPrize> findByAvailableTrue();
}

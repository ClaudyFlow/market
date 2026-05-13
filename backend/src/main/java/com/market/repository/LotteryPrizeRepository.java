package com.market.repository;

import com.market.entity.LotteryPrize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 抽奖奖品数据访问层
 * 对应实体：LotteryPrize
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface LotteryPrizeRepository extends JpaRepository<LotteryPrize, Long> {

    /**
     * 查询所有可用的奖品列表
     *
     * @return 可用奖品列表
     */
    List<LotteryPrize> findByAvailableTrue();
}

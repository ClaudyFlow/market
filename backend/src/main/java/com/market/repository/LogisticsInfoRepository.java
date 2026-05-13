package com.market.repository;

import com.market.entity.LogisticsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 物流信息数据访问层
 * 对应实体：LogisticsInfo
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface LogisticsInfoRepository extends JpaRepository<LogisticsInfo, Long> {

    /**
     * 根据订单ID查询物流信息
     *
     * @param orderId 订单ID
     * @return 物流信息对象，不存在返回empty
     */
    Optional<LogisticsInfo> findByOrderId(Long orderId);

    /**
     * 根据运单号查询物流信息
     *
     * @param trackingNo 运单号
     * @return 物流信息对象，不存在返回empty
     */
    Optional<LogisticsInfo> findByTrackingNo(String trackingNo);
}

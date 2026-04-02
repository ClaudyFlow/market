package com.market.repository;

import com.market.entity.LogisticsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 物流信息数据访问层
 */
@Repository
public interface LogisticsInfoRepository extends JpaRepository<LogisticsInfo, Long> {
    
    Optional<LogisticsInfo> findByOrderId(Long orderId);
    
    Optional<LogisticsInfo> findByTrackingNo(String trackingNo);
}

package com.market.repository;

import com.market.entity.LogisticsTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 物流轨迹数据访问层
 */
@Repository
public interface LogisticsTrackRepository extends JpaRepository<LogisticsTrack, Long> {
    
    List<LogisticsTrack> findByTrackingNoOrderByTimeDesc(String trackingNo);
    
    List<LogisticsTrack> findByLogisticsInfoId(Long logisticsInfoId);
}

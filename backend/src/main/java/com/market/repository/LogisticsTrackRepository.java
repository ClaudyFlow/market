package com.market.repository;

import com.market.entity.LogisticsTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 物流轨迹数据访问层
 * 对应实体：LogisticsTrack
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface LogisticsTrackRepository extends JpaRepository<LogisticsTrack, Long> {

    /**
     * 根据运单号查询物流轨迹列表，按时间倒序
     *
     * @param trackingNo 运单号
     * @return 物流轨迹列表
     */
    List<LogisticsTrack> findByTrackingNoOrderByTimeDesc(String trackingNo);

    /**
     * 根据物流信息ID查询物流轨迹列表
     *
     * @param logisticsInfoId 物流信息ID
     * @return 物流轨迹列表
     */
    List<LogisticsTrack> findByLogisticsInfoId(Long logisticsInfoId);
}

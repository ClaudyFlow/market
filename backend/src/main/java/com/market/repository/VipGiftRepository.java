package com.market.repository;

import com.market.entity.VipGift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * VIP 礼包数据访问层
 */
@Repository
public interface VipGiftRepository extends JpaRepository<VipGift, Long> {
    
    List<VipGift> findByType(String type);
    
    List<VipGift> findByStatus(String status);
    
    List<VipGift> findByVipLevelRequiredLessThanEqualOrderByVipLevelRequiredAsc(Integer vipLevelRequired);
    
    Page<VipGift> findByTypeAndStatus(String type, String status, Pageable pageable);
    
    @Query("SELECT g FROM VipGift g WHERE g.status = 'ACTIVE' AND g.type = :type " +
           "AND g.vipLevelRequired <= :vipLevel ORDER BY g.vipLevelRequired ASC")
    List<VipGift> findAvailableGifts(@Param("type") String type, @Param("vipLevel") Integer vipLevel);
    
    @Query("SELECT g FROM VipGift g WHERE g.status = 'ACTIVE' AND g.type IN ('DAILY', 'MONTHLY') " +
           "AND g.vipLevelRequired <= :vipLevel ORDER BY g.type ASC, g.vipLevelRequired ASC")
    List<VipGift> findPeriodGifts(@Param("vipLevel") Integer vipLevel);
}

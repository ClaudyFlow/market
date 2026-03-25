package com.market.repository;

import com.market.entity.VipLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * VIP 等级数据访问层
 */
@Repository
public interface VipLevelRepository extends JpaRepository<VipLevel, Long> {
    
    Optional<VipLevel> findByLevel(Integer level);
    
    List<VipLevel> findAllByOrderByLevelAsc();
    
    @Query("SELECT v FROM VipLevel v WHERE v.growthValueRequired <= :growthValue ORDER BY v.growthValueRequired DESC")
    Optional<VipLevel> findHighestLevelByGrowthValue(@Param("growthValue") Integer growthValue);
    
    List<VipLevel> findByGrowthValueRequiredLessThanEqualOrderByGrowthValueRequiredDesc(Integer growthValue);
}

package com.market.repository;

import com.market.entity.VipLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * VIP等级数据访问层
 * 对应实体：VipLevel
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface VipLevelRepository extends JpaRepository<VipLevel, Long> {

    /**
     * 根据VIP等级查询
     *
     * @param level VIP等级
     * @return VIP等级对象，不存在返回empty
     */
    Optional<VipLevel> findByLevel(Integer level);

    /**
     * 查询所有VIP等级，按等级升序
     *
     * @return VIP等级列表
     */
    List<VipLevel> findAllByOrderByLevelAsc();

    /**
     * 根据成长值查询最高可達的VIP等级
     *
     * @param growthValue 成长值
     * @return VIP等级对象，不存在返回empty
     */
    @Query("SELECT v FROM VipLevel v WHERE v.growthValueRequired <= :growthValue ORDER BY v.growthValueRequired DESC")
    Optional<VipLevel> findHighestLevelByGrowthValue(@Param("growthValue") Integer growthValue);

    /**
     * 查询小于等于指定成长值的VIP等级列表，按成长值降序
     *
     * @param growthValue 成长值
     * @return VIP等级列表
     */
    List<VipLevel> findByGrowthValueRequiredLessThanEqualOrderByGrowthValueRequiredDesc(Integer growthValue);
}

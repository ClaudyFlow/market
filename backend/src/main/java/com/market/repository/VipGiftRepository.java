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
 * VIP礼包数据访问层
 * 对应实体：VipGift
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface VipGiftRepository extends JpaRepository<VipGift, Long> {

    /**
     * 根据类型查询VIP礼包列表
     *
     * @param type 礼包类型
     * @return VIP礼包列表
     */
    List<VipGift> findByType(String type);

    /**
     * 根据状态查询VIP礼包列表
     *
     * @param status 礼包状态
     * @return VIP礼包列表
     */
    List<VipGift> findByStatus(String status);

    /**
     * 查询小于等于指定VIP等级的礼包列表，按所需等级升序
     *
     * @param vipLevelRequired VIP等级
     * @return VIP礼包列表
     */
    List<VipGift> findByVipLevelRequiredLessThanEqualOrderByVipLevelRequiredAsc(Integer vipLevelRequired);

    /**
     * 分页查询指定类型和状态的VIP礼包
     *
     * @param type 礼包类型
     * @param status 礼包状态
     * @param pageable 分页参数
     * @return VIP礼包分页
     */
    Page<VipGift> findByTypeAndStatus(String type, String status, Pageable pageable);

    /**
     * 查询用户可用的礼包列表（根据VIP等级）
     *
     * @param type 礼包类型
     * @param vipLevel 用户VIP等级
     * @return 可用礼包列表
     */
    @Query("SELECT g FROM VipGift g WHERE g.status = 'ACTIVE' AND g.type = :type " +
           "AND g.vipLevelRequired <= :vipLevel ORDER BY g.vipLevelRequired ASC")
    List<VipGift> findAvailableGifts(@Param("type") String type, @Param("vipLevel") Integer vipLevel);

    /**
     * 查询周期性礼包（每日/每月）
     *
     * @param vipLevel 用户VIP等级
     * @return 周期性礼包列表
     */
    @Query("SELECT g FROM VipGift g WHERE g.status = 'ACTIVE' AND g.type IN ('DAILY', 'MONTHLY') " +
           "AND g.vipLevelRequired <= :vipLevel ORDER BY g.type ASC, g.vipLevelRequired ASC")
    List<VipGift> findPeriodGifts(@Param("vipLevel") Integer vipLevel);
}

package com.market.repository;

import com.market.entity.VipGiftRecord;
import com.market.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * VIP礼包领取记录数据访问层
 * 对应实体：VipGiftRecord
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface VipGiftRecordRepository extends JpaRepository<VipGiftRecord, Long> {

    /**
     * 根据用户查询VIP礼包领取记录
     *
     * @param user 用户对象
     * @return VIP礼包领取记录列表
     */
    List<VipGiftRecord> findByUser(User user);

    /**
     * 分页查询用户VIP礼包领取记录
     *
     * @param user 用户对象
     * @param pageable 分页参数
     * @return VIP礼包领取记录分页
     */
    Page<VipGiftRecord> findByUser(User user, Pageable pageable);

    /**
     * 查询用户最近领取的指定礼包记录
     *
     * @param user 用户对象
     * @param giftId 礼包ID
     * @param startTime 起始时间
     * @return 领取记录列表
     */
    @Query("SELECT r FROM VipGiftRecord r WHERE r.user = :user AND r.gift.id = :giftId " +
           "AND r.claimedAt >= :startTime ORDER BY r.claimedAt DESC")
    List<VipGiftRecord> findRecentClaims(@Param("user") User user,
                                         @Param("giftId") Long giftId,
                                         @Param("startTime") LocalDateTime startTime);

    /**
     * 统计用户领取指定礼包的次数
     *
     * @param user 用户对象
     * @param giftId 礼包ID
     * @return 领取次数
     */
    @Query("SELECT COUNT(r) FROM VipGiftRecord r WHERE r.user = :user AND r.gift.id = :giftId")
    long countByUserAndGift(@Param("user") User user, @Param("giftId") Long giftId);

    /**
     * 查询用户最近一次领取的指定礼包记录
     *
     * @param user 用户对象
     * @param giftId 礼包ID
     * @return 领取记录对象，不存在返回empty
     */
    Optional<VipGiftRecord> findByUserAndGiftIdOrderByClaimedAtDesc(User user, Long giftId);

    /**
     * 根据用户和礼包类型查询领取记录
     *
     * @param user 用户对象
     * @param giftType 礼包类型
     * @return 领取记录列表
     */
    List<VipGiftRecord> findByUserAndGiftType(User user, String giftType);

    /**
     * 查询用户最近领取的指定类型礼包记录
     *
     * @param user 用户对象
     * @param giftType 礼包类型
     * @param startTime 起始时间
     * @return 领取记录列表
     */
    @Query("SELECT r FROM VipGiftRecord r WHERE r.user = :user AND r.gift.type = :giftType " +
           "AND r.claimedAt >= :startTime ORDER BY r.claimedAt DESC")
    List<VipGiftRecord> findRecentClaimsByType(@Param("user") User user,
                                               @Param("giftType") String giftType,
                                               @Param("startTime") LocalDateTime startTime);
}

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
 * VIP 礼包领取记录数据访问层
 */
@Repository
public interface VipGiftRecordRepository extends JpaRepository<VipGiftRecord, Long> {
    
    List<VipGiftRecord> findByUser(User user);
    
    Page<VipGiftRecord> findByUser(User user, Pageable pageable);
    
    @Query("SELECT r FROM VipGiftRecord r WHERE r.user = :user AND r.gift.id = :giftId " +
           "AND r.claimedAt >= :startTime ORDER BY r.claimedAt DESC")
    List<VipGiftRecord> findRecentClaims(@Param("user") User user, 
                                         @Param("giftId") Long giftId,
                                         @Param("startTime") LocalDateTime startTime);
    
    @Query("SELECT COUNT(r) FROM VipGiftRecord r WHERE r.user = :user AND r.gift.id = :giftId")
    long countByUserAndGift(@Param("user") User user, @Param("giftId") Long giftId);
    
    Optional<VipGiftRecord> findByUserAndGiftIdOrderByClaimedAtDesc(User user, Long giftId);
    
    List<VipGiftRecord> findByUserAndGiftType(User user, String giftType);
    
    @Query("SELECT r FROM VipGiftRecord r WHERE r.user = :user AND r.gift.type = :giftType " +
           "AND r.claimedAt >= :startTime ORDER BY r.claimedAt DESC")
    List<VipGiftRecord> findRecentClaimsByType(@Param("user") User user,
                                               @Param("giftType") String giftType,
                                               @Param("startTime") LocalDateTime startTime);
}

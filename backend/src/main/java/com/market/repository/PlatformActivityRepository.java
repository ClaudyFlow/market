package com.market.repository;

import com.market.entity.PlatformActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatformActivityRepository extends JpaRepository<PlatformActivity, Long> {

    List<PlatformActivity> findByStatusOrderBySortOrderAsc(String status);

    List<PlatformActivity> findByStatusInOrderBySortOrderAsc(List<String> statuses);

    @Query("SELECT pa FROM PlatformActivity pa WHERE pa.status = 'ACTIVE' AND pa.startTime <= CURRENT_TIMESTAMP AND pa.endTime >= CURRENT_TIMESTAMP ORDER BY pa.sortOrder ASC")
    List<PlatformActivity> findActiveActivities();

    @Modifying
    @Query("UPDATE PlatformActivity pa SET pa.participatingMerchantCount = :count WHERE pa.id = :id")
    void updateParticipatingMerchantCount(@Param("id") Long id, @Param("count") Integer count);
}

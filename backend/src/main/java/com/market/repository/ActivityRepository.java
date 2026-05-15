package com.market.repository;

import com.market.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Page<Activity> findByStatus(String status, Pageable pageable);
    Page<Activity> findByMerchantId(Long merchantId, Pageable pageable);
    Page<Activity> findByType(String type, Pageable pageable);
    List<Activity> findByStatusAndStartTimeBeforeAndEndTimeAfter(String status, LocalDateTime now, LocalDateTime now2);
    List<Activity> findByProductId(Long productId);
    boolean existsByProductIdAndStatus(Long productId, String status);
}
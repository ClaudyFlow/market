package com.market.repository;

import com.market.entity.MerchantActivityOptOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MerchantActivityOptOutRepository extends JpaRepository<MerchantActivityOptOut, Long> {

    Optional<MerchantActivityOptOut> findByMerchantIdAndActivityId(Long merchantId, Long activityId);

    List<MerchantActivityOptOut> findByMerchantId(Long merchantId);

    List<MerchantActivityOptOut> findByActivityId(Long activityId);

    List<MerchantActivityOptOut> findByActivityIdAndOptedOutFalse(Long activityId);

    List<MerchantActivityOptOut> findByActivityIdAndOptedOutTrue(Long activityId);

    boolean existsByMerchantIdAndActivityId(Long merchantId, Long activityId);
}

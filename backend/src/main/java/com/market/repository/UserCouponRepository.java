package com.market.repository;

import com.market.entity.User;
import com.market.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    List<UserCoupon> findByUser(User user);
    List<UserCoupon> findByUserAndStatus(User user, String status);
}

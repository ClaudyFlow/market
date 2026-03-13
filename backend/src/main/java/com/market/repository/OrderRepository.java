package com.market.repository;

import com.market.entity.Order;
import com.market.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    Optional<Order> findByOrderNo(String orderNo);
    List<Order> findByUserOrderByCreatedAtDesc(User user);
}

package com.mySpringProject.OnlineBookShop.order.db;

import com.mySpringProject.OnlineBookShop.order.domain.Order;
import com.mySpringProject.OnlineBookShop.order.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusAndCreatedAtLessThanEqual(OrderStatus status, LocalDateTime timestamp);
}

package com.product.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.product.Entity.OrderEntity;

@Repository
public interface OrderRepo extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByUserId(Long userId);

    List<OrderEntity> findByOrderDateBetween(
            LocalDateTime start,
            LocalDateTime end
    );
}
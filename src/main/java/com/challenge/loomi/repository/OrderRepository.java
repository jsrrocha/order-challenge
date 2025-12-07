package com.challenge.loomi.repository;

import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Page<Order> findAllByCustomerId(String customerId, Pageable pageable);

    long countByCustomerIdAndStatus(String customerId, OrderStatus status);

    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Order o JOIN o.items i WHERE o.customerId = :customerId AND i.productId = :productId AND o.status = :status")
    boolean existsByCustomerAndProduct(@Param("customerId") String customerId, @Param("productId") String productId, @Param("status") OrderStatus status);
}
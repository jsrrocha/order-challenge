package com.challenge.loomi.service;

import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.domain.enums.OrderStatus;
import com.challenge.loomi.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProcessingService {
    private final OrderRepository orderRepository;
    private final OrderProcessingHelper helper; // Injetamos o Helper

    @Transactional
    public void processOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            return;
        }

        try {
            helper.executeStrategies(order);
            helper.finalizeSuccess(order);
        } catch (Exception e) {
            helper.handleError(order, e);
        }
    }
}
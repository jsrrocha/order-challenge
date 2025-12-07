package com.challenge.loomi.service.strategy;

import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.domain.entity.OrderItem;
import com.challenge.loomi.domain.enums.OrderType;

public interface OrderProcessingStrategy {
    boolean supports(OrderType type);
    void process(Order order, OrderItem item);
}
package com.challenge.loomi.infra.broker;

import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.domain.entity.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record OrderCreatedEvent(
    String eventId,
    String eventType,
    LocalDateTime timestamp,
    OrderPayload payload
) {

    public static OrderCreatedEvent from(Order order) {
        return new OrderCreatedEvent(
            UUID.randomUUID().toString(),
            "ORDER_CREATED",
            LocalDateTime.now(),
            new OrderPayload(
                order.getId().toString(),
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getItems().stream()
                    .map(OrderItemPayload::from)
                    .collect(Collectors.toList())
            )
        );
    }
}

record OrderPayload(
    String orderId,
    String customerId,
    BigDecimal totalAmount,
    List<OrderItemPayload> items
) {}

record OrderItemPayload(
    String productId,
    Integer quantity
) {
    public static OrderItemPayload from(OrderItem item) {
        return new OrderItemPayload(
            item.getProductId(),
            item.getQuantity()
        );
    }
}
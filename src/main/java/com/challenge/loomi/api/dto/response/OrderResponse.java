package com.challenge.loomi.api.dto.response;

import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.domain.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// Record para representar o pedido completo
public record OrderResponse(
    UUID orderId,
    String customerId,
    BigDecimal totalAmount,
    OrderStatus status,
    LocalDateTime createdAt,
    List<OrderItemResponse> items
) {
    // Mapper estático: Converte Entidade -> DTO
    public static OrderResponse from(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getCustomerId(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getCreatedAt(),
            order.getItems().stream().map(OrderItemResponse::from).toList()
        );
    }
}


package com.challenge.loomi.api.dto.response;

import com.challenge.loomi.domain.entity.OrderItem;

import java.math.BigDecimal;
import java.util.Map;

record OrderItemResponse(
        String productId,
        Integer quantity,
        BigDecimal unitPrice,
        Map<String, Object> metadata
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getProductId(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getMetadata()
        );
    }
}
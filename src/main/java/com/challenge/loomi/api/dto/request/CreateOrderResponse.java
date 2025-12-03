package com.challenge.loomi.api.dto.request;

import com.challenge.loomi.domain.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateOrderResponse(
        UUID orderId,
        String customerId,
        BigDecimal totalAmount,
        OrderStatus status,
        LocalDateTime createdAt
) {}
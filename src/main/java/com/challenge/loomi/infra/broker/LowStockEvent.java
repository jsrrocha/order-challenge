package com.challenge.loomi.infra.broker;

import java.time.LocalDateTime;
import java.util.UUID;

public record LowStockEvent(
    String eventId,
    String eventType,
    LocalDateTime timestamp,
    String productId,
    int remainingStock
) {
    public static LowStockEvent from(String productId, int remainingStock) {
        return new LowStockEvent(
            UUID.randomUUID().toString(),
            "LOW_STOCK_ALERT",
            LocalDateTime.now(),
            productId,
            remainingStock
        );
    }
}
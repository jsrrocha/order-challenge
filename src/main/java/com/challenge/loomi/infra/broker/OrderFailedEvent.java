package com.challenge.loomi.infra.broker;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderFailedEvent(
    String eventId,
    String eventType,
    LocalDateTime timestamp,
    String orderId,
    String customerId,
    String failureReason
) {
    public static OrderFailedEvent from(String orderId, String customerId, String reason) {
        return new OrderFailedEvent(
            UUID.randomUUID().toString(),
            "ORDER_FAILED",
            LocalDateTime.now(),
            orderId,
            customerId,
            reason
        );
    }
}
package com.challenge.loomi.infra.broker;

import com.challenge.loomi.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreated(Order order) {
        try {
            log.info("Sending ORDER_CREATED event for order: {}", order.getId());
            OrderCreatedEvent event = OrderCreatedEvent.from(order);
            kafkaTemplate.send("order-events", order.getId().toString(), event);
        } catch (Exception e) {
            log.error("Error sending order event", e);
        }
    }

    public void sendLowStockAlert(String productId, int remainingStock) {
        try {
            log.warn("🚨 Sending LOW_STOCK_ALERT for product: {}", productId);
            LowStockEvent event = LowStockEvent.from(productId, remainingStock);
            kafkaTemplate.send("stock-alerts", productId, event);
        } catch (Exception e) {
            log.error("Failed to send low stock alert", e);
        }
    }

    public void sendOrderFailed(Order order, String reason) {
        try {
            log.error("Sending ORDER_FAILED event for order: {}. Reason: {}", order.getId(), reason);
            OrderFailedEvent event = OrderFailedEvent.from(
                    order.getId().toString(),
                    order.getCustomerId(),
                    reason
            );

            kafkaTemplate.send("order-failures", order.getId().toString(), event);
        } catch (Exception e) {
            log.error("Failed to send order failure event", e);
        }
    }
}
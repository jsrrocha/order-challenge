package com.challenge.loomi.infra.broker;

import com.challenge.loomi.service.OrderProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {
    private final OrderProcessingService processingService;

    @KafkaListener(topics = "order-events", groupId = "loomi-group")
    public void consume(OrderCreatedEvent event) {
        log.info("Received Kafka event: {}", event.eventId());

        if ("ORDER_CREATED".equals(event.eventType())) {
            try {
                String orderIdStr = event.payload().orderId();
                processingService.processOrder(UUID.fromString(orderIdStr));
                
            } catch (Exception e) {
                log.error("Error consuming event", e);
            }
        }
    }
}
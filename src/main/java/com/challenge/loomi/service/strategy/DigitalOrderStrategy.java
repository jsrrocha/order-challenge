package com.challenge.loomi.service.strategy;

import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.domain.entity.OrderItem;
import com.challenge.loomi.domain.enums.OrderStatus;
import com.challenge.loomi.domain.enums.OrderType;
import com.challenge.loomi.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor // Necessário para injetar o repository
@Slf4j
public class DigitalOrderStrategy implements OrderProcessingStrategy {

    private final OrderRepository orderRepository;

    @Override
    public boolean supports(OrderType type) {
        return type == OrderType.DIGITAL;
    }

    @Override
    public void process(Order order, OrderItem item) {
        String customerId = order.getCustomerId();
        String productId = item.getProductId();

        log.info("[DIGITAL] Processing digital license for product: {}", productId);

        boolean alreadyOwned = orderRepository.existsByCustomerAndProduct(customerId, productId, OrderStatus.PROCESSED);

        if (alreadyOwned) {
            log.error("Customer {} already owns product {}", customerId, productId);
            throw new RuntimeException("ALREADY_OWNED: Customer already purchased this digital content.");
        }

        String licenseKey = UUID.randomUUID().toString().toUpperCase();

        String email = "customer@loomi.com";
        if (item.getMetadata() != null && item.getMetadata().containsKey("email")) {
            email = (String) item.getMetadata().get("email");
        }

        log.info("[DIGITAL] License Generated: {}", licenseKey);
        log.info("[DIGITAL] 📧 Sent access link to {}", email);

    }
}
package com.challenge.loomi.service.strategy;

import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.domain.entity.OrderItem;
import com.challenge.loomi.domain.enums.OrderStatus;
import com.challenge.loomi.domain.enums.OrderType;
import com.challenge.loomi.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionOrderStrategy implements OrderProcessingStrategy {

    private final OrderRepository orderRepository;

    @Override
    public boolean supports(OrderType type) {
        return type == OrderType.SUBSCRIPTION;
    }

    @Override
    public void process(Order order, OrderItem item) {
        String customerId = order.getCustomerId();
        String productId = item.getProductId();

        log.info("[SUBSCRIPTION] Validating rules for user: {}", customerId);

        long activeSubscriptions = orderRepository.countByCustomerIdAndStatus(customerId, OrderStatus.PROCESSED);

        if (activeSubscriptions >= 5) {
            log.error("User {} has reached the limit of 5 active subscriptions.", customerId);
            throw new RuntimeException("SUBSCRIPTION_LIMIT_EXCEEDED: Max 5 active subscriptions allowed.");
        }

        boolean alreadyHasSubscription = orderRepository.existsByCustomerAndProduct(customerId, productId, OrderStatus.PROCESSED);

        if (alreadyHasSubscription) {
            log.error("User {} already has an active subscription for product {}.", customerId, productId);
            throw new RuntimeException("DUPLICATE_SUBSCRIPTION: You already have this plan active.");
        }

        log.info("[SUBSCRIPTION] Validation passed. Activating plan for product: {}", productId);
        log.info("[SUBSCRIPTION] First charge scheduled successfully.");
    }
}
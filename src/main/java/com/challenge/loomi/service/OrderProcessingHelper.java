package com.challenge.loomi.service;

import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.domain.entity.Product;
import com.challenge.loomi.domain.enums.OrderStatus;
import com.challenge.loomi.infra.broker.OrderEventProducer;
import com.challenge.loomi.repository.OrderRepository;
import com.challenge.loomi.repository.ProductRepository;
import com.challenge.loomi.service.strategy.OrderProcessingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderProcessingHelper {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final List<OrderProcessingStrategy> strategies;
    private final OrderEventProducer eventProducer;


    public void executeStrategies(Order order) {
        for (var item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            OrderProcessingStrategy strategy = getStrategyForType(product);
            strategy.process(order, item);
        }
    }

    public void finalizeSuccess(Order order) {
        if (order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.PROCESSED);
            log.info("Order {} processed successfully.", order.getId());
        } else if (order.getStatus() == OrderStatus.PENDING_APPROVAL) {
            log.info("Order {} is waiting for manual approval (Corporate Rule).", order.getId());
        }
        orderRepository.save(order);
    }


    public void handleError(Order order, Exception e) {
        log.error("Failed to process order {}", order.getId(), e);

        order.setStatus(OrderStatus.FAILED);
        orderRepository.save(order);

        eventProducer.sendOrderFailed(order, e.getMessage());
    }

    private OrderProcessingStrategy getStrategyForType(Product product) {
        return strategies.stream()
                .filter(s -> s.supports(product.getType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No strategy for type: " + product.getType()));
    }
}
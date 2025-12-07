package com.challenge.loomi.service;

import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.domain.entity.Product;
import com.challenge.loomi.domain.enums.OrderStatus;
import com.challenge.loomi.repository.OrderRepository;
import com.challenge.loomi.repository.ProductRepository;
import com.challenge.loomi.service.strategy.OrderProcessingStrategy;
import com.challenge.loomi.infra.broker.OrderEventProducer; // Importante
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProcessingService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final List<OrderProcessingStrategy> strategies;
    private final OrderEventProducer eventProducer; // Injetado para enviar eventos de sucesso/erro

    @Transactional
    public void processOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            return;
        }

        try {
            for (var item : order.getItems()) {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                OrderProcessingStrategy strategy = strategies.stream()
                        .filter(s -> s.supports(product.getType()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("No strategy for type: " + product.getType()));

                strategy.process(order, item);
            }

            if (order.getStatus() == OrderStatus.PENDING) {
                order.setStatus(OrderStatus.PROCESSED);
                log.info("Order {} processed successfully.", orderId);
            } else if (order.getStatus() == OrderStatus.PENDING_APPROVAL) {
                log.info("⏳ Order {} is waiting for manual approval (Corporate Rule).", orderId);
            }

            orderRepository.save(order);

        } catch (Exception e) {
            log.error("Failed to process order {}", orderId, e);

            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);

            // TODO: Enviar event de falha
        }
    }
}
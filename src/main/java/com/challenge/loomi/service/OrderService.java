package com.challenge.loomi.service;

import com.challenge.loomi.api.dto.request.CreateOrderRequest;
import com.challenge.loomi.api.dto.request.CreateOrderResponse;
import com.challenge.loomi.api.dto.response.OrderResponse;
import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.infra.broker.OrderEventProducer;
import com.challenge.loomi.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderEventProducer eventProducer;
    private final OrderFactory orderFactory;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        log.info("Call create order for customer: {}", request.customerId());

        Order order = orderFactory.createOrder(request);
        Order savedOrder = orderRepository.saveAndFlush(order);
        log.info("Order created successfully. ID: {}", savedOrder.getId());

        eventProducer.sendOrderCreated(savedOrder);

        return new CreateOrderResponse(
                savedOrder.getId(),
                savedOrder.getCustomerId(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus(),
                savedOrder.getCreatedAt()
        );
    }

    public OrderResponse getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(OrderResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }

    public Page<OrderResponse> listOrdersByCustomer(String customerId, Pageable pageable) {
        return orderRepository.findAllByCustomerId(customerId, pageable)
                .map(OrderResponse::from);
    }
}
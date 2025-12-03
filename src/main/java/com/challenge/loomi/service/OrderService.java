package com.challenge.loomi.service;

import com.challenge.loomi.api.dto.request.CreateOrderRequest;
import com.challenge.loomi.api.dto.request.CreateOrderResponse;
import com.challenge.loomi.api.dto.response.OrderResponse;
import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.domain.entity.OrderItem;
import com.challenge.loomi.domain.entity.Product;
import com.challenge.loomi.repository.OrderRepository;
import com.challenge.loomi.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        log.info("Call create order for customer: {}", request.customerId());

        Order order = Order.builder()
                .customerId(request.customerId())
                .items(new ArrayList<>())
                .build();

        BigDecimal calculatedTotal = BigDecimal.ZERO;

        for (var itemReq : request.items()) {
            Product product = productRepository.findByIdAndActiveTrue(itemReq.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found or inactive: "
                                    + itemReq.productId()));

            BigDecimal itemTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemReq.quantity()));

            calculatedTotal = calculatedTotal.add(itemTotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .quantity(itemReq.quantity())
                    .unitPrice(product.getPrice())
                    .metadata(itemReq.metadata())
                    .build();

            order.addItem(orderItem);
        }

        order.setTotalAmount(calculatedTotal);
        
        Order savedOrder = orderRepository.saveAndFlush(order);
        log.info("Order created successfully. ID: {}", savedOrder.getId());

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
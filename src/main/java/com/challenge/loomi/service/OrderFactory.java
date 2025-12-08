package com.challenge.loomi.service;

import com.challenge.loomi.api.dto.request.CreateOrderRequest;
import com.challenge.loomi.api.dto.request.OrderItemRequest;
import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.domain.entity.OrderItem;
import com.challenge.loomi.domain.entity.Product;
import com.challenge.loomi.domain.enums.OrderStatus;
import com.challenge.loomi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFactory {
    private final ProductRepository productRepository;

    public Order createOrder(CreateOrderRequest request) {
        List<OrderItem> items = request.items().stream()
                .map(this::createOrderItem)
                .toList();

        BigDecimal totalAmount = calculateTotal(items);

        Order order = Order.builder()
                .customerId(request.customerId())
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .items(items)
                .build();

        items.forEach(item -> item.setOrder(order));

        return order;
    }

    private OrderItem createOrderItem(OrderItemRequest itemReq) {
        Product product = productRepository.findByIdAndActiveTrue(itemReq.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found or inactive: " + itemReq.productId()));

        return OrderItem.builder()
                .productId(product.getId())
                .quantity(itemReq.quantity())
                .unitPrice(product.getPrice())
                .metadata(itemReq.metadata())
                .build();
    }

    private BigDecimal calculateTotal(List<OrderItem> items) {
        return items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
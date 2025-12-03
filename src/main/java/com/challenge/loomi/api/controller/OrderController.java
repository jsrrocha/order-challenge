package com.challenge.loomi.api.controller;

import com.challenge.loomi.api.dto.request.CreateOrderRequest;
import com.challenge.loomi.api.dto.request.CreateOrderResponse;
import com.challenge.loomi.api.dto.response.OrderResponse;
import com.challenge.loomi.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        CreateOrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID orderId) {
        OrderResponse response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> listOrders(
            @RequestParam String customerId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<OrderResponse> response = orderService.listOrdersByCustomer(customerId, pageable);
        return ResponseEntity.ok(response);
    }
}
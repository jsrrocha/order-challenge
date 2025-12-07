package service;

import com.challenge.loomi.api.dto.request.CreateOrderRequest;
import com.challenge.loomi.api.dto.request.CreateOrderResponse;
import com.challenge.loomi.api.dto.request.OrderItemRequest;
import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.domain.entity.Product;
import com.challenge.loomi.domain.enums.OrderStatus;
import com.challenge.loomi.domain.enums.OrderType;
import com.challenge.loomi.infra.broker.OrderEventProducer;
import com.challenge.loomi.repository.OrderRepository;
import com.challenge.loomi.repository.ProductRepository;
import com.challenge.loomi.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderEventProducer eventProducer;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("Should create order successfully")
    void createOrder_Success() {
        String productId = "PROD-TEST-001";

        Product mockProduct = Product.builder()
                .id(productId)
                .name("Test Product")
                .price(new BigDecimal("100.00"))
                .type(OrderType.PHYSICAL)
                .active(true)
                .stockQuantity(10)
                .build();

        CreateOrderRequest request = new CreateOrderRequest(
                "customer-123",
                List.of(new OrderItemRequest(productId, 2, null))
        );

        when(productRepository.findByIdAndActiveTrue(eq(productId)))
                .thenReturn(Optional.of(mockProduct));

        when(orderRepository.saveAndFlush(any(Order.class)))
                .thenAnswer(i -> i.getArgument(0));

        CreateOrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(OrderStatus.PENDING, response.status());
        assertEquals(new BigDecimal("200.00"), response.totalAmount());

        verify(orderRepository).saveAndFlush(any(Order.class));
        verify(eventProducer).sendOrderCreated(any(Order.class));
    }

    @Test
    @DisplayName("Should fail when product not found or inactive")
    void createOrder_ProductNotFound() {
        String invalidId = "INVALID-ID";
        CreateOrderRequest request = new CreateOrderRequest(
                "customer-123",
                List.of(new OrderItemRequest(invalidId, 1, null))
        );

        when(productRepository.findByIdAndActiveTrue(eq(invalidId)))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(request));

        verify(orderRepository, never()).saveAndFlush(any());
        verify(eventProducer, never()).sendOrderCreated(any());
    }
}
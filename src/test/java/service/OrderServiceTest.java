package service;

import com.challenge.loomi.api.dto.request.CreateOrderRequest;
import com.challenge.loomi.api.dto.request.CreateOrderResponse;
import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.domain.enums.OrderStatus;
import com.challenge.loomi.infra.broker.OrderEventProducer;
import com.challenge.loomi.repository.OrderRepository;
import com.challenge.loomi.service.OrderFactory;
import com.challenge.loomi.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventProducer eventProducer;

    @Mock
    private OrderFactory orderFactory;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("Should coordinate order creation successfully")
    void createOrder_Success() {
        CreateOrderRequest request = new CreateOrderRequest("customer-123", Collections.emptyList());

        Order assembledOrder = Order.builder()
                .customerId("customer-123")
                .totalAmount(BigDecimal.TEN)
                .status(OrderStatus.PENDING)
                .build();

        when(orderFactory.createOrder(any())).thenReturn(assembledOrder);
        when(orderRepository.saveAndFlush(any(Order.class))).thenReturn(assembledOrder);

        CreateOrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(OrderStatus.PENDING, response.status());

        verify(orderFactory).createOrder(request);
        verify(orderRepository).saveAndFlush(assembledOrder);
        verify(eventProducer).sendOrderCreated(assembledOrder);
    }
}
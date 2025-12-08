package service;

import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.domain.entity.OrderItem;
import com.challenge.loomi.domain.entity.Product;
import com.challenge.loomi.domain.enums.OrderStatus;
import com.challenge.loomi.domain.enums.OrderType;
import com.challenge.loomi.infra.broker.OrderEventProducer;
import com.challenge.loomi.repository.OrderRepository;
import com.challenge.loomi.repository.ProductRepository;
import com.challenge.loomi.service.OrderProcessingHelper;
import com.challenge.loomi.service.strategy.OrderProcessingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderProcessingHelperTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventProducer eventProducer;

    @Mock
    private OrderProcessingStrategy mockStrategy;

    private OrderProcessingHelper helper;

    @BeforeEach
    void setup() {
        List<OrderProcessingStrategy> strategies = Collections.singletonList(mockStrategy);
        helper = new OrderProcessingHelper(productRepository, orderRepository, strategies, eventProducer);
    }

    @Test
    void executeStrategies_Success() {
        Product product = Product.builder().id("P1").type(OrderType.PHYSICAL).build();
        Order order = Order.builder().items(List.of(OrderItem.builder().productId("P1").build())).build();

        when(productRepository.findById("P1")).thenReturn(Optional.of(product));
        when(mockStrategy.supports(OrderType.PHYSICAL)).thenReturn(true);

        helper.executeStrategies(order);

        verify(mockStrategy).process(eq(order), any(OrderItem.class));
    }

    @Test
    void executeStrategies_ProductNotFound() {
        Order order = Order.builder().items(List.of(OrderItem.builder().productId("INVALID").build())).build();
        when(productRepository.findById("INVALID")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> helper.executeStrategies(order));
    }

    @Test
    void executeStrategies_NoStrategyFound() {
        Product product = Product.builder().id("P1").type(OrderType.DIGITAL).build();
        Order order = Order.builder().items(List.of(OrderItem.builder().productId("P1").build())).build();

        when(productRepository.findById("P1")).thenReturn(Optional.of(product));
        when(mockStrategy.supports(OrderType.DIGITAL)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> helper.executeStrategies(order));
    }

    @Test
    void finalizeSuccess_Normal() {
        Order order = Order.builder().id(UUID.randomUUID()).status(OrderStatus.PENDING).build();

        helper.finalizeSuccess(order);

        assertEquals(OrderStatus.PROCESSED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void finalizeSuccess_CorporateRule() {
        Order order = Order.builder().id(UUID.randomUUID()).status(OrderStatus.PENDING_APPROVAL).build();

        helper.finalizeSuccess(order);

        assertEquals(OrderStatus.PENDING_APPROVAL, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void handleError() {
        Order order = Order.builder().id(UUID.randomUUID()).customerId("cust1").status(OrderStatus.PENDING).build();
        RuntimeException ex = new RuntimeException("Error msg");

        helper.handleError(order, ex);

        assertEquals(OrderStatus.FAILED, order.getStatus());
        verify(orderRepository).save(order);
        verify(eventProducer).sendOrderFailed(order, "Error msg");
    }
}
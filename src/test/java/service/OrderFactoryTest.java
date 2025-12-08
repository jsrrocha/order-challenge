package service;

import com.challenge.loomi.api.dto.request.CreateOrderRequest;
import com.challenge.loomi.api.dto.request.OrderItemRequest;
import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.domain.entity.Product;
import com.challenge.loomi.domain.enums.OrderStatus;
import com.challenge.loomi.repository.ProductRepository;
import com.challenge.loomi.service.OrderFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderFactoryTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderFactory orderFactory;

    @Test
    @DisplayName("Should assemble order correctly with calculated total")
    void assembleOrder_Success() {
        String prod1Id = "P1";
        String prod2Id = "P2";

        Product product1 = Product.builder().id(prod1Id).price(new BigDecimal("100.00")).active(true).build();
        Product product2 = Product.builder().id(prod2Id).price(new BigDecimal("50.00")).active(true).build();

        CreateOrderRequest request = new CreateOrderRequest(
                "customer-1",
                List.of(
                        new OrderItemRequest(prod1Id, 2, Map.of()),
                        new OrderItemRequest(prod2Id, 1, Map.of())
                )
        );

        when(productRepository.findByIdAndActiveTrue(prod1Id)).thenReturn(Optional.of(product1));
        when(productRepository.findByIdAndActiveTrue(prod2Id)).thenReturn(Optional.of(product2));

        Order result = orderFactory.createOrder(request);

        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals("customer-1", result.getCustomerId());
        
        assertEquals(new BigDecimal("250.00"), result.getTotalAmount());
        assertEquals(2, result.getItems().size());
        
        assertNotNull(result.getItems().get(0).getOrder());
    }

    @Test
    @DisplayName("Should throw exception if product is not active or found")
    void assembleOrder_ProductNotFound() {
        CreateOrderRequest request = new CreateOrderRequest(
                "customer-1",
                List.of(new OrderItemRequest("INVALID", 1, null))
        );

        when(productRepository.findByIdAndActiveTrue("INVALID")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> orderFactory.createOrder(request));
    }
}
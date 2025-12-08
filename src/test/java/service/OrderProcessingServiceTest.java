package service;

import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.domain.enums.OrderStatus;
import com.challenge.loomi.repository.OrderRepository;
import com.challenge.loomi.service.OrderProcessingHelper;
import com.challenge.loomi.service.OrderProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderProcessingServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProcessingHelper helper;

    @InjectMocks
    private OrderProcessingService service;

    @Test
    void processOrder_Success() {
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder().id(orderId).status(OrderStatus.PENDING).build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        service.processOrder(orderId);

        verify(helper).executeStrategies(order);
        verify(helper).finalizeSuccess(order);
        verify(helper, never()).handleError(any(), any());
    }

    @Test
    void processOrder_Failure() {
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder().id(orderId).status(OrderStatus.PENDING).build();
        RuntimeException exception = new RuntimeException("Strategy failed");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        
        doThrow(exception).when(helper).executeStrategies(order);

        service.processOrder(orderId);

        verify(helper).executeStrategies(order);
        verify(helper, never()).finalizeSuccess(any());
        verify(helper).handleError(order, exception);
    }
}
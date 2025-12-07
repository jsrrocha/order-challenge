package com.challenge.loomi.service.strategy;

import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.domain.entity.OrderItem;
import com.challenge.loomi.domain.entity.Product;
import com.challenge.loomi.domain.enums.OrderType;
import com.challenge.loomi.repository.ProductRepository;
import com.challenge.loomi.infra.broker.OrderEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PhysicalOrderStrategy implements OrderProcessingStrategy {

    private final ProductRepository productRepository;
    private final OrderEventProducer eventProducer; // Injeção do Producer

    @Override
    public boolean supports(OrderType type) {
        return type == OrderType.PHYSICAL;
    }

    @Override
    public void process(Order order, OrderItem item) {
        Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStockQuantity() < item.getQuantity()) {
            throw new RuntimeException("OUT_OF_STOCK: Product " + product.getName() + " is unavailable.");
        }

        int newStock = product.getStockQuantity() - item.getQuantity();
        product.setStockQuantity(newStock);
        productRepository.save(product);

        if (newStock < 5) {
            log.warn("⚠️ [LOW STOCK ALERT] Product {} has only {} items remaining! Sending event...", product.getId(), newStock);
            // Dispara o evento para o Kafka
            eventProducer.sendLowStockAlert(product.getId(), newStock);      }

        log.info("[PHYSICAL] Stock reserved. Dispatching from warehouse.");
    }
}
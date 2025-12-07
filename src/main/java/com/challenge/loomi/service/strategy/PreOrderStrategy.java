package com.challenge.loomi.service.strategy;

import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.domain.entity.OrderItem;
import com.challenge.loomi.domain.enums.OrderType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class PreOrderStrategy implements OrderProcessingStrategy {

    @Override
    public boolean supports(OrderType type) {
        return type == OrderType.PRE_ORDER;
    }

    @Override
    public void process(Order order, OrderItem item) {
        if (item.getMetadata() != null && item.getMetadata().containsKey("releaseDate")) {
            String dateStr = (String) item.getMetadata().get("releaseDate");
            try {
                LocalDate releaseDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
                if (releaseDate.isBefore(LocalDate.now())) {
                    throw new RuntimeException("RELEASE_DATE_PASSED: Cannot pre-order an already released item.");
                }
                log.info("[PRE_ORDER] Release date validated: {}", releaseDate);
            } catch (Exception e) {
                log.warn("[PRE_ORDER] Invalid date format in metadata: {}", dateStr);
            }
        }

        log.info("[PRE_ORDER] Reserved slot for future release. Item: {}", item.getProductId());
    }
}
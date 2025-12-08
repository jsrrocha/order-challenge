package com.challenge.loomi.service.strategy;

import com.challenge.loomi.domain.entity.Order;
import com.challenge.loomi.domain.entity.OrderItem;
import com.challenge.loomi.domain.enums.OrderStatus;
import com.challenge.loomi.domain.enums.OrderType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class CorporateOrderStrategy implements OrderProcessingStrategy {

    @Override
    public boolean supports(OrderType type) {
        return type == OrderType.CORPORATE;
    }

    @Override
    public void process(Order order, OrderItem item) {
        log.info("[CORPORATE] Checking policies for customer: {}", order.getCustomerId());

        if (item.getQuantity() > 100) {
            BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            BigDecimal discountAmount = itemTotal.multiply(new BigDecimal("0.15"));

            BigDecimal newTotal = order.getTotalAmount().subtract(discountAmount);
            order.setTotalAmount(newTotal);

            log.info("[CORPORATE] Volume discount applied! -${} (15% off on item {}). New Total: ${}",
                    discountAmount, item.getProductId(), newTotal);
        }

        BigDecimal autoApprovalLimit = new BigDecimal("50000.00");

        if (order.getTotalAmount().compareTo(autoApprovalLimit) > 0) {
            log.warn("[CORPORATE] Order value ${} exceeds auto-approval limit. Set to PENDING_APPROVAL.", order.getTotalAmount());
            order.setStatus(OrderStatus.PENDING_APPROVAL);
        } else {
            log.info("[CORPORATE] B2B Order Approved automatically.");
        }
    }
}
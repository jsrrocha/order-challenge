package com.challenge.loomi.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Map;

public record OrderItemRequest(
    
    @NotNull(message = "Product ID is required")
    String productId,

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    Integer quantity,

    Map<String, Object> metadata
) {}
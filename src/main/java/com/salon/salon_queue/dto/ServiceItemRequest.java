package com.salon.salon_queue.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ServiceItemRequest(
        @NotBlank(message = "Service name is required")
        String name,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", message = "Price cannot be negative")
        BigDecimal price,

        @NotNull(message = "Duration is required")
        @Min(value = 5, message = "Duration must be at least 5 minutes")
        Integer durationMinutes
) {}
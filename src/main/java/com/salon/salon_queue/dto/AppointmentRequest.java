package com.salon.salon_queue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record AppointmentRequest(
        @NotBlank(message = "Customer name is required")
        String customerName,

        @NotBlank(message = "Customer phone is required")
        @Pattern(regexp = "^\\+?[0-9]{10,13}$", message = "Phone must be 10 to 13 digits")
        String customerPhone,

        @NotNull(message = "Service id is required")
        Long serviceId,

        @NotNull(message = "Staff id is required")
        Long staffId,

        @NotNull(message = "Start time is required")
        LocalDateTime startTime
) {}
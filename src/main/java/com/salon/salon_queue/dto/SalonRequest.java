package com.salon.salon_queue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record SalonRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Address is required") String address,
        @NotNull(message = "Open time is required") LocalTime openTime,
        @NotNull(message = "Close time is required") LocalTime closeTime
) {}
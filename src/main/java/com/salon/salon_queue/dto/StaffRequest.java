package com.salon.salon_queue.dto;

import jakarta.validation.constraints.NotBlank;

public record StaffRequest(
        @NotBlank(message = "Staff name is required")
        String name,

        @NotBlank(message = "Specialization is required")
        String specialization,

        Boolean active
) {}
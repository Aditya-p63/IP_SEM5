package com.salon.salon_queue.dto;

public record StaffResponse(
        Long id,
        String name,
        String specialization,
        boolean active,
        Long salonId
) {}
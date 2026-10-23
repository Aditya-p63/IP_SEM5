package com.salon.salon_queue.dto;

import com.salon.salon_queue.entity.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        String customerName,
        String customerPhone,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status,
        Long serviceId,
        String serviceName,
        Long staffId,
        String staffName,
        Long salonId
) {}
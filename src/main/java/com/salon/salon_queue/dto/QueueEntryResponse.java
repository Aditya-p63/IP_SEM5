package com.salon.salon_queue.dto;

import com.salon.salon_queue.entity.QueueStatus;

import java.time.LocalDateTime;

public record QueueEntryResponse(
        Long id,
        String customerName,
        String customerPhone,
        LocalDateTime joinedAt,
        LocalDateTime startedAt,
        QueueStatus status,
        Long serviceId,
        String serviceName,
        Long staffId,
        String staffName,
        Long salonId
) {}
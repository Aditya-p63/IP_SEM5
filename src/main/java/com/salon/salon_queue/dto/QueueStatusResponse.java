package com.salon.salon_queue.dto;

import com.salon.salon_queue.entity.QueueStatus;

public record QueueStatusResponse(
        Long entryId,
        String customerName,
        QueueStatus status,
        Integer position,
        Integer estimatedWaitMinutes,
        String message
) {}
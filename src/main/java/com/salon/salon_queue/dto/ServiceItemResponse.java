package com.salon.salon_queue.dto;

import java.math.BigDecimal;

public record ServiceItemResponse(
        Long id,
        String name,
        BigDecimal price,
        Integer durationMinutes,
        Long salonId
) {}
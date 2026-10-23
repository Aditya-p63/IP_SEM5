package com.salon.salon_queue.dto;

import java.time.LocalTime;

public record SalonResponse(
        Long id,
        String name,
        String address,
        LocalTime openTime,
        LocalTime closeTime
) {}
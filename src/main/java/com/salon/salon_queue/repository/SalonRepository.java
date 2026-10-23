package com.salon.salon_queue.repository;

import com.salon.salon_queue.entity.Salon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalonRepository extends JpaRepository<Salon, Long> {
}
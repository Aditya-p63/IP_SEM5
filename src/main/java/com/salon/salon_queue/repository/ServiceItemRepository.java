package com.salon.salon_queue.repository;

import com.salon.salon_queue.entity.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {

    List<ServiceItem> findBySalonId(Long salonId);

    boolean existsBySalonId(Long salonId);
}
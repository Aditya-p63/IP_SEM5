package com.salon.salon_queue.repository;

import com.salon.salon_queue.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    List<Staff> findBySalonId(Long salonId);

    long countBySalonIdAndActiveTrue(Long salonId);

    boolean existsBySalonId(Long salonId);
}
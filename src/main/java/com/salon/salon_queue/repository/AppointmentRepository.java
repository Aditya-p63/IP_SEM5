package com.salon.salon_queue.repository;

import com.salon.salon_queue.entity.Appointment;
import com.salon.salon_queue.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findBySalonIdOrderByStartTimeAsc(Long salonId);

    List<Appointment> findBySalonIdAndStartTimeBetweenOrderByStartTimeAsc(
            Long salonId, LocalDateTime from, LocalDateTime to);

    List<Appointment> findByStaffIdAndStatusAndStartTimeBetweenOrderByStartTimeAsc(
            Long staffId, AppointmentStatus status, LocalDateTime from, LocalDateTime to);

    boolean existsBySalonId(Long salonId);

    boolean existsByServiceId(Long serviceId);

    boolean existsByStaffId(Long staffId);

    // True if this staff member already has a BOOKED appointment overlapping [start, end)
    @Query("""
            select count(a) > 0 from Appointment a
            where a.staff.id = :staffId
              and a.status = com.salon.salon_queue.entity.AppointmentStatus.BOOKED
              and a.startTime < :end
              and a.endTime > :start
              and a.id <> :excludeId
            """)
    boolean existsOverlap(@Param("staffId") Long staffId,
                          @Param("start") LocalDateTime start,
                          @Param("end") LocalDateTime end,
                          @Param("excludeId") Long excludeId);
}
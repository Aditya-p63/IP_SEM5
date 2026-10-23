package com.salon.salon_queue.repository;

import com.salon.salon_queue.entity.QueueEntry;
import com.salon.salon_queue.entity.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface QueueEntryRepository extends JpaRepository<QueueEntry, Long> {

    List<QueueEntry> findBySalonIdOrderByJoinedAtAsc(Long salonId);

    List<QueueEntry> findBySalonIdAndStatusOrderByJoinedAtAsc(Long salonId, QueueStatus status);

    List<QueueEntry> findByStaffIdAndStatus(Long staffId, QueueStatus status);

    boolean existsByStaffIdAndStatus(Long staffId, QueueStatus status);

    boolean existsBySalonId(Long salonId);

    boolean existsByServiceId(Long serviceId);

    boolean existsByStaffId(Long staffId);

    // All WAITING entries in the same salon that joined before this one.
    // The id check breaks ties if two people join in the exact same instant.
    @Query("""
            select q from QueueEntry q
            where q.salon.id = :salonId
              and q.status = com.salon.salon_queue.entity.QueueStatus.WAITING
              and (q.joinedAt < :joinedAt or (q.joinedAt = :joinedAt and q.id < :id))
            order by q.joinedAt asc, q.id asc
            """)
    List<QueueEntry> findWaitingAhead(@Param("salonId") Long salonId,
                                      @Param("joinedAt") LocalDateTime joinedAt,
                                      @Param("id") Long id);
}
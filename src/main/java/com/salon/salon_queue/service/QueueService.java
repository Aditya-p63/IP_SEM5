package com.salon.salon_queue.service;

import com.salon.salon_queue.dto.QueueEntryRequest;
import com.salon.salon_queue.dto.QueueEntryResponse;
import com.salon.salon_queue.dto.QueueStatusResponse;
import com.salon.salon_queue.entity.*;
import com.salon.salon_queue.exception.InvalidRequestException;
import com.salon.salon_queue.exception.ResourceNotFoundException;
import com.salon.salon_queue.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueEntryRepository queueRepository;
    private final SalonRepository salonRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final StaffRepository staffRepository;

    @Transactional
    public QueueEntryResponse join(Long salonId, QueueEntryRequest request) {
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salon not found with id " + salonId));
        ServiceItem service = findService(request.serviceId(), salonId);

        QueueEntry entry = new QueueEntry();
        entry.setCustomerName(request.customerName());
        entry.setCustomerPhone(request.customerPhone());
        entry.setJoinedAt(LocalDateTime.now());
        entry.setStatus(QueueStatus.WAITING);
        entry.setService(service);
        entry.setSalon(salon);
        return toResponse(queueRepository.save(entry));
    }

    @Transactional(readOnly = true)
    public List<QueueEntryResponse> getBySalon(Long salonId, QueueStatus status) {
        List<QueueEntry> list = (status == null)
                ? queueRepository.findBySalonIdOrderByJoinedAtAsc(salonId)
                : queueRepository.findBySalonIdAndStatusOrderByJoinedAtAsc(salonId, status);
        return list.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public QueueEntryResponse getById(Long id) {
        return toResponse(findEntry(id));
    }

    @Transactional
    public QueueEntryResponse update(Long id, QueueEntryRequest request) {
        QueueEntry entry = findEntry(id);
        if (entry.getStatus() != QueueStatus.WAITING) {
            throw new InvalidRequestException("Only WAITING entries can be edited");
        }
        entry.setCustomerName(request.customerName());
        entry.setCustomerPhone(request.customerPhone());
        entry.setService(findService(request.serviceId(), entry.getSalon().getId()));
        return toResponse(queueRepository.save(entry));
    }

    @Transactional
    public QueueEntryResponse changeStatus(Long id, QueueStatus newStatus, Long staffId) {
        QueueEntry entry = findEntry(id);
        QueueStatus current = entry.getStatus();

        boolean valid = (current == QueueStatus.WAITING && newStatus == QueueStatus.IN_SERVICE)
                || (current == QueueStatus.WAITING && newStatus == QueueStatus.LEFT)
                || (current == QueueStatus.IN_SERVICE && newStatus == QueueStatus.DONE);
        if (!valid) {
            throw new InvalidRequestException("Cannot change status from " + current + " to " + newStatus);
        }

        if (newStatus == QueueStatus.IN_SERVICE) {
            if (staffId == null) {
                throw new InvalidRequestException("staffId is required to start service");
            }
            Staff staff = staffRepository.findById(staffId)
                    .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id " + staffId));
            if (!staff.getSalon().getId().equals(entry.getSalon().getId()) || !staff.isActive()) {
                throw new InvalidRequestException("Staff must be active and belong to this salon");
            }
            entry.setStaff(staff);
            entry.setStartedAt(LocalDateTime.now());
        }
        entry.setStatus(newStatus);
        return toResponse(queueRepository.save(entry));
    }

    @Transactional
    public void delete(Long id) {
        queueRepository.delete(findEntry(id));
    }

    // Live position and wait time (the frontend polls this)
    @Transactional(readOnly = true)
    public QueueStatusResponse getLiveStatus(Long id) {
        QueueEntry entry = findEntry(id);

        if (entry.getStatus() != QueueStatus.WAITING) {
            String msg = switch (entry.getStatus()) {
                case IN_SERVICE -> "Your service has started";
                case DONE -> "Your service is complete";
                default -> "You left the queue";
            };
            return new QueueStatusResponse(id, entry.getCustomerName(), entry.getStatus(), null, null, msg);
        }

        Long salonId = entry.getSalon().getId();
        List<QueueEntry> ahead = queueRepository.findWaitingAhead(salonId, entry.getJoinedAt(), id);
        int position = ahead.size() + 1;

        int minutes = 0;
        for (QueueEntry a : ahead) {
            minutes += a.getService().getDurationMinutes();
        }
        // add the time still left for customers currently being served
        LocalDateTime now = LocalDateTime.now();
        for (QueueEntry s : queueRepository.findBySalonIdAndStatusOrderByJoinedAtAsc(salonId, QueueStatus.IN_SERVICE)) {
            long elapsed = Duration.between(s.getStartedAt(), now).toMinutes();
            minutes += (int) Math.max(0, s.getService().getDurationMinutes() - elapsed);
        }

        long activeStaff = Math.max(1, staffRepository.countBySalonIdAndActiveTrue(salonId));
        int wait = (int) ((minutes + activeStaff - 1) / activeStaff); // divide, rounded up

        return new QueueStatusResponse(id, entry.getCustomerName(), entry.getStatus(),
                position, wait, "You are #" + position + " in line, about " + wait + " min wait");
    }

    private QueueEntry findEntry(Long id) {
        return queueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Queue entry not found with id " + id));
    }

    private ServiceItem findService(Long serviceId, Long salonId) {
        ServiceItem service = serviceItemRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id " + serviceId));
        if (!service.getSalon().getId().equals(salonId)) {
            throw new InvalidRequestException("Service does not belong to this salon");
        }
        return service;
    }

    private QueueEntryResponse toResponse(QueueEntry q) {
        Staff s = q.getStaff();
        return new QueueEntryResponse(
                q.getId(), q.getCustomerName(), q.getCustomerPhone(),
                q.getJoinedAt(), q.getStartedAt(), q.getStatus(),
                q.getService().getId(), q.getService().getName(),
                s == null ? null : s.getId(),
                s == null ? null : s.getName(),
                q.getSalon().getId());
    }
}
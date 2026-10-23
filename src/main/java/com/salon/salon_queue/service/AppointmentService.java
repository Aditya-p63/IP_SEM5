package com.salon.salon_queue.service;

import com.salon.salon_queue.dto.AppointmentRequest;
import com.salon.salon_queue.dto.AppointmentResponse;
import com.salon.salon_queue.entity.*;
import com.salon.salon_queue.exception.ConflictException;
import com.salon.salon_queue.exception.InvalidRequestException;
import com.salon.salon_queue.exception.ResourceNotFoundException;
import com.salon.salon_queue.repository.AppointmentRepository;
import com.salon.salon_queue.repository.SalonRepository;
import com.salon.salon_queue.repository.ServiceItemRepository;
import com.salon.salon_queue.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private static final int SLOT_STEP_MINUTES = 15;

    private final AppointmentRepository appointmentRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final StaffRepository staffRepository;
    private final SalonRepository salonRepository;

    @Transactional
    public AppointmentResponse create(AppointmentRequest request) {
        Appointment appointment = new Appointment();
        applyRequest(appointment, request, -1L); // -1 matches no real row
        return toResponse(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getBySalon(Long salonId, LocalDate date) {
        findSalon(salonId);
        List<Appointment> list = (date == null)
                ? appointmentRepository.findBySalonIdOrderByStartTimeAsc(salonId)
                : appointmentRepository.findBySalonIdAndStartTimeBetweenOrderByStartTimeAsc(
                salonId, date.atStartOfDay(), date.plusDays(1).atStartOfDay());
        return list.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getById(Long id) {
        return toResponse(findAppointment(id));
    }

    @Transactional
    public AppointmentResponse update(Long id, AppointmentRequest request) {
        Appointment appointment = findAppointment(id);
        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new InvalidRequestException("Only BOOKED appointments can be edited");
        }
        applyRequest(appointment, request, id); // exclude itself from the overlap check
        return toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentResponse cancel(Long id) {
        Appointment appointment = findAppointment(id);
        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new InvalidRequestException("Only BOOKED appointments can be cancelled");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public void delete(Long id) {
        appointmentRepository.delete(findAppointment(id));
    }

    @Transactional(readOnly = true)
    public List<LocalTime> getAvailableSlots(Long salonId, Long serviceId, Long staffId, LocalDate date) {
        Salon salon = findSalon(salonId);
        ServiceItem service = findService(serviceId);
        Staff staff = findStaff(staffId);

        if (!service.getSalon().getId().equals(salonId) || !staff.getSalon().getId().equals(salonId)) {
            throw new InvalidRequestException("Service and staff must belong to this salon");
        }
        if (!staff.isActive()) {
            throw new InvalidRequestException("Staff member is not active");
        }

        List<Appointment> booked = appointmentRepository
                .findByStaffIdAndStatusAndStartTimeBetweenOrderByStartTimeAsc(
                        staffId, AppointmentStatus.BOOKED,
                        date.atStartOfDay(), date.plusDays(1).atStartOfDay());

        int duration = service.getDurationMinutes();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime closing = date.atTime(salon.getCloseTime());
        LocalDateTime cursor = date.atTime(salon.getOpenTime());
        List<LocalTime> slots = new ArrayList<>();

        while (!cursor.plusMinutes(duration).isAfter(closing)) {
            LocalDateTime slotStart = cursor;
            LocalDateTime slotEnd = cursor.plusMinutes(duration);
            boolean inFuture = !slotStart.isBefore(now);
            boolean free = booked.stream().noneMatch(a ->
                    a.getStartTime().isBefore(slotEnd) && a.getEndTime().isAfter(slotStart));
            if (inFuture && free) {
                slots.add(slotStart.toLocalTime());
            }
            cursor = cursor.plusMinutes(SLOT_STEP_MINUTES);
        }
        return slots;
    }

    // ---- shared booking rules (used by create and update) ----

    private void applyRequest(Appointment appointment, AppointmentRequest request, Long excludeId) {
        ServiceItem service = findService(request.serviceId());
        Staff staff = findStaff(request.staffId());
        Salon salon = service.getSalon();

        if (!staff.getSalon().getId().equals(salon.getId())) {
            throw new InvalidRequestException("Staff and service belong to different salons");
        }
        if (!staff.isActive()) {
            throw new InvalidRequestException("Staff member is not active");
        }

        LocalDateTime start = request.startTime();
        LocalDateTime end = start.plusMinutes(service.getDurationMinutes());

        if (start.isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException("Appointment cannot be in the past");
        }
        validateSalonHours(salon, start, end);

        if (appointmentRepository.existsOverlap(staff.getId(), start, end, excludeId)) {
            throw new ConflictException("Staff member is already booked during this time");
        }

        appointment.setCustomerName(request.customerName());
        appointment.setCustomerPhone(request.customerPhone());
        appointment.setStartTime(start);
        appointment.setEndTime(end);
        appointment.setService(service);
        appointment.setStaff(staff);
        appointment.setSalon(salon);
    }

    private void validateSalonHours(Salon salon, LocalDateTime start, LocalDateTime end) {
        boolean sameDay = start.toLocalDate().equals(end.toLocalDate());
        boolean opensOk = !start.toLocalTime().isBefore(salon.getOpenTime());
        boolean closesOk = !end.toLocalTime().isAfter(salon.getCloseTime());
        if (!sameDay || !opensOk || !closesOk) {
            throw new InvalidRequestException("Appointment must fit within salon hours ("
                    + salon.getOpenTime() + " to " + salon.getCloseTime() + ")");
        }
    }

    private Salon findSalon(Long id) {
        return salonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salon not found with id " + id));
    }

    private ServiceItem findService(Long id) {
        return serviceItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id " + id));
    }

    private Staff findStaff(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id " + id));
    }

    private Appointment findAppointment(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id " + id));
    }

    private AppointmentResponse toResponse(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getCustomerName(),
                a.getCustomerPhone(),
                a.getStartTime(),
                a.getEndTime(),
                a.getStatus(),
                a.getService().getId(),
                a.getService().getName(),
                a.getStaff().getId(),
                a.getStaff().getName(),
                a.getSalon().getId()
        );
    }
}
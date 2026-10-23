package com.salon.salon_queue.controller;

import com.salon.salon_queue.dto.AppointmentRequest;
import com.salon.salon_queue.dto.AppointmentResponse;
import com.salon.salon_queue.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/appointments")
    public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(request));
    }

    @GetMapping("/salons/{salonId}/appointments")
    public List<AppointmentResponse> getBySalon(
            @PathVariable Long salonId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return appointmentService.getBySalon(salonId, date);
    }

    @GetMapping("/appointments/{id}")
    public AppointmentResponse getById(@PathVariable Long id) {
        return appointmentService.getById(id);
    }

    @PutMapping("/appointments/{id}")
    public AppointmentResponse update(@PathVariable Long id,
                                      @Valid @RequestBody AppointmentRequest request) {
        return appointmentService.update(id, request);
    }

    @PatchMapping("/appointments/{id}/cancel")
    public AppointmentResponse cancel(@PathVariable Long id) {
        return appointmentService.cancel(id);
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/salons/{salonId}/available-slots")
    public List<LocalTime> availableSlots(
            @PathVariable Long salonId,
            @RequestParam Long serviceId,
            @RequestParam Long staffId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return appointmentService.getAvailableSlots(salonId, serviceId, staffId, date);
    }
}
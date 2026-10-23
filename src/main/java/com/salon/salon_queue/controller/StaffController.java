package com.salon.salon_queue.controller;

import com.salon.salon_queue.dto.StaffRequest;
import com.salon.salon_queue.dto.StaffResponse;
import com.salon.salon_queue.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @PostMapping("/salons/{salonId}/staff")
    public ResponseEntity<StaffResponse> create(@PathVariable Long salonId,
                                                @Valid @RequestBody StaffRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(staffService.create(salonId, request));
    }

    @GetMapping("/salons/{salonId}/staff")
    public List<StaffResponse> getBySalon(@PathVariable Long salonId) {
        return staffService.getBySalon(salonId);
    }

    @GetMapping("/staff/{id}")
    public StaffResponse getById(@PathVariable Long id) {
        return staffService.getById(id);
    }

    @PutMapping("/staff/{id}")
    public StaffResponse update(@PathVariable Long id,
                                @Valid @RequestBody StaffRequest request) {
        return staffService.update(id, request);
    }

    @DeleteMapping("/staff/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        staffService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
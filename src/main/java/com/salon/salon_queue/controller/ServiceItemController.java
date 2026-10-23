package com.salon.salon_queue.controller;

import com.salon.salon_queue.dto.ServiceItemRequest;
import com.salon.salon_queue.dto.ServiceItemResponse;
import com.salon.salon_queue.service.ServiceItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ServiceItemController {

    private final ServiceItemService serviceItemService;

    @PostMapping("/salons/{salonId}/services")
    public ResponseEntity<ServiceItemResponse> create(@PathVariable Long salonId,
                                                      @Valid @RequestBody ServiceItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceItemService.create(salonId, request));
    }

    @GetMapping("/salons/{salonId}/services")
    public List<ServiceItemResponse> getBySalon(@PathVariable Long salonId) {
        return serviceItemService.getBySalon(salonId);
    }

    @GetMapping("/services/{id}")
    public ServiceItemResponse getById(@PathVariable Long id) {
        return serviceItemService.getById(id);
    }

    @PutMapping("/services/{id}")
    public ServiceItemResponse update(@PathVariable Long id,
                                      @Valid @RequestBody ServiceItemRequest request) {
        return serviceItemService.update(id, request);
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
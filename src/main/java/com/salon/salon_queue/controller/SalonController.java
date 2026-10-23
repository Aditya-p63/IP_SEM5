package com.salon.salon_queue.controller;

import com.salon.salon_queue.dto.SalonRequest;
import com.salon.salon_queue.dto.SalonResponse;
import com.salon.salon_queue.service.SalonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salons")
@RequiredArgsConstructor
public class SalonController {

    private final SalonService salonService;

    @PostMapping
    public ResponseEntity<SalonResponse> create(@Valid @RequestBody SalonRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salonService.create(req));
    }

    @GetMapping
    public List<SalonResponse> getAll() {
        return salonService.getAll();
    }

    @GetMapping("/{id}")
    public SalonResponse getById(@PathVariable Long id) {
        return salonService.getById(id);
    }

    @PutMapping("/{id}")
    public SalonResponse update(@PathVariable Long id, @Valid @RequestBody SalonRequest req) {
        return salonService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        salonService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
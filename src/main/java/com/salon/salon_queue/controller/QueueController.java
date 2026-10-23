package com.salon.salon_queue.controller;

import com.salon.salon_queue.dto.QueueEntryRequest;
import com.salon.salon_queue.dto.QueueEntryResponse;
import com.salon.salon_queue.dto.QueueStatusResponse;
import com.salon.salon_queue.entity.QueueStatus;
import com.salon.salon_queue.service.QueueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @PostMapping("/salons/{salonId}/queue")
    public ResponseEntity<QueueEntryResponse> join(@PathVariable Long salonId,
                                                   @Valid @RequestBody QueueEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(queueService.join(salonId, request));
    }

    @GetMapping("/salons/{salonId}/queue")
    public List<QueueEntryResponse> getBySalon(@PathVariable Long salonId,
                                               @RequestParam(required = false) QueueStatus status) {
        return queueService.getBySalon(salonId, status);
    }

    @GetMapping("/queue/{id}")
    public QueueEntryResponse getById(@PathVariable Long id) {
        return queueService.getById(id);
    }

    @GetMapping("/queue/{id}/status")
    public QueueStatusResponse liveStatus(@PathVariable Long id) {
        return queueService.getLiveStatus(id);
    }

    @PutMapping("/queue/{id}")
    public QueueEntryResponse update(@PathVariable Long id,
                                     @Valid @RequestBody QueueEntryRequest request) {
        return queueService.update(id, request);
    }

    @PatchMapping("/queue/{id}/status")
    public QueueEntryResponse changeStatus(@PathVariable Long id,
                                           @RequestParam QueueStatus status,
                                           @RequestParam(required = false) Long staffId) {
        return queueService.changeStatus(id, status, staffId);
    }

    @DeleteMapping("/queue/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        queueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
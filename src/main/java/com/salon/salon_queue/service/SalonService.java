package com.salon.salon_queue.service;

import com.salon.salon_queue.dto.SalonRequest;
import com.salon.salon_queue.dto.SalonResponse;
import com.salon.salon_queue.entity.Salon;
import com.salon.salon_queue.exception.InvalidRequestException;
import com.salon.salon_queue.exception.ResourceNotFoundException;
import com.salon.salon_queue.repository.SalonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalonService {

    private final SalonRepository salonRepository;

    public SalonResponse create(SalonRequest req) {
        validateHours(req);
        Salon salon = new Salon();
        apply(salon, req);
        return toResponse(salonRepository.save(salon));
    }

    public List<SalonResponse> getAll() {
        return salonRepository.findAll().stream().map(this::toResponse).toList();
    }

    public SalonResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public SalonResponse update(Long id, SalonRequest req) {
        validateHours(req);
        Salon salon = findOrThrow(id);
        apply(salon, req);
        return toResponse(salonRepository.save(salon));
    }

    public void delete(Long id) {
        Salon salon = findOrThrow(id);
        salonRepository.delete(salon);
    }

    // ---------- helpers ----------

    public Salon findOrThrow(Long id) {
        return salonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salon not found with id " + id));
    }

    private void validateHours(SalonRequest req) {
        if (!req.closeTime().isAfter(req.openTime())) {
            throw new InvalidRequestException("Close time must be after open time");
        }
    }

    private void apply(Salon salon, SalonRequest req) {
        salon.setName(req.name());
        salon.setAddress(req.address());
        salon.setOpenTime(req.openTime());
        salon.setCloseTime(req.closeTime());
    }

    private SalonResponse toResponse(Salon s) {
        return new SalonResponse(s.getId(), s.getName(), s.getAddress(),
                s.getOpenTime(), s.getCloseTime());
    }
}
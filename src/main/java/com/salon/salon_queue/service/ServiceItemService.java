package com.salon.salon_queue.service;

import com.salon.salon_queue.dto.ServiceItemRequest;
import com.salon.salon_queue.dto.ServiceItemResponse;
import com.salon.salon_queue.entity.Salon;
import com.salon.salon_queue.entity.ServiceItem;
import com.salon.salon_queue.exception.ResourceNotFoundException;
import com.salon.salon_queue.repository.SalonRepository;
import com.salon.salon_queue.repository.ServiceItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceItemService {

    private final ServiceItemRepository serviceItemRepository;
    private final SalonRepository salonRepository;

    @Transactional
    public ServiceItemResponse create(Long salonId, ServiceItemRequest request) {
        Salon salon = findSalon(salonId);
        ServiceItem item = new ServiceItem();
        item.setName(request.name());
        item.setPrice(request.price());
        item.setDurationMinutes(request.durationMinutes());
        item.setSalon(salon);
        return toResponse(serviceItemRepository.save(item));
    }

    @Transactional(readOnly = true)
    public List<ServiceItemResponse> getBySalon(Long salonId) {
        findSalon(salonId); // 404 if the salon doesn't exist
        return serviceItemRepository.findBySalonId(salonId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServiceItemResponse getById(Long id) {
        return toResponse(findItem(id));
    }

    @Transactional
    public ServiceItemResponse update(Long id, ServiceItemRequest request) {
        ServiceItem item = findItem(id);
        item.setName(request.name());
        item.setPrice(request.price());
        item.setDurationMinutes(request.durationMinutes());
        return toResponse(serviceItemRepository.save(item));
    }

    @Transactional
    public void delete(Long id) {
        ServiceItem item = findItem(id);
        serviceItemRepository.delete(item);
    }

    private Salon findSalon(Long salonId) {
        return salonRepository.findById(salonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salon not found with id " + salonId));
    }

    private ServiceItem findItem(Long id) {
        return serviceItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id " + id));
    }

    private ServiceItemResponse toResponse(ServiceItem item) {
        return new ServiceItemResponse(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getDurationMinutes(),
                item.getSalon().getId()
        );
    }
}
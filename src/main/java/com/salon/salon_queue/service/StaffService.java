package com.salon.salon_queue.service;

import com.salon.salon_queue.dto.StaffRequest;
import com.salon.salon_queue.dto.StaffResponse;
import com.salon.salon_queue.entity.Salon;
import com.salon.salon_queue.entity.Staff;
import com.salon.salon_queue.exception.ResourceNotFoundException;
import com.salon.salon_queue.repository.SalonRepository;
import com.salon.salon_queue.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final SalonRepository salonRepository;

    @Transactional
    public StaffResponse create(Long salonId, StaffRequest request) {
        Salon salon = findSalon(salonId);
        Staff staff = new Staff();
        staff.setName(request.name());
        staff.setSpecialization(request.specialization());
        staff.setActive(request.active() == null || request.active());
        staff.setSalon(salon);
        return toResponse(staffRepository.save(staff));
    }

    @Transactional(readOnly = true)
    public List<StaffResponse> getBySalon(Long salonId) {
        findSalon(salonId);
        return staffRepository.findBySalonId(salonId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StaffResponse getById(Long id) {
        return toResponse(findStaff(id));
    }

    @Transactional
    public StaffResponse update(Long id, StaffRequest request) {
        Staff staff = findStaff(id);
        staff.setName(request.name());
        staff.setSpecialization(request.specialization());
        if (request.active() != null) {
            staff.setActive(request.active());
        }
        return toResponse(staffRepository.save(staff));
    }

    @Transactional
    public void delete(Long id) {
        staffRepository.delete(findStaff(id));
    }

    private Salon findSalon(Long salonId) {
        return salonRepository.findById(salonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salon not found with id " + salonId));
    }

    private Staff findStaff(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id " + id));
    }

    private StaffResponse toResponse(Staff staff) {
        return new StaffResponse(
                staff.getId(),
                staff.getName(),
                staff.getSpecialization(),
                staff.isActive(),
                staff.getSalon().getId()
        );
    }
}
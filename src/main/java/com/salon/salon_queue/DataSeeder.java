package com.salon.salon_queue;

import com.salon.salon_queue.entity.Salon;
import com.salon.salon_queue.entity.ServiceItem;
import com.salon.salon_queue.entity.Staff;
import com.salon.salon_queue.repository.SalonRepository;
import com.salon.salon_queue.repository.ServiceItemRepository;
import com.salon.salon_queue.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final SalonRepository salonRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final StaffRepository staffRepository;

    @Override
    public void run(String... args) {
        if (salonRepository.count() > 0) {
            return; // data already exists, do nothing
        }

        Salon salon = new Salon();
        salon.setName("Glamour Studio");
        salon.setAddress("12 MG Road, Mumbai");
        salon.setOpenTime(LocalTime.of(9, 0));
        salon.setCloseTime(LocalTime.of(20, 0));
        salon = salonRepository.save(salon);

        saveService("Haircut", "300.00", 30, salon);
        saveService("Beard Trim", "150.00", 15, salon);
        saveService("Hair Colour", "1200.00", 60, salon);

        saveStaff("Riya Sharma", "Hair Stylist", salon);
        saveStaff("Amit Verma", "Barber", salon);
    }

    private void saveService(String name, String price, int minutes, Salon salon) {
        ServiceItem s = new ServiceItem();
        s.setName(name);
        s.setPrice(new BigDecimal(price));
        s.setDurationMinutes(minutes);
        s.setSalon(salon);
        serviceItemRepository.save(s);
    }

    private void saveStaff(String name, String specialization, Salon salon) {
        Staff st = new Staff();
        st.setName(name);
        st.setSpecialization(specialization);
        st.setActive(true);
        st.setSalon(salon);
        staffRepository.save(st);
    }
}